package net.cogzmc.punishments.types.impl.manager;

import com.mongodb.*;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CPlayerJoinException;
import net.cogzmc.core.player.mongo.CMongoDatabase;
import net.cogzmc.punishments.PunishEvent;
import net.cogzmc.punishments.PunishmentManager;
import net.cogzmc.punishments.Punishments;
import net.cogzmc.punishments.types.PunishmentException;
import net.cogzmc.punishments.types.TimedPunishment;
import net.cogzmc.punishments.types.impl.TargetOnlinesOnly;
import net.cogzmc.punishments.types.impl.model.MongoPunishment;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;

import java.net.InetAddress;
import java.util.*;

import static net.cogzmc.core.player.mongo.MongoUtils.getValueFrom;

@SuppressWarnings("UnusedParameters")
abstract class BaseMongoManager<T extends MongoPunishment> implements PunishmentManager<T> {
    protected final DBCollection collection;
    private final Map<CPlayer, T> activePunishments = new HashMap<>();
    protected final Class<T> punishmentClazz;

    public BaseMongoManager(Class<T> clazz) {
        punishmentClazz = clazz;
        if (Core.getInstance().getCDatabase() == null || !(Core.getInstance().getCDatabase() instanceof CMongoDatabase))
            throw new IllegalStateException("This is a mongo bean in a strange world! (You've used a mongo punishment system with a non-mongo DB");
        collection = ((CMongoDatabase) Core.getInstance().getCDatabase()).getCollection(clazz.getSimpleName().toLowerCase() + "_punishments");
        if (collection.getIndexInfo().size() == 1) {
            collection.createIndex(new BasicDBObject(PunishmentKey.TARGET.toString(), 1));
            collection.createIndex(new BasicDBObject(PunishmentKey.ISSUER.toString(), 1));
        }
    }

    private T createNewPunishment(COfflinePlayer target, String reason, COfflinePlayer issuer) {
        return createNewPunishment(null, target, reason, issuer, new Date(), true);
    }
    abstract T createNewPunishment(ObjectId objectId, COfflinePlayer target, String reason, COfflinePlayer issuer, Date issued, Boolean active);
    abstract boolean canConnect(CPlayer player, T punishment);

    void onJoin(CPlayer player, T punishment) {}
    void onLeave(CPlayer player, T punishment) {}
    void onPunish(CPlayer player, T punishment) {}
    void onRevoke(CPlayer player, T punishment) {}

    DBObject convertToDBObject(T punishment) {
        BasicDBObjectBuilder builder = BasicDBObjectBuilder.start().add(PunishmentKey.ISSUER.toString(), punishment.getIssuer().getUniqueIdentifier().toString())
                .add(PunishmentKey.DATE_ISSUED.toString(), punishment.getDateIssued())
                .add(PunishmentKey.TARGET.toString(), punishment.getTarget().getUniqueIdentifier().toString())
                .add(PunishmentKey.ACTIVE.toString(), punishment.isActive())
                .add(PunishmentKey.REASON.toString(), punishment.getMessage());
        if (punishment.getMongoId() != null) builder.add("_id", punishment.getMongoId());
        return builder.get();
    }

    T getFromDBObject(DBObject object) {
        UUID issuer = UUID.fromString(getValueFrom(object, PunishmentKey.ISSUER, String.class));
        UUID target = UUID.fromString(getValueFrom(object, PunishmentKey.TARGET, String.class));
        Boolean revoked = getValueFrom(object, PunishmentKey.ACTIVE, Boolean.class);
        String reason = getValueFrom(object, PunishmentKey.REASON, String.class);
        Date issued = getValueFrom(object, PunishmentKey.DATE_ISSUED, Date.class);
        ObjectId id = getValueFrom(object, "_id", ObjectId.class);
        return createNewPunishment(id, Core.getOfflinePlayerByUUID(target), reason, Core.getOfflinePlayerByUUID(issuer), issued, revoked);
    }

    @Override
    public T punish(COfflinePlayer target, String reason, COfflinePlayer issuer) throws PunishmentException {
        if (getActivePunishmentFor(target) != null) throw new PunishmentException("A punishment of this type already exists!");
        if (!(target instanceof CPlayer) && punishmentClazz.isAnnotationPresent(TargetOnlinesOnly.class)) throw new PunishmentException("You can only punish online players with this!");
        T newPunishment = createNewPunishment(target, reason, issuer);
        PunishEvent punishEvent = new PunishEvent(target, issuer, newPunishment);
        Bukkit.getPluginManager().callEvent(punishEvent);
        if (punishEvent.isCancelled()) throw new PunishmentException("The punishment was cancelled!");
        DBObject dbObject = convertToDBObject(newPunishment);
        newPunishment.setMongoId((ObjectId) dbObject.get("_id"));
        collection.save(dbObject);
        if (target instanceof CPlayer) onPunish((CPlayer) target, newPunishment);
        return newPunishment;
    }

    @Override
    public List<T> getPunishmentsFor(COfflinePlayer target) {
        DBCursor dbObjects = collection.find(new BasicDBObject(PunishmentKey.TARGET.toString(), target.getUniqueIdentifier().toString()));
        List<T> punishments = new ArrayList<>();
        for (DBObject dbObject : dbObjects) {
            punishments.add(getFromDBObject(dbObject));
        }
        return punishments;
    }

    @Override
    public T getActivePunishmentFor(COfflinePlayer target) {
        if (target instanceof CPlayer && activePunishments.containsKey(target)) return activePunishments.get(target);
        for (T t : getPunishmentsFor(target)) {
            if (t.isActive()) return t;
        }
        return null;
    }

    @Override
    public void onPlayerLogin(CPlayer player, InetAddress address) throws CPlayerJoinException {
        T activePunishmentFor = getActivePunishmentFor(player);
        if (activePunishmentFor == null) return;
        if (!canConnect(player, activePunishmentFor)) {
            throwJoinExceptionFor(activePunishmentFor);
        }
        activePunishments.put(player, activePunishmentFor);
        onJoin(player, activePunishmentFor);
    }

    protected void throwJoinExceptionFor(T activePunishmentFor) throws CPlayerJoinException {
        PrettyTime prettyTime = new PrettyTime();
        String timeSince = prettyTime.format(activePunishmentFor.getDateIssued());
        String expires = (activePunishmentFor instanceof TimedPunishment) ? prettyTime.format(new Date(activePunishmentFor.getDateIssued().getTime() + (((TimedPunishment) activePunishmentFor).getLengthInSeconds() * 1000))) : "never";
        throw new CPlayerJoinException(Core.getModule(Punishments.class).getFormat("disconnect-message-perm", false,
                new String[]{"<type>", activePunishmentFor.getClass().getSimpleName()},
                new String[]{"<reason>", activePunishmentFor.getMessage()},
                new String[]{"<issuer>", activePunishmentFor.getIssuer().getName()},
                new String[]{"<issued>", timeSince},
                new String[]{"<expires>", expires}
        ));
    }

    @Override
    public void revokePunishment(T punishment) {
        punishment.setActive(false);
        DBObject dbObject = convertToDBObject(punishment);
        collection.save(dbObject);
        if (punishment.getTarget() instanceof CPlayer) onRevoke((CPlayer) punishment.getTarget(), punishment);
    }

    @Override
    public void onPlayerDisconnect(CPlayer player) {
        if (!activePunishments.containsKey(player)) return;
        onLeave(player, activePunishments.get(player));
        activePunishments.remove(player);
    }
}

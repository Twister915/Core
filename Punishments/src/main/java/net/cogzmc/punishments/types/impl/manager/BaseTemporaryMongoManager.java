package net.cogzmc.punishments.types.impl.manager;

import com.mongodb.DBObject;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.punishments.TimedPunishmentManager;
import net.cogzmc.punishments.types.PunishmentException;
import net.cogzmc.punishments.types.impl.TargetOnlinesOnly;
import net.cogzmc.punishments.types.impl.model.MongoTemporaryPunishment;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.UUID;

import static net.cogzmc.core.player.mongo.MongoUtils.getValueFrom;

abstract class BaseTemporaryMongoManager<T extends MongoTemporaryPunishment> extends BaseMongoManager<T> implements TimedPunishmentManager<T> {
    public BaseTemporaryMongoManager(Class<T> clazz) {
        super(clazz);
    }

    abstract T createNewPunishment(ObjectId id, COfflinePlayer target, String reason, COfflinePlayer issuer, Boolean active, Date issued, Integer lengthInSeconds);

    @Override
    T createNewPunishment(ObjectId objectId, COfflinePlayer target, String reason, COfflinePlayer issuer, Date issued, Boolean active) {
        throw new UnsupportedOperationException("This is a temporary punishment manager!");
    }

    @Override
    DBObject convertToDBObject(T object) {
        DBObject dbObject = super.convertToDBObject(object);
        dbObject.put(PunishmentKey.LENGTH.toString(), object.getLengthInSeconds());
        return dbObject;
    }

    @Override
    T getFromDBObject(DBObject object) {
        UUID issuer = UUID.fromString(getValueFrom(object, PunishmentKey.ISSUER, String.class));
        UUID target = UUID.fromString(getValueFrom(object, PunishmentKey.TARGET, String.class));
        Boolean active = getValueFrom(object, PunishmentKey.ACTIVE, Boolean.class);
        String reason = getValueFrom(object, PunishmentKey.REASON, String.class);
        Date issued = getValueFrom(object, PunishmentKey.DATE_ISSUED, Date.class);
        Integer length = getValueFrom(object, PunishmentKey.LENGTH, Integer.class);
        ObjectId id = getValueFrom(object, "_id", ObjectId.class);
        return createNewPunishment(id, Core.getOfflinePlayerByUUID(target), reason, Core.getOfflinePlayerByUUID(issuer), active, issued, length);
    }

    @Override
    public T punish(COfflinePlayer target, String reason, COfflinePlayer issuer, Integer lengthInSeconds) throws PunishmentException {
        if (getActivePunishmentFor(target) != null) throw new PunishmentException("You cannot punish the same player twice!");
        if (!(target instanceof CPlayer) && punishmentClazz.isAnnotationPresent(TargetOnlinesOnly.class)) throw new PunishmentException("You can only punish online players with this!");
        T newPunishment = createNewPunishment(target, reason, issuer, lengthInSeconds);
        DBObject dbObject = convertToDBObject(newPunishment);
        newPunishment.setMongoId((ObjectId) dbObject.get("_id"));
        collection.save(dbObject);
        if (target instanceof CPlayer) onPunish((CPlayer) target, newPunishment);
        return newPunishment;
    }

    private T createNewPunishment(COfflinePlayer target, String reason, COfflinePlayer issuer, Integer lengthInSeconds) {
        return createNewPunishment(null, target, reason, issuer, true, new Date(), lengthInSeconds);
    }


    @Override
    public T punish(COfflinePlayer target, String reason, COfflinePlayer issuer) {
        throw new UnsupportedOperationException("This is a temporary punishment manager!");
    }

    @Override
    public T getActivePunishmentFor(COfflinePlayer target) {
        T activePunishmentFor = super.getActivePunishmentFor(target);
        if (activePunishmentFor == null) return null;
        if (isExpired(activePunishmentFor)) {
            revokePunishment(activePunishmentFor);
            return null;
        }
        return activePunishmentFor;
    }

    public boolean isExpired(T punish) {
        return (punish.getDateIssued().getTime() + (punish.getLengthInSeconds() * 1000)) < (new Date()).getTime();
    }
}

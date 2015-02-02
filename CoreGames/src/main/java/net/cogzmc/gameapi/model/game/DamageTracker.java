package net.cogzmc.gameapi.model.game;

import net.cogzmc.gameapi.model.arena.Arena;

import java.util.*;

public final class DamageTracker<ArenaType extends Arena> extends GameActionDelegate<ArenaType> {
    protected DamageTracker(Game<ArenaType> game) {
        super(game);
    }

    private Map<CPlayer, TrackedDamage> damageTrackers = new HashMap<>();

    @Override
    public void onPlayerJoinGame(CPlayer player) {
        damageTrackers.put(player, new TrackedDamage(player));
    }

    @Override
    public void onRegainHealth(CPlayer cPlayer, Double healthRegaining) {
        damageTrackers.get(cPlayer).regainedHealth(healthRegaining.intValue());
    }

    @Override
    public void onPlayerDead(CPlayer player, DamageCause cause) {
        clearDamagesFor(player);
    }

    @Override
    public void onPlayerKilled(CPlayer player, CPlayer killer, DamageCause cause) {
        clearDamagesFor(player);
    }

    @Override
    public void onPlayerKilled(CPlayer player, LivingEntity killer, DamageCause cause) {
        clearDamagesFor(player);
    }

    @Override
    public void onPlayerDamage(CPlayer player, DamageCause cause, Integer damageTaken) {
        damageTrackers.get(player).takenDamage.add(new DamageInstant(cause, new Date(), Point.of(player.getBukkitPlayer().getLocation()), damageTaken, null));
    }

    @Override
    public void onPlayerDamage(CPlayer target, CPlayer attacker, Integer damageTaken, DamageCause cause) {
        damageTrackers.get(target).takenDamage.add(new DamageInstant(cause, new Date(), Point.of(target.getBukkitPlayer().getLocation()), damageTaken, attacker.getBukkitPlayer()));
    }

    public ImmutableList<DamageInstant> getDamagesFor(CPlayer player) {
        return damageTrackers.get(player).getDamageInstants();
    }

    public void clearDamagesFor(CPlayer player) {
        damageTrackers.get(player).reset();
    }

    @Data
    public static class TrackedDamage {
        private final CPlayer player;
        @Getter(AccessLevel.NONE) private final List<DamageInstant> takenDamage = new LinkedList<>();
        @Setter(AccessLevel.NONE) private Integer waitingForRegain = 0;

        private void regainedHealth(Integer healthPoints) {
            ensureDamageSorted();
            Integer healthPointsTaken = 0;
            Integer healthPointsToTake = healthPoints+waitingForRegain;
            Iterator<DamageInstant> iterator = takenDamage.iterator();
            while (iterator.hasNext()) {
                DamageInstant damageInstant = iterator.next();
                if (healthPointsToTake-healthPointsTaken <= 0) break;
                if (damageInstant.getHealthTaken() > healthPointsToTake-healthPointsTaken) continue;
                healthPointsTaken += damageInstant.getHealthTaken();
                iterator.remove();
            }
            waitingForRegain = healthPointsToTake-healthPointsTaken;
        }

        public void ensureDamageSorted() {
            Collections.sort(takenDamage, new Comparator<DamageInstant>() {
                @Override
                public int compare(DamageInstant o1, DamageInstant o2) {
                    return (int) (o1.getTime().getTime()-o2.getTime().getTime());
                }
            });
        }

        public ImmutableList<DamageInstant> getDamageInstants() {
            return ImmutableList.copyOf(takenDamage);
        }

        public void reset() {
            waitingForRegain = 0;
            takenDamage.clear();
        }
    }

    @Data
    private static class DamageInstant {
        @NonNull private final DamageCause damageCause;
        @NonNull private final Date time;
        @NonNull private final Point location;
        @NonNull private final Integer healthTaken;
        private final LivingEntity source;
    }
}

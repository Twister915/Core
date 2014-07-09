package net.cogzmc.punishments.types.impl.manager;

public enum PunishmentKey {
    ISSUER("issuer"),
    TARGET("target"),
    REASON("reason"),
    LENGTH("length"),
    DATE_ISSUED("issued_date"),
    ACTIVE("active");

    private final String key;

    @Override
    public String toString() {
        return key;
    }

    PunishmentKey(String key) {
        this.key = key;
    }
}

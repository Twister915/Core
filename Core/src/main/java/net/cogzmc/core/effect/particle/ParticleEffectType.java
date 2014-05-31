package net.cogzmc.core.effect.particle;

import org.bukkit.Material;

@SuppressWarnings("SpellCheckingInspection")
public enum ParticleEffectType {
    HUGE_EXPLOSION("hugeexplosion"),
    LARGE_EXPLODE("largeexplode"),
    FIREWORKS_SPARK("fireworksSpark"),
    BUBBLE("bubble"),
    SUSPENDED("suspended"),
    DEPTH_SUSPEND("depthsuspend"),
    TOWN_AURA("townaura"),
    CRIT("crit"),
    MAGIC_CRIT("magicCrit"),
    SMOKE("smoke"),
    MOB_SPELL("mobSpell"),
    MOB_SPELL_AMBIENT("mobSpellAmbient"),
    SPELL("spell"),
    INSTANT_SPELL("instantSpell"),
    WITCH_MAGIC("witchMagic"),
    NOTE("note"),
    PORTAL("portal"),
    ENCHANTMENT_TABLE("enchantmenttable"),
    EXPLODE("explode"),
    FLAME("flame"),
    LAVA("lava"),
    FOOTSTEP("footstep"),
    SPLASH("splash"),
    LARGE_SMOKE("largesmoke"),
    CLOUD("cloud"),
    REDDUST("reddust"),
    SNOWBALL_POOF("snowballpoof"),
    DRIPWATER("dripWater"),
    DRIPLAVA("dripLava"),
    SNOW_SHOVEL("snowshovel"),
    SLIME("slime"),
    HEART("heart"),
    ANGRY_VILLAGER("angryVillager"),
    HAPPY_VILLAGER("happyVillager");

    private final String name;

    ParticleEffectType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static String customBlockCrack(Material block, Short data) {
        return "blockcrack_" + block.getId() + "_" + data;
    }

    public static String customBlockDust(Material block, Short data) {
        return "blockdust_" + block.getId() + " _" + data;
    }

    public static String customIconCrack(Material item) {
        return "iconcrack_" + item.getId();
    }
}

package net.cogzmc.core.player.mongo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.cogzmc.core.Core;
import net.cogzmc.core.gui.InventoryButton;
import net.cogzmc.core.player.*;
import net.cogzmc.core.player.scoreboard.ScoreboardAttachment;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true, of = {"username"})
@ToString(of = {"username"})
final class CMongoPlayer extends COfflineMongoPlayer implements CPlayer {
    @Getter private final String username;
    private WeakReference<Player> _bukkitPlayer;
    private PermissionAttachment permissionAttachment;
    @Getter private boolean firstJoin = false;
    @Getter private InetAddress address = null;
    @Getter private final CooldownManager cooldownManager = new CooldownManager();
    @Getter private final ScoreboardAttachment scoreboardAttachment;

    public CMongoPlayer(Player player, COfflineMongoPlayer offlinePlayer, CMongoPlayerManager manager) {
        super(offlinePlayer, manager);
        this.username = player.getName();
        this._bukkitPlayer = new WeakReference<>(player);
        this.scoreboardAttachment = new ScoreboardAttachment(this);
    }

    void onJoin(InetAddress address) throws DatabaseConnectException {
        Player bukkitPlayer = getBukkitPlayer();
        this.setLastKnownUsername(bukkitPlayer.getName());
        this.setLastTimeOnline(new Date());
        addIfUnique(this.getKnownUsernames(), bukkitPlayer.getName());
        this.address = address;
        logIP(address);
        if (this.getFirstTimeOnline() == null) {
            this.setFirstTimeOnline(new Date());
            this.firstJoin = true;
        }
        saveIntoDatabase();
        reloadPermissions();
    }

    void updateForSaving() {
        Date leaveTime = new Date();
        //                                                         Current time (ex: 1000) minus the time you joined last (ex 50) (result 950 millis online)
        //                         current online time          +  The amount of time spent online this time -> milliseconds
        this.setMillisecondsOnline(this.getMillisecondsOnline() + (leaveTime.getTime()-this.getLastTimeOnline().getTime()));
        this.setLastTimeOnline(leaveTime);
    }

    private static <T> void addIfUnique(List<T> list, T object) {
        if (list == null) return;
        for (T t : list) {
            if (t.equals(object)) return;
        }
        list.add(object);
    }

    @Override
    public String getName() {
        return getBukkitPlayer().getName();
    }

    @Override
    public boolean isOnline() {
        return getBukkitPlayer().isOnline();
    }

    @Override
    public void sendMessage(String... messages) {
        Player bukkitPlayer = getBukkitPlayer();
        for (String message : messages) {
            bukkitPlayer.sendMessage(message);
        }
    }

    @Override
    public void sendFullChatMessage(String... messageLines) {
        int chatBufferSize = 10;
        String[] strings = new String[chatBufferSize];
        int deltaLength = strings.length - messageLines.length;
        if (deltaLength < 0) throw new IllegalArgumentException("There are too many messages for this chat buffer");
        //Padding is the difference in the size of the chat buffer and the size of the message divided by two
        //Example, 8 lines should have a buffer of one on each side (top/bottom). 10-8 = 2, 2/2 = 1. The buffer should be one, and padding = 1!
        //Example 2, 9 lines should have a buffer of 0 on the top and 1 on the bottom. 10-9 = 1, 1/2 = 0.5, buffer = 0, padding = 0
        //Example 3, 5 lines should have a buffer of 2 one one side, 3 on another. 10-5 = 5, 5/2 = 2.5, 2.5 -> 2, buffer = 2, padding = 2
        double padding = Math.floor(deltaLength / 2);
        //x will start at the padding, the lower end of the buffer.
        //x will go until we reach the end of the strings by testing if it is at padding+messageLines.length
        //y tracks the index int the messageLines array.
        for (int x = (int)padding, y = 0; x < padding+messageLines.length; x++, y++) {
            strings[x] = messageLines[y];
        }
        for (String string : strings) {
            if (string == null) sendMessage("");
            else sendMessage(string);
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return getBukkitPlayer().hasPermission(permission);
    }

    @Override
    public void clearChatAll() {
        Player bukkitPlayer = getBukkitPlayer();
        for (int x = 0; x < 50; x++) {
            bukkitPlayer.sendMessage("");
        }
    }

    @Override
    public void clearChatVisible() {
        Player bukkitPlayer = getBukkitPlayer();
        for (int x = 0; x < 20; x++) {
            bukkitPlayer.sendMessage("");
        }
    }

    @Override
    public void playSoundForPlayer(Sound s, Float volume, Float pitch) {
        Player bukkitPlayer = getBukkitPlayer();
        bukkitPlayer.playSound(bukkitPlayer.getLocation(), s, volume, pitch);
    }

    @Override
    public void playSoundForPlayer(Sound s, Float volume) {
        playSoundForPlayer(s, volume, 0f);
    }

    @Override
    public void playSoundForPlayer(Sound s) {
        playSoundForPlayer(s, 10f);
    }

    @Override
    public void giveItem(Material material, Integer quantity, String title, String lore, Map<Enchantment, Integer> enchantments, Integer slot) {
        Player bukkitPlayer = getBukkitPlayer();
        ItemStack stack = new ItemStack(material, quantity);
        ItemMeta itemMeta = stack.getItemMeta();
        if (title != null) itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));
        if (lore != null) itemMeta.setLore(InventoryButton.wrapLoreText(lore));
        if (enchantments != null) stack.addUnsafeEnchantments(enchantments);
        if (slot != null) bukkitPlayer.getInventory().setItem(slot, stack);
        else bukkitPlayer.getInventory().addItem(stack);
    }

    @Override
    public void giveItem(Material material, Integer quantity, String title, String lore, Map<Enchantment, Integer> enchantments) {
        giveItem(material, quantity, title, lore, enchantments, null);
    }

    @Override
    public void giveItem(Material material, Integer quantity, String title, String lore) {
        giveItem(material, quantity, title, lore, null);
    }

    @Override
    public void giveItem(Material material, Integer quantity, String title) {
        giveItem(material, quantity, title, null);
    }

    @Override
    public void giveItem(Material material, String title) {
        giveItem(material, 1, title);
    }

    @Override
    public void giveItem(Material material, Integer quantity) {
        giveItem(material, quantity, null);
    }

    @Override
    public void giveItem(Material material) {
        giveItem(material, 1);
    }

    @Override
    public void addStatusEffect(PotionEffectType type, Integer level, Integer ticks, Boolean ambient) {
        getBukkitPlayer().addPotionEffect(new PotionEffect(type, ticks, Math.max(0, level - 1), ambient));
    }

    @Override
    public void addStatusEffect(PotionEffectType type, Integer level, Integer ticks) {
        addStatusEffect(type, level, ticks, false);
    }

    @Override
    public void addStatusEffect(PotionEffectType type, Integer level) {
        addStatusEffect(type, level, Integer.MAX_VALUE);
    }

    @Override
    public void addStatusEffect(PotionEffectType type) {
        addStatusEffect(type, 1);
    }

    @Override
    public void resetPlayer() {
        Player bukkitPlayer = getBukkitPlayer();
        bukkitPlayer.getInventory().clear();
        bukkitPlayer.setAllowFlight(false);
        bukkitPlayer.setFlying(false);
        bukkitPlayer.setFallDistance(0f);
        bukkitPlayer.setVelocity(new Vector());
        bukkitPlayer.resetMaxHealth();
        bukkitPlayer.setHealth(bukkitPlayer.getMaxHealth());
        bukkitPlayer.setRemainingAir(bukkitPlayer.getMaximumAir());
        bukkitPlayer.setFireTicks(0);
        bukkitPlayer.setFoodLevel(20);
        bukkitPlayer.setTotalExperience(0);
        bukkitPlayer.setExhaustion(0f);
        bukkitPlayer.resetPlayerTime();
        bukkitPlayer.resetPlayerWeather();
        for (PotionEffect potionEffect : bukkitPlayer.getActivePotionEffects()) {
            bukkitPlayer.removePotionEffect(potionEffect.getType());
        }
    }

    @Override
    public Player getBukkitPlayer() {
        return _bukkitPlayer.get();
    }

    @Override
    public COfflinePlayer getNewOfflinePlayer() {
        return new COfflineMongoPlayer(this, playerRepository);
    }

    @Override
    public GeoIPManager.GeoIPInfo getGeoIPInfo() {
        GeoIPManager geoIPManager = ((CMongoPlayerManager) playerRepository).getGeoIPManager();
        if (geoIPManager == null) return null;
        return geoIPManager.getInfoOn(address);
    }

    @Override
    public void reloadPermissions() {
        super.reloadPermissions();
        if (permissionAttachment != null) permissionAttachment.remove();
        permissionAttachment = getBukkitPlayer().addAttachment(Core.getInstance());
        for (Map.Entry<String, Boolean> stringBooleanEntry : getAllPermissions().entrySet()) {
            permissionAttachment.setPermission(stringBooleanEntry.getKey(), stringBooleanEntry.getValue());
        }
    }

}

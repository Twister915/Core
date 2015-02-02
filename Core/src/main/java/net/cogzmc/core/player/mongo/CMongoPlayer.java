package net.cogzmc.core.player.mongo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import net.cogzmc.core.Core;
import net.cogzmc.core.gui.InventoryButton;
import net.cogzmc.core.player.*;
import net.cogzmc.core.util.Point;
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
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.kitteh.tag.TagAPI;

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
    private final CMongoPlayerScoreboardManager scoreboardManager = new CMongoPlayerScoreboardManager(this);
    private String tagName;
    private String currentPrefix = null;

    public CMongoPlayer(Player player, COfflineMongoPlayer offlinePlayer, CMongoPlayerManager manager) {
        super(offlinePlayer, manager);
        this.username = player.getName();
        this._bukkitPlayer = new WeakReference<>(player);
    }

    void onLogin(InetAddress address) throws DatabaseConnectException {
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
        Player bukkitPlayer = getBukkitPlayer();
        return bukkitPlayer == null || bukkitPlayer.isOnline();
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
        for (int x = 0; x < 200; x++) {
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
    public void removeStatusEffect(PotionEffectType type) {
        getBukkitPlayer().removePotionEffect(type);
    }

    @Override
    public void resetPlayer() {
        Player bukkitPlayer = getBukkitPlayer();
        bukkitPlayer.getInventory().clear();
        bukkitPlayer.getInventory().setArmorContents(new ItemStack[4]);
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
    public Point getPoint() {
        return Point.of(getBukkitPlayer().getLocation());
    }

    @Override
    public Point getBlockPoint() {
        return Point.of(getBukkitPlayer().getLocation().getBlock());
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
    public void onJoin() {
        if (hasDisplayName() || hasTagName()) updateTag();
    }

    @Override
    public void setTagName(String tagName) {
        this.tagName = tagName;
        updateTag();
    }

    @Override
    public String getTagName() {
        return tagName == null ? getDisplayName() : tagName;
    }


    @Override
    public void removeTagName() {
        setTagName(null);
    }

    @Override
    public boolean hasTagName() {
        return tagName != null || hasDisplayName();
    }

    @Override
    public void setTagPrefix(String tagPrefix) {
        if(currentPrefix != null){
            for(CPlayer p : Core.getOnlinePlayers()){
                Player pl = p.getBukkitPlayer();
                if(pl != null){
                    Team t = pl.getScoreboard().getPlayerTeam(getBukkitPlayer());
                    if(t != null){
                        t.removePlayer(getBukkitPlayer());
                        if(t.getPlayers().isEmpty()){
                            t.unregister();
                        }
                    }
                }
            }
        }
        currentPrefix = tagPrefix;
        if(currentPrefix != null){
            for(CPlayer p : Core.getOnlinePlayers()){   //For each online player
                Player pl = p.getBukkitPlayer();    //Retrieve the bukkit instance of the player
                if(pl != null){                     //If they are still allocated
                    Team t = pl.getScoreboard().getTeam(tagPrefix); //Retrieve the team this player would be put into
                    if(t == null){                                  //Need to allocate a new team to this scoreboard
                        t = pl.getScoreboard().registerNewTeam(tagPrefix); //Register the new team with the prefix name for easy recovery
                        t.setAllowFriendlyFire(true);
                        t.setCanSeeFriendlyInvisibles(false);
                        t.setPrefix(tagPrefix);
                    }
                    t.addPlayer(getBukkitPlayer()); // Add me
                }
            }
        }
    }

    @Override
    public String getTagPrefix() {
        return currentPrefix;
    }

    @Override
    public void setScoreboard(@NonNull Scoreboard sb) {
        getBukkitPlayer().setScoreboard(sb);
        for(CPlayer p : Core.getOnlinePlayers()){
            String prefix = p.getTagPrefix();
            if(prefix != null){
                Team t = sb.getTeam(prefix);
                if(t == null){
                    t = sb.registerNewTeam(prefix);
                    t.setAllowFriendlyFire(true);
                    t.setCanSeeFriendlyInvisibles(false);
                    t.setPrefix(prefix);
                }
                t.addPlayer(p.getBukkitPlayer());
            }
        }
    }

    @Override
    public Scoreboard getScoreboard() {
        return getBukkitPlayer().getScoreboard();
    }

    @Override
    public CPlayerScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    @Override
    public void kickPlayer(String message) {
        if (Core.getNetworkManager() != null) {
            if (Core.getNetworkManager().kickViaNetworkManager(message, this)) return;
        }
        getBukkitPlayer().kickPlayer(message);
    }

    private void updateTag() {
        TagAPI.refreshPlayer(getBukkitPlayer());
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

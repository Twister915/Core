package net.cogzmc.coreessentials.commands;

public final class TabColorManager implements Listener, GroupReloadObserver {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        updatePlayerListName(Core.getOnlinePlayer(event.getPlayer()));
    }

    void updatePlayerListName(CPlayer player) {
        String tablistColor = player.getTablistColor() == null ? player.getPrimaryGroup().getTablistColor() : player.getTablistColor();
        String s = (tablistColor == null || player.hasDisplayName() ? Core.getPermissionsManager().getDefaultGroup().getTablistColor() : tablistColor) + player.getDisplayName();
        s = s.substring(0, Math.min(16, s.length()));
        player.getBukkitPlayer().setPlayerListName(ChatColor.translateAlternateColorCodes('&', s));
    }

    @Override
    public void onReloadPermissions(CMongoPermissionsManager manager) {
        for (CPlayer cPlayer : Core.getPlayerManager()) {
            updatePlayerListName(cPlayer);
        }
    }
}

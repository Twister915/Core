package net.cogzmc.core.player;

import net.cogzmc.core.Core;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;

/**
 * Listens for a bukkit player joining the server
 */
public class CPlayerJoinPrefixTagListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player me = e.getPlayer();
        for(CPlayer p : Core.getOnlinePlayers()){
            if(p.getTagPrefix() != null){
                Team prefixTeam = me.getScoreboard().getTeam(p.getTagPrefix());
                if(prefixTeam  == null){
                    prefixTeam = me.getScoreboard().registerNewTeam(p.getTagPrefix());
                    prefixTeam.setCanSeeFriendlyInvisibles(false);
                    prefixTeam.setAllowFriendlyFire(false);
                    prefixTeam.setPrefix(p.getTagPrefix());
                }
                prefixTeam.addPlayer(me);
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerKickEvent e){
        leave(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        leave(e.getPlayer());
    }

    public void leave(Player p){
        for(CPlayer pl : Core.getOnlinePlayers()){
            Team t = pl.getScoreboard().getPlayerTeam(p);
            if(t != null){
                t.removePlayer(p);
                if(t.getPlayers().isEmpty()){
                    t.unregister();
                }
            }
        }
    }

}

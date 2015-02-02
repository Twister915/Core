package net.cogzmc.core.player;

import lombok.Data;
import net.cogzmc.core.Core;

import java.util.ArrayList;
import java.util.List;

@Data
public final class CPlayerManagerSaveTask implements Runnable {
    private final CPlayerManager manager;

    @Override
    public void run() {
        List<CPlayer> failedToSave = new ArrayList<>();
        int savedPlayers = 0;
        synchronized (manager) {
            Core.getPermissionsManager().reloadPermissions();
            for (CPlayer cPlayer : manager.getOnlinePlayers()) {
                try {
                    cPlayer.saveIntoDatabase();
                    savedPlayers++;
                } catch (DatabaseConnectException e) {
                    failedToSave.add(cPlayer);
                    e.printStackTrace();
                }
            }
        }
        for (CPlayer cPlayer : failedToSave) {
            Core.logInfo("Failed to save " + cPlayer.toString());
        }
        if (savedPlayers > 0) Core.logInfo("Saved " + savedPlayers + " players!");
    }
}

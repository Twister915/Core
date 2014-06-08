package net.cogzmc.permissions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.cogzmc.core.model.Model;
import net.cogzmc.core.player.COfflinePlayer;

import java.util.Date;

@Getter @Setter @EqualsAndHashCode(callSuper = false)
public final class PermissionChange extends Model {
    private COfflinePlayer executor;
    private Date dateExecuted;
    private String changeMade;
}

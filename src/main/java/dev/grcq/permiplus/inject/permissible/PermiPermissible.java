package dev.grcq.permiplus.inject.permissible;

import dev.grcq.permiplus.PermiPlus;
import dev.grcq.permiplus.permission.PermissionHandler;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.PermissionAttachment;

public class PermiPermissible extends PermissibleBase {

    private final Player player;
    private final PermissionAttachment attachment;

    public PermiPermissible(Player player) {
        super(player);

        this.player = player;
        this.attachment = player.addAttachment(PermiPlus.getInstance());
    }

    @Override
    public boolean hasPermission(String inName) {
        PermissionHandler handler = new PermissionHandler();
        if (handler.hasPermission(inName)) return true;

        return super.hasPermission(inName);
    }

    @Override
    public synchronized void recalculatePermissions() {
        attachment.getPermissions().clear();

        super.recalculatePermissions();
    }
}

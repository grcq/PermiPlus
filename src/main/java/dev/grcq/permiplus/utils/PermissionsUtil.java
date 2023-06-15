package dev.grcq.permiplus.utils;

import dev.grcq.permiplus.PermiPlus;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class PermissionsUtil {

    private static Map<Player, PermissionAttachment> permissions = new HashMap<>();

    public static void addPermission(Player player, Permission permission) {
        if (!permissions.containsKey(player)) {
            permissions.put(player, player.addAttachment(PermiPlus.getInstance()));
        }

        PermissionAttachment attachment = permissions.get(player);
        attachment.setPermission(permission, true);
    }

    public static void addPermission(Player player, String permission) {
        addPermission(player, new Permission(permission));
    }

    public static void removePermission(Player player, Permission permission) {

    }

    public static void removePermission(Player player, String permission) {
        removePermission(player, new Permission(permission));
    }

    public static void clear() {
        for (Map.Entry<Player, PermissionAttachment> ppae : permissions.entrySet()) {
            ppae.getValue().remove();
        }

        permissions.clear();
    }

}

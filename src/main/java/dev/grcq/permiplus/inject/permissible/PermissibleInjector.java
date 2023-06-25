package dev.grcq.permiplus.inject.permissible;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class PermissibleInjector {

    public static void inject(Player player, Permissible permissible) {
        try {
            Field field = player.getClass().getField("perm");
            field.setAccessible(true);
            field.set(player, permissible);

            field.setAccessible(false);

            permissible.recalculatePermissions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

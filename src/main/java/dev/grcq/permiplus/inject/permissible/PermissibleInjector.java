package dev.grcq.permiplus.inject.permissible;

import cf.grcq.priveapi.utils.NMSUtils;
import cf.grcq.priveapi.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class PermissibleInjector {

    public static void inject(Player player, Permissible permissible) {
        try {
            Class<?> entityClass = Class.forName("org.craftbukkit." + VersionUtils.getNMSVersion() + ".entity.CraftEntity");
            Method getBaseMethod = entityClass.getDeclaredMethod("getPermissibleBase");
            getBaseMethod.setAccessible(true);

            PermissibleBase permBase = (PermissibleBase) getBaseMethod.invoke(null);

            Field permField = entityClass.getDeclaredField("perm");
            permField.setAccessible(true);

            permField.set(null, permissible);

            permissible.recalculatePermissions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

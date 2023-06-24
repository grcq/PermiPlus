package dev.grcq.permiplus.utils;

import com.google.common.collect.Lists;
import dev.grcq.permiplus.PermiPlus;
import dev.grcq.permiplus.group.Group;
import dev.grcq.permiplus.profile.Profile;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

@UtilityClass
public class PermissionsUtil {

    private static final Map<UUID, PermissionAttachment> attachments = new HashMap<>();

    public static void updatePermissions(Player player){
        try {
            try {
                if (getAttachment(player) != null) player.removeAttachment(getAttachment(player));
            } catch (IllegalArgumentException ignored) {}
            PermissionAttachment attachment = player.addAttachment(PermiPlus.getInstance());

            List<String> permissions = Lists.newArrayList(PermiPlus.getInstance().getProfileHandler().getProfile(player.getUniqueId()).getPermissions());
            for (String permission : permissions) {
                attachment.setPermission((permission.startsWith("-") ? permission.substring(1) : permission), !permission.startsWith("-"));
            }
            attachments.put(player.getUniqueId(), attachment);
            player.recalculatePermissions();
        }catch(ConcurrentModificationException ignored){}
    }

    public static PermissionAttachment getAttachment(Player player){
        return attachments.getOrDefault(player.getUniqueId(), null);
    }

    public static void remove(Player player){
        attachments.remove(player.getUniqueId());
    }

}

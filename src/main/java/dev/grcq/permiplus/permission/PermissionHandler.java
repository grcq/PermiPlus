package dev.grcq.permiplus.permission;

import com.google.common.collect.Lists;
import dev.grcq.permiplus.PermiPlus;
import dev.grcq.permiplus.group.Group;
import dev.grcq.permiplus.inject.permissible.PermiPermissible;
import dev.grcq.permiplus.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

public class PermissionHandler {

    private final List<PermissionNode> nodes;

    public PermissionHandler() {
        this.nodes = new ArrayList<>();
    }

    public void addNode(String permission) {
        this.nodes.add(new PermissionNode(permission));
    }

    public void removeNode(String permission) {
        nodes.removeIf(node -> node.getNode().equalsIgnoreCase(permission));
    }

    public boolean hasPermission(String permission) {
        for (PermissionNode node : nodes) {
            if (node.match(permission)) {
                return true;
            }
        }

        return false;
    }

    /*
    * Utilities for Bukkit permissions.
    */
    private static final Map<UUID, PermissionAttachment> attachments = new HashMap<>();

    public static void updatePermissions(Player player){
        try {
            try {
                if (getAttachment(player) != null) player.removeAttachment(getAttachment(player));
            } catch (IllegalArgumentException ignored) {}
            PermissionAttachment attachment = player.addAttachment(PermiPlus.getInstance());

            Profile profile = PermiPlus.getInstance().getProfileHandler().getProfile(player.getUniqueId());
            assert profile != null;
            List<String> permissions = Lists.newArrayList(profile.getPermissions());

            for (String parent : profile.getParents()) {
                Group group = PermiPlus.getInstance().getGroupHandler().getGroup(parent);
                assert group != null;
                for (String perm : group.getFullPermissions()) {
                    if (!permissions.contains(perm)) permissions.add(perm);
                }
            }

            for (String permission : permissions) {
                String s = permission.startsWith("-") ? permission.substring(1) : permission;
                PermiPlus.getInstance().getPermissionHandler().removeNode(s);
                PermiPlus.getInstance().getPermissionHandler().addNode(s);

                attachment.setPermission(s, !permission.startsWith("-"));
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

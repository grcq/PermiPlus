package dev.grcq.permiplus.inject.permissible;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import dev.grcq.permiplus.PermiPlus;
import dev.grcq.permiplus.permission.PermissionHandler;
import dev.grcq.permiplus.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PermiPermissible extends PermissibleBase {

    private static final Field ATTACHMENTS;

    static {
        try {
            ATTACHMENTS = PermissibleBase.class.getDeclaredField("attachments");
            ATTACHMENTS.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final Profile profile;
    private final Player player;
    private PermissibleBase oldBase;

    private final AtomicBoolean active = new AtomicBoolean(false);

    public PermiPermissible(Player player, Profile profile) {
        super(player);

        this.player = player;
        this.profile = profile;

        try {
            ATTACHMENTS.set(this, new ArrayList<>());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasPermission(String inName) {
        PermissionHandler handler = new PermissionHandler();
        if (handler.hasPermission(inName)) return true;

        return super.hasPermission(inName);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return this.hasPermission(perm.getName());
    }

    @Override
    public void setOp(boolean value) {
        this.player.setOp(value);
    }

    @Override
    public synchronized Set<PermissionAttachmentInfo> getEffectivePermissions() {
        Map<String, Boolean> permissionsMap = new HashMap<>();
        for (String permission : this.profile.getPermissions()) {
            boolean enabled = !permission.startsWith("-");
            permissionsMap.put((enabled ? permission : permission.substring(1)), enabled);
        }

        ImmutableSet.Builder<PermissionAttachmentInfo> builder = ImmutableSet.builder();
        permissionsMap.forEach((key, value) -> builder.add(new PermissionAttachmentInfo(this.player, key, null, value)));
        return builder.build();
    }

    @Override
    public synchronized PermissionAttachment addAttachment(Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin");

        PermissionAttachment attachment = new PermissionAttachment(plugin, this);
        return attachment;
    }

    @Override
    public synchronized PermissionAttachment addAttachment(Plugin plugin, @NonNull String permission, boolean value) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(permission, "permission");

        PermissionAttachment attachment = addAttachment(plugin);
        attachment.setPermission(permission, value);
        return attachment;
    }

    @Override
    public PermissionAttachment addAttachment(@NonNull Plugin plugin, int ticks) {
        Objects.requireNonNull(plugin, "plugin");

        if (!plugin.isEnabled()) {
            throw new IllegalArgumentException("Plugin " + plugin.getDescription().getFullName() + " is not enabled");
        }

        PermissionAttachment attachment = addAttachment(plugin);
        if (PermiPlus.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(plugin, attachment::remove, ticks) == -1) {
            attachment.remove();
            throw new RuntimeException("Could not add PermissionAttachment to " + this.player + " for plugin " + plugin.getDescription().getFullName() + ": Scheduler returned -1");
        }
        return attachment;
    }

    @Override
    public PermissionAttachment addAttachment(@NonNull Plugin plugin, @NonNull String permission, boolean value, int ticks) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(permission, "permission");

        PermissionAttachment attachment = addAttachment(plugin, ticks);
        attachment.setPermission(permission, value);
        return attachment;
    }

    @Override
    public synchronized void recalculatePermissions() {
        super.recalculatePermissions();
    }
}

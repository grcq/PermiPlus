package dev.grcq.permiplus.listeners;

import dev.grcq.permiplus.PermiPlus;
import dev.grcq.permiplus.inject.permissible.PermiPermissible;
import dev.grcq.permiplus.inject.permissible.PermissibleInjector;
import dev.grcq.permiplus.listeners.api.EventListener;
import dev.grcq.permiplus.profile.Profile;
import dev.grcq.permiplus.profile.ProfileHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener {

    @EventListener(event = PlayerLoginEvent.class, priority = EventPriority.LOWEST)
    public static void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = PermiPlus.getInstance().getProfileHandler();

        Profile profile = profileHandler.createProfile(player.getUniqueId(), player.getName());

        PermiPermissible permissible = new PermiPermissible(player, profile);
        PermissibleInjector.inject(player, permissible);
    }

    @EventListener(event = PlayerJoinEvent.class, priority = EventPriority.LOW)
    public static void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = PermiPlus.getInstance().getProfileHandler();

        Profile profile = profileHandler.createProfile(player.getUniqueId(), player.getName());
        profile.setUsername(player.getName());

        profile.save();
    }

    @EventListener(event = PlayerQuitEvent.class)
    public static void onQuit(PlayerQuitEvent event) {

    }

}

package dev.grcq.permiplus.listeners;

import dev.grcq.permiplus.PermiPlus;
import dev.grcq.permiplus.inject.permissible.PermiPermissible;
import dev.grcq.permiplus.inject.permissible.PermissibleInjector;
import dev.grcq.permiplus.listeners.api.EventListener;
import dev.grcq.permiplus.profile.Profile;
import dev.grcq.permiplus.profile.ProfileHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class ConnectionListener {

    @EventListener(event = PlayerJoinEvent.class)
    public static void onJoin(PlayerJoinEvent event) {
        ProfileHandler profileHandler = PermiPlus.getInstance().getProfileHandler();
        Player player = event.getPlayer();

        PermiPermissible permissible = new PermiPermissible(player);
        PermissibleInjector.inject(player, permissible);

        Profile profile = profileHandler.createProfile(player.getUniqueId(), player.getName());
        profile.setUsername(player.getName());

        profile.save();
    }

}

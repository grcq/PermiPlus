package dev.grcq.permiplus.listeners;

import dev.grcq.permiplus.PermiPlus;
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

        Profile profile = profileHandler.createProfile(player.getUniqueId(), player.getName());
        profile.setUsername(player.getName());

        profile.save();
    }

}

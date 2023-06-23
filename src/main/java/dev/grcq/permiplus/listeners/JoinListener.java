package dev.grcq.permiplus.listeners;

import dev.grcq.permiplus.PermiPlus;
import dev.grcq.permiplus.listeners.api.EventListener;
import org.bukkit.event.player.PlayerJoinEvent;

@EventListener(event = PlayerJoinEvent.class)
public class JoinListener {

    public static void onJoin(PlayerJoinEvent event) {
        PermiPlus.getInstance().getLogger().severe(event.getJoinMessage() + " - WORKS");
    }

}

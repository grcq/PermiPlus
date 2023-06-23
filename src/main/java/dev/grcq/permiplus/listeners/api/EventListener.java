package dev.grcq.permiplus.listeners.api;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {

    @NotNull Class<? extends Event> event();
    @NotNull EventPriority priority() default EventPriority.NORMAL;
    boolean ignoreCancelled() default false;

}

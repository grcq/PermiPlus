package dev.grcq.permiplus.listeners.api;

import dev.grcq.permiplus.PermiPlus;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class EventHandler {

    public void register(Class<?> clazz) {
        if (clazz.isAnnotationPresent(EventListener.class)) {
            if (clazz.getDeclaredMethods().length == 0) return;

            this.registerMethod(clazz.getDeclaredMethods()[0], clazz.getDeclaredAnnotation(EventListener.class));
            return;
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(EventListener.class)) continue;
            this.registerMethod(method);
        }
    }

    private void registerMethod(Method method, EventListener listener) {
        if (method.getParameterCount() != 1) return;
        if (method.getParameterTypes()[0] != listener.event()) return;

        PermiPlus.getInstance().getServer().getPluginManager().registerEvent(listener.event(), new Listener() {}, listener.priority(), new EventExecutor() {
            @Override
            public void execute(Listener listener, Event event) throws EventException {
                try {
                    method.invoke(null, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }, PermiPlus.getInstance());
    }

    public void registerMethod(Method method) {
        this.registerMethod(method, method.getDeclaredAnnotation(EventListener.class));
    }

}

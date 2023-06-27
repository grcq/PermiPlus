package dev.grcq.permiplus.rabbitmq;

import cf.grcq.priveapi.rabbitmq.Listener;
import cf.grcq.priveapi.rabbitmq.PacketListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.grcq.permiplus.PermiPlus;
import dev.grcq.permiplus.group.Group;

public class ServerListener implements Listener {

    @PacketListener(action = "edit_group")
    public void editGroup(JsonObject object) {
        String name = object.get("name").getAsString();
        JsonObject changes = object.get("changes").getAsJsonObject();

        Group group = PermiPlus.getInstance().getGroupHandler().createGroup(name);

        JsonArray permissionChanges = changes.get("permissions").getAsJsonArray();
        permissionChanges.forEach(perm -> {
            if (perm.getAsString().startsWith("-")) group.getPermissions().remove(perm.getAsString().substring(1));
            else group.getPermissions().add(perm.getAsString());
        });

        // TODO: Add all changes

        group.save();
    }

}

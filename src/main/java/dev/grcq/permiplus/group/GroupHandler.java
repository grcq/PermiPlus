package dev.grcq.permiplus.group;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.grcq.permiplus.PermiPlus;
import dev.grcq.permiplus.database.MySQL;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GroupHandler {

    @Getter
    private final List<Group> groups = new ArrayList<>();

    private final MySQL mySQL;

    public GroupHandler() {
        this.mySQL = PermiPlus.getInstance().getMySQL();
        createGroup(PermiPlus.getInstance().getConfig().getString("groups.default-group-name"));
        if (!this.loadGroups()) throw new RuntimeException("An error occurred attempting to load the groups.");
    }

    private boolean loadGroups() {
        this.groups.clear();
        ResultSet rs = mySQL.execute("SELECT * FROM groups;", new HashMap<>());
        JsonArray array = mySQL.toJson(rs);

        if (array.size() == 0) return false;

        List<JsonObject> objects = new ArrayList<>();
        for (JsonElement jsonElement : array) {
            JsonObject object = (JsonObject) jsonElement;

            rs = mySQL.execute("SELECT * FROM profile_parents WHERE name='" + object.get("name").getAsInt() + "';", new HashMap<>());
            JsonArray parents = mySQL.toJson(rs);

            rs = mySQL.execute("SELECT * FROM profile_permissions WHERE name='" + object.get("name").getAsInt() + "';", new HashMap<>());
            JsonArray perms = mySQL.toJson(rs);

            object.add("permissions", perms);
            object.add("parents", parents);

            objects.add(object);
        }

        CompletableFuture.runAsync(() -> {
            for (JsonObject object : objects) {
                Group group = PermiPlus.GSON.fromJson(object, Group.class);

                this.groups.add(group);
            }
        });

        return true;
    }

    @SneakyThrows
    @Nullable
    public Group getGroup(String name) {
        return CompletableFuture.supplyAsync(() -> {
            ResultSet rs = mySQL.execute("SELECT * FROM groups WHERE name='" + name + "';", new HashMap<>());
            JsonArray array = mySQL.toJson(rs);
            if (array == null || array.size() == 0) return null;

            JsonObject object = (JsonObject) array.get(0);
            if (object == null) return null;

            rs = mySQL.execute("SELECT * FROM group_parents WHERE name='" + name + "';", new HashMap<>());
            JsonArray parents = mySQL.toJson(rs);

            rs = mySQL.execute("SELECT * FROM group_permissions WHERE name='" + name + "';", new HashMap<>());
            JsonArray perms = mySQL.toJson(rs);

            object.add("permissions", perms);
            object.add("parents", parents);

            return PermiPlus.GSON.fromJson(object, Group.class);
        }).get();
    }

    public Group createGroup(String name) {
        if (exists(name)) return getGroup(name);
        Group group = new Group(name, name.toLowerCase(), "", "");
        mySQL.update("INSERT INTO groups (name, displayName) VALUES ('%s', '%s');".formatted(name, name.toLowerCase()));
        return group;
    }

    public boolean exists(String name) {
        return getGroup(name) != null;
    }

    public void update() {
        this.loadGroups();
    }

}

package dev.grcq.permiplus.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.grcq.permiplus.PermiPlus;
import dev.grcq.permiplus.database.MySQL;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ProfileHandler {

    @Getter
    private final List<Profile> profiles = new ArrayList<>();

    private final MySQL mySQL;

    public ProfileHandler() {
        this.mySQL = PermiPlus.getInstance().getMySQL();
        if (!this.loadProfiles()) throw new RuntimeException("An error occurred attempting to load the profiles.");
    }

    private boolean loadProfiles() {
        this.profiles.clear();
        ResultSet rs = mySQL.execute("SELECT * FROM profiles;", new HashMap<>());
        JsonArray array = mySQL.toJson(rs);

        List<JsonObject> objects = new ArrayList<>();
        for (JsonElement jsonElement : array) {
            JsonObject object = (JsonObject) jsonElement;

            rs = mySQL.execute("SELECT * FROM profile_parents WHERE uuid='" + object.get("id").getAsString() + "';", new HashMap<>());
            JsonArray parents = mySQL.toJson(rs);

            rs = mySQL.execute("SELECT * FROM profile_permissions WHERE uuid='" + object.get("id").getAsString() + "';", new HashMap<>());
            JsonArray perms = mySQL.toJson(rs);

            object.add("permissions", perms);
            object.add("parents", parents);

            objects.add(object);
        }

        CompletableFuture.runAsync(() -> {
            for (JsonObject object : objects) {
                Profile profile = PermiPlus.GSON.fromJson(object, Profile.class);

                this.profiles.add(profile);
            }
        });

        return true;
    }

    @SneakyThrows
    @Nullable
    public Profile getProfile(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            ResultSet rs = mySQL.execute("SELECT * FROM profiles WHERE uuid='" + uuid + "';", new HashMap<>());
            JsonArray array = mySQL.toJson(rs);
            if (array == null || array.size() == 0) return null;

            JsonObject object = (JsonObject) array.get(0);
            if (object == null) return null;

            rs = mySQL.execute("SELECT * FROM profile_parents WHERE uuid='" + uuid + "';", new HashMap<>());
            JsonArray parents = mySQL.toJson(rs);

            rs = mySQL.execute("SELECT * FROM profile_permissions WHERE uuid='" + uuid + "';", new HashMap<>());
            JsonArray perms = mySQL.toJson(rs);

            object.add("permissions", perms);
            object.add("parents", parents);

            return PermiPlus.GSON.fromJson(object, Profile.class);
        }).get();
    }

    @SneakyThrows
    @Nullable
    public Profile getProfile(String name) {
        return CompletableFuture.supplyAsync(() -> {
            ResultSet rs = mySQL.execute("SELECT * FROM profiles WHERE username='" + name + "';", new HashMap<>());
            JsonArray array = mySQL.toJson(rs);
            if (array == null || array.size() == 0) return null;

            JsonObject object = (JsonObject) array.get(0);
            if (object == null) return null;

            rs = mySQL.execute("SELECT * FROM profile_parents WHERE uuid='" + object.get("uuid").getAsString() + "';", new HashMap<>());
            JsonArray parents = mySQL.toJson(rs);

            rs = mySQL.execute("SELECT * FROM profile_permissions WHERE uuid='" + object.get("uuid").getAsString() + "';", new HashMap<>());
            JsonArray perms = mySQL.toJson(rs);

            object.add("permissions", perms);
            object.add("parents", parents);

            return PermiPlus.GSON.fromJson(object, Profile.class);
        }).get();
    }

    public Profile createProfile(UUID uuid, String name) {
        if (exists(uuid)) return getProfile(uuid);
        Profile group = new Profile(uuid.toString(), name);
        mySQL.update("INSERT INTO profiles (uuid, username) VALUES ('%s', '%s');".formatted(uuid.toString(), name));
        return group;
    }

    public boolean exists(UUID uuid) {
        return getProfile(uuid) != null;
    }

    public boolean exists(String name) {
        return getProfile(name) != null;
    }

    public void update() {
        this.loadProfiles();
    }

}

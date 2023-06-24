package dev.grcq.permiplus;

import cf.grcq.priveapi.chat.listener.InputListener;
import cf.grcq.priveapi.command.CommandHandler;
import cf.grcq.priveapi.gui.listener.GUIListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import dev.grcq.permiplus.commands.parameters.ChatColorParameterType;
import dev.grcq.permiplus.commands.parameters.GroupParameterType;
import dev.grcq.permiplus.database.MySQL;
import dev.grcq.permiplus.group.Group;
import dev.grcq.permiplus.group.GroupHandler;
import dev.grcq.permiplus.listeners.ConnectionListener;
import dev.grcq.permiplus.listeners.api.EventHandler;
import dev.grcq.permiplus.profile.ProfileHandler;
import dev.grcq.permiplus.utils.PermissionsUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class PermiPlus extends JavaPlugin {

    public static Gson GSON = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();

    @Getter
    private static PermiPlus instance;

    @Getter
    private MySQL mySQL;
    @Getter
    private GroupHandler groupHandler;
    @Getter
    private ProfileHandler profileHandler;

    @Override
    public void onLoad() {
        this.saveDefaultConfig();
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        instance = this;

        if (this.getConfig().isSet("groups.default-group-name")) this.getConfig().set("groups.default-group-name", "Default");

        this.mySQL = new MySQL();
        mySQL.update("CREATE TABLE IF NOT EXISTS groups (displayName VARCHAR(64), " +
                "name VARCHAR(32) PRIMARY KEY, prefix VARCHAR(32) DEFAULT '', suffix VARCHAR(32) DEFAULT ''," +
                " colour VARCHAR(10) DEFAULT NULL, priority BIGINT DEFAULT 0);");
        mySQL.update("CREATE TABLE IF NOT EXISTS group_parents (group VARCHAR(32), parent VARCHAR(32));");
        mySQL.update("CREATE TABLE IF NOT EXISTS group_permissions (group VARCHAR(32), permission VARCHAR(128));");
        mySQL.update("CREATE TABLE IF NOT EXISTS profiles (uuid VARCHAR(100) PRIMARY KEY, username VARCHAR(32)," +
                " prefix VARCHAR(32) DEFAULT '', suffix VARCHAR(32) DEFAULT '');");
        mySQL.update("CREATE TABLE IF NOT EXISTS profile_groups (uuid VARCHAR(64), parent VARCHAR(32));");
        mySQL.update("CREATE TABLE IF NOT EXISTS profile_permissions (uuid VARCHAR(64), permission VARCHAR(128));");

        this.groupHandler = new GroupHandler();
        this.profileHandler = new ProfileHandler();

        CommandHandler.registerParameter(ChatColor.class, new ChatColorParameterType());
        CommandHandler.registerParameter(Group.class, new GroupParameterType());
        CommandHandler.registerAll(this);

        this.getServer().getPluginManager().registerEvents(new GUIListener(), this);
        this.getServer().getPluginManager().registerEvents(new InputListener(), this);
        EventHandler eventListener = new EventHandler();
        eventListener.registerAll(this);

        this.getDataFolder().mkdirs();
        File file = new File(this.getDataFolder(), "data.yml");
        if (!file.exists()) {
            if (!file.createNewFile()) throw new RuntimeException("An error occurred creating 'data.yml' file. Does it already exist?");
        }

        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player player : getServer().getOnlinePlayers()) {
                PermissionsUtil.updatePermissions(player);
            }
        }, 40L, 40L);
    }

    @Override
    public void onDisable() {
        this.saveDefaultConfig();
    }
}

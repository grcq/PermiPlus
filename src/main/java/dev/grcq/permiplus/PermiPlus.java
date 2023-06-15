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
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
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
        mySQL.update("CREATE TABLE IF NOT EXISTS groups (id BIGINT AUTO_INCREMENT PRIMARY KEY DEFAULT NULL, displayName VARCHAR(64), " +
                "name VARCHAR(32), prefix VARCHAR(32) DEFAULT '', suffix VARCHAR(32) DEFAULT '', colour VARCHAR(10) DEFAULT NULL, priority BIGINT DEFAULT 0);");
        mySQL.update("CREATE TABLE IF NOT EXISTS group_parents (groupId BIGINT, parentId BIGINT);");
        mySQL.update("CREATE TABLE IF NOT EXISTS group_permissions (groupId BIGINT, permission VARCHAR(128));");
        mySQL.update("CREATE TABLE IF NOT EXISTS profiles (id BIGINT AUTO_INCREMENT PRIMARY KEY DEFAULT NULL, uuid VARCHAR(100), username VARCHAR(32)," +
                " prefix VARCHAR(32) DEFAULT '', suffix VARCHAR(32) DEFAULT '');");
        mySQL.update("CREATE TABLE IF NOT EXISTS profile_groups (userId BIGINT, groupId BIGINT);");
        mySQL.update("CREATE TABLE IF NOT EXISTS profile_permissions (userId BIGINT, permission VARCHAR(128));");

        this.groupHandler = new GroupHandler();

        CommandHandler.init();
        CommandHandler.registerParameter(ChatColor.class, new ChatColorParameterType());
        CommandHandler.registerParameter(Group.class, new GroupParameterType());
        CommandHandler.registerAll(this);

        this.getServer().getPluginManager().registerEvents(new GUIListener(), this);
        this.getServer().getPluginManager().registerEvents(new InputListener(), this);

        this.getDataFolder().mkdirs();
        File file = new File(this.getDataFolder(), "data.yml");
        if (!file.exists()) {
            if (!file.createNewFile()) throw new RuntimeException("An error occurred creating 'data.yml' file. Does it already exist?");
        }
    }

    @Override
    public void onDisable() {
        this.saveDefaultConfig();
    }
}

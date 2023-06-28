package dev.grcq.permiplus.commands;

import cf.grcq.priveapi.command.Command;
import cf.grcq.priveapi.command.parameter.Param;
import cf.grcq.priveapi.utils.Paginate;
import cf.grcq.priveapi.utils.Util;
import dev.grcq.permiplus.PermiPlus;
import dev.grcq.permiplus.group.Group;
import dev.grcq.permiplus.group.GroupHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MainCommands {

    @Command(names = {"permi", "permiplus", "pp", "pplus", "permip"}, permission = "permiplus.command.help")
    public static void permi(CommandSender sender) {
        sender.sendMessage(Util.format("&8[&9&lPermi&b&lPlus&8] &9Help &7=========="));
        sender.sendMessage(Util.format("&7- &b/permi &fhelp"));
        sender.sendMessage(Util.format("&7- &b/permi &fgroup"));
    }

    @Command(names = {"permi help"}, permission = "permiplus.command.help")
    public static void help(CommandSender sender) {
        permi(sender);
    }

    @Command(names = {"permi group"}, permission = "permiplus.command.group")
    public static void group(CommandSender sender) {
        sender.sendMessage(Util.format("&8[&9&lPermi&b&lPlus&8] &9Group &7=========="));
        sender.sendMessage(Util.format("&7- &b/permi &fgroup create <name>"));
        sender.sendMessage(Util.format("&7- &b/permi &fgroup delete <name>"));
        sender.sendMessage(Util.format("&7- &b/permi &fgroup edit <name>"));
        sender.sendMessage(Util.format("&7- &b/permi &fgroup list"));
    }

    @Command(names = {"permi group create"}, permission = "permiplus.command.group.create")
    public static void groupCreate(CommandSender sender, @Param(name = "name") String name) {
        GroupHandler groupHandler = PermiPlus.getInstance().getGroupHandler();

        if (groupHandler.exists(name)) {
            sender.sendMessage(Util.format("&cError: The group '%s' already exists.".formatted(name.toLowerCase())));
            return;
        }

        Group group = groupHandler.createGroup(name);
        group.setColour("&f");

        sender.sendMessage(Util.format("&aSuccess! The group '&f%s&a' was successfully created.".formatted(name.toLowerCase())));
        groupHandler.update();
    }

    @Command(names = {"permi group delete"}, permission = "permiplus.command.delete")
    public static void groupDelete(CommandSender sender, @Param(name = "name") Group group) {
        GroupHandler groupHandler = PermiPlus.getInstance().getGroupHandler();
        if (group.getName().equalsIgnoreCase(PermiPlus.getInstance().getConfig().getString("groups.default-group-name"))) {
            sender.sendMessage(Util.format("&cError: You cannot delete the default group!"));
            return;
        }

        PermiPlus.getInstance().getMySQL().update("DELETE FROM groups WHERE name='%s'".formatted(group.getName()));
        PermiPlus.getInstance().getMySQL().update("DELETE FROM group_permissions WHERE groupName='%s'".formatted(group.getName()));
        PermiPlus.getInstance().getMySQL().update("DELETE FROM group_parents WHERE groupName='%s'".formatted(group.getName()));
        sender.sendMessage(Util.format("&aSuccess! The group '&f%s&a' was successfully deleted.".formatted((group.getColour() == null ? "" : group.getColour()) + group.getName())));

        groupHandler.update();
    }

    @Command(names = {"permi group list"}, permission = "permiplus.command.list")
    public static void list(CommandSender sender, @Param(name = "page", defaultValue = "1") int page) {
        new Paginate<Group>() {

            @Override
            public String getTopHeader(int i, int i1) {
                return Util.format("&9List of all groups &f[" + i + "/" + i1 + "] &7==========");
            }

            @Override
            public String format(Group group, int i) {
                return Util.format("&7- " + (group.getColour() != null ? group.getColour() : "&f") + group.getDisplayName() + " &f(" + group.getName() + ")");
            }

        }.display(sender, page, PermiPlus.getInstance().getGroupHandler().getGroups());

    }

}

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

    @Command(names = {"permi", "permiplus", "pp", "pplus", "permip"}, permission = "permiplus.admin")
    public static void permi(CommandSender sender) {
        sender.sendMessage(Util.format("&8[&9&lPermi&b&lPlus&8] &9Help &7=========="));
        sender.sendMessage(Util.format("&7- &b/permi &fhelp"));
        sender.sendMessage(Util.format("&7- &b/permi &fgroup"));
    }

    @Command(names = {"permi help"})
    public static void help(CommandSender sender) {
        permi(sender);
    }

    @Command(names = {"permi group"})
    public static void group(CommandSender sender) {
        sender.sendMessage(Util.format("&8[&9&lPermi&b&lPlus&8] &9Group &7=========="));
        sender.sendMessage(Util.format("&7- &b/permi &fgroup create <name>"));
        sender.sendMessage(Util.format("&7- &b/permi &fgroup delete <name>"));
        sender.sendMessage(Util.format("&7- &b/permi &fgroup edit <name>"));
        sender.sendMessage(Util.format("&7- &b/permi &fgroup list"));
    }

    @Command(names = {"permi group create"})
    public static void groupCreate(CommandSender sender, @Param(name = "name") String name, @Param(name = "colour", defaultValue = "WHITE") ChatColor colour) {
        GroupHandler groupHandler = PermiPlus.getInstance().getGroupHandler();

        if (groupHandler.exists(name)) {
            sender.sendMessage(Util.format("&cError: The group '%s' already exists.".formatted(name.toLowerCase())));
            return;
        }

        Group group = groupHandler.createGroup(name);
        group.setColour("&" + colour.getChar());

        sender.sendMessage(Util.format("&aSuccess! The group '%s' was successfully created.".formatted(colour + name.toLowerCase())));
    }

    @Command(names = {"permi group list"})
    public static void list(CommandSender sender, @Param(name = "page", defaultValue = "1") int page) {
        new Paginate<Group>() {

            @Override
            public String getTopHeader(int i, int i1) {
                return Util.format("&9List of all groups &f[" + i + "/" + i1 + "] &7==========");
            }

            @Override
            public String format(Group group, int i) {
                return Util.format("&7- " + group.getColour() + group.getDisplayName() + " &f(" + group.getName() + ")");
            }

        }.display(sender, page, PermiPlus.getInstance().getGroupHandler().getGroups());

    }

}

package dev.grcq.permiplus.commands.parameters;

import cf.grcq.priveapi.command.parameter.ParameterType;
import cf.grcq.priveapi.utils.Util;
import dev.grcq.permiplus.PermiPlus;
import dev.grcq.permiplus.group.Group;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class GroupParameterType implements ParameterType<Group> {

    @Override
    public Group transform(CommandSender sender, String s) {
        Group group = PermiPlus.getInstance().getGroupHandler().getGroup(s);
        if (group != null) return group;

        sender.sendMessage(Util.format("&cError: The group '%s' does not exist.".formatted(s)));
        return null;
    }

    @Override
    public List<String> tabComplete(Player player, String source) {
        return PermiPlus.getInstance().getGroupHandler().getGroups().stream().map(Group::getName).filter(g -> g.startsWith(source)).collect(Collectors.toList());
    }

}

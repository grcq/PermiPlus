package dev.grcq.permiplus.commands.parameters;

import cf.grcq.priveapi.command.parameter.ParameterType;
import cf.grcq.priveapi.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChatColorParameterType implements ParameterType<ChatColor> {

    @Override
    public ChatColor transform(CommandSender commandSender, String s) {
        if (commandSender instanceof Player && !tabComplete((Player) commandSender, "").contains(s.toUpperCase())) {
            commandSender.sendMessage(Util.format("&cThis colour does not exist."));
            return null;
        }

        return ChatColor.valueOf(s);
    }

    @Override
    public List<String> tabComplete(Player player, String source) {
        return Arrays.asList(ChatColor.values()).stream().map(ChatColor::toString).filter(c -> c.startsWith(source)).collect(Collectors.toList());
    }
}

package net.pl3x.bukkit.kraken.command;

import com.google.common.collect.ImmutableList;
import net.pl3x.bukkit.kraken.FishingManager;
import net.pl3x.bukkit.kraken.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CmdKraken implements TabExecutor {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("command.kraken.others")) {
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    .map(HumanEntity::getName).collect(Collectors.toList());
        }
        return ImmutableList.of();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (!sender.hasPermission("command.kraken.others")) {
                Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                Lang.send(sender, Lang.PLAYER_NOT_ONLINE);
                return true;
            }

            FishingManager.FISHING_MANAGER.get(target).unleashTheKraken();
            return true;
        }

        if (!(sender instanceof Player)) {
            Lang.send(sender, Lang.PLAYER_COMMAND);
            return true;
        }

        if (!sender.hasPermission("command.kraken")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        FishingManager.FISHING_MANAGER.get(sender).unleashTheKraken();
        return true;
    }
}

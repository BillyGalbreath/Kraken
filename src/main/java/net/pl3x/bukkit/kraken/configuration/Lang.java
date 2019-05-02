package net.pl3x.bukkit.kraken.configuration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class Lang {
    public static String COMMAND_NO_PERMISSION;
    public static String PLAYER_COMMAND;
    public static String PLAYER_NOT_ONLINE;

    public static String PLAYER_UNLEASHED_KRAKEN;
    public static String KRAKEN_HAS_BEEN_UNLEASHED;

    public static String DEFEATED;
    public static String ESCAPED;

    public static void reload(Plugin plugin) {
        String langFile = Config.LANGUAGE_FILE;
        File configFile = new File(plugin.getDataFolder(), langFile);
        if (!configFile.exists()) {
            plugin.saveResource(Config.LANGUAGE_FILE, false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        COMMAND_NO_PERMISSION = config.getString("command-no-permission", "&4You do not have permission for that command");
        PLAYER_COMMAND = config.getString("player-command", "&4That command is only available to players");
        PLAYER_NOT_ONLINE = config.getString("player-not-online", "&4That player is not online right now");

        PLAYER_UNLEASHED_KRAKEN = config.getString("broadcast-unleashed", "&b{player} has unleashed the kraken!");
        KRAKEN_HAS_BEEN_UNLEASHED = config.getString("unleashed", "&aTHE KRAKEN HAS BEEN UNLEASHED!");
        DEFEATED = config.getString("defeated", "&aYou have slain the kraken!");
        ESCAPED = config.getString("escaped", "&aYou have escaped from the kraken!");
    }

    public static void broadcast(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> Lang.send(player, message));
    }

    public static void send(CommandSender recipient, String message) {
        if (message == null) {
            return;
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (ChatColor.stripColor(message).isEmpty()) {
            return; // do not send blank messages
        }

        for (String part : message.split("\n")) {
            recipient.sendMessage(part);
        }
    }

    public static boolean isEmpty(String message) {
        return message == null ||
                message.isEmpty() ||
                ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message)).isEmpty();
    }
}

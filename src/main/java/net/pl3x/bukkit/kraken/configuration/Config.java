package net.pl3x.bukkit.kraken.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {
    public static String LANGUAGE_FILE;

    public static int TRIES;
    public static double HEALTH;
    public static String NAME;
    public static int ATTACK_INTERVAL;
    public static double ATTACK_DAMAGE;
    public static boolean GLOBAL_EFFECTS;
    public static boolean ALLOW_ESCAPE;
    public static String LOOT_TABLE;

    public static void reload(Plugin plugin) {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        LANGUAGE_FILE = config.getString("language-file", "lang-en.yml");

        TRIES = config.getInt("tries", 50);
        HEALTH = config.getDouble("health", 50.0);
        NAME = config.getString("name", "The Kraken");
        ATTACK_INTERVAL = config.getInt("attack-interval", 1);
        ATTACK_DAMAGE = config.getDouble("attack-damage", 1.0);
        GLOBAL_EFFECTS = config.getBoolean("global-effects", false);
        ALLOW_ESCAPE = config.getBoolean("allow-escape", false);
        LOOT_TABLE = config.getString("loot-table", "minecraft:squid");
    }
}

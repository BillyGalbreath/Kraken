package net.pl3x.bukkit.kraken.util;

import net.pl3x.bukkit.kraken.Kraken;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.logging.Level;

public class LootTableUtil {
    private static Boolean support;
    private static LootTableCache cache;

    public static void setLootTable(LivingEntity entity, String loottable) {
        if (support == null) {
            try {
                Class.forName("org.bukkit.entity.Mob");
                Class.forName("org.bukkit.loot.LootTable");
                Class.forName("org.bukkit.NamespacedKey");
                org.bukkit.loot.Lootable.class.getDeclaredMethod("setLootTable", org.bukkit.loot.LootTable.class);
                support = true;
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                support = false;
            }
        }

        if (support) {
            if (cache == null) {
                cache = new LootTableCache(loottable);
            }

            if (cache.lootTable != null) {
                ((org.bukkit.entity.Mob) entity).setLootTable(cache.lootTable);
            }
        }
    }

    static class LootTableCache {
        private org.bukkit.loot.LootTable lootTable;

        LootTableCache(String loottable) {
            try {
                org.bukkit.NamespacedKey key;
                if (loottable.contains(":")) {
                    String[] split = loottable.split(":");
                    key = new org.bukkit.NamespacedKey(split[0], split[1]);
                } else {
                    key = new org.bukkit.NamespacedKey("minecraft", loottable);
                }
                org.bukkit.loot.LootTable lootTable = Bukkit.getLootTable(key);
                if (lootTable == null) {
                    Kraken.INSTANCE.getLogger().warning("Could not find loot_table '" + loottable);
                }
                this.lootTable = lootTable;
            } catch (Exception e) {
                Kraken.INSTANCE.getLogger().log(Level.SEVERE, "Could not set loot table to entity", e);
            }
        }
    }
}

package net.pl3x.bukkit.kraken;

import net.pl3x.bukkit.kraken.command.CmdKraken;
import net.pl3x.bukkit.kraken.configuration.Config;
import net.pl3x.bukkit.kraken.configuration.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Kraken extends JavaPlugin {
    public static Kraken INSTANCE;

    public Kraken() {
        INSTANCE = this;
    }

    public void onEnable() {
        Config.reload(this);
        Lang.reload(this);

        getCommand("kraken").setExecutor(new CmdKraken());

        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                FishingManager.FISHING_MANAGER.put(event.getPlayer(), new FishingManager(event.getPlayer()));
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                FishingManager.FISHING_MANAGER.remove(event.getPlayer());
            }

            @EventHandler
            public void onPlayerFish(PlayerFishEvent event) {
                FishingManager fishingManager = FishingManager.FISHING_MANAGER.get(event.getPlayer());
                if (event.getState() == PlayerFishEvent.State.FISHING) {
                    if (!event.getPlayer().hasPermission("bypass.kraken")) {
                        event.setCancelled(fishingManager.exploitPrevention());
                    }
                }
            }
        }, this);
    }
}

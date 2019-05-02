package net.pl3x.bukkit.kraken;

import net.pl3x.bukkit.kraken.configuration.Config;
import net.pl3x.bukkit.kraken.configuration.Lang;
import net.pl3x.bukkit.kraken.task.KrakenAttackTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class FishingManager {
    public final static Map<Player, FishingManager> FISHING_MANAGER = new HashMap<>();
    public final static Set<Material> TRANSPARENT_MATERIALS = Arrays.stream(Material.values())
            .filter(Material::isTransparent)
            .collect(Collectors.toSet());

    private final Player player;

    private int fishingTries = 0;
    private long fishingTimestamp = 0L;
    private Location fishingTarget;

    public FishingManager(Player player) {
        this.player = player;
    }

    public boolean unleashTheKraken() {
        return unleashTheKraken(true);
    }

    private boolean unleashTheKraken(boolean forceSpawn) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        if (!forceSpawn && (fishingTries < Config.TRIES || fishingTries <= random.nextInt(200))) {
            return false;
        }

        World world = player.getWorld();

        Entity vehicle = player.getVehicle();
        if (vehicle != null && vehicle.getType() == EntityType.BOAT) {
            vehicle.eject();
            vehicle.remove();
        }

        player.setPlayerWeather(WeatherType.DOWNFALL);
        player.teleport(player.getTargetBlock(null, 100).getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        Lang.send(player, Lang.KRAKEN_HAS_BEEN_UNLEASHED);

        Location location = player.getLocation();
        if (Config.GLOBAL_EFFECTS) {
            world.strikeLightningEffect(location);
            world.strikeLightningEffect(location);
            world.strikeLightningEffect(location);

            world.playSound(location, Sound.ENTITY_GHAST_SCREAM, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            Lang.broadcast(Lang.PLAYER_UNLEASHED_KRAKEN
                    .replace("{player}", player.getDisplayName()));
        } else {
            world.createExplosion(location.getX(), location.getY(), location.getZ(), 0F, false, false);
            world.createExplosion(location.getX(), location.getY(), location.getZ(), 0F, false, false);
            world.createExplosion(location.getX(), location.getY(), location.getZ(), 0F, false, false);

            player.playSound(location, Sound.ENTITY_GHAST_SCREAM, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        }

        if (player.getInventory().getItemInMainHand().getType() == Material.FISHING_ROD) {
            player.getInventory().setItemInMainHand(null);
        } else if (player.getInventory().getItemInOffHand().getType() == Material.FISHING_ROD) {
            player.getInventory().setItemInOffHand(null);
        }

        LivingEntity kraken = (LivingEntity) world.spawnEntity(player.getEyeLocation(), (random.nextInt(100) == 0 ? EntityType.CHICKEN : EntityType.SQUID));
        kraken.setCustomName(Config.NAME);

        int attackInterval = Config.ATTACK_INTERVAL * 20;

        if (kraken.isValid()) {
            new KrakenAttackTask(kraken, player).runTaskTimer(Kraken.INSTANCE, attackInterval, attackInterval);
            kraken.setMaxHealth(Config.HEALTH);
            kraken.setHealth(kraken.getMaxHealth());
        } else {
            new KrakenAttackTask(kraken, player, player.getLocation()).runTaskTimer(Kraken.INSTANCE, attackInterval, attackInterval);
        }

        if (!forceSpawn) {
            fishingTries = 0;
        }

        return true;
    }

    public boolean exploitPrevention() {
        Block targetBlock = player.getTargetBlock(TRANSPARENT_MATERIALS, 100);

        if (!targetBlock.isLiquid()) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        boolean hasFished = (currentTime < fishingTimestamp + 1000L);

        fishingTries = hasFished ? fishingTries + 1 : Math.max(fishingTries - 1, 0);
        fishingTimestamp = currentTime;

        Location targetLocation = targetBlock.getLocation();
        boolean sameTarget = (fishingTarget != null && fishingTarget.equals(targetLocation));

        fishingTries = sameTarget ? fishingTries + 1 : Math.max(fishingTries - 1, 0);
        fishingTarget = targetLocation;

        return unleashTheKraken(false);
    }
}
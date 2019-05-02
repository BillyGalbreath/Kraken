package net.pl3x.bukkit.kraken.task;

import net.pl3x.bukkit.kraken.configuration.Config;
import net.pl3x.bukkit.kraken.configuration.Lang;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class KrakenAttackTask extends BukkitRunnable {
    private LivingEntity kraken;
    private Player player;
    private Location location;

    public KrakenAttackTask(LivingEntity kraken2, Player player) {
        this.kraken = kraken2;
        this.player = player;
    }

    public KrakenAttackTask(LivingEntity kraken2, Player player, Location location) {
        this.kraken = kraken2;
        this.player = player;
        this.location = location;
    }

    @Override
    public void run() {
        if (location != null) {
            Location playerLocation = player.getLocation();

            if (player.isValid() && playerLocation.getBlock().isLiquid()) {
                World world = player.getWorld();

                krakenAttack(playerLocation, world);
            } else {
                Lang.send(player, Lang.ESCAPED);
                player.resetPlayerWeather();
                cancel();
            }

            return;
        }

        if (!kraken.isValid()) {
            Lang.send(player, Lang.DEFEATED);

            player.resetPlayerWeather();
            cancel();
        }

        if (player.isValid()) {
            Location location = player.getLocation();

            if (!location.getBlock().isLiquid() && Config.ALLOW_ESCAPE) {
                Lang.send(player, Lang.ESCAPED);

                kraken.remove();
                player.resetPlayerWeather();
                cancel();
                return;
            }

            kraken.teleport(player);
            krakenAttack(location, player.getWorld());
        } else {
            kraken.remove();
            cancel();
        }
    }

    private void krakenAttack(Location playerLocation, World world) {
        player.damage(Config.ATTACK_DAMAGE, kraken);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        if (Config.GLOBAL_EFFECTS) {
            world.playSound(location, Sound.ENTITY_GHAST_SCREAM, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            world.strikeLightningEffect(playerLocation);
        } else {
            player.playSound(location, Sound.ENTITY_GHAST_SCREAM, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            world.createExplosion(playerLocation.getX(), playerLocation.getY(), playerLocation.getZ(), 0F, false, false);
        }
    }
}


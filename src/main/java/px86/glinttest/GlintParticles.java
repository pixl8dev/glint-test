package px86.glinttest;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

public class GlintParticles {

    private static final Random random = new Random();

    public static void spawn(Player player, int variation) {
        switch (variation) {
            case 1 -> spawnClassicGlint(player);
            case 2 -> spawnEnchantedSwirl(player);
            case 3 -> spawnSoulFireGlint(player);
            case 4 -> spawnStarBurst(player);
        }
    }

    private static void spawnClassicGlint(Player player) {
        Location loc = player.getLocation().add(0, 0.5, 0);
        Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(85, 255, 255), 0.8f);

        for (int i = 0; i < 6; i++) {
            double x = (random.nextDouble() - 0.5) * 0.8;
            double y = random.nextDouble() * 1.8;
            double z = (random.nextDouble() - 0.5) * 0.8;

            Location particleLoc = loc.clone().add(x, y, z);
            player.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, 0, 0, 0, 0, dust);
        }
    }

    private static void spawnEnchantedSwirl(Player player) {
        double time = System.currentTimeMillis() / 500.0;

        double[][] armorSlots = {
                {0.6, 0.2},   // boots
                {0.55, 0.9},  // leggings
                {0.65, 1.35}, // chestplate
                {0.5, 1.75}   // helmet
        };

        Material[] armorTypes = {
                Material.DIAMOND_BOOTS,
                Material.DIAMOND_LEGGINGS,
                Material.DIAMOND_CHESTPLATE,
                Material.DIAMOND_HELMET
        };

        ItemStack[] equipped = {
                player.getInventory().getBoots(),
                player.getInventory().getLeggings(),
                player.getInventory().getChestplate(),
                player.getInventory().getHelmet()
        };

        Particle.DustTransition fromPurple = new Particle.DustTransition(
                Color.fromRGB(170, 0, 255), Color.fromRGB(60, 0, 100), 0.55f
        );
        Particle.DustTransition toPurple = new Particle.DustTransition(
                Color.fromRGB(200, 100, 255), Color.fromRGB(80, 0, 120), 0.45f
        );

        for (int a = 0; a < 4; a++) {
            if (equipped[a] == null || equipped[a].getType() != armorTypes[a]) continue;

            Location base = player.getLocation().add(0, armorSlots[a][1], 0);
            double maxRadius = armorSlots[a][0];

            for (int i = 0; i < 36; i++) {
                double t = i / 36.0;
                double angle = time + (t * Math.PI * 3);
                double radius = t * maxRadius;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                double y = t * 0.3 - 0.15;

                Location particleLoc = base.clone().add(x, y, z);
                player.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, particleLoc, 1, 0, 0, 0, 0,
                        i % 2 == 0 ? fromPurple : toPurple);
            }
        }
    }

    private static void spawnSoulFireGlint(Player player) {
        Location loc = player.getLocation().add(0, 0.1, 0);

        for (int i = 0; i < 5; i++) {
            double x = (random.nextDouble() - 0.5) * 0.6;
            double y = random.nextDouble() * 2.0;
            double z = (random.nextDouble() - 0.5) * 0.6;

            Location particleLoc = loc.clone().add(x, y, z);
            player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc, 1, 0.02, 0.05, 0.02, 0.01);
        }

        for (int i = 0; i < 3; i++) {
            double x = (random.nextDouble() - 0.5) * 0.4;
            double y = random.nextDouble() * 1.8;
            double z = (random.nextDouble() - 0.5) * 0.4;

            Location particleLoc = loc.clone().add(x, y, z);
            player.getWorld().spawnParticle(Particle.ASH, particleLoc, 1, 0.02, 0.02, 0.02, 0.01);
        }
    }

    private static void spawnStarBurst(Player player) {
        Location loc = player.getLocation().add(0, 0.9, 0);
        double time = System.currentTimeMillis() / 300.0;

        for (int i = 0; i < 12; i++) {
            double angle = (i * Math.PI * 2 / 12) + time;
            double radius = 0.4 + Math.sin(time * 2) * 0.1;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            double y = 0.9 + Math.sin(time + i) * 0.2;

            Location particleLoc = loc.clone().add(x, y, z);
            Particle.DustOptions dust = new Particle.DustOptions(
                    Color.fromRGB(255, 220, 50), 0.9f
            );
            player.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, 0, 0, 0, 0, dust);
        }

        for (int i = 0; i < 3; i++) {
            double x = (random.nextDouble() - 0.5) * 1.0;
            double y = random.nextDouble() * 2.0;
            double z = (random.nextDouble() - 0.5) * 1.0;

            Location particleLoc = loc.clone().add(x, y, z);
            player.getWorld().spawnParticle(Particle.TOTEM, particleLoc, 1, 0.1, 0.1, 0.1, 0.5);
        }
    }
}

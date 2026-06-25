package px86.glinttest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlintTest extends JavaPlugin implements Listener {

    private final Map<UUID, Integer> playerVariation = new HashMap<>();
    private final Map<UUID, Boolean> playerEnabled = new HashMap<>();
    private final Map<UUID, BukkitTask> activeTasks = new HashMap<>();
    private int defaultVariation;
    private int spawnInterval;
    private boolean defaultEnabled;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("glintvariation").setExecutor(new GlintCommand(this));
        getCommand("glinttoggle").setExecutor(new GlintCommand(this));

        getLogger().info("GlintTest has been enabled!");
    }

    @Override
    public void onDisable() {
        activeTasks.values().forEach(BukkitTask::cancel);
        activeTasks.clear();
        getLogger().info("GlintTest has been disabled!");
    }

    private void loadConfig() {
        defaultVariation = getConfig().getInt("default-variation", 1);
        spawnInterval = getConfig().getInt("spawn-interval", 2);
        defaultEnabled = getConfig().getBoolean("enabled", true);
    }

    public int getVariation(Player player) {
        return playerVariation.getOrDefault(player.getUniqueId(), defaultVariation);
    }

    public void setVariation(Player player, int variation) {
        playerVariation.put(player.getUniqueId(), variation);
        stopEffect(player);
        startEffect(player);
    }

    public boolean isEnabled(Player player) {
        return playerEnabled.getOrDefault(player.getUniqueId(), defaultEnabled);
    }

    public void toggleEnabled(Player player) {
        UUID uuid = player.getUniqueId();
        boolean current = playerEnabled.getOrDefault(uuid, defaultEnabled);
        playerEnabled.put(uuid, !current);

        if (!current) {
            startEffect(player);
        } else {
            stopEffect(player);
        }
    }

    public int getSpawnInterval() {
        return spawnInterval;
    }

    public void startEffect(Player player) {
        if (!isEnabled(player)) return;
        if (hasDiamondArmor(player)) {
            startGlintTask(player);
        }
    }

    public void stopEffect(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitTask task = activeTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }

    private void startGlintTask(Player player) {
        UUID uuid = player.getUniqueId();
        stopEffect(player);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (!player.isOnline()) {
                stopEffect(player);
                return;
            }
            if (!isEnabled(player) || !hasDiamondArmor(player)) {
                stopEffect(player);
                return;
            }
            GlintParticles.spawn(player, getVariation(player));
        }, 0L, spawnInterval);

        activeTasks.put(uuid, task);
    }

    private boolean hasDiamondArmor(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();

        return isDiamondArmor(helmet) || isDiamondArmor(chestplate)
                || isDiamondArmor(leggings) || isDiamondArmor(boots);
    }

    private boolean isDiamondArmor(ItemStack item) {
        if (item == null) return false;
        String name = item.getType().name();
        return name.contains("DIAMOND") && (name.contains("HELMET") || name.contains("CHESTPLATE")
                || name.contains("LEGGINGS") || name.contains("BOOTS"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isEnabled(player) && hasDiamondArmor(player)) {
            Bukkit.getScheduler().runTaskLater(this, () -> startEffect(player), 20L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        stopEffect(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (hasDiamondArmor(player)) {
                startEffect(player);
            } else {
                stopEffect(player);
            }
        }, 1L);
    }
}

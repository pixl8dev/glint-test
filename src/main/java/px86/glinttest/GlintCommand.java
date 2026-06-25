package px86.glinttest;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlintCommand implements CommandExecutor {

    private final GlintTest plugin;

    public GlintCommand(GlintTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("glintvariation")) {
            return handleVariation(sender, args);
        } else if (command.getName().equalsIgnoreCase("glinttoggle")) {
            return handleToggle(sender);
        }
        return false;
    }

    private boolean handleVariation(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (!player.hasPermission("glinttest.command")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.GOLD + "Glint " + ChatColor.GRAY + "| " + ChatColor.WHITE + "Usage: /glintvariation <1-5>");
            player.sendMessage(ChatColor.GOLD + "Glint " + ChatColor.GRAY + "| " + ChatColor.WHITE + "Current variation: " + plugin.getVariation(player));
            return true;
        }

        int variation;
        try {
            variation = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.GOLD + "Glint " + ChatColor.GRAY + "| " + ChatColor.WHITE + "Please enter a number between 1 and 5.");
            return true;
        }

        if (variation < 1 || variation > 5) {
            player.sendMessage(ChatColor.GOLD + "Glint " + ChatColor.GRAY + "| " + ChatColor.WHITE + "Please enter a number between 1 and 5.");
            return true;
        }

        plugin.setVariation(player, variation);
        String name = getVariationName(variation);
        player.sendMessage(ChatColor.GOLD + "Glint " + ChatColor.GRAY + "| " + ChatColor.WHITE + "Set variation to " + ChatColor.AQUA + name + ChatColor.WHITE + "!");
        return true;
    }

    private boolean handleToggle(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (!player.hasPermission("glinttest.toggle")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        plugin.toggleEnabled(player);
        boolean enabled = plugin.isEnabled(player);
        String status = enabled ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED";
        player.sendMessage(ChatColor.GOLD + "Glint " + ChatColor.GRAY + "| " + ChatColor.WHITE + "Glint effect " + status + ChatColor.WHITE + "!");
        return true;
    }

    private String getVariationName(int variation) {
        return switch (variation) {
            case 1 -> "Classic Cyan";
            case 2 -> "Enchanted Swirl";
            case 3 -> "Soul Fire";
            case 4 -> "Star Burst";
            case 5 -> "Smoke Trail";
            default -> "Unknown";
        };
    }
}

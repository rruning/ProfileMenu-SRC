package org.profilemenu;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCommand implements CommandExecutor {

    private final ProfileMenu plugin;

    public MenuCommand(ProfileMenu plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("profilemenu.reload")) {
                sender.sendMessage(ColorUtils.colorize("&cNo tienes permisos."));
                return true;
            }
            plugin.reloadConfig();
            plugin.loadMenuConfig();
            sender.sendMessage(ColorUtils.colorize("&aConfiguraciones recargadas."));
            return true;
        }

        if (sender instanceof Player player) {
            MenuListener listener = new MenuListener(plugin);

            player.openInventory(listener.createHopperMenu(player));
        }
        return true;
    }
}
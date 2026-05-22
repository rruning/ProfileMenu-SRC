package org.profilemenu;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ProfileMenu extends JavaPlugin {

    private File menuConfigFile;
    private FileConfiguration menuConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        loadMenuConfig();

        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getCommand("profilemenu").setExecutor(new MenuCommand(this));

        getLogger().info("ProfileMenu activado correctamente.");
    }

    public void loadMenuConfig() {
        menuConfigFile = new File(getDataFolder(), "menus/config.yml");
        if (!menuConfigFile.exists()) {
            menuConfigFile.getParentFile().mkdirs();
            saveResource("menus/config.yml", false);
        }
        menuConfig = YamlConfiguration.loadConfiguration(menuConfigFile);
    }

    public FileConfiguration getMenuConfig() {
        return menuConfig;
    }
}
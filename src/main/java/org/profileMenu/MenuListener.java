package org.profilemenu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.stream.Collectors;

public class MenuListener implements Listener {

    private final ProfileMenu plugin;

    public MenuListener(ProfileMenu plugin) {
        this.plugin = plugin;
    }

    private boolean isJoinItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        String configName = ColorUtils.colorize(plugin.getConfig().getString("join-item.name"));
        return item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(configName);
    }

    private void giveJoinItem(Player p) {
        if (!plugin.getConfig().getBoolean("join-item.enabled")) return;

        int slot = plugin.getConfig().getInt("join-item.slot");
        Material mat = Material.matchMaterial(plugin.getConfig().getString("join-item.material", "NETHER_STAR"));
        if (mat == null) {
            plugin.getLogger().warning("El material en config.yml no es válido.");
            return;
        }

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        if (mat == Material.PLAYER_HEAD && meta instanceof SkullMeta skullMeta) {
            if (plugin.getConfig().contains("join-item.username")) {
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(plugin.getConfig().getString("join-item.username")));
            } else {
                skullMeta.setOwningPlayer(p);
            }
        }

        meta.setDisplayName(ColorUtils.colorize(plugin.getConfig().getString("join-item.name")));

        List<String> lore = plugin.getConfig().getStringList("join-item.lore").stream()
                .map(ColorUtils::colorize)
                .collect(Collectors.toList());
        meta.setLore(lore);
        item.setItemMeta(meta);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            p.getInventory().setItem(slot, item);
        }, 5L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        giveJoinItem(e.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        giveJoinItem(e.getPlayer());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (isJoinItem(e.getItemDrop().getItemStack())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.getDrops().removeIf(this::isJoinItem);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = e.getItem();
            if (item == null || !item.hasItemMeta()) return;

            if (isJoinItem(item)) {
                e.getPlayer().openInventory(createHopperMenu(e.getPlayer()));
            }
        }
    }

    public Inventory createHopperMenu(Player player) {
        String title = ColorUtils.colorize(plugin.getMenuConfig().getString("menu.title", "Profile"));
        Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, title);

        ConfigurationSection itemsSection = plugin.getMenuConfig().getConfigurationSection("menu.items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemConfig = itemsSection.getConfigurationSection(key);
                int slot = itemConfig.getInt("slot");
                Material mat = Material.matchMaterial(itemConfig.getString("material", "STONE"));

                if (mat != null) {
                    ItemStack item = new ItemStack(mat);
                    ItemMeta meta = item.getItemMeta();

                    if (mat == Material.PLAYER_HEAD && meta instanceof SkullMeta skullMeta) {
                        if (itemConfig.contains("username")) {
                            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(itemConfig.getString("username")));
                        } else {
                            skullMeta.setOwningPlayer(player);
                        }
                    }

                    meta.setDisplayName(ColorUtils.colorize(itemConfig.getString("name")));

                    List<String> lore = itemConfig.getStringList("lore").stream()
                            .map(ColorUtils::colorize)
                            .collect(Collectors.toList());
                    meta.setLore(lore);
                    item.setItemMeta(meta);

                    inv.setItem(slot, item);
                }
            }
        }
        return inv;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String title = ColorUtils.colorize(plugin.getMenuConfig().getString("menu.title", "Profile"));

        if (e.getView().getTitle().equals(title)) {
            e.setCancelled(true);

            if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) return;

            Player p = (Player) e.getWhoClicked();
            int clickedSlot = e.getSlot();

            ConfigurationSection itemsSection = plugin.getMenuConfig().getConfigurationSection("menu.items");
            if (itemsSection != null) {
                for (String key : itemsSection.getKeys(false)) {
                    ConfigurationSection itemConfig = itemsSection.getConfigurationSection(key);
                    if (itemConfig.getInt("slot") == clickedSlot) {

                        List<String> actions = itemConfig.getStringList("actions");
                        for (String action : actions) {
                            if (action.startsWith("[message] ")) {
                                String msg = action.replace("[message] ", "");
                                p.sendMessage(ColorUtils.colorize(msg));
                            }
                        }

                        p.closeInventory();
                        break;
                    }
                }
            }
            return;
        }

        if (isJoinItem(e.getCurrentItem()) || isJoinItem(e.getCursor())) {
            e.setCancelled(true);
        }
    }
}
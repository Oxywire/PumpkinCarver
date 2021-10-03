package com.oxywire.pumpkincarver.listeners;

import com.oxywire.pumpkincarver.PumpkinCarverPlugin;
import com.oxywire.pumpkincarver.utils.Utils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class NPCListener implements Listener {

    private final PumpkinCarverPlugin plugin;

    public NPCListener(final PumpkinCarverPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(final NPCRightClickEvent event) {
        if (event.getNPC().getId() == plugin.getConfig().getInt("redeem.npc-id")) {
            final Player player = event.getClicker();
            final ItemStack hand = player.getInventory().getItemInMainHand();
            if (hand != null && hand.hasItemMeta() && hand.getItemMeta().getPersistentDataContainer()
                    .has(new NamespacedKey(plugin, "pumpkincarver-customitem"), PersistentDataType.STRING)) {
                player.getInventory().remove(hand);
                Utils.showTitle(player, plugin.getConfig().getConfigurationSection("redeem.title"));
                Utils.sendMessage(player, plugin.getConfig().getString("redeem.message"));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfig().getString("redeem.console-command"));
            }
        }
    }
}

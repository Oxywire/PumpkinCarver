package com.oxywire.pumpkincarver.listeners;

import com.oxywire.pumpkincarver.PumpkinCarverPlugin;
import com.oxywire.pumpkincarver.utils.Utils;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerEvents implements Listener {

    private final PumpkinCarverPlugin plugin;
    private final Map<UUID, Long> placeProtection = new HashMap<>();
    private final Map<UUID, Long> breakProtection = new HashMap<>();

    public PlayerEvents(final PumpkinCarverPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(final BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (plugin.getConfig().getStringList("excluded-blocks").contains(String.valueOf(event.getBlock().getType()))) {
            return;
        }

        final Player player = event.getPlayer();
        placeProtection.put(player.getUniqueId(), System.currentTimeMillis() + 5000);
        if (inProtection(player.getUniqueId(), breakProtection)) {
            return;
        }
        if (shouldDrop(plugin.getConfig().getDouble("rates.block-break"))) {
            dropPumpkin(player);
        }
    }

    @EventHandler
    public void onPlace(final BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (plugin.getConfig().getStringList("excluded-blocks").contains(String.valueOf(event.getBlock().getType()))) {
            return;
        }

        final Player player = event.getPlayer();
        breakProtection.put(player.getUniqueId(), System.currentTimeMillis() + 5000);
        if (inProtection(player.getUniqueId(), placeProtection)) {
            return;
        }
        if (shouldDrop(plugin.getConfig().getDouble("rates.block-place"))) {
            dropPumpkin(player);
        }
    }

    @EventHandler
    public void onKill(final EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final Player player = entity.getKiller();

        if (entity instanceof Mob && player != null && shouldDrop(plugin.getConfig().getDouble("rates.mob-kill"))) {
            dropPumpkin(player);
        }
    }

    private void dropPumpkin(final Player player) {
        final Location loc = player.getLocation();

        plugin.addDropped();
        loc.getWorld().dropItem(loc, Utils.getItem(plugin, player.getName(), plugin.getDropped()));
        Utils.showTitle(player, plugin.getConfig().getConfigurationSection("broadcast.title"));
        player.playSound(loc, Sound.valueOf(plugin.getConfig().getString("broadcast.sound")), 1, 1);
        Utils.sendMessage(plugin.getServer(), plugin.getConfig().getString("broadcast.broadcast-msg"),
                Template.of("player", player.getName()),
                Template.of("number", String.valueOf(plugin.getDropped())));
    }

    private boolean shouldDrop(final double chance) {
        return Math.random() < chance / 100;
    }

    private boolean inProtection(final UUID uuid, final Map<UUID, Long> map) {
        return map.containsKey(uuid) && map.get(uuid) > System.currentTimeMillis();
    }
}

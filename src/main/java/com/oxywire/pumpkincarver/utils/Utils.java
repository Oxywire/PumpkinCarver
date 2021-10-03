package com.oxywire.pumpkincarver.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.oxywire.pumpkincarver.PumpkinCarverPlugin;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;
import java.util.UUID;
import java.util.stream.Collectors;

public final class Utils {

    private Utils() {
    }

    public static Component color(final String text) {
        return Component.text(text.replace('&', ChatColor.COLOR_CHAR));
    }

    public static void sendMessage(final Audience audience, final String lang) {
        audience.sendMessage(color(lang));
    }

    public static void showTitle(final Audience audience, final ConfigurationSection config) {
        audience.showTitle(Title.title(
                color(config.getString("title")),
                color(config.getString("subtitle")),
                Title.Times.of(
                        Duration.ofSeconds(config.getInt("fade-in")),
                        Duration.ofSeconds(config.getInt("stay")),
                        Duration.ofSeconds(config.getInt("fade-out"))
                )
        ));
    }

    public static ItemStack getItem(final PumpkinCarverPlugin plugin, final int number) {
        final ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        final SkullMeta meta = (SkullMeta) skull.getItemMeta();
        final PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());

        profile.setProperty(new ProfileProperty("textures", plugin.getConfig().getString("item.texture")));

        meta.setPlayerProfile(profile);
        meta.displayName(color(plugin.getConfig().getString("item.name").replace("%GlobalPumpkinCounter%", String.valueOf(number))));
        meta.lore(plugin.getConfig().getStringList("item.lore").stream().map(Utils::color).collect(Collectors.toList()));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "pumpkincarver-customitem"), PersistentDataType.STRING, "pumpkin");
        skull.setItemMeta(meta);

        return skull;
    }
}

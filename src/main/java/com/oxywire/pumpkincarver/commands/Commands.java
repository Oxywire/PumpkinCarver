package com.oxywire.pumpkincarver.commands;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.paper.PaperCommandManager;
import com.oxywire.pumpkincarver.PumpkinCarverPlugin;
import com.oxywire.pumpkincarver.utils.Utils;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands {

    private final PumpkinCarverPlugin plugin;

    public Commands(final PumpkinCarverPlugin plugin, final PaperCommandManager<CommandSender> manager) {
        this.plugin = plugin;
        final Command.Builder<CommandSender> builder = manager.commandBuilder("pumpkincarver");

        manager.command(builder.handler(context -> {
            Utils.sendMessage(context.getSender(), plugin.getConfig().getString("messages.pumpkins"),
                    Template.of("number", String.valueOf(plugin.getDropped())));
        }));

        manager.command(builder.literal("drop")
                .senderType(Player.class)
                .permission("pumpkincarver.admin")
                .handler(context -> {
                    final Player sender = (Player) context.getSender();
                    dropItem(sender.getLocation(), sender.getName());
                    Utils.sendMessage(sender, plugin.getConfig().getString("messages.pumpkins-drop"),
                            Template.of("player", sender.getName()),
                            Template.of("number", String.valueOf(plugin.getDropped())));
                }));

        manager.command(builder.literal("give")
                .permission("pumpkincarver.admin")
                .argument(PlayerArgument.of("player"))
                .argument(IntegerArgument.of("number"))
                .handler(context -> {
                    final Player sender = (Player) context.getSender();
                    final Player target = context.get("player");
                    final int number = context.get("number");
                    dropItem(target.getLocation(), target.getName(), number);
                    Utils.sendMessage(sender, plugin.getConfig().getString("messages.pumpkins-give"),
                            Template.of("number", String.valueOf(number)),
                            Template.of("player", target.getName()));
                })
        );

        manager.command(builder.literal("reload")
                .permission("pumpkincarver.admin")
                .handler(context -> {
                    plugin.reloadConfig();
                    Utils.sendMessage(context.getSender(), plugin.getConfig().getString("messages.reload"));
                }));
    }

    private void dropItem(final Location loc, final String name) {
        plugin.addDropped();
        loc.getWorld().dropItem(loc, Utils.getItem(plugin, name, plugin.getDropped()));
    }

    private void dropItem(final Location loc, final String name, final int number) {
        loc.getWorld().dropItem(loc, Utils.getItem(plugin, name, number));
    }
}

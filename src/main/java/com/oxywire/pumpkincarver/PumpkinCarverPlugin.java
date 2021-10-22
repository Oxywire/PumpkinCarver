package com.oxywire.pumpkincarver;

import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.oxywire.pumpkincarver.commands.Commands;
import com.oxywire.pumpkincarver.listeners.NPCListener;
import com.oxywire.pumpkincarver.listeners.PlayerEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public final class PumpkinCarverPlugin extends JavaPlugin {

    private File dataFile;
    private FileConfiguration data;
    private int dropped;
    private List<String> claimed;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initData();
        dropped = this.data.getInt("dropped");
        claimed = this.data.getStringList("claimed");
        autoSave();
        commands();
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new NPCListener(this), this);
    }

    @Override
    public void onDisable() {
        this.data.set("dropped", this.dropped);
        this.data.set("claimed", this.claimed);
        saveData();
    }

    private void commands() {
        try {
            final PaperCommandManager<CommandSender> manager = new PaperCommandManager<>(
                    this,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity()
            );

            if (manager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
                manager.registerAsynchronousCompletions();
            }

            new Commands(this, manager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        this.dataFile = new File(this.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            saveResource("data.yml", false);
        }
        this.data = new YamlConfiguration();
        try {
            this.data.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void autoSave() {
        getServer().getScheduler().runTaskTimer(this, () -> {
            this.data.set("dropped", this.dropped);
            this.data.set("claimed", this.claimed);
            saveData();
        }, 0, 6000); // 5 mins
    }

    public int getDropped() {
        return this.dropped;
    }

    public void addDropped() {
        this.dropped++;
    }

    public List<String> getClaimed() {
        return this.claimed;
    }

    public void addClaimed(final UUID uuid) {
        this.claimed.add(uuid.toString());
    }
}

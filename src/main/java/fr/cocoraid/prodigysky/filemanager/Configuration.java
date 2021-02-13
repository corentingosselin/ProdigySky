package fr.cocoraid.prodigysky.filemanager;

import fr.cocoraid.prodigysky.ProdigySky;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Configuration {

    private List<World> enabledWorlds = new ArrayList<>();

    private ProdigySky instance;
    public Configuration(ProdigySky plugin) {
        this.instance = plugin;
    }

    public Configuration init() {
        File file = new File(instance.getDataFolder(), "configuration.yml");
        if (!file.exists()) {
            instance.saveResource("configuration.yml", false);

        }
        return this;
    }


    private static final String ENABLED_WORLD = "enabled-worlds";
    public void load() {
        ConsoleCommandSender cc = Bukkit.getConsoleSender();

        File file = new File(instance.getDataFolder() , "configuration.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);

        if(data.isSet(ENABLED_WORLD)) {
            List<String> worlds = data.getStringList(ENABLED_WORLD);
            for (String world : worlds) {
                World w = Bukkit.getWorld(world);
                if(w  == null) {
                    cc.sendMessage("§6[ProdigySky Warning] §4 " + world + " §cdoes not exist for enabled-world in configuration.yml");
                    continue;
                }
                enabledWorlds.add(w);
            }
        }
    }

    public List<World> getEnabledWorlds() {
        return enabledWorlds;
    }
}

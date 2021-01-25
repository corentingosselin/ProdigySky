package fr.cocoraid.prodigysky.filemanager;

import fr.cocoraid.prodigysky.ProdigySky;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Configuration {

  private final List<World> enabledWorlds = new ArrayList<>();
  private final ProdigySky instance;
  private static final String ENABLED_WORLD = "enabled-worlds";

  public Configuration(ProdigySky plugin) {
    this.instance = plugin;
  }

  public Configuration init() {
    File file = new File(this.instance.getDataFolder(), "configuration.yml");
    if (!file.exists()) {
      this.instance.saveResource("configuration.yml", false);
    }

    return this;
  }

  public void load() {
    ConsoleCommandSender cc = Bukkit.getConsoleSender();
    File file = new File(this.instance.getDataFolder(), "configuration.yml");
    FileConfiguration data = YamlConfiguration.loadConfiguration(file);
    if (data.isSet("enabled-worlds")) {
      List<String> worlds = data.getStringList("enabled-worlds");

      for (String world : worlds) {
        World w = Bukkit.getWorld(world);
        if (w == null) {
          cc.sendMessage("§6[ProdigySky Warning] §4 " + world
              + " §cdoes not exist for enabled-world in configuration.yml");
        } else {
          this.enabledWorlds.add(w);
        }
      }
    }

  }

  public List<World> getEnabledWorlds() {
    return this.enabledWorlds;
  }
}

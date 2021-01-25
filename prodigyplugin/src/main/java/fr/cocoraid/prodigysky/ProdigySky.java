package fr.cocoraid.prodigysky;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandManager;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import fr.cocoraid.prodigysky.commands.MainCMD;
import fr.cocoraid.prodigysky.filemanager.Configuration;
import fr.cocoraid.prodigysky.filemanager.CustomBiomes;
import fr.cocoraid.prodigysky.listeners.EventListener;
import fr.cocoraid.prodigysky.listeners.PacketListener;
import fr.cocoraid.prodigysky.versiondetector.VersionDetector;
import fr.prodigysky.api.ProdigySkyAPI;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ProdigySky extends JavaPlugin {

  private Configuration configuration;
  private CustomBiomes customBiomes;
  private ProdigySkyAPI prodigySkyAPI;

  public void onEnable() {
    VersionDetector versionDetector = new VersionDetector(this);

    prodigySkyAPI = new ProdigySkyAPIImpl(this, versionDetector.getPackets());
    getServer().getServicesManager()
        .register(ProdigySkyAPI.class, prodigySkyAPI, this, ServicePriority.High);

    this.displayBanner();
    this.configuration = new Configuration(this);
    this.configuration.init();
    this.configuration.load();
    this.customBiomes = new CustomBiomes(this, versionDetector.getBiomes());
    this.loadCommands();
    Map<UUID, World> changeWorlds = new HashMap<>();
    Bukkit.getPluginManager().registerEvents(new EventListener(this, prodigySkyAPI, changeWorlds), this);
    new PacketListener(this, prodigySkyAPI, changeWorlds);
    (new BukkitRunnable() {
      public void run() {
      }
    }).runTaskTimerAsynchronously(this, 20L, 20L);
  }

  private void displayBanner() {
    ConsoleCommandSender cc = Bukkit.getConsoleSender();
    cc.sendMessage("§5 ______               _ _                 §b______ _");
    cc.sendMessage("§5(_____ \\             | (_)               §b/ _____) |");
    cc.sendMessage("§5 _____) )___ ___   __| |_  ____ _   _   §b( (____ | |  _ _   _ ");
    cc.sendMessage("§5|  ____/ ___) _ \\ / _  | |/ _  | | | |   §b\\____ \\| |_/ ) | | |");
    cc.sendMessage("§5| |   | |  | |_| ( (_| | ( (_| | |_| |  §b _____) )  _ (| |_| |");
    cc.sendMessage("§5|_|   |_|   \\___/ \\____|_|\\___ |\\__  |§b  (______/|_| \\_)\\__  |");
    cc.sendMessage("§5                          (_____(____/      §b           (____/");
    cc.sendMessage("§d The prodigy is the man who knows how to shape the sky");
  }

  private void loadCommands() {
    BukkitCommandManager manager = new BukkitCommandManager(this);
    manager.getCommandConditions().addCondition(World.class, "worldEnabled", (c, exec, value) -> {
      if (value != null) {
        if (!this.configuration.getEnabledWorlds().contains(value)) {
          throw new ConditionFailedException(
              "The world " + value.getName() + " is not added in the enabled world list !");
        }
      }
    });
    manager.getCommandConditions()
        .addCondition(Player.class, "playerWorldEnabled", (c, exec, value) -> {
          if (value != null) {
            if (!this.configuration.getEnabledWorlds().contains(value.getWorld())) {
              throw new ConditionFailedException("The world " + value.getWorld().getName()
                  + " is not added in the enabled world list !");
            }
          }
        });
    manager.getCommandCompletions()
        .registerAsyncCompletion("biomeName", (c) -> this.customBiomes.getBiomeList().keySet());
    manager.registerCommand(new MainCMD(this, prodigySkyAPI));
  }

  public CustomBiomes getCustomBiomes() {
    return this.customBiomes;
  }

  public Configuration getConfiguration() {
    return this.configuration;
  }
}

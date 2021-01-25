package fr.cocoraid.prodigysky.versiondetector;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.cocoraid.prodigysky.nms.biomes.Biomes;
import fr.cocoraid.prodigysky.nms.packet.Packets;

public class VersionDetector {

  private Biomes biomes;
  private Packets packets;

  public VersionDetector(JavaPlugin javaPlugin) {
    String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    if (version.equals("v1_16_R2")) {
      this.biomes = new fr.cocoraid.prodigysky.nms.Biomes_1_16R2();
      this.packets = new fr.cocoraid.prodigysky.nms.Packets_1_16R2(javaPlugin);
    } else if (version.equals("v1_16_R3")) {
      this.biomes = new fr.cocoraid.prodigysky.nms.Biomes_1_16R3();
      this.packets = new fr.cocoraid.prodigysky.nms.Packets_1_16R3(javaPlugin);
    } else {
      javaPlugin.getServer().getConsoleSender().sendMessage(
          "§4[ProdigyNightclub] The plugin is not compatible with this version sorry :'(");
      Bukkit.getPluginManager().disablePlugin(javaPlugin);
    }
  }

  public Biomes getBiomes() {
    return biomes;
  }

  public Packets getPackets() {
    return packets;
  }
}

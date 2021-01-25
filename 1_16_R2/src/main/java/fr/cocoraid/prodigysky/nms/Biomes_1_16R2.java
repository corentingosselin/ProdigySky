package fr.cocoraid.prodigysky.nms;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;

import fr.cocoraid.prodigysky.nms.biomes.Biomes;
import net.minecraft.server.v1_16_R2.BiomeBase;
import net.minecraft.server.v1_16_R2.IRegistry;
import net.minecraft.server.v1_16_R2.MinecraftServer;
import net.minecraft.server.v1_16_R2.WorldServer;

public class Biomes_1_16R2 implements Biomes {

  public Map<String, Integer> getBiomes() {
    Map<String, Integer> biomes = new HashMap<>();

    WorldServer ws = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
    MinecraftServer server = ws.getMinecraftServer();
    server.getCustomRegistry().b(IRegistry.ay).keySet().forEach((k) -> {
      if (k.getKey().startsWith("prodigysky/")) {
        BiomeBase bb = server.getCustomRegistry().b(IRegistry.ay).get(k);
        int biomeID = server.getCustomRegistry().b(IRegistry.ay).a(bb);

        biomes.put(k.getKey().replace("prodigysky/", ""), biomeID);
      }

    });
    return biomes;
  }
}

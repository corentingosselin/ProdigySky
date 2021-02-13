package fr.cocoraid.prodigysky.nms.biomes.versions;

import fr.cocoraid.prodigysky.nms.biomes.Biomes;
import net.minecraft.server.v1_16_R3.BiomeBase;
import net.minecraft.server.v1_16_R3.IRegistry;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

import java.util.HashMap;
import java.util.Map;

public class Biomes_1_16R3 implements Biomes {
    @Override
    public Map<String, Integer> getBiomes() {
        Map<String, Integer> biomes = new HashMap<>();
        WorldServer ws = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
        MinecraftServer server = ws.getMinecraftServer();
        server.getCustomRegistry().b(IRegistry.ay).keySet().forEach(k -> {
            if(k.getKey().startsWith("prodigysky/")) {
                BiomeBase bb =  server.getCustomRegistry().b(IRegistry.ay).get(k);
                int biomeID = server.getCustomRegistry().b(IRegistry.ay).a(bb);
                biomes.put(k.getKey().replace("prodigysky/",""), biomeID);
            }
        });
        return biomes;
    }
}

package fr.prodigysky.api;


import java.util.Map;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;

import fr.cocoraid.prodigysky.feature.BiomeData;

public interface ProdigySkyAPI {

  void setBiome(
      Player player, String biomeTemplate, boolean smog, EffectDuration duration);

  void removeBiome(Player player);

  void removeBiome(World w);

  void setBiome(World world, String biomeTemplate, boolean smog, EffectDuration duration);

  Map<UUID, BiomeData> getBiomePlayer();

  Map<World, BiomeData> getBiomeWorlds();

  BiomeData getBiomeData(UUID uuid);

  BiomeData getBiomeData(World w);
}

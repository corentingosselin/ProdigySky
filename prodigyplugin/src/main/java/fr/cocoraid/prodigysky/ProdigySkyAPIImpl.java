package fr.cocoraid.prodigysky;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import fr.cocoraid.prodigysky.feature.BiomeData;
import fr.cocoraid.prodigysky.nms.packet.Packets;
import fr.prodigysky.api.EffectDuration;
import fr.prodigysky.api.ProdigySkyAPI;

public class ProdigySkyAPIImpl implements ProdigySkyAPI {

  private final Map<World, BiomeData> biomeWorlds = new HashMap<>();
  private final Map<UUID, BiomeData> biomePlayer = new HashMap<>();
  private final ProdigySky prodigySky;
  private final Packets packets;

  public ProdigySkyAPIImpl(ProdigySky prodigySky, Packets packets) {
    this.prodigySky = prodigySky;
    this.packets = packets;
  }

  @Override
  public void setBiome(
      Player player, String biomeTemplate, boolean smog, EffectDuration duration) {
    BiomeData biomeData = new BiomeData(biomeTemplate.toLowerCase(), duration);
    biomeData.setSmog(smog);
    if (!hasExactlyTheSameBiome(player, biomeData)) {
      boolean oldSmog = getBiomePlayer().containsKey(player.getUniqueId()) && getBiomePlayer()
          .get(player.getUniqueId())
          .isSmog();
      player.setPlayerTime(0L, false);
      biomePlayer.put(player.getUniqueId(), biomeData);
      World w = player.getWorld();
      boolean alreadySmog = false;
      if (getBiomeWorlds().containsKey(w) && getBiomeWorlds().get(w).isSmog() || oldSmog) {
        alreadySmog = true;
      }

      if (smog && !alreadySmog) {
        packets.setSmog(player);
      } else if (alreadySmog && !smog) {
        packets.restoreDefaultWorld(player);
      }

      if (!getBiomeWorlds().containsKey(w) || getBiomeWorlds().containsKey(w) && getBiomeWorlds()
          .get(w)
          .getName().equalsIgnoreCase(biomeTemplate)) {
        packets.sendFakeBiome(player);
      }

    }
  }

  @Override
  public void removeBiome(Player player) {
    if (getBiomePlayer().containsKey(player.getUniqueId())) {
      boolean hasSmog = getBiomePlayer().get(player.getUniqueId()).isSmog();
      getBiomePlayer().remove(player.getUniqueId());
      packets.sendFakeBiome(player);
      if (hasSmog) {
        packets.restoreDefaultWorld(player);
      }

    }
  }

  @Override
  public void removeBiome(World w) {
    if (getBiomeWorlds().containsKey(w)) {
      boolean hasSmog = getBiomeWorlds().get(w).isSmog();
      EffectDuration duration = getBiomeWorlds().get(w).getDuration();
      List<UUID> tempPlayers = getBiomeWorlds().get(w).getTempPlayers();
      getBiomeWorlds().remove(w);
      if (duration == EffectDuration.PERSISTENT) {
        Bukkit.getOnlinePlayers().stream().filter((cur) -> cur.getWorld().equals(w))
            .forEach((cur) -> {
              packets.sendFakeBiome(cur);
              boolean playerSmog =
                  getBiomePlayer().containsKey(cur.getUniqueId()) && getBiomePlayer()
                      .get(cur.getUniqueId())
                      .isSmog();
              if (hasSmog && !playerSmog) {
                packets.restoreDefaultWorld(cur);
              }

            });
      } else if (duration == EffectDuration.VOLATILE && !tempPlayers.isEmpty()) {
        tempPlayers.forEach((uuid) -> {
          Player cur = Bukkit.getPlayer(uuid);
          packets.sendFakeBiome(cur);
          boolean playerSmog = cur != null && getBiomePlayer().containsKey(cur.getUniqueId()) && getBiomePlayer()
              .get(cur.getUniqueId())
              .isSmog();
          if (hasSmog && !playerSmog) {
            packets.restoreDefaultWorld(cur);
          }

        });
      }

    }
  }

  @Override
  public void setBiome(World world, String biomeTemplate, boolean smog, EffectDuration duration) {
    BiomeData biomeData = new BiomeData(biomeTemplate.toLowerCase(), duration);
    biomeData.setSmog(smog);
    if (!hasExactlyTheSameBiome(world, biomeData)) {
      boolean hasWorldSmog = biomeWorlds.containsKey(world) && biomeWorlds.get(world).isSmog();
      biomeWorlds.put(world, biomeData);
      Bukkit.getOnlinePlayers().stream().filter((cur) ->
          cur.getWorld().equals(world) && !getBiomePlayer().containsKey(cur.getUniqueId()))
          .forEach((cur) -> {
            cur.setPlayerTime(0L, false);
            if (duration == EffectDuration.VOLATILE) {
              biomeData.getTempPlayers().add(cur.getUniqueId());
            }

            boolean smogged = false;
            if (!hasWorldSmog && smog && (!getBiomePlayer().containsKey(cur.getUniqueId())
                || getBiomePlayer().containsKey(cur.getUniqueId()) && !getBiomePlayer()
                .get(cur.getUniqueId())
                .isSmog())) {
              smogged = true;
              packets.setSmog(cur);
            }

            if (!smogged && !smog && hasWorldSmog) {
              packets.restoreDefaultWorld(cur);
            }

            packets.sendFakeBiome(cur);
          });
    }
  }

  @Override
  public Map<UUID, BiomeData> getBiomePlayer() {
    return biomePlayer;
  }

  @Override
  public Map<World, BiomeData> getBiomeWorlds() {
    return biomeWorlds;
  }

  private boolean hasExactlyTheSameBiome(Player p, BiomeData toCompare) {
    if (!biomePlayer.containsKey(p.getUniqueId())) {
      return false;
    } else {
      BiomeData bd = biomePlayer.get(p.getUniqueId());
      return bd.getName().equalsIgnoreCase(toCompare.getName()) && bd.isSmog() == toCompare.isSmog()
          && bd.getDuration() == toCompare.getDuration();
    }
  }

  private boolean hasExactlyTheSameBiome(World world, BiomeData toCompare) {
    if (!biomeWorlds.containsKey(world)) {
      return false;
    } else {
      BiomeData bd = biomeWorlds.get(world);
      return bd.getName().equalsIgnoreCase(toCompare.getName()) && bd.isSmog() == toCompare.isSmog()
          && bd.getDuration() == toCompare.getDuration();
    }
  }

  @Override
  public BiomeData getBiomeData(UUID uuid) {
    return getBiomePlayer().get(uuid);
  }

  @Override
  public BiomeData getBiomeData(World w) {
    return getBiomeWorlds().get(w);
  }

}

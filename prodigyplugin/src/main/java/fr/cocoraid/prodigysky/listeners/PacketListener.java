package fr.cocoraid.prodigysky.listeners;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftReflection;

import fr.cocoraid.prodigysky.ProdigySky;
import fr.cocoraid.prodigysky.reflection.NMSUtils;
import fr.prodigysky.api.ProdigySkyAPI;

public class PacketListener {

  private static final Class<?> dimensionManagerClass = MinecraftReflection
      .getMinecraftClass("DimensionManager");

  public PacketListener(final ProdigySky instance, ProdigySkyAPI prodigySkyAPI,
      Map<UUID, World> changeWorlds) {
    ProtocolManager manager = ProtocolLibrary.getProtocolManager();
    manager.addPacketListener(new PacketAdapter(instance, Server.MAP_CHUNK) {
      public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player p = event.getPlayer();
        String biomeName;
        int biomeID;
        int[] biomeIDs;
        if (prodigySkyAPI.getBiomePlayer().containsKey(p.getUniqueId())) {
          biomeName = prodigySkyAPI.getBiomePlayer().get(p.getUniqueId()).getName();
          biomeID = instance.getCustomBiomes().getBiomeList().get(biomeName);
          biomeIDs = packet.getIntegerArrays().read(0);
          Arrays.fill(biomeIDs, biomeID);
          packet.getIntegerArrays().write(0, biomeIDs);
        } else if (prodigySkyAPI.getBiomeWorlds().containsKey(p.getWorld())) {
          biomeName = prodigySkyAPI.getBiomeWorlds().get(p.getWorld()).getName();
          biomeID = instance.getCustomBiomes().getBiomeList().get(biomeName);
          biomeIDs = packet.getIntegerArrays().read(0);
          Arrays.fill(biomeIDs, biomeID);
          packet.getIntegerArrays().write(0, biomeIDs);
        }

      }
    });
    ProtocolLibrary.getProtocolManager()
        .addPacketListener(new PacketAdapter(PacketAdapter.params(instance,
            Server.LOGIN, Server.RESPAWN)) {
          public void onPacketSending(PacketEvent event) {
            Player p = event.getPlayer();
            World w = changeWorlds.containsKey(p.getUniqueId()) ? changeWorlds.get(p.getUniqueId())
                : p.getWorld();
            changeWorlds.remove(p.getUniqueId());
            boolean smog = false;
            if (prodigySkyAPI.getBiomePlayer().containsKey(p.getUniqueId())) {
              smog = prodigySkyAPI.getBiomePlayer().get(p.getUniqueId()).isSmog();
            } else if (prodigySkyAPI.getBiomeWorlds().containsKey(w)) {
              smog = prodigySkyAPI.getBiomeWorlds().get(w).isSmog();
            }

            if (smog) {
              Object dm = event.getPacket().getModifier()
                  .withType(PacketListener.dimensionManagerClass).read(0);
              Object clone = NMSUtils.cloneDimension(dm, true);
              event.getPacket().getModifier().withType(PacketListener.dimensionManagerClass)
                  .write(0, clone);
            }

          }
        });
    ProtocolLibrary.getProtocolManager()
        .addPacketListener(new PacketAdapter(PacketAdapter.params(instance,
            Server.UPDATE_TIME)) {
          public void onPacketSending(PacketEvent event) {
            Player p = event.getPlayer();
            if (prodigySkyAPI.getBiomePlayer().containsKey(p.getUniqueId()) || prodigySkyAPI
                .getBiomeWorlds().containsKey(p.getWorld())) {
              event.getPacket().getLongs().write(0, 0L);
            }

          }
        });
  }
}

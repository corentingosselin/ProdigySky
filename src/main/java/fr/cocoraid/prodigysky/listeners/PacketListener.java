package fr.cocoraid.prodigysky.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftReflection;
import fr.cocoraid.prodigysky.ProdigySky;
import fr.cocoraid.prodigysky.nms.NMSUtils;
import fr.prodigysky.api.ProdigySkyAPI;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class PacketListener {

    ProtocolManager manager = ProtocolLibrary.getProtocolManager();
    private static final Class<?> dimensionManagerClass = MinecraftReflection.getMinecraftClass("DimensionManager");

    public PacketListener(ProdigySky instance) {
        manager.addPacketListener(new PacketAdapter(instance, PacketType.Play.Server.MAP_CHUNK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player p = event.getPlayer();
                if (ProdigySkyAPI.getBiomePlayer().containsKey(p.getUniqueId())) {
                    String biomeName = ProdigySkyAPI.getBiomePlayer().get(p.getUniqueId()).getName();
                    int biomeID = instance.getCustomBiomes().getBiomes().get(biomeName);
                    int[] biomeIDs = packet.getIntegerArrays().read(0);
                    // replace all biome
                    if(biomeIDs != null) {
                        Arrays.fill(biomeIDs, biomeID); //id of custombiome
                        packet.getIntegerArrays().write(0, biomeIDs);
                    }

                } else if (ProdigySkyAPI.getBiomeWorlds().containsKey(p.getWorld())) {
                    String biomeName = ProdigySkyAPI.getBiomeWorlds().get(p.getWorld()).getName();
                    int biomeID = instance.getCustomBiomes().getBiomes().get(biomeName);
                    int[] biomeIDs = packet.getIntegerArrays().read(0);
                    // replace all biome
                    if(biomeIDs != null) {
                        Arrays.fill(biomeIDs, biomeID); //id of custombiome
                        packet.getIntegerArrays().write(0, biomeIDs);
                    }
                }
            }
        });



        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(PacketAdapter.params(instance, PacketType.Play.Server.LOGIN, PacketType.Play.Server.RESPAWN)) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        Player p = event.getPlayer();
                        World w = EventListener.changeWorlds.containsKey(p.getUniqueId()) ? EventListener.changeWorlds.get(p.getUniqueId()) : p.getWorld();
                        EventListener.changeWorlds.remove(p.getUniqueId());
                        boolean smog = false;
                        if (ProdigySkyAPI.getBiomePlayer().containsKey(p.getUniqueId())) {
                            smog = ProdigySkyAPI.getBiomePlayer().get(p.getUniqueId()).isSmog();
                        } else if (ProdigySkyAPI.getBiomeWorlds().containsKey(w)) {
                            smog = ProdigySkyAPI.getBiomeWorlds().get(w).isSmog();
                        }
                        if (smog) {
                            Object dm = event.getPacket().getModifier().withType(dimensionManagerClass).read(0);
                            Object clone = NMSUtils.cloneDimension(dm,true);
                            event.getPacket().getModifier().withType(dimensionManagerClass).write(0,clone);
                        }
                    }
                }
        );


        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(PacketAdapter.params(instance, PacketType.Play.Server.UPDATE_TIME)) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        Player p = event.getPlayer();
                        if (ProdigySkyAPI.getBiomePlayer().containsKey(p.getUniqueId()) || ProdigySkyAPI.getBiomeWorlds().containsKey(p.getWorld())) {
                            event.getPacket().getLongs().write(0,6000L);
                        }
                    }
                }
        );

    }




}

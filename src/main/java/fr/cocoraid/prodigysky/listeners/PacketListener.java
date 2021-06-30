package fr.cocoraid.prodigysky.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import fr.cocoraid.prodigysky.ProdigySky;
import fr.cocoraid.prodigysky.feature.BiomeData;
import fr.prodigysky.api.ProdigySkyAPI;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Random;

public class PacketListener {

    ProtocolManager manager = ProtocolLibrary.getProtocolManager();
    public PacketListener(ProdigySky instance) {
        manager.addPacketListener(new PacketAdapter(instance, PacketType.Play.Server.MAP_CHUNK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player p = event.getPlayer();
                if (ProdigySkyAPI.getBiomePlayers().containsKey(p.getUniqueId())) {
                    BiomeData biomeData = ProdigySkyAPI.getBiomePlayers().get(p.getUniqueId());
                    if(biomeData.getWorld() != null && !biomeData.getWorld().equals(biomeData.getWorld())) return;
                    int[] biomeIDs = packet.getIntegerArrays().read(0);
                    int biomeId = instance.getConfiguration().getBiomes().get(biomeData.getName());
                    Arrays.fill(biomeIDs, biomeId); //id of custombiome
                    packet.getIntegerArrays().write(0, biomeIDs);
                }
            }
        });

    }


}

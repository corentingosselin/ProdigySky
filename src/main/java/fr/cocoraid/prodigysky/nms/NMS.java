package fr.cocoraid.prodigysky.nms;

import fr.cocoraid.prodigysky.ProdigySky;
import fr.cocoraid.prodigysky.nms.biome.Biome;
import fr.cocoraid.prodigysky.nms.biome.BiomeBaseWrapper_1_17R1;
import fr.cocoraid.prodigysky.nms.packet.Packets;
import fr.cocoraid.prodigysky.nms.packet.versions.Packets_1_17R1;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class NMS {

    private Packets packets;
    private Biome biome;
    public NMS(ProdigySky instance) {
        ConsoleCommandSender cc = Bukkit.getConsoleSender();
        try {
                this.packets = new Packets_1_17R1();
                this.biome = new BiomeBaseWrapper_1_17R1();
                //cc.sendMessage("§4[ProdigyNightclub] The plugin is not compatible with this version sorry :'(");
                //Bukkit.getPluginManager().disablePlugin(instance);


        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public Biome getBiome() {
        return biome;
    }

    public Packets getPackets() {
        return packets;
    }
}

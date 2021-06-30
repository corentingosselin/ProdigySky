package fr.prodigysky.api;

import fr.cocoraid.prodigysky.ProdigySky;
import fr.cocoraid.prodigysky.feature.BiomeData;
import fr.cocoraid.prodigysky.nms.packet.Packets;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProdigySkyAPI {


    private static Map<UUID, BiomeData> biomePlayers = new HashMap<>();
    private static Packets packets = ProdigySky.getInstance().getNMS().getPackets();

    public static void setBiome(Player player, String biome, EffectDuration duration, @Nullable  World world) {
        BiomeData biomeData = new BiomeData(biome.toLowerCase(), duration);
        biomeData.setWorld(world);

        //already has this fake biome
        if (hasExactlyTheSameBiome(player, biomeData)) return;

        //player.setPlayerTime(0, false);
        biomePlayers.put(player.getUniqueId(), biomeData);

        // if world has no biome or (biome contained and biome is not the same)
        //we can set the custom biome
        packets.sendFakeBiome(player);

    }

    public static void removeBiome(Player player) {
        if (!biomePlayers.containsKey(player.getUniqueId())) return;
        biomePlayers.remove(player.getUniqueId());
        //remove custom biome
        packets.sendFakeBiome(player);

    }

    private static boolean hasExactlyTheSameBiome(Player p, BiomeData toCompare) {
        if (biomePlayers.containsKey(p.getUniqueId())) {
            BiomeData bd = biomePlayers.get(p.getUniqueId());
            return bd.getName().equalsIgnoreCase(toCompare.getName())
                    && bd.getDuration() == toCompare.getDuration()
                    && bd.getWorld().equals(p.getWorld());
        }
        return false;
    }


    public static Map<UUID, BiomeData> getBiomePlayers() {
        return biomePlayers;
    }
}

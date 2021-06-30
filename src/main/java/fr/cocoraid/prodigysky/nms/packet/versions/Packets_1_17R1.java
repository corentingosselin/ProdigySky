package fr.cocoraid.prodigysky.nms.packet.versions;

import fr.cocoraid.prodigysky.nms.packet.Packets;
import net.minecraft.core.IRegistry;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.dimension.DimensionManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Packets_1_17R1 implements Packets {

    @Override
    public void sendFakeBiome(Player player) {
        for (Chunk chunk : getChunkAround(player.getLocation().getChunk(), 10)) {
            net.minecraft.world.level.chunk.Chunk c = ((CraftChunk)chunk).getHandle();
            ((CraftPlayer) player).getHandle().b.sendPacket(new PacketPlayOutMapChunk(c));
        }
    }



    private Collection<Chunk> getChunkAround(Chunk origin, int radius) {
        World world = origin.getWorld();

        int length = (radius * 2) + 1;
        Set<Chunk> chunks = new HashSet<>(length * length);

        int cX = origin.getX();
        int cZ = origin.getZ();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                chunks.add(world.getChunkAt(cX + x, cZ + z));
            }
        }
        return chunks;
    }


}

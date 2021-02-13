package fr.cocoraid.prodigysky.nms.packet.versions;

import com.mojang.authlib.GameProfile;
import fr.cocoraid.prodigysky.ProdigySky;
import fr.cocoraid.prodigysky.nms.NMSUtils;
import fr.cocoraid.prodigysky.nms.Reflection;
import fr.cocoraid.prodigysky.nms.packet.Packets;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.defaults.VersionCommand;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.TrackingRange;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Packets_1_16R3 implements Packets {

    private static final Reflection.MethodInvoker refreshChunksMethod = Reflection.getMethod(PlayerChunkMap.class,"a", EntityPlayer.class,boolean.class);


    @Override
    public void sendFakeBiome(Player player) {
        refreshChunksAround(player);
    }

    @Override
    public void setSmog(Player player) {
        EntityPlayer ep = ((CraftPlayer)player).getHandle();
        WorldServer ws = ((CraftWorld)player.getWorld()).getHandle();

        //check already smog or not
        MinecraftKey dimension = (MinecraftKey) NMSUtils.getEffects().get(ws.getDimensionManager());
        if(dimension.getKey().equalsIgnoreCase("the_nether")) return;
        Location l = player.getLocation();
        boolean flag = ws.getGameRules().getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
        boolean flag1 = ws.getGameRules().getBoolean(GameRules.REDUCED_DEBUG_INFO);
        //must resent out login to modify the dimension
        PacketPlayOutLogin login = new PacketPlayOutLogin(ep.getId(), //entity id
                ep.playerInteractManager.getGameMode(), //current gamemode
                ep.playerInteractManager.c(), //previous gamemode
                BiomeManager.a(ws.getSeed()), //seed
                ws.worldData.isHardcore(), //is hardcore
                ep.getMinecraftServer().F(), //ressourceKey world
                ep.getMinecraftServer().customRegistry,
                ep.getWorldServer().getDimensionManager(),
                ws.getDimensionKey(),ep.getMinecraftServer().getMaxPlayers()
                ,ws.spigotConfig.viewDistance,flag1,!flag,ws.isDebugWorld(),ws.isFlatWorld());

        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(ep.getWorldServer().getDimensionManager(), ws.getDimensionKey(), BiomeManager.a(ws.getSeed()), ep.playerInteractManager.getGameMode(), ep.playerInteractManager.c(), ws.isDebugWorld(), ws.isFlatWorld(), true);
        PacketPlayOutPosition position = new PacketPlayOutPosition( l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<>(), 0);
        PlayerConnection con = ep.playerConnection;
        con.sendPacket(login);
        con.sendPacket(respawn);
        ep.updateAbilities();
        con.sendPacket(position);

        ws.getMinecraftServer().getPlayerList().updateClient(ep);
    }


    public void restoreDefaultWorld(Player player) {
        Location l = player.getLocation();
        EntityPlayer ep = ((CraftPlayer)player).getHandle();
        WorldServer ws = ep.getWorldServer();

        boolean flag = ws.getGameRules().getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
        boolean flag1 = ws.getGameRules().getBoolean(GameRules.REDUCED_DEBUG_INFO);
        //must resent out login to modify the dimension
        PacketPlayOutLogin login = new PacketPlayOutLogin(ep.getId(), //entity id
                ep.playerInteractManager.getGameMode(), //current gamemode
                ep.playerInteractManager.c(), //previous gamemode
                BiomeManager.a(ws.getSeed()), //seed
                ws.worldData.isHardcore(), //is hardcore
                ep.getMinecraftServer().F(), //ressourceKey world
                ep.getMinecraftServer().customRegistry,
                ws.getDimensionManager(),
                ws.getDimensionKey(),ep.getMinecraftServer().getMaxPlayers()
                ,ws.spigotConfig.viewDistance,flag1,!flag,ws.isDebugWorld(),ws.isFlatWorld());

        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(ws.getDimensionManager(), ws.getDimensionKey(), BiomeManager.a(ws.getSeed()), ep.playerInteractManager.getGameMode(), ep.playerInteractManager.c(), ws.isDebugWorld(), ws.isFlatWorld(), true);
        PacketPlayOutPosition position = new PacketPlayOutPosition( l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<>(), 0);

        PlayerConnection con = ep.playerConnection;
        con.sendPacket(login);
        con.sendPacket(respawn);
        ep.updateAbilities();
        con.sendPacket(position);
    }


    private void refreshChunksAround(Player player) {
        WorldServer worldServer = ((CraftWorld) player.getWorld()).getHandle();
        EntityPlayer ep = ((CraftPlayer)player).getHandle();

        if(Bukkit.getName().equalsIgnoreCase("paper")) {
            refreshChunksMethod.invoke(worldServer.getChunkProvider().playerChunkMap,ep,false);
            refreshChunksMethod.invoke(worldServer.getChunkProvider().playerChunkMap,ep,true);
        } else {

            new BukkitRunnable() {
                @Override
                public void run() {
                    refreshChunksMethod.invoke(worldServer.getChunkProvider().playerChunkMap, ep, true);
                }
            }.runTaskLaterAsynchronously(ProdigySky.getInstance(), 3L);
        }
    }

}

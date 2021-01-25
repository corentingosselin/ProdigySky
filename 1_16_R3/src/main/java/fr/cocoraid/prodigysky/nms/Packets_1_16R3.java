package fr.cocoraid.prodigysky.nms;

import fr.cocoraid.prodigysky.nms.packet.Packets;

import java.util.HashSet;

import fr.cocoraid.prodigysky.reflection.Reflection;
import net.minecraft.server.v1_16_R3.BiomeManager;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.GameRules;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import net.minecraft.server.v1_16_R3.PacketPlayOutLogin;
import net.minecraft.server.v1_16_R3.PacketPlayOutPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutRespawn;
import net.minecraft.server.v1_16_R3.PlayerChunkMap;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.WorldServer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Packets_1_16R3 implements Packets {

  private static final Reflection.MethodInvoker refreshChunksMethod;
  private final JavaPlugin javaPlugin;

  public Packets_1_16R3(JavaPlugin javaPlugin) {
    this.javaPlugin = javaPlugin;
  }

  public void sendFakeBiome(Player player) {
    this.refreshChunksAround(player);
  }

  public void setSmog(Player player) {
    EntityPlayer ep = ((CraftPlayer) player).getHandle();
    WorldServer ws = ep.getWorldServer();
    MinecraftKey dimension = ws.getDimensionKey().a();
    if (!dimension.getKey().equalsIgnoreCase("the_nether")) {
      Location l = player.getLocation();
      boolean flag = ws.getGameRules().getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
      boolean flag1 = ws.getGameRules().getBoolean(GameRules.REDUCED_DEBUG_INFO);
      PacketPlayOutLogin login = new PacketPlayOutLogin(ep.getId(),
          ep.playerInteractManager.getGameMode(), ep.playerInteractManager.c(),
          BiomeManager.a(ws.getSeed()), ws.worldData.isHardcore(), ep.getMinecraftServer().F(),
          ep.getMinecraftServer().customRegistry, ep.getWorldServer().getDimensionManager(),
          ws.getDimensionKey(), ep.getMinecraftServer().getMaxPlayers(),
          ws.spigotConfig.viewDistance, flag1, !flag, ws.isDebugWorld(), ws.isFlatWorld());
      PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(
          ep.getWorldServer().getDimensionManager(), ws.getDimensionKey(),
          BiomeManager.a(ws.getSeed()), ep.playerInteractManager.getGameMode(),
          ep.playerInteractManager.c(), ws.isDebugWorld(), ws.isFlatWorld(), true);
      PacketPlayOutPosition position = new PacketPlayOutPosition(l.getX(), l.getY(), l.getZ(),
          l.getYaw(), l.getPitch(), new HashSet<>(), 0);
      PlayerConnection con = ep.playerConnection;
      con.sendPacket(login);
      con.sendPacket(respawn);
      ep.updateAbilities();
      con.sendPacket(position);
    }
  }

  public void restoreDefaultWorld(Player player) {
    Location l = player.getLocation();
    EntityPlayer ep = ((CraftPlayer) player).getHandle();
    WorldServer ws = ep.getWorldServer();
    boolean flag = ws.getGameRules().getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
    boolean flag1 = ws.getGameRules().getBoolean(GameRules.REDUCED_DEBUG_INFO);
    PacketPlayOutLogin login = new PacketPlayOutLogin(ep.getId(),
        ep.playerInteractManager.getGameMode(), ep.playerInteractManager.c(),
        BiomeManager.a(ws.getSeed()), ws.worldData.isHardcore(), ep.getMinecraftServer().F(),
        ep.getMinecraftServer().customRegistry, ep.getWorldServer().getDimensionManager(),
        ws.getDimensionKey(), ep.getMinecraftServer().getMaxPlayers(), ws.spigotConfig.viewDistance,
        flag1, !flag, ws.isDebugWorld(), ws.isFlatWorld());
    PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(
        ep.getWorldServer().getDimensionManager(), ws.getDimensionKey(),
        BiomeManager.a(ws.getSeed()), ep.playerInteractManager.getGameMode(),
        ep.playerInteractManager.c(), ws.isDebugWorld(), ws.isFlatWorld(), true);
    PacketPlayOutPosition position = new PacketPlayOutPosition(l.getX(), l.getY(), l.getZ(),
        l.getYaw(), l.getPitch(), new HashSet<>(), 0);
    PlayerConnection con = ep.playerConnection;
    con.sendPacket(login);
    con.sendPacket(respawn);
    ep.updateAbilities();
    con.sendPacket(position);
  }

  private void refreshChunksAround(Player player) {
    final WorldServer worldServer = ((CraftWorld) player.getWorld()).getHandle();
    final EntityPlayer ep = ((CraftPlayer) player).getHandle();
    (new BukkitRunnable() {
      public void run() {
        Packets_1_16R3.refreshChunksMethod
            .invoke(worldServer.getChunkProvider().playerChunkMap, ep, true);
      }
    }).runTaskLater(javaPlugin, 3L);
  }

  static {
    refreshChunksMethod = Reflection
        .getMethod(PlayerChunkMap.class, "a", EntityPlayer.class, Boolean.TYPE);
  }
}

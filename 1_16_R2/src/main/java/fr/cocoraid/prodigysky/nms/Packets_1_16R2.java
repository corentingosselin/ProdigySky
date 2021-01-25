package fr.cocoraid.prodigysky.nms;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.cocoraid.prodigysky.nms.packet.Packets;
import fr.cocoraid.prodigysky.reflection.Reflection;
import net.minecraft.server.v1_16_R2.BiomeManager;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import net.minecraft.server.v1_16_R2.GameRules;
import net.minecraft.server.v1_16_R2.MinecraftKey;
import net.minecraft.server.v1_16_R2.PacketPlayOutLogin;
import net.minecraft.server.v1_16_R2.PacketPlayOutPosition;
import net.minecraft.server.v1_16_R2.PacketPlayOutRespawn;
import net.minecraft.server.v1_16_R2.PlayerChunkMap;
import net.minecraft.server.v1_16_R2.PlayerConnection;
import net.minecraft.server.v1_16_R2.WorldServer;

public class Packets_1_16R2 implements Packets {

  private static final Reflection.MethodInvoker refreshChunksMethod;
  private final JavaPlugin javaPlugin;

  public Packets_1_16R2(JavaPlugin javaPlugin) {
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
        Packets_1_16R2.refreshChunksMethod
            .invoke(worldServer.getChunkProvider().playerChunkMap, ep, true);
      }
    }).runTaskLater(javaPlugin, 3L);
  }

  static {
    refreshChunksMethod = Reflection
        .getMethod(PlayerChunkMap.class, "a", EntityPlayer.class, Boolean.TYPE);
  }
}

package fr.cocoraid.prodigysky.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import fr.cocoraid.prodigysky.ProdigySky;
import fr.cocoraid.prodigysky.nms.NMSUtils;
import fr.prodigysky.api.EffectDuration;
import fr.prodigysky.api.ProdigySkyAPI;
import fr.prodigysky.api.SkyEffect;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;

@CommandAlias("prodigysky|pgs")
public class MainCMD extends BaseCommand {

    private ProdigySky instance;
    public MainCMD(ProdigySky instance) {
        this.instance = instance;
    }




    @Default
    @CommandPermission("pgs.help")
    @Description("Show Help menu")
    public static void help(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage("§5Prodigy§bSky §fhelp:");
            player.sendMessage("    §2- §5Set your own sky: §f/pgs set <biomeTemplate> <effect> <duration> ");
            player.sendMessage("    §2- §5Set your own sky: §f/pgs setplayer <biomeTemplate> <player> <effect> <duration> ");
            player.sendMessage("    §2- §5Set your own sky: §f/pgs setworld <biomeTemplate> <world> <effect> <duration> ");
            player.sendMessage("  ");
            player.sendMessage("    §4- §5Remove sky color for you: §f/pgs remove");
            player.sendMessage("    §4- §5Remove sky color for player: §f/pgs removeplayer <player>");
            player.sendMessage("    §4- §5Remove sky color for world: §f/pgs removeworld <world>");

        }
    }

    @CommandPermission("pgs.set")
    @CommandCompletion("@biomeName")
    @Subcommand("set")
    public void set(@Conditions("playerWorldEnabled") Player player, String biomeName, SkyEffect skyEffect, EffectDuration duration) {
        ProdigySkyAPI.setBiome(player,biomeName, skyEffect == SkyEffect.SMOG, duration);
    }

    @CommandPermission("pgs.set.other")
    @CommandCompletion("@biomeName @players")
    @Subcommand("setplayer")
    public void setPlayer(CommandSender sender, String biomeName, String target, SkyEffect skyEffect, EffectDuration duration) {
        if(Bukkit.getPlayer(target) == null || !Bukkit.getPlayer(target).isOnline()) {
            sender.sendMessage("§cThe player " + target + " does not exist !");
            return;
        }

        Player player = Bukkit.getPlayer(target);
        if(!instance.getConfiguration().getEnabledWorlds().contains(player.getWorld())) {
            sender.sendMessage("§cThe player " + player.getName() + " is not inside a world where the sky color is enabled");
            return;
        }

        ProdigySkyAPI.setBiome(player,biomeName, skyEffect == SkyEffect.SMOG, duration);
    }

    @CommandPermission("pgs.set.world")
    @CommandCompletion("@biomeName")
    @Subcommand("setworld")
    public void setWorld(CommandSender sender, String biomeName, @Conditions("worldEnabled") World world, SkyEffect skyEffect, EffectDuration duration) {
        ProdigySkyAPI.setBiome(world,biomeName, skyEffect == SkyEffect.SMOG, duration);
    }

    @CommandPermission("pgs.remove.worlds")
    @CommandCompletion("@worlds")
    @Subcommand("removeworld")
    public void removeWorld(CommandSender sender, String w) {
        if(Bukkit.getWorld(w) == null) {
            sender.sendMessage("§cWorld " + w + " not found !");
            return;
        }
        ProdigySkyAPI.removeBiome(Bukkit.getWorld(w));
    }

    @CommandPermission("pgs.remove")
    @Subcommand("remove")
    public void remove(Player sender) {
        ProdigySkyAPI.removeBiome(sender);
    }

    @CommandPermission("pgs.remove.other")
    @CommandCompletion("@players")
    @Subcommand("removeplayer")
    public void remove(CommandSender sender, String target) {
        if(Bukkit.getPlayer(target) == null || !Bukkit.getPlayer(target).isOnline()) {
            sender.sendMessage("§cThe player " + target + " does not exist !");
            return;
        }
        ProdigySkyAPI.removeBiome(Bukkit.getPlayer(target));
    }


    @Subcommand("test")
    public void test(Player player) {
        Location l = player.getLocation();
        EntityPlayer ep = ((CraftPlayer)player).getHandle();
        WorldServer ws = ep.getWorldServer();


        DimensionManager dm = (DimensionManager) NMSUtils.cloneDimension(ws.getDimensionManager(),true);
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
                dm,
                ws.getDimensionKey(),ep.getMinecraftServer().getMaxPlayers()
                ,ws.spigotConfig.viewDistance,flag1,!flag,ws.isDebugWorld(),ws.isFlatWorld());

        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(ws.getDimensionManager(), ws.getDimensionKey(), BiomeManager.a(ws.getSeed()), ep.playerInteractManager.getGameMode(), ep.playerInteractManager.c(), ws.isDebugWorld(), ws.isFlatWorld(), true);
        PacketPlayOutPosition position = new PacketPlayOutPosition( l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<>(), 0);

        PlayerConnection con = ep.playerConnection;
        con.sendPacket(login);
        con.sendPacket(respawn);
        ep.updateAbilities();
        con.sendPacket(position);

        instance.getNMS().getPackets().sendFakeBiome(player);
    }

}

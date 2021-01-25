package fr.cocoraid.prodigysky.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import fr.cocoraid.prodigysky.ProdigySky;
import fr.prodigysky.api.EffectDuration;
import fr.prodigysky.api.ProdigySkyAPI;
import fr.prodigysky.api.SkyEffect;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("prodigysky|pgs")
public class MainCMD extends BaseCommand {
   private final ProdigySky instance;
   private final ProdigySkyAPI prodigySkyAPI;

   public MainCMD(ProdigySky instance, ProdigySkyAPI prodigySkyAPI) {
      this.instance = instance;
      this.prodigySkyAPI = prodigySkyAPI;
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
      prodigySkyAPI.setBiome(player, biomeName, skyEffect == SkyEffect.SMOG, duration);
   }

   @CommandPermission("pgs.set.other")
   @CommandCompletion("@biomeName @players")
   @Subcommand("setplayer")
   public void setPlayer(CommandSender sender, String biomeName, String target, SkyEffect skyEffect, EffectDuration duration) {
      if (Bukkit.getPlayer(target) != null && Bukkit.getPlayer(target).isOnline()) {
         Player player = Bukkit.getPlayer(target);
         if (!this.instance.getConfiguration().getEnabledWorlds().contains(player.getWorld())) {
            sender.sendMessage("§cThe player " + player.getName() + " is not inside a world where the sky color is enabled");
         } else {
            prodigySkyAPI.setBiome(player, biomeName, skyEffect == SkyEffect.SMOG, duration);
         }
      } else {
         sender.sendMessage("§cThe player " + target + " does not exist !");
      }
   }

   @CommandPermission("pgs.set.world")
   @CommandCompletion("@biomeName")
   @Subcommand("setworld")
   public void setWorld(CommandSender sender, String biomeName, @Conditions("worldEnabled") World world, SkyEffect skyEffect, EffectDuration duration) {
      prodigySkyAPI.setBiome(world, biomeName, skyEffect == SkyEffect.SMOG, duration);
   }

   @CommandPermission("pgs.remove.worlds")
   @CommandCompletion("@worlds")
   @Subcommand("removeworld")
   public void removeWorld(CommandSender sender, String w) {
      if (Bukkit.getWorld(w) == null) {
         sender.sendMessage("§cWorld " + w + " not found !");
      } else {
         prodigySkyAPI.removeBiome(Bukkit.getWorld(w));
      }
   }

   @CommandPermission("pgs.remove")
   @Subcommand("remove")
   public void remove(Player sender) {
      prodigySkyAPI.removeBiome(sender);
   }

   @CommandPermission("pgs.remove.other")
   @CommandCompletion("@players")
   @Subcommand("removeplayer")
   public void remove(CommandSender sender, String target) {
      if (Bukkit.getPlayer(target) != null && Bukkit.getPlayer(target).isOnline()) {
         prodigySkyAPI.removeBiome(Bukkit.getPlayer(target));
      } else {
         sender.sendMessage("§cThe player " + target + " does not exist !");
      }
   }
}

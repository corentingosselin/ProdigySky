package fr.cocoraid.prodigysky.listeners;

import fr.cocoraid.prodigysky.ProdigySky;
import fr.cocoraid.prodigysky.feature.BiomeData;
import fr.prodigysky.api.EffectDuration;
import fr.prodigysky.api.ProdigySkyAPI;

import java.util.Map;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EventListener implements Listener {

  private final ProdigySky prodigySky;
  private final ProdigySkyAPI api;
  private final Map<UUID, World> changeWorlds;

  public EventListener(ProdigySky prodigySky, ProdigySkyAPI api,
      Map<UUID, World> changeWorlds) {
    this.prodigySky = prodigySky;
    this.api = api;
    this.changeWorlds = changeWorlds;
  }


  @EventHandler
  public void join(PlayerJoinEvent e) {
    if (e.getPlayer().hasPermission("pgs.admin") && prodigySky.getCustomBiomes()
        .isMissingDatapacks()) {
      e.getPlayer().sendMessage(
          "§d[ProdigySky] §fSome custom biomes added in ProdigySky/custombiomes folder are not loaded by the server");
      e.getPlayer().sendMessage(
          "               §fPlease restart the server, to load the new biomes thanks...");
    }

  }

  @EventHandler
  public void changeWorldHook(PlayerTeleportEvent e) {
    if (!e.getTo().getWorld().equals(e.getFrom().getWorld())) {
      changeWorlds.put(e.getPlayer().getUniqueId(), e.getTo().getWorld());
      this.removeVolatileWorldPlayer(e.getFrom().getWorld(), e.getPlayer().getUniqueId());
    }
  }

  private void removeVolatileWorldPlayer(World w, UUID uuid) {
    if (api.getBiomeWorlds().containsKey(w)) {
      BiomeData bd = api.getBiomeData(w);
      if (bd.getDuration() == EffectDuration.VOLATILE && bd.getTempPlayers().contains(uuid)) {
        bd.getTempPlayers().remove(uuid);
        if (bd.getTempPlayers().isEmpty()) {
          api.getBiomeWorlds().remove(w);
        }
      }
    }

  }

  @EventHandler
  public void leave(PlayerQuitEvent e) {
    UUID uuid = e.getPlayer().getUniqueId();
    World w = e.getPlayer().getWorld();
    this.removeVolatileWorldPlayer(w, uuid);
    if (api.getBiomePlayer().containsKey(uuid)
        && api.getBiomePlayer().get(uuid).getDuration() == EffectDuration.VOLATILE) {
      api.getBiomePlayer().remove(uuid);
    }

    if (api.getBiomePlayer().containsKey(uuid)
        && api.getBiomeData(uuid).getDuration() == EffectDuration.VOLATILE) {
      api.getBiomePlayer().remove(uuid);
    }

  }
}

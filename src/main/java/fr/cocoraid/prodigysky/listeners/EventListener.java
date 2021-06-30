package fr.cocoraid.prodigysky.listeners;

import fr.prodigysky.api.EffectDuration;
import fr.prodigysky.api.ProdigySkyAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {




    @EventHandler
    public void leave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(ProdigySkyAPI.getBiomePlayers().containsKey(p.getUniqueId())) {
            if(ProdigySkyAPI.getBiomePlayers().get(p.getUniqueId()).getDuration() == EffectDuration.VOLATILE) {
                ProdigySkyAPI.getBiomePlayers().remove(p.getUniqueId());
            }
        }

    }
}

package fr.cocoraid.prodigysky.nms.packet;

import org.bukkit.entity.Player;

public interface Packets {
   void sendFakeBiome(Player var1);

   void setSmog(Player var1);

   void restoreDefaultWorld(Player var1);
}

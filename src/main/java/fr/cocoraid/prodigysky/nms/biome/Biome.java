package fr.cocoraid.prodigysky.nms.biome;

import net.minecraft.world.level.biome.BiomeBase;

public interface Biome {

     BiomeBase build(String fogColor, String waterColor, String waterFogColor, String skyColor,String grassColor, String foliageColor);

    BiomeBase build(String fogColor,String waterColor,String waterFogColor,String skyColor);
}

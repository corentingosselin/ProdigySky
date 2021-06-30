package fr.cocoraid.prodigysky.nms.biome;

import com.mojang.serialization.Codec;
import net.minecraft.data.worldgen.BiomeDecoratorGroups;
import net.minecraft.data.worldgen.WorldGenSurfaceComposites;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeFog;
import net.minecraft.world.level.biome.BiomeSettingsGeneration;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.levelgen.WorldGenStage;

public class BiomeBaseWrapper_1_17R1 implements Biome {


    private String fogColor, waterColor, waterFogColor, skyColor;
    private String grassColor,foliageColor;

    @Override
    public BiomeBase build(String fogColor, String waterColor, String waterFogColor, String skyColor) {
        return build(fogColor,waterColor,waterFogColor,skyColor,null,null);
    }

    @Override
    public BiomeBase build(String fogColor, String waterColor, String waterFogColor, String skyColor,String grassColor, String foliageColor) {
        this.fogColor = fogColor;
        this.waterColor = waterColor;
        this.waterFogColor = waterFogColor;
        this.skyColor = skyColor;
        this.grassColor = grassColor;
        this.foliageColor = foliageColor;
        return build();
    }



    public BiomeBase build() {
        //void generation
        BiomeSettingsGeneration.a gen = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.p);
        gen.a(WorldGenStage.Decoration.j, BiomeDecoratorGroups.W);

        BiomeFog.a biomeFogCodec = new BiomeFog.a()
                .a(Integer.parseInt(fogColor, 16)) //fog color
                .b(Integer.parseInt(waterColor, 16)) //water color
                .c(Integer.parseInt(waterFogColor, 16)) //water fog color
                .d(Integer.parseInt(skyColor, 16)); //skycolor
                //.e() //foliage color (leaves, fines and more)
                //.f() //grass blocks color
                //.a(BiomeParticle)
                //a(Music)

        if(foliageColor != null)
            biomeFogCodec.e(Integer.parseInt(foliageColor, 16));
        if(grassColor != null)
            biomeFogCodec.f(Integer.parseInt(grassColor, 16));

        return new BiomeBase.a()
                .a(BiomeBase.Precipitation.a) //none
                .a(BiomeBase.Geography.a) //none
                .a(0F) //depth ocean or not // var3 ? -1.8F : -1.0F
                .b(0F) //scale Lower values produce flatter terrain
                .c(0F) //temperature
                .d(0F) //maybe important, foliage and grass color
                .a(biomeFogCodec.a()) //biomefog
                .a(BiomeSettingsMobs.c) //same as void biome
                .a(gen.a()) //same as void biome
                .a();
    }


}

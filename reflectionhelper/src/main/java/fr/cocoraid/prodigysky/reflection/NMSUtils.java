package fr.cocoraid.prodigysky.reflection;

import com.comphenix.protocol.utility.MinecraftReflection;
import java.util.OptionalLong;

public class NMSUtils {
   private static final Class<?> minecraftKeyClass = MinecraftReflection.getMinecraftKeyClass();
   private static final Class<?> dimensionManagerClass = MinecraftReflection.getMinecraftClass("DimensionManager");
   private static final Class<?> genLayerZoomClass = MinecraftReflection.getMinecraftClass("GenLayerZoomer");
   private static final Reflection.ConstructorInvoker dmConstructor;
   private static final Reflection.ConstructorInvoker minecraftKeyConstructor;
   private static final Reflection.FieldAccessor fixedTime;
   private static final Reflection.FieldAccessor hasSkylight;
   private static final Reflection.FieldAccessor hasCeiling;
   private static final Reflection.FieldAccessor ultraWarm;
   private static final Reflection.FieldAccessor natural;
   private static final Reflection.FieldAccessor coordinateScale;
   private static final Reflection.FieldAccessor createDragonBattle;
   private static final Reflection.FieldAccessor piglinSafe;
   private static final Reflection.FieldAccessor bedWorks;
   private static final Reflection.FieldAccessor respawnAnchorWorks;
   private static final Reflection.FieldAccessor hasRaids;
   private static final Reflection.FieldAccessor logicalHeight;
   private static final Reflection.FieldAccessor genLayerZoomer;
   private static final Reflection.FieldAccessor infiniBurn;
   private static final Reflection.FieldAccessor effects;
   private static final Reflection.FieldAccessor ambientLight;

   public static Object cloneDimension(Object originalDimension, boolean smog) {
      return dmConstructor.invoke(fixedTime.get(originalDimension), hasSkylight.get(originalDimension), hasCeiling.get(originalDimension), ultraWarm.get(originalDimension), natural.get(originalDimension), coordinateScale.get(originalDimension), createDragonBattle.get(originalDimension), piglinSafe.get(originalDimension), bedWorks.get(originalDimension), respawnAnchorWorks.get(originalDimension), hasRaids.get(originalDimension), logicalHeight.get(originalDimension), genLayerZoomer.get(originalDimension), infiniBurn.get(originalDimension), smog ? minecraftKeyConstructor.invoke("the_nether") : minecraftKeyConstructor.invoke("overworld"), ambientLight.get(originalDimension));
   }

   public static Reflection.FieldAccessor getEffects() {
      return effects;
   }

   static {
      dmConstructor = Reflection.getConstructor(dimensionManagerClass, OptionalLong.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Double.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Integer.TYPE, genLayerZoomClass, minecraftKeyClass, minecraftKeyClass, Float.TYPE);
      minecraftKeyConstructor = Reflection.getConstructor(minecraftKeyClass, String.class);
      fixedTime = Reflection.getField(dimensionManagerClass, "fixedTime", OptionalLong.class);
      hasSkylight = Reflection.getField(dimensionManagerClass, "hasSkylight", Boolean.TYPE);
      hasCeiling = Reflection.getField(dimensionManagerClass, "hasCeiling", Boolean.TYPE);
      ultraWarm = Reflection.getField(dimensionManagerClass, "ultraWarm", Boolean.TYPE);
      natural = Reflection.getField(dimensionManagerClass, "natural", Boolean.TYPE);
      coordinateScale = Reflection.getField(dimensionManagerClass, "coordinateScale", Double.TYPE);
      createDragonBattle = Reflection.getField(dimensionManagerClass, "createDragonBattle", Boolean.TYPE);
      piglinSafe = Reflection.getField(dimensionManagerClass, "piglinSafe", Boolean.TYPE);
      bedWorks = Reflection.getField(dimensionManagerClass, "bedWorks", Boolean.TYPE);
      respawnAnchorWorks = Reflection.getField(dimensionManagerClass, "respawnAnchorWorks", Boolean.TYPE);
      hasRaids = Reflection.getField(dimensionManagerClass, "hasRaids", Boolean.TYPE);
      logicalHeight = Reflection.getField(dimensionManagerClass, "logicalHeight", Integer.TYPE);
      genLayerZoomer = Reflection.getField(dimensionManagerClass, "genLayerZoomer", genLayerZoomClass);
      infiniBurn = Reflection.getField(dimensionManagerClass, "infiniburn", minecraftKeyClass);
      effects = Reflection.getField(dimensionManagerClass, "effects", minecraftKeyClass);
      ambientLight = Reflection.getField(dimensionManagerClass, "ambientLight", Float.TYPE);
   }
}

package fr.cocoraid.prodigysky.nms;

import com.comphenix.protocol.utility.MinecraftReflection;
import net.minecraft.server.v1_16_R3.MinecraftKey;


import java.util.OptionalLong;

public class NMSUtils {

    private static final Class<?> minecraftKeyClass = MinecraftReflection.getMinecraftKeyClass();
    private static final Class<?> dimensionManagerClass = MinecraftReflection.getMinecraftClass("DimensionManager");
    private static final Class<?> genLayerZoomClass = MinecraftReflection.getMinecraftClass("GenLayerZoomer");


    private static final Reflection.ConstructorInvoker dmConstructor = Reflection.getConstructor(dimensionManagerClass,
            OptionalLong.class,boolean.class,boolean.class,boolean.class,boolean.class
            ,double.class,boolean.class,boolean.class,boolean.class,boolean.class,boolean.class,int.class, genLayerZoomClass,minecraftKeyClass,minecraftKeyClass,float.class);
    private static Reflection.ConstructorInvoker minecraftKeyConstructor = Reflection.getConstructor(minecraftKeyClass,String.class);


    private static final Reflection.FieldAccessor fixedTime = Reflection.getField(dimensionManagerClass,"fixedTime",OptionalLong.class);
    private static final Reflection.FieldAccessor hasSkylight = Reflection.getField(dimensionManagerClass,"hasSkylight",boolean.class);
    private static final Reflection.FieldAccessor hasCeiling = Reflection.getField(dimensionManagerClass,"hasCeiling",boolean.class);
    private static final Reflection.FieldAccessor ultraWarm = Reflection.getField(dimensionManagerClass,"ultraWarm",boolean.class);
    private static final Reflection.FieldAccessor natural = Reflection.getField(dimensionManagerClass,"natural",boolean.class);
    private static final Reflection.FieldAccessor coordinateScale = Reflection.getField(dimensionManagerClass,"coordinateScale",double.class);
    private static final Reflection.FieldAccessor createDragonBattle = Reflection.getField(dimensionManagerClass,"createDragonBattle",boolean.class);
    private static final Reflection.FieldAccessor piglinSafe = Reflection.getField(dimensionManagerClass,"piglinSafe",boolean.class);
    private static final Reflection.FieldAccessor bedWorks = Reflection.getField(dimensionManagerClass,"bedWorks",boolean.class);
    private static final Reflection.FieldAccessor respawnAnchorWorks = Reflection.getField(dimensionManagerClass,"respawnAnchorWorks",boolean.class);
    private static final Reflection.FieldAccessor hasRaids = Reflection.getField(dimensionManagerClass,"hasRaids",boolean.class);
    private static final Reflection.FieldAccessor logicalHeight = Reflection.getField(dimensionManagerClass,"logicalHeight",int.class);
    private static final Reflection.FieldAccessor genLayerZoomer = Reflection.getField(dimensionManagerClass,"genLayerZoomer",genLayerZoomClass);
    private static final Reflection.FieldAccessor infiniBurn = Reflection.getField(dimensionManagerClass,"infiniburn",minecraftKeyClass);
    private static final Reflection.FieldAccessor effects = Reflection.getField(dimensionManagerClass,"effects",minecraftKeyClass);
    private static final Reflection.FieldAccessor ambientLight = Reflection.getField(dimensionManagerClass,"ambientLight",float.class);

    public static Object cloneDimension(Object originalDimension, boolean smog) {
        return  dmConstructor.invoke(
                fixedTime.get(originalDimension)
                ,hasSkylight.get(originalDimension)
                ,hasCeiling.get(originalDimension)
                ,ultraWarm.get(originalDimension)
                ,natural.get(originalDimension)
                ,coordinateScale.get(originalDimension)
                ,createDragonBattle.get(originalDimension)
                ,piglinSafe.get(originalDimension)
                ,bedWorks.get(originalDimension),
                respawnAnchorWorks.get(originalDimension),
                hasRaids.get(originalDimension),
                logicalHeight.get(originalDimension),
                genLayerZoomer.get(originalDimension),
                infiniBurn.get(originalDimension),
                smog ?  minecraftKeyConstructor.invoke("the_nether") :  minecraftKeyConstructor.invoke("overworld"),
                ambientLight.get(originalDimension));
    }

    public static Reflection.FieldAccessor getEffects() {
        return effects;
    }
}

package fr.cocoraid.prodigysky.feature;

import fr.prodigysky.api.EffectDuration;
import org.bukkit.World;

public class BiomeData {

    private String name;
    private EffectDuration duration;
    private World world;

    public BiomeData(String name, EffectDuration duration) {
        this.name = name;
        this.duration = duration;
    }

    public void setWorld(World world) {
        this.world = world;
    }


    public String getName() {
        return name;
    }

    public EffectDuration getDuration() {
        return duration;
    }

    public World getWorld() {
        return world;
    }
}

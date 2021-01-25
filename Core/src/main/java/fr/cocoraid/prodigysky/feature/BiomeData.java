package fr.cocoraid.prodigysky.feature;

import fr.prodigysky.api.EffectDuration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BiomeData {

    private String name;
    private EffectDuration duration;
    private boolean smog;

    private List<UUID> tempPlayers = new ArrayList<>();

    public BiomeData(String name, EffectDuration duration) {
        this.name = name;
        this.duration = duration;
    }

    public void setSmog(boolean smog) {
        this.smog = smog;
    }

    public String getName() {
        return name;
    }

    public EffectDuration getDuration() {
        return duration;
    }

    public boolean isSmog() {
        return smog;
    }

    public List<UUID> getTempPlayers() {
        return tempPlayers;
    }
}

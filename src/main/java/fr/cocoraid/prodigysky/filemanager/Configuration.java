package fr.cocoraid.prodigysky.filemanager;

import com.mojang.serialization.Lifecycle;
import fr.cocoraid.prodigysky.ProdigySky;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.biome.BiomeBase;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Configuration {

    private Map<String, Integer> biomes = new HashMap<>();
    private static final Pattern HEX_PATTERN = Pattern.compile("^([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");

    private List<World> enabledWorlds = new ArrayList<>();

    private ProdigySky instance;
    public Configuration(ProdigySky plugin) {
        this.instance = plugin;
    }

    public Configuration init() {
        File file = new File(instance.getDataFolder(), "configuration.yml");
        if (!file.exists()) {
            instance.saveResource("configuration.yml", false);

        }
        return this;
    }


    private static final String ENABLED_WORLD = "enabled-worlds";
    public void load() {
        ConsoleCommandSender cc = Bukkit.getConsoleSender();

        File file = new File(instance.getDataFolder() , "configuration.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);

        if(data.isSet(ENABLED_WORLD)) {
            List<String> worlds = data.getStringList(ENABLED_WORLD);
            for (String world : worlds) {
                World w = Bukkit.getWorld(world);
                if(w  == null) {
                    cc.sendMessage("§6[ProdigySky Warning] §4 " + world + " §cdoes not exist for enabled-world in configuration.yml");
                    continue;
                }
                enabledWorlds.add(w);
            }
        }

        DedicatedServer ds = ((CraftServer)Bukkit.getServer()).getHandle().getServer();
        ConfigurationSection section = data.getConfigurationSection("custom_colors");

        if(data.isSet("custom_colors")) {
            for (String colorName : section.getKeys(false)) {
                if (biomes.containsKey(colorName)) {
                    Bukkit.getLogger().log(Level.WARNING, "Color name " + colorName + " already exists !");
                    continue;
                }
                ResourceKey<BiomeBase> newKey = ResourceKey.a(IRegistry.aO, new MinecraftKey(colorName));

                String key = colorName + ".";
                String fog = section.getString(key + "fog");
                String sky = section.getString(key + "sky");
                String water = section.getString(key + "water");
                String waterFog = section.getString(key + "water_fog");
                String foliage = section.getString(key + "foliage");
                String grass = section.getString(key + "grass");

                if (!isHex(fog)) continue;
                if (!isHex(sky)) continue;
                if (!isHex(water)) continue;
                if (!isHex(waterFog)) continue;

                if (foliage != null && !isHex(foliage)) continue;
                if (grass != null && !isHex(grass)) continue;


                BiomeBase biomeBase = instance.getNMS().getBiome()
                        .build(fog,
                                water,
                                waterFog,
                                sky,
                                grass,
                                foliage);

                IRegistryWritable<BiomeBase> rw = ds.getCustomRegistry().b(IRegistry.aO);
                rw.a(newKey, biomeBase, Lifecycle.stable());
                int id = ds.getCustomRegistry().d(IRegistry.aO).getId(biomeBase);
                biomes.put(colorName.toLowerCase(), id);
            }
        }
    }

    private boolean isHex(String hex) {
        Matcher matcher = HEX_PATTERN.matcher(hex);
        boolean match = matcher.matches();
        if(!match) Bukkit.getLogger().log(Level.WARNING, " Color with hex " + hex + " is invalid");
        return match;
    }


    public Map<String, Integer> getBiomes() {
        return biomes;
    }

    public List<World> getEnabledWorlds() {
        return enabledWorlds;
    }
}

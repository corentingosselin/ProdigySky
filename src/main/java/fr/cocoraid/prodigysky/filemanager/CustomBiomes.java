package fr.cocoraid.prodigysky.filemanager;

import fr.cocoraid.prodigysky.ProdigySky;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CustomBiomes {


    private boolean missingDatapacks;
    private ProdigySky instance;
    public CustomBiomes(ProdigySky instance)  {
        this.instance = instance;
        ConsoleCommandSender cc = Bukkit.getConsoleSender();

        loadDefaults();

        List<String> biomesWaiting = new ArrayList<>();
        File biomeFolder = new File(instance.getDataFolder().getPath() + "/custom_biomes");
        for (File file : biomeFolder.listFiles()) {
            if(file.getName().endsWith(".json")) {
                biomesWaiting.add(file.getName().replace(".json","").toLowerCase());
                placeBiomeForWorlds(file);
            }
        }

        registerCustomBiomes();

        cleanup(biomesWaiting);

        if(biomesWaiting.stream().anyMatch(b -> !biomes.containsKey(b))) {
            cc.sendMessage("§6[ProdigySky Warning] §cOne or more biomes are not yet loaded by the server... Please restart the server (dont't reload)");
            this.missingDatapacks = true;
        }
    }


    private Map<String, Integer> biomes = new HashMap<>();
    private void registerCustomBiomes() {
        biomes = instance.getNMS().getBiomes().getBiomes();
    }


    private void placeBiomeForWorlds(File biome) {
        for (World w : instance.getConfiguration().getEnabledWorlds()) {
            final String DATAPACK_PATH = w.getWorldFolder().getPath() + "/datapacks/prodigysky/data/minecraft/worldgen/biome/prodigysky";
            File folder = new File(DATAPACK_PATH);
            if(!folder.exists())
                folder.mkdirs();

            File metaFile = new File(w.getWorldFolder().getPath() + "/datapacks/prodigysky/pack.mcmeta");
            if(!metaFile.exists()) {
                InputStream is = instance.getResource("pack.mcmeta");
                try {
                    FileUtils.copyInputStreamToFile(is, metaFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            try {
                FileUtils.copyFileToDirectory(biome, folder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private void loadDefaults() {
        File customBiomeFolder = new File(instance.getDataFolder().getPath() + "/custom_biomes");
        if(customBiomeFolder.exists()) return;

        final String path = "examples";
        final File jarFile = new File(instance.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

        final JarFile jar;
        try {
            jar = new JarFile(jarFile);
            final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            while(entries.hasMoreElements()) {
                final String name = entries.nextElement().getName();
                if (name.startsWith(path + "/") && !name.endsWith("/")) { //filter according to the path
                    instance.saveResource(name,false);
                }
            }
            jar.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        File sourceFile = new File(instance.getDataFolder().getPath() + "/examples");
        sourceFile.renameTo(customBiomeFolder);
    }

    private void cleanup(List<String> customBiomes) {
        //read all worlds
        for (World world : Bukkit.getWorlds()) {
            //list prodisky datapacks
            File prskyDatapack = new File(world.getWorldFolder().getPath() + "/datapacks/prodigysky");
            if(prskyDatapack.exists()) {

                if(!instance.getConfiguration().getEnabledWorlds().contains(world)) {
                    try {
                        FileUtils.deleteDirectory(prskyDatapack);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                final String DATAPACK_PATH = world.getWorldFolder().getPath() + "/datapacks/prodigysky/data/minecraft/worldgen/biome/prodigysky";
                File folder = new File(DATAPACK_PATH);
                if(folder.exists()) {
                    for (File biome : folder.listFiles()) {
                        if (biome.getName().endsWith(".json")) {
                            String name = biome.getName().replace(".json", "").toLowerCase();
                            if (!customBiomes.contains(name)) {
                                try {
                                    FileUtils.forceDelete(biome);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

            }
        }
    }


    public Map<String, Integer> getBiomes() {
        return biomes;
    }

    public boolean isMissingDatapacks() {
        return missingDatapacks;
    }
}

package fr.cocoraid.prodigysky;

import co.aikar.commands.PaperCommandManager;
import fr.cocoraid.prodigysky.commands.MainCMD;
import fr.cocoraid.prodigysky.filemanager.Configuration;
import fr.cocoraid.prodigysky.listeners.EventListener;
import fr.cocoraid.prodigysky.listeners.PacketListener;
import fr.cocoraid.prodigysky.nms.NMS;
import fr.prodigysky.api.EffectDuration;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProdigySky extends JavaPlugin {



    private PaperCommandManager manager;
    private static ProdigySky instance;
    private NMS nms;
    private Configuration configuration;

    @Override
    public void onEnable() {

        displayBanner();

        instance = this;
        nms = new NMS(this);
        this.configuration = new Configuration(this);
        configuration.init();
        configuration.load();

        loadCommands();

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        new PacketListener(this);

    }


    @Override
    public void onDisable() {

    }


    private void displayBanner() {
        ConsoleCommandSender cc = Bukkit.getConsoleSender();


        cc.sendMessage("§5" + " ______               _ _                 §b______ _");
        cc.sendMessage("§5" + "(_____ \\             | (_)               §b/ _____) |");
        cc.sendMessage("§5" + " _____) )___ ___   __| |_  ____ _   _   §b( (____ | |  _ _   _ ");
        cc.sendMessage("§5" + "|  ____/ ___) _ \\ / _  | |/ _  | | | |   §b\\____ \\| |_/ ) | | |");
        cc.sendMessage("§5" + "| |   | |  | |_| ( (_| | ( (_| | |_| |  §b _____) )  _ (| |_| |");
        cc.sendMessage("§5" + "|_|   |_|   \\___/ \\____|_|\\___ |\\__  |§b  (______/|_| \\_)\\__  |");
        cc.sendMessage("§5" + "                          (_____(____/      §b           (____/");
        cc.sendMessage("§d" + " The prodigy is the man who knows how to shape the sky");

    }

    private void loadCommands() {
        this.manager = new PaperCommandManager(this);

        manager.getCommandCompletions().registerAsyncCompletion("biomeName", c -> {
            return getConfiguration().getBiomes().keySet();
        });

        manager.getCommandCompletions().registerAsyncCompletion("effectDuration", c -> {
            return Arrays.stream(EffectDuration.values()).map(e -> e.name().toLowerCase()).collect(Collectors.toList());
        });
        manager.registerCommand(new MainCMD(this));
    }


    public Configuration getConfiguration() {
        return configuration;
    }

    public static ProdigySky getInstance() {
        return instance;
    }

    public NMS getNMS() {
        return nms;
    }

}

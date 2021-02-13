package fr.cocoraid.prodigysky;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import fr.cocoraid.prodigysky.commands.MainCMD;
import fr.cocoraid.prodigysky.filemanager.Configuration;
import fr.cocoraid.prodigysky.filemanager.CustomBiomes;
import fr.cocoraid.prodigysky.listeners.EventListener;
import fr.cocoraid.prodigysky.listeners.PacketListener;
import fr.cocoraid.prodigysky.nms.NMS;
import fr.prodigysky.api.ProdigySkyAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ProdigySky extends JavaPlugin {


    private PaperCommandManager manager;
    private static ProdigySky instance;
    private NMS nms;
    private Configuration configuration;
    private CustomBiomes customBiomes;
    @Override
    public void onEnable() {

        displayBanner();

        instance = this;
        nms = new NMS(this);
        this.configuration = new Configuration(this);
        configuration.init();
        configuration.load();
        this.customBiomes = new CustomBiomes(this);

        loadCommands();

        Bukkit.getPluginManager().registerEvents(new EventListener(),this);
        new PacketListener(this);

        new BukkitRunnable() {

            @Override
            public void run() {



            }
        }.runTaskTimerAsynchronously(this, 20,20);



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
        manager.getCommandConditions().addCondition(World.class, "worldEnabled", (c, exec, value) -> {
            if (value == null) {
                return;
            }
            if (!configuration.getEnabledWorlds().contains(value)) {
                throw new ConditionFailedException("The world " + value.getName() + " is not added in the enabled world list !");
            }
        });

        manager.getCommandConditions().addCondition(Player.class, "playerWorldEnabled", (c, exec, value) -> {
            if (value == null) {
                return;
            }
            if (!configuration.getEnabledWorlds().contains(value.getWorld())) {
                throw new ConditionFailedException("The world " + value.getWorld().getName() + " is not added in the enabled world list !");
            }
        });

        manager.getCommandCompletions().registerAsyncCompletion("biomeName", c -> {
            return customBiomes.getBiomes().keySet();
        });
        manager.registerCommand(new MainCMD(this));
    }






    public CustomBiomes getCustomBiomes() {
        return customBiomes;
    }

    public static ProdigySky getInstance() {
        return instance;
    }

    public NMS getNMS() {
        return nms;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}

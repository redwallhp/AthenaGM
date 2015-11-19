package io.github.redwallhp.athenagm.modules.broadcasts;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.modules.Module;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Displays server alerts/tips on a timed rotation.
 * To enable broadcasts, add a "broadcasts.txt" file to the plugin's
 * data folder, with one message per line.
 */
public class BroadcastsModule implements Module {


    private AthenaGM plugin;
    private List<String> messages;


    public String getModuleName() {
        return "broadcasts";
    }


    public BroadcastsModule(AthenaGM plugin) {
        this.plugin = plugin;
        this.messages = new ArrayList<String>();
        loadMessages();
        startMessageCycle();
    }


    public void unload() {}


    private void loadMessages() {
        File file = new File(plugin.getDataFolder(), "broadcasts.txt");
        if (!file.exists()) return;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                messages.add(ChatColor.translateAlternateColorCodes('&', line));
                line = reader.readLine();
            }
            reader.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }


    private void startMessageCycle() {
        if (messages.size() < 1) return;
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                printMessage();
            }
        }, 1200L, 6000L);
    }


    private void printMessage() {
        String msg = messages.get(new Random().nextInt(messages.size()));
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendMessage(String.format("%s\u24D8%s %s", ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, msg));
        }
    }


}

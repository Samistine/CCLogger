package me.alrik94.plugins.cclogger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatLogger implements Listener {

    private CCLogger plugin;

    public ChatLogger(final CCLogger plugins) {
        plugins.getServer().getPluginManager().registerEvents(this, plugins);
        plugin = plugins;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(PlayerChatEvent event) throws IOException {
        //player instance
        Player player = event.getPlayer();
        //player name
        String name = player.getName();
        //player command
        String message = event.getMessage();
        //player Location instance
        Location location = player.getLocation();
        //player X coordinate
        int xLocation = (int) location.getX();
        //player Y coordinate
        int yLocation = (int) location.getY();
        //player Z coordinate
        int zLocation = (int) location.getZ();
        //player World instance
        World world = location.getWorld();
        //player IP address
        String ipAddress = player.getAddress().getAddress().getHostAddress();
        //player world name
        String worldName = world.getName();
        //process date and time
        String date = getDate();
        //Check player
        checkPlayer(name);
        //process ALL the informations
        processInformation(name, message, xLocation, yLocation, zLocation, worldName, date, ipAddress);
    }

    public void processInformation(String playerName, String content, int x, int y, int z, String worldName, String date, String ipAddress) {
        boolean globalChat = plugin.getConfig().getBoolean("Log.toggle.globalChat");
        boolean playerChat = plugin.getConfig().getBoolean("Log.toggle.playerChat");
        boolean logNotifyChat = plugin.getConfig().getBoolean("Log.toggle.logNotifyChat");
        boolean inGameNotifications = plugin.getConfig().getBoolean("Log.toggle.inGameNotifications");
        // boolean logNotifyCommands = plugin.getConfig().getBoolean("Log.toggle.logNotifyCommands");
        File playersFolder = new File(plugin.getDataFolder(), "players");
        File chatFile = new File(plugin.getDataFolder(), "chat.log");
        File playerFile = new File(playersFolder, playerName + ".log");
        File notifyChatFile = new File(plugin.getDataFolder(), "notifyChat.log");


        if (globalChat) {
            Writer.writeFile(formatLog(playerName, content, x, y, z, worldName, date, ipAddress), chatFile);
        }
        if (playerChat) {
            Writer.writeFile(formatLog(playerName, content, x, y, z, worldName, date, ipAddress), playerFile);
        }
        if (checkNotifyList(content) && logNotifyChat) {
            Writer.writeFile(formatLog(playerName, content, x, y, z, worldName, date, ipAddress), notifyChatFile);
        }
        if (checkNotifyList(content) && inGameNotifications) {
            Notifier.notifyPlayer(ChatColor.BLUE + "[" + ChatColor.RED + "CCLogger" + ChatColor.BLUE + "] " + ChatColor.GOLD + playerName + ": " + ChatColor.WHITE + content);
            if (logNotifyChat) {
                Notifier.notifyPlayer(ChatColor.BLUE + "[" + ChatColor.RED + "CCLogger" + ChatColor.BLUE + "] " + ChatColor.WHITE + "data has been logged to notifyChat.log!");
            }
        }
        
    }

    public void checkPlayer(String name) throws IOException {
        File playersFolder = new File(plugin.getDataFolder(), "players");
        File file = new File(playersFolder, name + ".log");
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public String[] formatLog(String playerName, String command, int x, int y, int z, String worldName, String date, String ipAddress) {
        String format = plugin.getConfig().getString("Log.logFormat");
        String log = format;
        if (log.contains("%ip")) {
            log = log.replaceAll("%ip", ipAddress);
        }
        if (log.contains("%date")) {
            log = log.replaceAll("%date", date);
        }
        if (log.contains("%world")) {
            log = log.replaceAll("%world", worldName);
        }
        if (log.contains("%x")) {
            log = log.replaceAll("%x", Integer.toString(x));
        }
        if (log.contains("%y")) {
            log = log.replaceAll("%y", Integer.toString(y));
        }
        if (log.contains("%z")) {
            log = log.replaceAll("%z", Integer.toString(z));
        }
        if (log.contains("%name")) {
            log = log.replaceAll("%name", playerName);
        }
        if (log.contains("%content")) {
            log = log.replaceAll("%content", Matcher.quoteReplacement(command));
        }

        String[] logArray = {log};
        return logArray;
    }

    public String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public boolean checkNotifyList(String message) {
        List<String> messageList = plugin.getConfig().getStringList("Log.notifications.chat");
        for (int i = 0; i < messageList.size(); i++) {
            if (message.toLowerCase().contains(messageList.get(i))) {
                return true;
            }
        }
        return false;
    }
}

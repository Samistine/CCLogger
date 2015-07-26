package me.alrik94.plugins.cclogger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatLogger
        implements Listener {

    private CCLogger plugin;
    

    public ChatLogger(CCLogger plugins) {
        plugins.getServer().getPluginManager().registerEvents(this, plugins);
        this.plugin = plugins;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) throws IOException {
        Player player = event.getPlayer();

        String name = player.getName();

        String message = event.getMessage();

        String ipAddress = player.getAddress().getAddress().getHostAddress();

        String date = getDate();

        checkPlayer(name);

        processInformation(player, name, message, date, ipAddress);
    }

    public void processInformation(Player player, String playerName, String content, String date, String ipAddress) {
        boolean globalChat = this.plugin.getConfig().getBoolean("Log.toggle.globalChat");
        boolean playerChat = this.plugin.getConfig().getBoolean("Log.toggle.playerChat");
        boolean logNotifyChat = this.plugin.getConfig().getBoolean("Log.toggle.logNotifyChat");
        boolean inGameNotifications = this.plugin.getConfig().getBoolean("Log.toggle.inGameNotifications");

        File playersFolder = new File(this.plugin.getDataFolder(), "players");
        File chatFile = new File(this.plugin.getDataFolder(), "chat.log");
        File playerFile = new File(playersFolder, playerName + ".log");
        File notifyChatFile = new File(this.plugin.getDataFolder(), "notifyChat.log");

        
        
        if (!checkExemptionList(player)) {
            if (globalChat) {
                plugin.writer.writeFile(formatLog(playerName, content, date, ipAddress), chatFile);
            }
            if (playerChat) {
                plugin.writer.writeFile(formatLog(playerName, content, date, ipAddress), playerFile);
            }
            if ((checkNotifyList(content)) && (logNotifyChat)) {
                plugin.writer.writeFile(formatLog(playerName, content, date, ipAddress), notifyChatFile);
            }
            if ((checkNotifyList(content)) && (inGameNotifications)) {
                plugin.chatNotifier.notifyPlayer(ChatColor.BLUE + "[" + ChatColor.RED + "CCLogger" + ChatColor.BLUE + "] " + ChatColor.GOLD + playerName + ": " + ChatColor.WHITE + content);
            }
        }
        
        plugin.database.writeChatContent(playerName, content, date, ipAddress);
    }

    public void checkPlayer(String name)
            throws IOException {
        File playersFolder = new File(this.plugin.getDataFolder(), "players");
        File file = new File(playersFolder, name + ".log");
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public String[] formatLog(String playerName, String command, String date, String ipAddress) {
        String format = this.plugin.getConfig().getString("Log.logFormat");
        String log = format;
        if (log.contains("%ip")) {
            log = log.replaceAll("%ip", ipAddress);
        }
        if (log.contains("%date")) {
            log = log.replaceAll("%date", date);
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
        List messageList = this.plugin.getConfig().getStringList("Log.notifications.chat");
        for (int i = 0; i < messageList.size(); i++) {
            if (message.toLowerCase().contains((CharSequence) messageList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public boolean checkExemptionList(Player player) {
        if (player.hasPermission("cclogger.exempt")) {
            return true;
        }
        return false;
    }
}
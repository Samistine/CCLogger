package me.alrik94.plugins.cclogger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ChatLogger
        implements Listener {

    private CCLogger plugin;

    public ChatLogger(CCLogger plugins) {
        this.plugin = plugins;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(ChatEvent event) throws IOException {
        if (event.isCommand() || event.isCancelled()) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String name = player.getName();
        UUID uuid = player.getUniqueId();
        String message = event.getMessage();
        String ipAddress = player.getAddress().getAddress().getHostAddress();
        String date = getDate();
        String server = player.getServer().getInfo().getName();

        checkPlayer(uuid);

        processInformation(player, name, message, date, ipAddress, server);
    }

    public void processInformation(ProxiedPlayer player, String playerName, String content, String date, String ipAddress, String server) {
        boolean globalChat = this.plugin.getConfig().getBoolean("Log.toggle.globalChat");
        boolean playerChat = this.plugin.getConfig().getBoolean("Log.toggle.playerChat");
        boolean logNotifyChat = this.plugin.getConfig().getBoolean("Log.toggle.logNotifyChat");
        boolean inGameNotifications = this.plugin.getConfig().getBoolean("Log.toggle.inGameNotifications");

        File playersFolder = new File(this.plugin.getDataFolder(), "players");
        File chatFile = new File(this.plugin.getDataFolder(), "chat.log");
        File playerFile = new File(playersFolder, player.getUniqueId().toString() + ".log");
        File notifyChatFile = new File(this.plugin.getDataFolder(), "notifyChat.log");

        if (!checkExemptionList(player)) {
            if (globalChat) {
                plugin.writer.writeFile(formatLog(playerName, content, date, ipAddress, server), chatFile);
            }
            if (playerChat) {
                plugin.writer.writeFile(formatLog(playerName, content, date, ipAddress, server), playerFile);
            }
            if ((checkNotifyList(content)) && (logNotifyChat)) {
                plugin.writer.writeFile(formatLog(playerName, content, date, ipAddress, server), notifyChatFile);
            }
            if ((checkNotifyList(content)) && (inGameNotifications)) {
                plugin.chatNotifier.notifyPlayer(ChatColor.BLUE + "[" + ChatColor.RED + "CCLogger" + ChatColor.BLUE + "] " + ChatColor.GOLD + playerName + ": " + ChatColor.WHITE + content);
            }
        }

        plugin.database.writeChatContent(playerName, content, date, ipAddress); //TODO: Server Support
    }

    public void checkPlayer(UUID uuid) throws IOException {
        File playersFolder = new File(this.plugin.getDataFolder(), "players");
        File file = new File(playersFolder, uuid.toString() + ".log");
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public String[] formatLog(String playerName, String command, String date, String ipAddress, String server) {
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
        if (log.contains("%server")) {
            log = log.replaceAll("%server", server);
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
        List<String> messageList = this.plugin.getConfig().getStringList("Log.notifications.chat");
        for (String messageList1 : messageList) {
            if (message.toLowerCase().contains((CharSequence) messageList1)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkExemptionList(ProxiedPlayer player) {
        return player.hasPermission("cclogger.exempt");
    }
}

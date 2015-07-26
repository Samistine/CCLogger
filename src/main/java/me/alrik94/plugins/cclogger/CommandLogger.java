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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandLogger
        implements Listener {

    private CCLogger plugin;
    private Notifier notifier;

    public CommandLogger(CCLogger plugins) {
        plugins.getServer().getPluginManager().registerEvents(this, plugins);
        this.plugin = plugins;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) throws IOException {
        Player player = event.getPlayer();

        String name = player.getName();

        String command = event.getMessage();

        String ipAddress = player.getAddress().getAddress().getHostAddress();

        String date = getDate();

        checkPlayer(name);

        processInformation(player, name, command, date, ipAddress);
    }

    public void processInformation(Player player, String playerName, String command, String date, String ipAddress) {
        boolean playerCommand = this.plugin.getConfig().getBoolean("Log.toggle.playerCommands");
        boolean globalCommand = this.plugin.getConfig().getBoolean("Log.toggle.globalCommands");
        boolean logNotifyCommands = this.plugin.getConfig().getBoolean("Log.toggle.logNotifyCommands");
        boolean inGameNotifications = this.plugin.getConfig().getBoolean("Log.toggle.inGameNotifications");
        File playersFolder = new File(this.plugin.getDataFolder(), "players");
        File commandFile = new File(this.plugin.getDataFolder(), "commands.log");
        File playerFile = new File(playersFolder, playerName + ".log");
        File notifyCommandFile = new File(this.plugin.getDataFolder(), "notifyCommands.log");

        
        
        if (!checkExemptionList(player)) {
            if ((globalCommand)
                    && (!commandCheck(command))) {
                plugin.writer.writeFile(formatLog(playerName, command, date, ipAddress), commandFile);
            }

            if ((playerCommand)
                    && (!commandCheck(command))) {
                plugin.writer.writeFile(formatLog(playerName, command, date, ipAddress), playerFile);
            }

            if ((checkNotifyList(command)) && (logNotifyCommands)) {
                plugin.writer.writeFile(formatLog(playerName, command, date, ipAddress), notifyCommandFile);
            }
            if ((checkNotifyList(command)) && (inGameNotifications)) {
                plugin.chatNotifier.notifyPlayer(ChatColor.BLUE + "[" + ChatColor.RED + "CCLogger" + ChatColor.BLUE + "] " + ChatColor.GOLD + playerName + ": " + ChatColor.WHITE + command);
            }
        }
        
        plugin.database.writeCommandContent(playerName, command, date, ipAddress);
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

    public boolean commandCheck(String command) {
        List commands = this.plugin.getConfig().getStringList("Log.commands.blacklist");
        String[] commandsplit = command.split(" ");
        String commandconvert = commandsplit[0];
        for (int i = 0; i < commands.size(); i++) {
            if (commandconvert.matches((String) commands.get(i))) {
                return true;
            }
        }
        return false;
    }

    public boolean checkNotifyList(String command) {
        List commands = this.plugin.getConfig().getStringList("Log.notifications.commands");
        String[] commandsplit = command.split(" ");
        String commandconvert = commandsplit[0];
        for (int i = 0; i < commands.size(); i++) {
            if (commandconvert.matches((String) commands.get(i))) {
                return true;
            }
        }
        return false;
    }

    public void checkPlayer(String name) throws IOException {
        File playersFolder = new File(this.plugin.getDataFolder(), "players");
        File file = new File(playersFolder, name + ".log");
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public boolean checkExemptionList(Player player) {
        if (player.hasPermission("cclogger.exempt")) {
            return true;
        }
        return false;
    }
}
package me.alrik94.plugins.cclogger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class LoginLogger
        implements Listener {

    private CCLogger plugin;

    public LoginLogger(CCLogger plugins) {
        this.plugin = plugins;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PostLoginEvent event) throws IOException {
        ProxiedPlayer player = event.getPlayer();
        String name = player.getName();
        String ipAddress = player.getAddress().getAddress().getHostAddress();
        String date = getDate();
        String login = "1";
        checkPlayer(name);
        processInformationJoin(player, name, login, date, ipAddress);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerDisconnectEvent event) throws IOException {
        ProxiedPlayer player = event.getPlayer();
        String name = player.getName();
        String ipAddress = player.getAddress().getAddress().getHostAddress();
        String date = getDate();
        String login = "0";
        checkPlayer(name);
        processInformationQuit(player, name, login, date, ipAddress);
    }

    public void processInformationJoin(ProxiedPlayer player, String playerName, String login, String date, String ipAddress) {
        boolean globalLogin = this.plugin.getConfig().getBoolean("Log.toggle.globalLogin");
        boolean playerLogin = this.plugin.getConfig().getBoolean("Log.toggle.playerLogin");
        File playersFolder = new File(this.plugin.getDataFolder(), "players");
        File chatFile = new File(this.plugin.getDataFolder(), "chat.log");
        File playerFile = new File(playersFolder, playerName + ".log");
        String[] log = {"[" + date + "] " + playerName + " logged in."};

        
        
        if (!checkExemptionList(player)) {
            if (globalLogin) {
                plugin.writer.writeFile(log, chatFile);
            }
            if (playerLogin) {
                plugin.writer.writeFile(log, playerFile);
            }
        }
        
        plugin.database.writeLoginContent(playerName, login, date, ipAddress);
    }

    public void processInformationQuit(ProxiedPlayer player, String playerName, String login, String date, String ipAddress) {
        boolean globalLogin = this.plugin.getConfig().getBoolean("Log.toggle.globalLogin");
        boolean playerLogin = this.plugin.getConfig().getBoolean("Log.toggle.playerLogin");
        File playersFolder = new File(this.plugin.getDataFolder(), "players");
        File chatFile = new File(this.plugin.getDataFolder(), "chat.log");
        File playerFile = new File(playersFolder, playerName + ".log");
        String[] log = {"[" + date + "] " + playerName + " logged out."};

        
        
        if (!checkExemptionList(player)) {
            if (globalLogin) {
                plugin.writer.writeFile(log, chatFile);
            }
            if (playerLogin) {
                plugin.writer.writeFile(log, playerFile);
            }
        }
        
        plugin.database.writeLoginContent(playerName, login, date, ipAddress);
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

    public boolean checkExemptionList(ProxiedPlayer player) {
        if (player.hasPermission("cclogger.exempt")) {
            return true;
        }
        return false;
    }
}
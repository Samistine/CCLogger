package me.alrik94.plugins.cclogger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
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
        UUID uuid = player.getUniqueId();
        String ipAddress = player.getAddress().getAddress().getHostAddress();
        String date = getDate();
        String login = "1";
        checkPlayer(uuid);
        processInformationJoin(player, name, login, date, ipAddress);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerDisconnectEvent event) throws IOException {
        ProxiedPlayer player = event.getPlayer();
        String name = player.getName();
        UUID uuid = player.getUniqueId();
        String ipAddress = player.getAddress().getAddress().getHostAddress();
        String date = getDate();
        String login = "0";
        checkPlayer(uuid);
        processInformationQuit(player, name, login, date, ipAddress);
    }

    public void processInformationJoin(ProxiedPlayer player, String playerName, String login, String date, String ipAddress) {
        boolean globalLogin = this.plugin.getConfig().getBoolean("Log.toggle.globalLogin");
        boolean playerLogin = this.plugin.getConfig().getBoolean("Log.toggle.playerLogin");
        File playersFolder = new File(this.plugin.getDataFolder(), "players");
        File chatFile = new File(this.plugin.getDataFolder(), "chat.log");
        File playerFile = new File(playersFolder, player.getUniqueId().toString() + ".log");
        String[] log = {"[" + date + "] " + playerName + "(" + player.getUniqueId().toString() + ")" + " logged in."};

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
        File playerFile = new File(playersFolder, player.getUniqueId().toString() + ".log");
        String[] log = {"[" + date + "] " + playerName + "(" + player.getUniqueId().toString() + ")" + " logged out."};

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

    public void checkPlayer(UUID uuid) throws IOException {
        File playersFolder = new File(this.plugin.getDataFolder(), "players");
        File file = new File(playersFolder, uuid.toString() + ".log");
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
        return player.hasPermission("cclogger.exempt");
    }
}

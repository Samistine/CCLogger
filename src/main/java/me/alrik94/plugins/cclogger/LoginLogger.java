package me.alrik94.plugins.cclogger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LoginLogger
        implements Listener {

    private CCLogger plugin;

    public LoginLogger(CCLogger plugins) {
        plugins.getServer().getPluginManager().registerEvents(this, plugins);
        this.plugin = plugins;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
        Player player = event.getPlayer();
        String name = player.getName();
        Location location = player.getLocation();
        int x = (int) location.getX();
        int y = (int) location.getY();
        int z = (int) location.getZ();
        World world = location.getWorld();
        String ipAddress = player.getAddress().getAddress().getHostAddress();
        String worldName = world.getName();
        String date = getDate();
        String login = "1";
        checkPlayer(name);
        processInformationJoin(player, name, login, x, y, z, worldName, date, ipAddress);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) throws IOException {
        Player player = event.getPlayer();
        String name = player.getName();
        Location location = player.getLocation();
        int x = (int) location.getX();
        int y = (int) location.getY();
        int z = (int) location.getZ();
        World world = location.getWorld();
        String ipAddress = player.getAddress().getAddress().getHostAddress();
        String worldName = world.getName();
        String date = getDate();
        String login = "0";
        checkPlayer(name);
        processInformationQuit(player, name, login, x, y, z, worldName, date, ipAddress);
    }

    public void processInformationJoin(Player player, String playerName, String login, int x, int y, int z, String worldName, String date, String ipAddress) {
        boolean globalLogin = this.plugin.getConfig().getBoolean("Log.toggle.globalLogin");
        boolean playerLogin = this.plugin.getConfig().getBoolean("Log.toggle.playerLogin");
        File playersFolder = new File(this.plugin.getDataFolder(), "players");
        File chatFile = new File(this.plugin.getDataFolder(), "chat.log");
        File playerFile = new File(playersFolder, playerName + ".log");
        String[] log = {"[" + date + "] " + playerName + " logged in."};

        plugin.database.writeLoginContent(playerName, login, x, y, z, worldName, date, ipAddress);
        
        if (!checkExemptionList(player)) {
            if (globalLogin) {
                Writer.writeFile(log, chatFile);
            }
            if (playerLogin) {
                Writer.writeFile(log, playerFile);
            }
        }
    }

    public void processInformationQuit(Player player, String playerName, String login, int x, int y, int z, String worldName, String date, String ipAddress) {
        boolean globalLogin = this.plugin.getConfig().getBoolean("Log.toggle.globalLogin");
        boolean playerLogin = this.plugin.getConfig().getBoolean("Log.toggle.playerLogin");
        File playersFolder = new File(this.plugin.getDataFolder(), "players");
        File chatFile = new File(this.plugin.getDataFolder(), "chat.log");
        File playerFile = new File(playersFolder, playerName + ".log");
        String[] log = {"[" + date + "] " + playerName + " logged out."};

        plugin.database.writeLoginContent(playerName, login, x, y, z, worldName, date, ipAddress);
        
        if (!checkExemptionList(player)) {
            if (globalLogin) {
                Writer.writeFile(log, chatFile);
            }
            if (playerLogin) {
                Writer.writeFile(log, playerFile);
            }
        }
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
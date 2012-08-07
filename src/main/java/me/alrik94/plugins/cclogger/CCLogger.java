package me.alrik94.plugins.cclogger;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CCLogger extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private File configFile = null;
    private CommandLogger commandLogger = null;
    private ChatLogger chatLogger = null;
    private LoginLogger loginLogger = null;
    private Notifier chatNotifier = null;
    private CommandExec commandExec;

    @Override
    public void onDisable() {
        // TODO: Place any custom disable code here.
    }

    @Override
    public void onEnable() {
        commandLogger = new CommandLogger(this);
        chatLogger = new ChatLogger(this);
        loginLogger = new LoginLogger(this);
        chatNotifier = new Notifier(this);
        commandExec = new CommandExec(this);
        getCommand("recent").setExecutor(commandExec);
        configCheck();
        folderCheck();
    }

    public void folderCheck() {
        File playersFolder = new File(getDataFolder(), "players");
        if (!playersFolder.exists()) {
            playersFolder.mkdir();
        }
    }

    public void configCheck() {
        String[] blacklist = {"/help", "/who", "/home"};
//        String[] whitelist = { "/kill", "/smite", "/sudo" };
        String[] commandNotifyList = {"/pl", "/item", "/give"};
        String[] chatNotifyList = {"kill", "ddos", "hack", "flymod"};
        File dataFolder = getDataFolder();
        try {
            config = getConfig();
            configFile = new File(dataFolder, "config.yml");
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            config.options().header(defaultFormat());
            if (!config.contains("Log.toggle.globalCommands")) {
                config.set("Log.toggle.globalCommands", true);
            }
            if (!config.contains("Log.toggle.globalChat")) {
                config.set("Log.toggle.globalChat", true);
            }
            if (!config.contains("Log.toggle.playerCommands")) {
                config.set("Log.toggle.playerCommands", true);
            }
            if (!config.contains("Log.toggle.playerChat")) {
                config.set("Log.toggle.playerChat", true);
            }
            if (!config.contains("Log.toggle.logNotifyChat")) {
                config.set("Log.toggle.logNotifyChat", true);
            }
            if (!config.contains("Log.toggle.inGameNotifications")) {
                config.set("Log.toggle.inGameNotifications", true);
            }
            if (!config.contains("Log.toggle.logNotifyCommands")) {
                config.set("Log.toggle.logNotifyCommands", true);
            }
//            if (!config.contains("Log.toggle.commandsWhitelist")) {
//                config.set("Log.toggle.commandsWhitelist", false);
//            }
            if (!config.contains("Log.toggle.playerLogin")) {
                config.set("Log.toggle.playerLogin", true);
            }
            if (!config.contains("Log.toggle.globalLogin")) {
                config.set("Log.toggle.globalLogin", true);
            }
            if (!config.contains("Log.commands.blacklist")) {
                config.addDefault("Log.commands.blacklist", Arrays.asList(blacklist));
            }
//                if(!config.contains("Log.commands.whitelist")) {
//                    config.addDefault("Log.commands.whitelist", Arrays.asList(whitelist));
//                }

            if (!config.contains("Log.logFormat")) {
                config.addDefault("Log.logFormat", "[%ip][%date (%world: %x,%y,%z)] %name: %content");
            }
            if (!config.contains("Log.notifications.chat")) {
                config.addDefault("Log.notifications.chat", Arrays.asList(chatNotifyList));
            }
            if (!config.contains("Log.notifications.commands")) {
                config.addDefault("Log.notifications.commands", Arrays.asList(commandNotifyList));
            }
            config.options().copyDefaults(true);
            saveConfig();

        } catch (Exception e) {
        }
    }

    public String defaultFormat() {
        String dateFormat = "%date = The date and time when the content is logged.\n";
        String worldFormat = "%world = The world the player is in when the content is logged.\n";
        String xCoord = "%x = The x coordinate of player when the content is logged.\n";
        String yCoord = "%y = The y coordinate of player when the content is logged.\n";
        String zCoord = "%z = The z coordinate of player when the content is logged.\n";
        String nameFormat = "%name = The name of the player that created the content that is logged.\n";
        String contentFormat = "%content = The content that is logged.\n";
        String format = (dateFormat + worldFormat + xCoord + yCoord + zCoord + nameFormat + contentFormat);
        return format;
    }
}

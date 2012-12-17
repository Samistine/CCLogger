package me.alrik94.plugins.cclogger;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import lib.PatPeter.SQLibrary.SQLite;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CCLogger extends JavaPlugin
        implements Listener {

    private FileConfiguration config;
    private File configFile = null;
    private CommandLogger commandLogger = null;
    private ChatLogger chatLogger = null;
    private LoginLogger loginLogger = null;
    private Notifier chatNotifier = null;
    public Database database;
    public SQLite sqlite;

    @Override
    public void onDisable() {
        sqlite.close();
    }

    @Override
    public void onEnable() {
        this.database = new Database(this);
        this.commandLogger = new CommandLogger(this);
        this.chatLogger = new ChatLogger(this);
        this.loginLogger = new LoginLogger(this);
        this.chatNotifier = new Notifier(this);
        configCheck();
        folderCheck();
        database.sqlConnection();
        database.sqlTableCheck();
    }

    public void folderCheck() {
        File playersFolder = new File(getDataFolder(), "players");
        if (!playersFolder.exists()) {
            playersFolder.mkdir();
        }
    }

    public void configCheck() {
        String[] blacklist = {"/help", "/who", "/home"};

        String[] commandNotifyList = {"/pl", "/item", "/give"};
        String[] chatNotifyList = {"kill", "ddos", "hack", "flymod"};
        File dataFolder = getDataFolder();
        try {
            this.config = getConfig();
            this.configFile = new File(dataFolder, "config.yml");
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }
            if (!this.configFile.exists()) {
                this.configFile.createNewFile();
            }
            this.config.options().header(defaultFormat());
            if (!this.config.contains("Log.toggle.globalCommands")) {
                this.config.set("Log.toggle.globalCommands", Boolean.valueOf(true));
            }
            if (!this.config.contains("Log.toggle.globalChat")) {
                this.config.set("Log.toggle.globalChat", Boolean.valueOf(true));
            }
            if (!this.config.contains("Log.toggle.playerCommands")) {
                this.config.set("Log.toggle.playerCommands", Boolean.valueOf(true));
            }
            if (!this.config.contains("Log.toggle.playerChat")) {
                this.config.set("Log.toggle.playerChat", Boolean.valueOf(true));
            }
            if (!this.config.contains("Log.toggle.logNotifyChat")) {
                this.config.set("Log.toggle.logNotifyChat", Boolean.valueOf(true));
            }
            if (!this.config.contains("Log.toggle.inGameNotifications")) {
                this.config.set("Log.toggle.inGameNotifications", Boolean.valueOf(true));
            }
            if (!this.config.contains("Log.toggle.logNotifyCommands")) {
                this.config.set("Log.toggle.logNotifyCommands", Boolean.valueOf(true));
            }

            if (!this.config.contains("Log.toggle.playerLogin")) {
                this.config.set("Log.toggle.playerLogin", Boolean.valueOf(true));
            }
            if (!this.config.contains("Log.toggle.globalLogin")) {
                this.config.set("Log.toggle.globalLogin", Boolean.valueOf(true));
            }
            if (!this.config.contains("Log.commands.blacklist")) {
                this.config.addDefault("Log.commands.blacklist", Arrays.asList(blacklist));
            }

            if (!this.config.contains("Log.logFormat")) {
                this.config.addDefault("Log.logFormat", "[%ip][%date (%world: %x,%y,%z)] %name: %content");
            }
            if (!this.config.contains("Log.notifications.chat")) {
                this.config.addDefault("Log.notifications.chat", Arrays.asList(chatNotifyList));
            }
            if (!this.config.contains("Log.notifications.commands")) {
                this.config.addDefault("Log.notifications.commands", Arrays.asList(commandNotifyList));
            }
            this.config.options().copyDefaults(true);
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
        String format = dateFormat + worldFormat + xCoord + yCoord + zCoord + nameFormat + contentFormat;
        return format;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ccreload")) {
            reloadConfig();
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("ccl")) {
            if(args.length > 0 && args[0].equalsIgnoreCase("count")){
                if(args.length == 1){
                    sender.sendMessage(ChatColor.RED + "/ccl count <word> <player>");
                }
                if (args.length == 2){
                    String word = args[1];
                    int count = 0;
                    try {
                        count = database.countWord(word);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    sender.sendMessage("[CCLogger] '"+word+"': "+count+"");
                }
                if (args.length == 3){
                    String word = args[1];
                    String playerName = args[2];
                    int count = 0;
                    try {
                        count = database.countWordFromPlayer(playerName, word);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    sender.sendMessage("[CCLogger] "+ChatColor.UNDERLINE+playerName+ChatColor.RESET+" has said '"+ChatColor.BOLD+word+ChatColor.RESET+"' "+ChatColor.BLUE+count+" times.");
                    
                }
                
            }
            
            
            return true;
        }
        return false;
    }
}

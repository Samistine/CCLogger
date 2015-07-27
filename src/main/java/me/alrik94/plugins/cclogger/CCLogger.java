package me.alrik94.plugins.cclogger;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import lib.PatPeter.SQLibrary.SQLite;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public class CCLogger extends Plugin {

    private BungeeConfig bc;

    private Configuration config;
    private File configFile;
    private CommandLogger commandLogger;
    private ChatLogger chatLogger;
    private LoginLogger loginLogger;
    public Notifier chatNotifier;
    public Database database;
    public Writer writer;
    public SQLite sqlite;

    @Override
    public void onDisable() {
        database.sqlite.close();
    }

    public Configuration getConfig() {
        return bc.getConfig();
    }

    @Override
    public void onEnable() {
        bc = new BungeeConfig(this);
        this.database = new Database(this);
        this.writer = new Writer(this);
        this.commandLogger = new CommandLogger(this);
        this.chatLogger = new ChatLogger(this);
        this.loginLogger = new LoginLogger(this);
        this.chatNotifier = new Notifier(this);
        configCheck();
        folderCheck();
        database.sqlConnection();
        database.sqlTableCheck();
        registerCommands();
        System.out.println("[CCLogger] has been enabled.");
    }

    public void folderCheck() {
        File playersFolder = new File(getDataFolder(), "players");
        if (!playersFolder.exists()) {
            playersFolder.mkdir();
        }
    }

    public void configCheck() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        if (!bc.getFile().exists()) {
            try {
                bc.getFile().createNewFile();
                try (InputStream is = getResourceAsStream(bc.getFile().getName())) {
                    OutputStream os = new FileOutputStream(bc.getFile());
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }

        }
        this.config = getConfig();
    }

    public void registerCommands() {
        Command ccreload = new Command("gccreload", "cclogger.reload") {
            @Override
            public void execute(CommandSender sender, String[] args) {
                bc.load();
                sender.sendMessage(ChatColor.BLUE + "[CCLogger] configuration reloaded.");
            }
        };

        Command ccl = new Command("gccl") {

            @Override
            public void execute(CommandSender sender, String[] args) {
                if (args.length > 0 && args[0].equalsIgnoreCase("count")) {
                    if (args.length == 1) {
                        sender.sendMessage(ChatColor.RED + "/ccl count <word> <player>");
                    }
                    if (args.length == 2) {
                        String word = args[1];
                        int count = 0;
                        try {
                            count = database.countWord(word);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        sender.sendMessage(ChatColor.BLUE + word + ChatColor.RED + " has been said " + ChatColor.BLUE + count + ChatColor.RED + " times.");
                    }
                    if (args.length == 3) {
                        String word = args[1];
                        String playerName = args[2];
                        int count = 0;
                        try {
                            count = database.countWordFromPlayer(playerName, word);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        sender.sendMessage(ChatColor.RED + playerName + " has said " + ChatColor.BLUE + word + " " + count + ChatColor.RED + " times.");
                    }
                }
            }
        };
        
        getProxy().getPluginManager().registerCommand(this, ccreload);
        getProxy().getPluginManager().registerCommand(this, ccl);

    }
}

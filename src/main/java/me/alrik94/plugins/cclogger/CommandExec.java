/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.alrik94.plugins.cclogger;

import java.io.File;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Alrik
 */
public class CommandExec implements CommandExecutor {

    private CCLogger plugin;

    public CommandExec(CCLogger plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (cmd.getName().equalsIgnoreCase("recent")) { // If the player typed /basic then do the following...
            recentCommand(sender, cmd, label, args);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("basic2")) {
            if (player == null) {
                sender.sendMessage("this command can only be run by a player");
            } else {
                // do something else...
            }
            return true;
        }
        return false;
    }

    public void recentCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String playerName = sender.getName();
        File playersFolder = new File(plugin.getDataFolder(), "players");
        File playerFile = new File(playersFolder, playerName + ".log");
        String readLastLines = Reader.readLastLines(playerFile, 10);
        sender.sendMessage(readLastLines);

    }
}

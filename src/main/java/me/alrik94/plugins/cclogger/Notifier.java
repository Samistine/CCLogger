/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.alrik94.plugins.cclogger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Alrik
 */
public class Notifier {

    private CCLogger plugin;

    public Notifier(final CCLogger plugins) {

        plugin = plugins;
    }

    public static void notifyPlayer(String string) {

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.hasPermission("cclogger.notify")) {
                player.sendMessage(string);
            }
        }
    }
}

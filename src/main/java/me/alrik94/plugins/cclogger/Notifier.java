package me.alrik94.plugins.cclogger;

import net.md_5.bungee.api.connection.ProxiedPlayer;


public class Notifier {

    private CCLogger plugin;

    public Notifier(CCLogger plugins) {
        this.plugin = plugins;
    }

    public void notifyPlayer(String string) {
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            if (player.hasPermission("cclogger.notify")) {
                player.sendMessage(string);
            }
        }
    }
}
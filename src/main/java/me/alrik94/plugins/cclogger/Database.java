
package me.alrik94.plugins.cclogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import lib.PatPeter.SQLibrary.SQLite;
import org.apache.commons.lang3.StringUtils;

public class Database {
    
    private CCLogger plugin;
    public SQLite sqlite;

    public Database(CCLogger plugins) {
        this.plugin = plugins;
    }
    
    public void sqlConnection() {
        sqlite = new SQLite(plugin.getLogger(),
                "CCLogger",
                "data",
                plugin.getDataFolder());
//Make sure sqlite is the same as the variable you specified at the top of the plugin!
        try {
            sqlite.open();
        } catch (Exception e) {
            plugin.getLogger().info(e.getMessage());
            //plugin.getPluginLoader().disablePlugin(plugin);
            plugin.getProxy().stop("CCLogger expierenced an exception with the database");
        }
    }
    
    public void sqlTableCheck() {
        if (sqlite.checkTable("chat")) {
            return;
        } else {
            sqlite.query("CREATE TABLE 'chat'(playername VARCHAR(50), content VARCHAR(50), date VARCHAR(50), ipaddress VARCHAR(50));");

            sqlite.query("INSERT INTO 'chat'(playername, content) VALUES('Pew446', '08/09/2012');"); //This is optional. You can do this later if you want.

            plugin.getLogger().info("Table 'chat' has been created");
        }
        if (sqlite.checkTable("commands")) {
            return;
        } else {
            sqlite.query("CREATE TABLE 'commands'(playername VARCHAR(50), content VARCHAR(50), date VARCHAR(50), ipaddress VARCHAR(50));");

            sqlite.query("INSERT INTO 'commands'(playername, content) VALUES('Pew446', '08/09/2012');"); //This is optional. You can do this later if you want.

            plugin.getLogger().info("Table 'commands' has been created");
        }
        if (sqlite.checkTable("logins")) {
            return;
        } else {
            sqlite.query("CREATE TABLE 'logins'(playername VARCHAR(50), loginlogout VARCHAR(50), date VARCHAR(50), ipaddress VARCHAR(50));");

            sqlite.query("INSERT INTO 'logins'(playername, loginlogout) VALUES('Pew446', '1');"); //This is optional. You can do this later if you want.

            plugin.getLogger().info("Table 'logins' has been created");
        }
    }
    
    public void writeChatContent(String playerName, String content, String date, String ipAddress) {
        sqlite.query("INSERT INTO 'chat'(playername, content, date, ipaddress) VALUES('" + playerName + "', '" + content + "', '" + date + "', '" + ipAddress + "');");
    }

    public void writeCommandContent(String playerName, String command, String date, String ipAddress) {
        sqlite.query("INSERT INTO 'commands'(playername, content, date, ipaddress) VALUES('" + playerName + "', '" + command + "', '" + date + "', '" + ipAddress + "');");
    }

    public void writeLoginContent(String playerName, String loginlogout, String date, String ipAddress) {
        sqlite.query("INSERT INTO 'logins'(playername, loginlogout, date, ipaddress) VALUES('" + playerName + "', '" + loginlogout + "', '" + date + "', '" + ipAddress + "');");
    }
    
    public int countWordFromPlayer(String playerName, String word) throws SQLException{
        int count = 0;
        ResultSet result = sqlite.query("SELECT content FROM chat WHERE playername='"+playerName+"';");
        while(result.next()){
            String content = result.getString("content").toLowerCase();
            String searched = word.toLowerCase();
            if(content.contains(searched)){
                int times = StringUtils.countMatches(content, searched);
                count+=times;
            }
        }
        return count;
    }
    public int countWord(String word) throws SQLException{
        int count = 0;
        ResultSet result = sqlite.query("SELECT content FROM chat;");
        while(result.next()){
            String content = result.getString("content").toLowerCase();
            String searched = word.toLowerCase();
            if(content.contains(searched)){
                int times = StringUtils.countMatches(content, searched);
                count+=times;
            }
        }
        return count;
    }
    public int totalPlayerChatCount(String playerName) throws SQLException{
        int count = 0;
        ResultSet result = sqlite.query("SELECT content FROM chat WHERE playername='"+playerName+"';");
        while(result.next()){
            String[] content = result.getString("content").toLowerCase().split(" ");
            for(int i = 0; i<content.length; i++){
                if(content[i].equals(" ")) {
                }else{
                    count++;
                }
            }
        }
        return count;
    }
}

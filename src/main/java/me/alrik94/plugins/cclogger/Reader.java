package me.alrik94.plugins.cclogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Reader {

    private static CCLogger plugin;

    public static String readLastLines(File file, int lines) {
        try {
            BufferedReader br = new BufferedReader (new FileReader(file));
        } catch (FileNotFoundException ex) {
        }
        
        
        
        return "apwod";
    }
}

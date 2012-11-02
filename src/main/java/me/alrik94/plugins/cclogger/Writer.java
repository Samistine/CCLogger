package me.alrik94.plugins.cclogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {

    public static void createFile(File file, String[] i) {
        BufferedWriter buffWriter = null;
        FileWriter fileWriter = null;
        try {
            file.createNewFile();
            fileWriter = new FileWriter(file, true);
            buffWriter = new BufferedWriter(fileWriter);


            for (String string : i) {

                buffWriter.write(string);
                buffWriter.newLine();
            }

            buffWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (buffWriter != null) {
                    buffWriter.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(String[] i, File type) {
        File log = type;
        BufferedWriter buffwriter = null;
        FileWriter filewriter = null;
        try {
            filewriter = new FileWriter(log, true);
            buffwriter = new BufferedWriter(filewriter);


            for (String s : i) {

                buffwriter.write(s);
                buffwriter.newLine();
            }

            buffwriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

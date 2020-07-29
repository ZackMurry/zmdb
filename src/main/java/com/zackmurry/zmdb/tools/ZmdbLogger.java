package com.zackmurry.zmdb.tools;

import com.zackmurry.zmdb.files.FileEditor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ZmdbLogger {

    /**
     * puts text into log file and system.out.print's it
     * @param text input into logger
     */
    public static void log(String text) {
        System.out.println(text);

        File logFile = new File("log.txt");
        if(!logFile.exists()) {
            try {
                logFile.createNewFile(); //todo maybe do something about this warning
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //getting the time so i can add it to the log
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String time = sdf.format(new Date());

        FileEditor.silentWriteToStartOfFile(text + " | " + time + "\n", logFile);

    }

    public static void log(String title, String text) {
        System.out.println(title);
        System.out.println(text);
    }

    public static void clearLog() {
        File logFile = new File("log.txt");
        if(!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        FileEditor.silentRemoveAllTextFromFile(logFile);

    }

}

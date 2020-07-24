package com.zackmurry.zmdb.controller.files;

import com.zackmurry.zmdb.ZmdbLogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileEditor {

    public static final String VALUE_SEPARATOR = " &n ";

    private DataLoader dataLoader = new DataLoader();

    public FileEditor() {
        File dataFolder = new File("data/databases");
        if(!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

    }


    /**
     * @param name name of file
     * adds a new top-level file
     */
    public int newDatabaseFile(String name) {
        File dbFile = new File("data/databases/" + name);

        if(!dbFile.exists()) {
            if(!dbFile.mkdir()) return 0;
        }

        return 1;
    }

    public int newTableFile(String databaseName, String tableName) {
        File dbFile = new File("data/databases/" + databaseName);
        if(!dbFile.exists()) {
            ZmdbLogger.log("Cannot create table: database file doesn't exist.");
            return 0;
        }

        File tableFile = new File("data/databases/" + databaseName + "/" + tableName);
        if(tableFile.exists()) {
            ZmdbLogger.log("Cannot create table: table file already exists.");
            return 0;
        }

        if(tableFile.mkdir()) return 1; //mkdir() returns true if it's successful

        ZmdbLogger.log("Cannot create table: create new file failed.");
        return 0;
    }

    public int newColumnFile(String databaseName, String tableName, String columnName, String columnType) {

        File dbFile = new File("data/databases/" + databaseName);
        if(!dbFile.exists()) {
            ZmdbLogger.log("Cannot create table: database file doesn't exist.");
            return 0;
        }

        File tableFile = new File("data/databases/" + databaseName + "/" + tableName);
        if(!tableFile.exists()) {
            ZmdbLogger.log("Cannot create column: table file doesn't exist.");
            return 0;
        }

        File columnFile = new File("data/databases/" + databaseName + "/" + tableName + "/" + columnName);
        if(columnFile.exists()) {
            ZmdbLogger.log("Cannot create column: column file already exists.");
            return 0;
        }

        try {
            if(!columnFile.createNewFile()) {
                ZmdbLogger.log("Cannot create column: create new file failed.");
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //writing initial stuff to file

        //declaring type of data
        writeToFile("@Type=" + columnType + "\n", columnFile);

        return 1;
    }

    public static int writeToFile(String text, File file) {
        try {
            FileWriter myWriter = new FileWriter(file, true);
            myWriter.write(text);
            myWriter.close();
            ZmdbLogger.log("Successfully wrote to file: " + file.getAbsolutePath());
        } catch (IOException e) {
            ZmdbLogger.log("An error occurred while writing to " + file.getAbsolutePath());
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    public static int writeToColumn(String text, String databaseName, String tableName, String columnName) {
        File file = new File("data/databases/" + databaseName + "/" + tableName + "/" + columnName);
        if(!file.exists()) {
            ZmdbLogger.log("Error writing row: " + file.getPath() + " does not exist.");
            return 0;
        }
        return writeToFile(text + VALUE_SEPARATOR, file);
    }


}

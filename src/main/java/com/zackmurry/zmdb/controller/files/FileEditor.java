package com.zackmurry.zmdb.controller.files;

import com.zackmurry.zmdb.ZmdbLogger;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.*;

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

    /**
     * Writes to a file without any logging nor return values. Useful for writing to the log.txt file
     * @param text text to write
     * @param file file to write to
     */
    public static void silentWriteToFile(String text, File file) {
        try {
            FileWriter myWriter = new FileWriter(file, true);
            myWriter.write(text);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int silentWriteToStartOfFile(String text, File file) {
        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder fileText = new StringBuilder();
            while(true) {
                String readLine = br.readLine();
                if(readLine == null) break;
                fileText.append(readLine).append("\n");
            }
            return silentReplaceFileText(text + fileText.toString(), file);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    public static int writeToColumn(String text, String databaseName, String tableName, String columnName) {
        File file = new File("data/databases/" + databaseName + "/" + tableName + "/" + columnName);
        if(!file.exists()) {
            ZmdbLogger.log("Error writing row: " + file.getPath() + " does not exist.");
            return 0;
        }
        return writeToFile(text + VALUE_SEPARATOR, file);
    }

    public static int silentRemoveAllTextFromFile(File file) {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(""); //since append mode is off, it just sets the text to ""
            fw.close();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int silentReplaceFileText(String text, File file) {
        try {
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(text);
            myWriter.close();
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int deleteDatabaseFile(String databaseName) {
        File file = new File("data/databases/" + databaseName);
        if(!file.exists()) {
            ZmdbLogger.log("Could not delete database " + databaseName + " because the file could not be found.");
            return 0;
        }
        try{
            FileUtils.deleteDirectory(file);
            return 1;
        } catch (Exception e) {
            ZmdbLogger.log("Error while deleting directory of database " + databaseName + ".");
            e.printStackTrace();
            return 0;
        }
    }

    public static int deleteTableFile(String databaseName, String tableName) {
        File file = new File("data/databases/" + databaseName + "/" + tableName);
        if(!file.exists()) {
            ZmdbLogger.log("Could not delete table " + tableName + " in database " + databaseName + " because the file could not be found.");
            return 0;
        }
        try{
            FileUtils.deleteDirectory(file);
            return 1;
        } catch (Exception e) {
            ZmdbLogger.log("Error while deleting directory of table " + databaseName + ".");
            e.printStackTrace();
            return 0;
        }
    }

    public static int deleteColumnFile(String databaseName, String tableName, String columnName) {
        File file = new File("data/databases/" + databaseName + "/" + tableName + "/" + columnName);
        if(!file.exists()) {
            ZmdbLogger.log("Could not delete column " + columnName + " in table " + tableName + " in database " + databaseName + " because the file could not be found.");
            return 0;
        }

        return file.delete() ? 1 : 0;
    }



}

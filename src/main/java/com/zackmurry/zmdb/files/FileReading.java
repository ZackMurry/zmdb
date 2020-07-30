package com.zackmurry.zmdb.files;

import com.zackmurry.zmdb.tools.ZmdbLogger;

import javax.print.DocFlavor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * shout out: https://www.w3schools.com/java/java_files_read.asp
 */
public class FileReading {

    public String readFile(File file) {
        StringBuilder out = new StringBuilder();
        try {
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                out.append(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            ZmdbLogger.log("An error occurred while reading file " + file + ".");
            e.printStackTrace();
        }

        return out.toString();
    }

    public static String readFirstLine(File file) {
        String out = "";
        try {
            Scanner myReader = new Scanner(file);
            if (myReader.hasNextLine()) {
                out = myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            ZmdbLogger.log("An error occurred while reading file " + file + ".");
            e.printStackTrace();
        }

        return out;
    }

    /**
     * useful for reading columns
     * @param file file to read
     * @return returns a string of all of the lines
     */
    public static String getAllLinesButFirst(File file) {
        StringBuilder out = new StringBuilder();
        try {
            Scanner myReader = new Scanner(file);
            myReader.nextLine(); //skipping first line
            while (myReader.hasNextLine()) {
                out.append(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            ZmdbLogger.log("An error occurred while reading file " + file + ".");
            e.printStackTrace();
        }

        return out.toString();
    }

    public static String[] getFileLines(File file) {
        ArrayList<String> outList = new ArrayList<>();
        try {
            Scanner fileReader = new Scanner(file);
            while (fileReader.hasNextLine()) {
                outList.add(fileReader.nextLine());
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            ZmdbLogger.log("An error occurred while reading file " + file + ".");
            e.printStackTrace();
        }
        return outList.toArray(String[]::new); //converting to String[]
    }

    /**
     * gets an integer from an index from a file. stops once it finds the index.
     * @param file file to read from
     * @param index String to look to when getting the int
     * @return the integer it finds. if none, -1;
     */
    public static int readIntFromIndex(File file, String index) {
        String[] lines = getFileLines(file);
        for(String line : lines) {
            System.out.println(line);
            if(line.startsWith(index)) {
                line = line.replace(index,"");
                try{
                    return Integer.parseInt(line);
                } catch (Exception e) { //continues looking for another index
                    e.printStackTrace();
                    ZmdbLogger.log("Unable to get integer from index " + index + " from file " + file.getPath() + ". Continuing to look for another index.");
                }
            }
        }
        //if nothing was found or Integer.parseInt failed
        return -1;
    }

    public static String getTypeFromColumn(String databaseName, String tableName, String columnName) {
        File columnFile = new File("data/databases/" + databaseName + "/" + tableName + "/" + columnName + ".txt");
        if(!columnFile.exists()) {
            ZmdbLogger.log("Couldn't get type from column " + columnName + " in table " + tableName + " in database " + databaseName + " because the file couldn't be found.");
            return "";
        }
        String firstLine = readFirstLine(columnFile);
        firstLine = firstLine.replace(FileEditor.TYPE_INDICATOR, "");
        return firstLine;
    }

    public static boolean columnExists(String databaseName, String tableName, String columnName) {
        File columnFile = new File("data/databases/" + databaseName + "/" + tableName + "/" + columnName + ".txt");
        return columnFile.exists();
    }

    public static boolean tableExists(String databaseName, String tableName) {
        File tableFile = new File("data/databases/" + databaseName + "/" + tableName);
        return tableFile.exists();
    }

    public static boolean databaseExists(String databaseName) {
        File databaseFile = new File("data/databases/" + databaseName);
        return databaseFile.exists();
    }

    public static String getTableIndexColumn(String databaseName, String tableName) {
        File detailsFile = new File("data/databases/" + databaseName + "/" + tableName + "/details.txt");
        if(!detailsFile.exists()) {
            ZmdbLogger.log("Unable to get index column from table " + tableName + " in database " + databaseName + " because the details file couldn't be found.");
            return "";
        }
        return readFirstLine(detailsFile).replace(FileEditor.INDEX_INDICATOR, "");
    }

    public static String readLogData() {
        File logFile = new File("log.txt");
        if(!logFile.exists()) {
            return "Unable to get log file.";
        }

        String[] lines = getFileLines(logFile);
        StringBuilder outBuilder = new StringBuilder();

        for(String line : lines) {
            outBuilder.append(line).append("\n");
        }
        return outBuilder.toString();
    }

}

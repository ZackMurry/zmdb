package com.zackmurry.zmdb.files;

import com.zackmurry.zmdb.entities.Column;
import com.zackmurry.zmdb.entities.Database;
import com.zackmurry.zmdb.entities.Table;
import com.zackmurry.zmdb.tools.ZmdbLogger;
import com.zackmurry.zmdb.controller.proto.ProtoRow;
import com.zackmurry.zmdb.settings.CustomizationPort;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class FileEditor {

    //todo add ability to change these (would need to store them in settings.txt) (mostly just VALUE_SEPARATOR)
    public static final String VALUE_SEPARATOR = "@n%"; //maybe change to something that doesn't have spaces
    public static final String TYPE_INDICATOR = "@Type=";
    public static final String INDEX_INDICATOR = "@Index=";

    public static final String PORT_INDICATOR = "@Port=";

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
    public static int newDatabaseFile(String name) {
        File dbFile = new File("data/databases/" + name);

        if(!dbFile.exists()) {
            if(!dbFile.mkdir()) return 0;
        }

        return 1;
    }

    public static int newTableFile(String databaseName, String tableName) {
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




        if(!tableFile.mkdir()) return 0; //mkdir() returns true if it's successful

        File detailsFile = new File("data/databases/" + databaseName + "/" + tableName + "/details.txt"); //file for info about the table
        try {
            if(!detailsFile.createNewFile()) {
                ZmdbLogger.log("An error occurred while creating the details file of " + tableName + " in database " + databaseName + ".");
                return 0;
            }
        } catch (IOException e) {
            ZmdbLogger.log("IOException was made while creating the details file of " + tableName + " in database " + databaseName + ".");
            e.printStackTrace();
            return 0;
        }

        silentReplaceFileText(INDEX_INDICATOR + "NULL", detailsFile);

        return 1;
    }

    public static int newColumnFile(String databaseName, String tableName, String columnName, String columnType) {
        File tableFile = new File("data/databases/" + databaseName + "/" + tableName);
        if(!tableFile.exists()) {
            ZmdbLogger.log("Cannot create column: table file doesn't exist.");
            return 0;
        }

        //working with index columns
        String[] listTableFiles = tableFile.list();
        if(listTableFiles != null) {
            if(listTableFiles.length < 2) {
                File detailsFile = new File("data/databases/" + databaseName + "/" + tableName + "/details.txt");
                if(!detailsFile.exists()) {
                    ZmdbLogger.log("No details file found for table " + tableName + ".");
                }
                if(setIndexOfTable(detailsFile, columnName) != 1) {
                    ZmdbLogger.log("Unable to set index of table " + tableName + ".");
                }
            }
        }


        File columnFile = new File("data/databases/" + databaseName + "/" + tableName + "/" + columnName + ".txt");
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
        writeToFile(TYPE_INDICATOR + columnType + "\n", columnFile);

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
     * same thing as writeToFile, but append mode is off
     * @param text text to write
     * @param file file to write to
     * @return
     */
    public static int replaceFileText(String text, File file) {
        try {
            FileWriter myWriter = new FileWriter(file);
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
        File file = new File("data/databases/" + databaseName + "/" + tableName + "/" + columnName + ".txt");
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
        File file = new File("data/databases/" + databaseName + "/" + tableName + "/" + columnName + ".txt");
        if(!file.exists()) {
            ZmdbLogger.log("Could not delete column " + columnName + " in table " + tableName + " in database " + databaseName + " because the file could not be found.");
            return 0;
        }

        return file.delete() ? 1 : 0;
    }

    public static int deleteRowFromTable(String databaseName, String tableName, ProtoRow protoRow) {
        File tableFile = new File("data/databases/" + databaseName + "/" + tableName);
        if(!tableFile.exists()) {
            ZmdbLogger.log("Could not delete row in table " + tableName + " in database " + databaseName + " because the file couldn't be found.");
            return 0;
        }
        File[] columnFiles = tableFile.listFiles();
        if(columnFiles == null) {
            ZmdbLogger.log("Could not delete row in table " + tableName + " in database " + databaseName + " because no column files could be found.");
            return 0;
        }
        for(File columnFile : columnFiles) {
            int index = -1;
            for (int i = 0; i < protoRow.getOrder().size(); i++) {
                if(columnFile.getName().equals(protoRow.getOrder().get(i))) index = i;
            }
            if(index == -1) {
                ZmdbLogger.log("Could not delete row in table " + tableName + " in database " + databaseName + " because the order was wrong.");
                return 0;
            }
            String[] rowsInColumn = FileReading.getAllLinesButFirst(columnFile).split(VALUE_SEPARATOR);
            int deleteIndex = -1;
            for (int i = 0; i < rowsInColumn.length; i++) {
                if(rowsInColumn[i].equals(protoRow.getData().get(index))) deleteIndex = i;
            }
            if(deleteIndex == -1) {
                ZmdbLogger.log("Could not delete row in table " + tableName + " in database " + databaseName + " because the delete index wasn't found.");
                return 0;
            }
            if(deleteRowFromColumn(columnFile, deleteIndex) != 1) {
                return 0;
            }
        }
        return 1;

    }

    public static int deleteRowFromTableByIndex(String databaseName, String tableName, int index) {
        File tableFile = new File("data/databases/" + databaseName + "/" + tableName);
        if(!tableFile.exists()) {
            ZmdbLogger.log("Couldn't delete row " + index + " from table " + tableName + " in database " + databaseName + " because the table file doesn't exist.");
            return 0;
        }
        File[] columnFiles = tableFile.listFiles();

        if(columnFiles == null) {
            ZmdbLogger.log("No columns to delete rows from"); //also indicates that something went wrong with the details.txt process
            return 0;
        }

        for(File columnFile : columnFiles) {
            if(columnFile.getName().equals("details.txt")) continue;
            if(deleteRowFromColumn(columnFile, index) != 1) {
                return 0;
            }
        }

        return 1;
    }

    public static int deleteRowFromColumn(File file, int deleteIndex) {
        if(!file.exists()) {
            ZmdbLogger.log("Couldn't delete row from column because the file (" + file.getAbsolutePath() + ") could not be found.");
            return 0;
        }
        ArrayList<Object> rows = new ArrayList<>(Arrays.asList(FileReading.getAllLinesButFirst(file).split(VALUE_SEPARATOR)));
        rows.remove(deleteIndex);
        replaceRows(file, rows);
        return 1;
    }

    public static void replaceRows(File file, ArrayList<Object> rows) {
        String firstLine = FileReading.readFirstLine(file);
        String rowString = rows.toString().replace(", ", VALUE_SEPARATOR).replace("[", "").replace("]", VALUE_SEPARATOR);


        silentReplaceFileText(firstLine + "\n" + rowString, file);
    }

    /**
     * @param file details file of table
     */
    public static int setIndexOfTable(File file, String index) {
        if(!file.exists()) {
            ZmdbLogger.log("Couldn't change file " + file + " because it doesn't exist.");
            return 0;
        }
        silentReplaceFileText(INDEX_INDICATOR + index, file);
        return 1;
    }

    public static int changeTableIndex(String databaseName, String tableName, String columnName) {
        File detailsFile = new File("data/databases" + "/" + databaseName + "/" + tableName + "/details.txt");
        if(!detailsFile.exists()) {
            try {
                if(!detailsFile.createNewFile()) {
                    ZmdbLogger.log("Unable to create a new details file for " + tableName + " in database " + databaseName + ".");
                    return 0;
                }
            } catch (IOException e) {
                ZmdbLogger.log("IOException was made while creating the details file of " + tableName + " in database " + databaseName + ".");
                e.printStackTrace();
                return 0;
            }
        }

        setIndexOfTable(detailsFile, columnName);
        return 1;
    }

    public static int deleteAllSubdirectories(File file) {
        File[] files = file.listFiles();
        if(files != null) {
            for (File subdir : files) {
                if(deleteFolder(subdir) != 1) return 0;
            }
        }
        return 1;

    }

    public static int deleteFolder(File file) {
        File[] files = file.listFiles();
        if(files != null) {
            for(File subdirectory : files) {
                deleteFolder(subdirectory);
            }
        }
        return file.delete() ? 1 : 0;
    }

    public static int deleteAllSubdirectoriesExcept(File file, HashSet<String> except) {
        File[] files = file.listFiles();
        if(files != null) {
            for (File subdir : files) {
                if(except.contains(subdir.getName())) continue;
                if(deleteFolder(subdir) != 1) return 0;
            }
        }
        return 1;

    }

    public static void updatePortInSettings(int newPort) {
        File settingsFile = new File("settings.txt");
        if(!settingsFile.exists()) {
            try {
                if(!settingsFile.createNewFile()) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                ZmdbLogger.log("Error creating settings file.");
                return;
            }
        }

        replaceFileText(PORT_INDICATOR + newPort, settingsFile); //todo rework this once i add more settings
    }

    public static int getPortFromSettings() {
        File settingsFile = new File("settings.txt");
        if(!settingsFile.exists()) {
            //just set to the default port
            return CustomizationPort.DEFAULT_PORT;
        }
        return FileReading.readIntFromIndex(settingsFile, PORT_INDICATOR);
    }

    /**
     * todo this could definitely be cleaner
     * @param databaseName database to create file in
     * @param tableName table to create file in
     * @param columnName name of columnFile
     * @param column column details to use (useful for rows)
     * @param columnType type of column
     * @return
     */
    public static int newColumnFileFromColumnObject(String databaseName, String tableName, String columnName, Column<?> column, String columnType) {
        File columnFile = new File("data/databases/" + databaseName + "/" + tableName + "/" + columnName + ".txt");
        if(columnFile.exists()) {
            ZmdbLogger.log("Cannot create column file: column file already exists.");
            return 0;
        }
        try {
            if(!columnFile.createNewFile()) {
                ZmdbLogger.log("Could not create a column file for column " + columnName + " for some reason.");
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeToFile(TYPE_INDICATOR + columnType + "\n", columnFile);
        for(Object row : column.getAllRows()) {
            if(writeToColumn(row.toString(), databaseName, tableName, columnName) != 1) {
                ZmdbLogger.log("Error writing rows to column file " + columnName + ".");
                return 0;
            }
        }

        //setting the index of the table to column.getName() if this is the only column there
        File tableFile = new File("data/databases/" + databaseName + "/" + tableName);
        File[] listOfTableFiles = tableFile.listFiles();
        if(listOfTableFiles == null) {
            ZmdbLogger.log("Error: file for table " + tableName + " has no subdirectories when it should have at least two.");
            return 0;
        }

        if(listOfTableFiles.length == 2) {
            File tableDetailsFile = new File("data/databases/" + databaseName + "/" + tableName + "/details.txt");
            return setIndexOfTable(tableDetailsFile, columnName);
        }

        return 1;
    }

    //todo see if i actually need a table as a parameter (could get originalTableFile details from table)
    public static int newTableFileFromTableObject(String databaseName, String tableName, Table table, File originalTableFile) {
        File tableFile = new File("data/databases/" + databaseName + "/" + tableName);
        if(tableFile.exists()) {
            ZmdbLogger.log("Cannot create table file: table file already exists.");
            return 0;
        }

        if(newTableFile(databaseName, tableName) != 1) {
            return 0;
        }

        File[] listOriginalTableFiles = originalTableFile.listFiles();

        if(listOriginalTableFiles == null) {
            ZmdbLogger.log("Error copying table: the original table should have at least one subdirectory.");
            return 0;
        }

        for(File columnFile : listOriginalTableFiles) {
            try {
                //you have to create each file before you copy it for some reason
                //File newColumnFile = new File("data/databases/")

                Files.copy(
                        Paths.get(columnFile.getPath()),
                        Paths.get(new File("data/databases/" + databaseName + "/" + tableName + "/" + columnFile.getName()).getPath()),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                ZmdbLogger.log("Unable to copy a column file to its destination.");
                return 0;
            }
        }

        return 1;
    }

    /**
     * @param newDatabaseName what to name the new database
     * @param copyFromDatabase database to copy from
     * @return 1 for success, 0 for fail
     */
    public static int copyPasteDatabase(String newDatabaseName, Database copyFromDatabase) {
        File copyFromFile = new File("data/databases/" + copyFromDatabase.getName());
        if(!copyFromFile.exists()) {
            ZmdbLogger.log("Unable to find file for database " + copyFromDatabase.getName() + ".");
            return 0;
        }

        File newDatabaseFile = new File("data/databases/" + newDatabaseName);
        if(newDatabaseFile.exists()) {
            if(!newDatabaseFile.delete()) {
                ZmdbLogger.log("Unable to delete file for database " + newDatabaseName + " to paste new information there.");
                return 0;
            }
        }

        if(!newDatabaseFile.mkdir()) {
            ZmdbLogger.log("Unable to make directory for database " + newDatabaseName + ".");
            return 0;
        }

        File[] tableFiles = copyFromFile.listFiles();

        //if there's nothing in the database, we're done
        if(tableFiles == null) {
            return 1;
        }

        int out = 1;

        for(File tableFile : tableFiles) {
            String tableName = tableFile.getName();
            if(!(new File("data/databases/" + newDatabaseName + "/" + tableName).mkdir())) {
                ZmdbLogger.log("Unable to create a file for table " + tableFile.getName() + ". Continuing with the paste.");
                out = 0;
                continue;
            }
            File[] columnFiles = tableFile.listFiles();
            if(columnFiles == null) {
                //there should be at least a details file, so this probably won't happen unless the user manually deletes/adds something
                ZmdbLogger.log("Unable to find column files for table " + tableFile.getName() + ". Continuing with the paste.");
                out = 0;
                continue;
            }

            //column files could also be the details.txt file
            for(File columnFile : columnFiles) {
                String columnName = columnFile.getName().replace(".txt", "");
                File newColumnFile = new File("data/databases/" + newDatabaseName + "/" + tableName + "/" + columnName + ".txt");
                if(!newColumnFile.mkdir()) {
                    ZmdbLogger.log("Unable to create a file for column " + columnName + " in table " + tableName + ". Continuing with the paste.");
                    out = 0;
                    continue;
                }
                Path oldColumnPath = Paths.get(columnFile.getPath());
                Path newColumnPath = Paths.get(newColumnFile.getPath());
                try {
                    Files.copy(
                            oldColumnPath,
                            newColumnPath,
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                    ZmdbLogger.log("Unable to paste column " + columnName + ". Continuing the paste.");
                    out = 0;
                }

            }
        }
        return out;

    }

    public static int renameDatabaseFile(String databaseName, String newDatabaseName) {
        System.out.println(databaseName + ", " + newDatabaseName);
        File databaseFile = new File("data/databases/" + databaseName);
        if(!databaseFile.isDirectory()) {
            ZmdbLogger.log("Unable to rename database " + databaseName + " because the file could not be found.");
            return 0;
        }

        //checking that the file that it's being renamed to doesn't already exist
        File newDatabaseFile = new File("data/databases/" + newDatabaseName);
        if(newDatabaseFile.isDirectory()) {
            ZmdbLogger.log("Unable to rename database " + databaseName + " to " + newDatabaseName + " because a database already exists with the same name.");
            return 0;
        }

        if(databaseFile.renameTo(newDatabaseFile)) {
            return 1;
        }
        else {
            ZmdbLogger.log("Unable to rename database " + databaseName + " to " + newDatabaseName + ". This might be because you have the file open in file explorer. Try closing it and trying again.");
            return 0;
        }

    }

    public static int renameTableFile(String databaseName, String tableName, String newTableName) {
        File tableFile = new File("data/databases/" + databaseName + "/" + tableName);
        if(!tableFile.exists()) {
            ZmdbLogger.log("Unable to rename table " + tableName + " because the file could not be found.");
            return 0;
        }

        //checking that the there isn't already a table with the new name
        File newTableFile = new File("data/databases/" + databaseName + "/" + newTableName);
        if(newTableFile.exists()) {
            ZmdbLogger.log("Unable to rename table " + tableName + " in database " + databaseName + " to " + newTableName + " because a table already exists with the same name in the same database.");
            return 0;
        }

        if(tableFile.renameTo(newTableFile)) {
            return 1;
        }
        else {
            ZmdbLogger.log("Unable to rename table " + databaseName + " in database " + databaseName + " to " + newTableName + ". This might be because you have file open in file explorer. Try closing it and trying again.");
            return 0;
        }
    }

    public static int renameColumnFile(String databaseName, String tableName, String columnName, String newColumnName) {
        File columnFile = new File("data/databases/" + databaseName + "/" + tableName + "/" + columnName + ".txt");
        if(!columnFile.exists()) {
            ZmdbLogger.log("Unable to rename column " + columnName + " in table " + tableName + " in database " + databaseName + " because the file could not be found.");
            return 0;
        }

        //checking that there isn't already a column there with the new name
        File newColumnFile = new File("data/databases/" + databaseName + "/" + tableName + "/" + newColumnName + ".txt");
        if(newColumnFile.exists()) {
            ZmdbLogger.log("Unable to rename column " + columnName + " in table " + tableName + " in database " + databaseName + " to " + newColumnName + " because a column already exists with the same name in the same place.");
            return 0;
        }

        if(!columnFile.renameTo(newColumnFile)) {
            ZmdbLogger.log("Unable to rename column " + columnName + " to " + newColumnName + " in table " + tableName + " in database " + databaseName + ". This might be because you have file open in file explorer. Try closing it and trying again.");
            return 0;
        }

        //checking if the index column of the table is this column
        if(FileReading.getTableIndexColumn(databaseName, tableName).equals(columnName)) {
            return setIndexOfTable(new File("data/databases/" + databaseName + "/" + tableName + "/details.txt"), newColumnName);
        }
        return 1;
    }

}

package com.zackmurry.zmdb.services;

import com.zackmurry.zmdb.tools.RequestPathHelper;
import com.zackmurry.zmdb.tools.ZmdbLogger;
import com.zackmurry.zmdb.files.FileEditor;
import com.zackmurry.zmdb.controller.proto.ProtoRow;
import com.zackmurry.zmdb.dao.DatabaseDao;
import com.zackmurry.zmdb.entities.Column;
import com.zackmurry.zmdb.entities.Database;
import com.zackmurry.zmdb.controller.proto.ProtoColumn;
import com.zackmurry.zmdb.entities.Table;
import com.zackmurry.zmdb.files.FileReading;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class DatabaseService {

    @Autowired
    private DatabaseDao databaseDao;

    private FileEditor fileEditor = new FileEditor();

    public FileEditor getFileEditor() {
        return fileEditor;
    }

    public int addDatabase(Database database) {
        if(isUnsafe(database.getName())) return 0;

        if(databaseDao.databaseExists(database.getName())) {
            ZmdbLogger.log("Cannot create database with name " + database.getName() + " because one already exists.");
            return 0;
        }
        //adding a new file with the database's name
        if(FileEditor.newDatabaseFile(database.getName()) != 1) {
            return 0;
        }
        return databaseDao.addDatabase(database);
    }

    /**
     * include___ is for adding objects without creating files
     */
    public int includeDatabase(Database database) {
        if(isUnsafe(database.getName())) return 0;
        return databaseDao.addDatabase(database);
    }

    public int addTable(Table table, String databaseName) {
        if(isUnsafe(table.getName())) return 0;
        if(databaseDao.tableExists(databaseName, table.getName())) {
            ZmdbLogger.log("Cannot create table with name " + table.getName() + " in database " + databaseName + " because one already exists.");
            return 0;
        }
        if(databaseDao.addTable(table, databaseName) == 1) {
            return FileEditor.newTableFile(databaseName, table.getName());
        }
        return 0;
    }

    public int includeTable(Table table, String databaseName) {
        if(isUnsafe(table.getName())) return 0;
        return databaseDao.addTable(table, databaseName);
    }

    public int getDatabaseCount() {
        return databaseDao.getDatabaseCount();
    }

    public Optional<Database> getDatabaseByName(String name) {
        return databaseDao.getDatabaseByName(name);
    }

    public int getTableCountOfDatabase(String databaseName) {
        return databaseDao.getTableCountOfDatabase(databaseName);
    }

    public List<Database> getAllDatabases() {
        return databaseDao.getAllDatabases();
    }

    public List<Table> getTablesFromDatabase(String databaseName) {
        return databaseDao.getTablesFromDatabase(databaseName);
    }

    public int addColumnToTable(String databaseName, String tableName, ProtoColumn protoColumn) {
        if(isUnsafe(protoColumn.getName())) return 0;
        if(databaseDao.columnExists(databaseName, tableName, protoColumn.getName())) {
            ZmdbLogger.log("Unable to create column with name " + protoColumn.getName() + " in table " + tableName + " in database " + databaseName + " because one alredy exists.");
            return 0;
        }

        //yikes 0.0
        Optional<Column<?>> optionalColumn = buildColumn(protoColumn.getType(), databaseName, tableName, protoColumn.getName());
        if(optionalColumn.isEmpty()) {
            ZmdbLogger.log("Couldn't add column " + protoColumn.getName() + " to table " + tableName + " in database " +  databaseName + " because the specified type doesn't exist or is not supported.");
            return 0;
        }
        if(databaseDao.addColumnToTable(databaseName, tableName, optionalColumn.get()) != 1) {
            return 0;
        }
        return FileEditor.newColumnFile(databaseName, tableName, protoColumn.getName(), protoColumn.getType());
    }

    public int includeColumnInTable(String databaseName, String tableName, ProtoColumn protoColumn) {
        if(isUnsafe(protoColumn.getName())) return 0;
        Optional<Column<?>> optionalColumn = buildColumn(protoColumn.getType(), databaseName, tableName, protoColumn.getName());
        if(optionalColumn.isEmpty()) {
            ZmdbLogger.log("Couldn't include column " + protoColumn.getName() + " in table " + tableName + " in database " + databaseName + " because the specified type doesn't exist or is not supported.");
            return 0;
        }
        return databaseDao.addColumnToTable(databaseName, tableName, optionalColumn.get());
    }

    /**
     *
     * @param type the type of column to be generated
     * @param databaseName the databaseName of the column
     * @param tableName the tableName of the column
     * @param name the name of the column
     * @return returns the built column
     */
    public Optional<Column<?>> buildColumn(String type, String databaseName, String tableName, String name) {
        switch(type) {
            case "Boolean":
                return Optional.of(new Column<Boolean>(databaseName, tableName, name));
            case "String":
                return Optional.of(new Column<String>(databaseName, tableName, name));
            case "Integer":
                return Optional.of(new Column<Integer>(databaseName, tableName, name));
            case "Double":
                return Optional.of(new Column<Double>(databaseName, tableName, name));
            case "Float":
                return Optional.of(new Column<Float>(databaseName, tableName, name));
            case "Character":
                return Optional.of(new Column<Character>(databaseName, tableName, name));
            case "Byte":
                return Optional.of(new Column<Byte>(databaseName, tableName, name));
            case "Short":
                return Optional.of(new Column<Short>(databaseName, tableName, name));
            case "Long":
                return Optional.of(new Column<Long>(databaseName, tableName, name));
            case "UUID":
                return Optional.of(new Column<UUID>(databaseName, tableName, name));
            case "UUID-auto":
                return Optional.of(new Column<UUID>(databaseName, tableName, name, true));
            default:
                return Optional.empty();
        }
    }

    public int addRowToTable(String databaseName, String tableName, ArrayList<Object> data, ArrayList<String> order) {
        return databaseDao.addRowToTable(databaseName, tableName, data, order);
    }

    public int includeRowInColumn(String databaseName, String tableName, String columnName, Object data) {
        return databaseDao.includeRowInColumn(databaseName, tableName, columnName, data);
    }

    public Table getTableByName(String databaseName, String tableName) {
        Optional<Table> optionalTable = databaseDao.getTable(databaseName, tableName);
        if(optionalTable.isEmpty()) return null;
        return optionalTable.get();
    }

    public Column<?> getColumnByName(String databaseName, String tableName, String columnName) {
        Optional<Column<?>> optionalColumn = databaseDao.getColumn(databaseName, tableName, columnName);
        if(optionalColumn.isEmpty()) {
            ZmdbLogger.log("Unable to get column " + columnName + ".");
            return null;
        }
        return optionalColumn.get();
    }

    public boolean tableContains(String databaseName, String tableName, ProtoRow protoRow) {
        return databaseDao.tableContains(databaseName, tableName, protoRow.getData(), protoRow.getOrder());
    }

    //todo add delete all databases/tables/columns
    public int deleteDatabaseByName(String databaseName) {
        if(FileEditor.deleteDatabaseFile(databaseName) == 0) return 0;
        return databaseDao.deleteDatabaseByName(databaseName);
    }

    public int deleteTableByName(String databaseName, String tableName) {
        if(FileEditor.deleteTableFile(databaseName, tableName) == 0) return 0;
        return databaseDao.deleteTableByName(databaseName, tableName);
    }

    public int deleteColumnByName(String databaseName, String tableName, String columnName) {
        if(FileEditor.deleteColumnFile(databaseName, tableName, columnName) == 0) return 0;

        //if the index column is this column
        File tableFile = new File("data/databases/" + databaseName + "/" + tableName);
        if(!tableFile.exists()) {
            ZmdbLogger.log("Error: file doesn't exist for table " + tableName + " in database " + databaseName + ". File path used was " + tableFile.getPath() + ".");
            return 0;
        }
        String indexColumnName = FileReading.readFirstLine(new File(tableFile.getPath() + "/details.txt")).replace(FileEditor.INDEX_INDICATOR, "");
        if(indexColumnName.equals(columnName)) {
            String[] tableList = tableFile.list();
            if(tableList == null) {
                ZmdbLogger.log("Error: no files found under table " + tableName + " in database " + databaseName + ". There should at least be a details.txt file.");
                return 0;
            }

            int numOfFilesInTable = tableList.length;

            //if there's only one other column file, then set it as the index column
            if(numOfFilesInTable == 2) {
                File[] listTableFiles = tableFile.listFiles();

                //this probably won't happen since i already counted the number of files
                if(listTableFiles == null) {
                    ZmdbLogger.log("List of table files is null. Unable to update index column of table " + tableName + " in database " + databaseName + ".");
                    return 0;
                }

                String otherColumnName = "";

                for(File subdirectoryOfTableFile : listTableFiles) {
                    if(subdirectoryOfTableFile.getName().equals("details.txt")) continue;

                    //now we're only looking at the single column file
                    otherColumnName = subdirectoryOfTableFile.getName().replace(".txt", "");
                }

                if(otherColumnName.equals("")) {
                    ZmdbLogger.log("Couldn't find name of other column of table " + tableName + " in database " + databaseName + ". Couldn't change index column.");
                    return 0;
                }

                FileEditor.setIndexOfTable(new File(tableFile.getPath() + "/details.txt"), otherColumnName);
                if(databaseDao.changeTableIndex(databaseName, tableName, otherColumnName) != 1) {
                    ZmdbLogger.log("Error while changing index column of table " + tableName + " in database " + databaseName + " as the table coulnd't be found.");
                    return 0;
                }
                ZmdbLogger.log("Automatically updated index column of table " + tableName + " in database " + databaseName + " to " + otherColumnName + ".");

            }

            //if there's multiple other column files
            if(numOfFilesInTable > 2){
                FileEditor.setIndexOfTable(tableFile, "NULL");
                //and log it
                ZmdbLogger.log("Index column deleted. Please set the index column of table " + tableName + " in database " + databaseName + ".");
            }

        }

        return databaseDao.deleteColumnByName(databaseName, tableName, columnName);
    }

    public int deleteRow(String databaseName, String tableName, ProtoRow protoRow) {
        if(FileEditor.deleteRowFromTable(databaseName, tableName, protoRow) != 1) return 0;
        return databaseDao.deleteRow(databaseName, tableName, protoRow);
    }

    public int deleteRowByIndex(String databaseName, String tableName, String index) {
        int intDex;
        try{
            intDex = Integer.parseInt(index);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            ZmdbLogger.log("Couldn't delete row " + index + " in table " + tableName + " in database " + databaseName + " because the index is not a valid integer.");
            return 0;
        }
        if(FileEditor.deleteRowFromTableByIndex(databaseName, tableName, intDex) != 1) {
            return 0;
        }
        return databaseDao.deleteRowByIndex(databaseName, tableName, intDex);
    }

    //somehow accounts for "OFF" :)
    public int changeTableIndex(String databaseName, String tableName, String columnName) {
        if(FileEditor.changeTableIndex(databaseName, tableName, columnName) != 1) {
            return 0;
        }
        return databaseDao.changeTableIndex(databaseName, tableName, columnName);
    }

    public String getTableIndex(String databaseName, String tableName) {
        return databaseDao.getTableIndex(databaseName, tableName);
    }

    public int deleteAllDatabases() {
        if(FileEditor.deleteAllSubdirectories(new File("data/databases")) != 1) {
            ZmdbLogger.log("Error occurred while deleting database files.");
            return 0;
        }
        return databaseDao.deleteAllDatabases();
    }

    public int deleteAllTablesInDatabase(String databaseName) {
        if(FileEditor.deleteAllSubdirectories(new File("data/databases/" + databaseName)) != 1) {
            ZmdbLogger.log("An error occurred while deleting all tables in database " + databaseName + ".");
            return 0;
        }
        return databaseDao.deleteAllTablesInDatabase(databaseName);
    }

    public int deleteAllColumnsInTable(String databaseName, String tableName) {
        HashSet<String> except = new HashSet<>();
        except.add("details.txt");
        File tableFile = new File("data/databases/" + databaseName + "/" + tableName);
        if(FileEditor.deleteAllSubdirectoriesExcept(tableFile, except) != 1) {
            ZmdbLogger.log("Ann error occurred while deleting all columns of table " + tableName + " in database " + databaseName + ".");
            return 0;
        }

        FileEditor.setIndexOfTable(new File(tableFile.getPath() + "/details.txt"), "NULL"); //clearing the index column
        return databaseDao.deleteAllColumnsInTable(databaseName, tableName);
    }


    public ArrayList<?> getAllRowsInColumn(String databaseName, String tableName, String columnName) {
        return databaseDao.getAllRowsInColumn(databaseName, tableName, columnName);
    }

    /**
     * @param databaseName database to copy to
     * @param tableName table to copy to
     * @param columnName column to copy to (doesn't have to exist)
     * @param copyFromPath path to copy from
     * @return 1 for success, 0 for fail
     */
    public int copyPasteColumn(String databaseName, String tableName, String columnName, String copyFromPath) {
        if(isUnsafe(columnName)) return 0;

        Optional<Column<?>> optionalColumn = databaseDao.getColumn(databaseName, tableName, columnName);

        //if i don't need to create a column (because it already exists)
        if(FileReading.columnExists(databaseName, tableName, columnName)) {
            //just delete the target column and move on bc why would i change the file name and replace all the text when i could just
            //delete it and move on like normal:)

            if(optionalColumn.isEmpty()) {
                //this would mean that the arraylist doesn't have a column but the file path does
                ZmdbLogger.log("Error: files and internal data out of sync. Please restart.");
                return 0;
            }
            if(databaseDao.deleteColumnByName(databaseName, tableName, columnName) != 1) return 0;
            if(FileEditor.deleteColumnFile(databaseName, tableName, columnName) != 1) return 0;
        }

        String copyFromDatabaseName = RequestPathHelper.getDatabaseNameFromRequestPath(copyFromPath);
        String copyFromTableName = RequestPathHelper.getTableNameFromRequestPath(copyFromPath, copyFromDatabaseName);
        String copyFromColumnName = RequestPathHelper.getColumnNameFromRequestPath(copyFromPath, copyFromDatabaseName, copyFromTableName);

        //this is the column that is being copied
        if(optionalColumn.isEmpty()) {
            ZmdbLogger.log("Unable to find column " + columnName + " in table " + copyFromTableName + " in database " + copyFromDatabaseName + ". Retrieved from path " + copyFromPath + ".");
            return 0;
        }

        //making a copy of the column
        Column<?> copyFromColumn;
        try {
            copyFromColumn = (Column<?>) optionalColumn.get().clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return 0;
        }

        //configuring the copy
        copyFromColumn.setName(columnName);
        copyFromColumn.setTableName(tableName);
        copyFromColumn.setDatabaseName(databaseName);

        //adding the new column to the arraylist and the files
        databaseDao.addColumnToTable(databaseName, tableName, copyFromColumn);
        return FileEditor.newColumnFileFromColumnObject(databaseName, tableName, columnName, copyFromColumn, FileReading.getTypeFromColumn(copyFromDatabaseName, copyFromTableName, copyFromColumnName));
    }

    public int copyPasteTable(String databaseName, String tableName, String copyFromPath) {
        if(isUnsafe(tableName)) return 0;
        //deleting the table at the paste location if it already exists
        if(FileReading.tableExists(databaseName, tableName)) {
            Optional<Table> optionalTable = databaseDao.getTable(databaseName, tableName);
            if(optionalTable.isEmpty()) {
                //this would mean that the table exists in files but not in the arraylist
                ZmdbLogger.log("Error: files and internal data out of sync. Please restart.");
                return 0;
            }
            if(databaseDao.deleteTableByName(databaseName, tableName) != 1) return 0;
            if(FileEditor.deleteTableFile(databaseName, tableName) != 1) return 0;
        }

        String copyFromDatabaseName = RequestPathHelper.getDatabaseNameFromRequestPath(copyFromPath);
        String copyFromTableName = RequestPathHelper.getTableNameFromRequestPathWithoutFinalSlash(copyFromPath, copyFromDatabaseName);

        Optional<Table> optionalTable = databaseDao.getTable(copyFromDatabaseName, copyFromTableName);
        if(optionalTable.isEmpty()) {
            ZmdbLogger.log("Unable to find table " + tableName + " in database " + copyFromDatabaseName + ". Retrieved from path " + copyFromPath + ".");
            return 0;
        }

        //making a copy of the table
        Table copyFromTable;
        try {
            copyFromTable = (Table) optionalTable.get().clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return 0;
        }

        copyFromTable.setName(tableName);
        copyFromTable.setDatabaseName(databaseName);
        for(Column<?> column : copyFromTable.getAllColumns()) {
            column.setTableName(tableName);
        }

        if(databaseDao.addTable(copyFromTable, databaseName) != 1) return 0;
        return FileEditor.newTableFileFromTableObject(
                databaseName,
                tableName,
                copyFromTable,
                new File("data/databases/" + copyFromDatabaseName + "/" + copyFromTableName));
    }

    public int copyPasteDatabase(String databaseName, String copyFromPath) {
        if(isUnsafe(databaseName)) return 0;
        if(FileReading.databaseExists(databaseName)) {
            Optional<Database> optionalDatabase = databaseDao.getDatabaseByName(databaseName);
            if(optionalDatabase.isEmpty()) {
                ZmdbLogger.log("Error: files and internal data out of sync. Please restart.");
                return 0;
            }

            if(databaseDao.deleteDatabaseByName(databaseName) != 1) return 0;
            if(FileEditor.deleteDatabaseFile(databaseName) != 1) return 0;
        }

        String copyFromDatabaseName = RequestPathHelper.getDatabaseNameFromRequestPathWithoutFinalSlash(copyFromPath);

        Optional<Database> optionalDatabase = databaseDao.getDatabaseByName(copyFromDatabaseName);

        if(optionalDatabase.isEmpty()) {
            ZmdbLogger.log("Unable to find database " + copyFromDatabaseName + ". Retrieved from path " + copyFromPath + ".");
            return 0;
        }

        //making a copy of the database
        Database copyFromDatabase;
        try {
            copyFromDatabase = (Database) optionalDatabase.get().clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return 0;
        }

        if(FileEditor.copyPasteDatabase(databaseName, copyFromDatabase) != 1) return 0;

        copyFromDatabase.setName(databaseName); //sets database name for its children too:)
        return databaseDao.addDatabase(copyFromDatabase);
    }

    public int renameDatabase(String databaseName, String newDatabaseName) {
        if(isUnsafe(newDatabaseName)) return 0;
        if(FileEditor.renameDatabaseFile(databaseName, newDatabaseName) != 1) {
            ZmdbLogger.log("Unable to rename the file for database " + databaseName + " for some reason.");
            return 0;
        }
        return databaseDao.renameDatabase(databaseName, newDatabaseName);
    }

    public int renameTable(String databaseName, String tableName, String newTableName) {
        if(isUnsafe(newTableName)) return 0;
        if(FileEditor.renameTableFile(databaseName, tableName, newTableName) != 1) {
            ZmdbLogger.log("Unable to rename the file for table " + tableName + " in database " + databaseName + " for some reason.");
            return 0;
        }
        return databaseDao.renameTable(databaseName, tableName, newTableName);
    }

    public int renameColumn(String databaseName, String tableName, String columnName, String newColumnName) {
        if(isUnsafe(newColumnName)) return 0;
        if(FileEditor.renameColumnFile(databaseName, tableName, columnName, newColumnName) != 1) {
            ZmdbLogger.log("Unable to rename the file for column " + columnName + " in table " + tableName + "  in database " + databaseName + " for some reason.");
            return 0;
        }
        return databaseDao.renameColumn(databaseName, tableName, columnName, newColumnName);
    }

    /**
     * checks if a string contains reserved words/chars
     * @return true if fine, else false
     */
    public static boolean isUnsafe(String string) {

        if(string.contains("/") || string.contains(".") || string.contains(" ")) {
            ZmdbLogger.log("Input cannot contain '.' or '/'. Please rename " + string + " and try again.");
            return true;
        }

        switch(string) {
            case "contains":
            case "NULL":
            case "index":
            case "paste":
            case "rows":
            case "count":
            case "OFF":
                break;
            default:
                return false;
        }

        ZmdbLogger.log("Error: " + string + " contains a reserved character is a reserved word. Please change it and try again.");
        return true;

    }

    public ArrayList<ArrayList<Object>> getRowByIndexColumn(String databaseName, String tableName, Object value) {
        Optional<Table> optionalTable = databaseDao.getTable(databaseName, tableName);
        ArrayList<Object> emptyObjectArrayList = new ArrayList<>();
        ArrayList<ArrayList<Object>> emptyArrayListList = new ArrayList<>();
        emptyArrayListList.add(emptyObjectArrayList);
        if(optionalTable.isEmpty()) {
            ZmdbLogger.log("Cannot get row from table " + tableName + " in database " + databaseName + " because it doesn't exist.");
            return emptyArrayListList;
        }
        Table table = optionalTable.get();
        Optional<Column<?>> optionalIndexColumn = table.getIndexColumn();
        if(optionalIndexColumn.isEmpty()) {
            ZmdbLogger.log("Cannot get row by index column from table " + tableName + "in database " + databaseName + " because it does not have an index column.");
            return emptyArrayListList;
        }
        Column<?> indexColumn = optionalIndexColumn.get();
        int rowIndex = indexColumn.getIndexOfItem(value);
        if(rowIndex < 0) {
            return emptyArrayListList;
        }

        Optional<ArrayList<ArrayList<Object>>> optionalArrayListList = table.getRowByIndex(rowIndex); //yikes. see documentation for table.getRowByIndex

        if(optionalArrayListList.isEmpty()) {
            ZmdbLogger.log("Error getting row from " + tableName + " in database " + databaseName + " from index " + rowIndex + ".");
            return emptyArrayListList;
        }

        return optionalArrayListList.get();

    }
}

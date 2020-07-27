package com.zackmurry.zmdb.services;

import com.zackmurry.zmdb.ZmdbLogger;
import com.zackmurry.zmdb.files.FileEditor;
import com.zackmurry.zmdb.controller.proto.ProtoRow;
import com.zackmurry.zmdb.dao.DatabaseDao;
import com.zackmurry.zmdb.entities.Column;
import com.zackmurry.zmdb.entities.Database;
import com.zackmurry.zmdb.controller.proto.ProtoColumn;
import com.zackmurry.zmdb.entities.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DatabaseService {

    @Autowired
    private DatabaseDao databaseDao;

    private FileEditor fileEditor = new FileEditor();

    public FileEditor getFileEditor() {
        return fileEditor;
    }

    public int addDatabase(Database database) {
        if(databaseDao.databaseExists(database.getName())) {
            ZmdbLogger.log("Cannot create database with name " + database.getName() + " because one already exists.");
            return 0;
        }
        //adding a new file with the database's name
        if(fileEditor.newDatabaseFile(database.getName()) != 1) {
            return 0;
        }
        return databaseDao.addDatabase(database);
    }

    /**
     * include___ is for adding objects without creating files
     */
    public int includeDatabase(Database database) {
        return databaseDao.addDatabase(database);
    }

    public int addTable(Table table, String databaseName) {
        if(databaseDao.tableExists(databaseName, table.getName())) {
            ZmdbLogger.log("Cannot create table with name " + table.getName() + " in database " + databaseName + " because one already exists.");
            return 0;
        }
        if(databaseDao.addTable(table, databaseName) == 1) {
            return fileEditor.newTableFile(databaseName, table.getName());
        }
        return 0;
    }

    public int includeTable(Table table, String databaseName) {
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
        return fileEditor.newColumnFile(databaseName, tableName, protoColumn.getName(), protoColumn.getType());
    }

    public int includeColumnInTable(String databaseName, String tableName, ProtoColumn protoColumn) {
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
        //todo add UUID type (maybe add option to auto-generate it)

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

    public int changeTableIndex(String databaseName, String tableName, String columnName) {
        if(FileEditor.changeTableIndex(databaseName, tableName, columnName) != 1) {
            return 0;
        }
        return databaseDao.changeTableIndex(databaseName, tableName, columnName);
    }

    public String getTableIndex(String databaseName, String tableName) {
        return databaseDao.getTableIndex(databaseName, tableName);
    }

}

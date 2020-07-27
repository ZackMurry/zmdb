package com.zackmurry.zmdb.services;

import com.zackmurry.zmdb.ZmdbLogger;
import com.zackmurry.zmdb.controller.files.FileEditor;
import com.zackmurry.zmdb.controller.proto.ProtoRow;
import com.zackmurry.zmdb.dao.DatabaseDao;
import com.zackmurry.zmdb.dao.DatabaseDataAccessService;
import com.zackmurry.zmdb.entities.Column;
import com.zackmurry.zmdb.entities.Database;
import com.zackmurry.zmdb.controller.proto.ProtoColumn;
import com.zackmurry.zmdb.entities.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.io.File;
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

        //todo defo want to make this into a function / use a constructor to set values
        //todo add UUID type (maybe add option to auto-generate it)

        int out;

        //yikes 0.0
        switch(protoColumn.getType()) {
            case "Boolean":
                Column<Boolean> column1 = new Column<>();
                column1.setTableName(tableName);
                column1.setDatabaseName(databaseName);
                column1.setName(protoColumn.getName());
                out = databaseDao.addColumnToTable(databaseName, tableName, column1);
                break;
            case "String":
                Column<String> column2 = new Column<>();
                column2.setTableName(tableName);
                column2.setDatabaseName(databaseName);
                column2.setName(protoColumn.getName());
                out = databaseDao.addColumnToTable(databaseName, tableName, column2);
                break;
            case "Integer":
                Column<Integer> column3 = new Column<>();
                column3.setTableName(tableName);
                column3.setDatabaseName(databaseName);
                column3.setName(protoColumn.getName());
                out = databaseDao.addColumnToTable(databaseName, tableName, column3);
                break;
            case "Double":
                Column<Double> column4 = new Column<>();
                column4.setTableName(tableName);
                column4.setDatabaseName(databaseName);
                column4.setName(protoColumn.getName());
                out = databaseDao.addColumnToTable(databaseName, tableName, column4);
            case "Float":
                Column<Float> column5 = new Column<>();
                column5.setTableName(tableName);
                column5.setDatabaseName(databaseName);
                column5.setName(protoColumn.getName());
                out = databaseDao.addColumnToTable(databaseName, tableName, column5);
                break;
            case "Character":
                Column<Character> column6 = new Column<>();
                column6.setTableName(tableName);
                column6.setDatabaseName(databaseName);
                column6.setName(protoColumn.getName());
                out = databaseDao.addColumnToTable(databaseName, tableName, column6);
                break;
            case "Byte":
                Column<Byte> column7 = new Column<>();
                column7.setTableName(tableName);
                column7.setDatabaseName(databaseName);
                column7.setName(protoColumn.getName());
                out = databaseDao.addColumnToTable(databaseName, tableName, column7);
                break;
            case "Short":
                Column<Short> column8 = new Column<>();
                column8.setTableName(tableName);
                column8.setDatabaseName(databaseName);
                column8.setName(protoColumn.getName());
                out = databaseDao.addColumnToTable(databaseName, tableName, column8);
                break;
            case "Long":
                Column<Long> column9 = new Column<>();
                column9.setTableName(tableName);
                column9.setDatabaseName(databaseName);
                column9.setName(protoColumn.getName());
                out = databaseDao.addColumnToTable(databaseName, tableName, column9);
                break;
            default:
                out = DatabaseDataAccessService.OPERATION_FAIL_VALUE;
                break;
        }

        if(out == DatabaseDataAccessService.OPERATION_SUCCESS_VALUE) fileEditor.newColumnFile(databaseName, tableName, protoColumn.getName(), protoColumn.getType());

        return out;
    }

    public int includeColumnInTable(String databaseName, String tableName, ProtoColumn protoColumn) {
        switch(protoColumn.getType()) {
            case "Boolean":
                Column<Boolean> column1 = new Column<>();
                column1.setTableName(tableName);
                column1.setDatabaseName(databaseName);
                column1.setName(protoColumn.getName());
                return databaseDao.addColumnToTable(databaseName, tableName, column1);
            case "String":
                Column<String> column2 = new Column<>();
                column2.setTableName(tableName);
                column2.setDatabaseName(databaseName);
                column2.setName(protoColumn.getName());
                return databaseDao.addColumnToTable(databaseName, tableName, column2);
            case "Integer":
                Column<Integer> column3 = new Column<>();
                column3.setTableName(tableName);
                column3.setDatabaseName(databaseName);
                column3.setName(protoColumn.getName());
                return databaseDao.addColumnToTable(databaseName, tableName, column3);
            case "Double":
                Column<Double> column4 = new Column<>();
                column4.setTableName(tableName);
                column4.setDatabaseName(databaseName);
                column4.setName(protoColumn.getName());
                return databaseDao.addColumnToTable(databaseName, tableName, column4);
            case "Float":
                Column<Float> column5 = new Column<>();
                column5.setTableName(tableName);
                column5.setDatabaseName(databaseName);
                column5.setName(protoColumn.getName());
                return databaseDao.addColumnToTable(databaseName, tableName, column5);
            case "Character":
                Column<Character> column6 = new Column<>();
                column6.setTableName(tableName);
                column6.setDatabaseName(databaseName);
                column6.setName(protoColumn.getName());
                return databaseDao.addColumnToTable(databaseName, tableName, column6);
            case "Byte":
                Column<Byte> column7 = new Column<>();
                column7.setTableName(tableName);
                column7.setDatabaseName(databaseName);
                column7.setName(protoColumn.getName());
                return databaseDao.addColumnToTable(databaseName, tableName, column7);
            case "Short":
                Column<Short> column8 = new Column<>();
                column8.setTableName(tableName);
                column8.setDatabaseName(databaseName);
                column8.setName(protoColumn.getName());
                return databaseDao.addColumnToTable(databaseName, tableName, column8);
            case "Long":
                Column<Long> column9 = new Column<>();
                column9.setTableName(tableName);
                column9.setDatabaseName(databaseName);
                column9.setName(protoColumn.getName());
                return databaseDao.addColumnToTable(databaseName, tableName, column9);
            default:
                return DatabaseDataAccessService.OPERATION_FAIL_VALUE;
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

}

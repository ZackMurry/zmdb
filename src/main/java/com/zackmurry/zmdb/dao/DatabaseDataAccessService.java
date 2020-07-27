package com.zackmurry.zmdb.dao;

import com.zackmurry.zmdb.ZmdbLogger;
import com.zackmurry.zmdb.controller.proto.ProtoRow;
import com.zackmurry.zmdb.entities.Column;
import com.zackmurry.zmdb.entities.Database;
import com.zackmurry.zmdb.entities.Table;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import javax.xml.crypto.Data;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 *
 * reserved words:
 *
 * contains
 *
 *
 */



@Repository
public class DatabaseDataAccessService implements DatabaseDao {

    public static final int OPERATION_SUCCESS_VALUE = 1;
    public static final int OPERATION_FAIL_VALUE = 0;

    private List<Database> databases = new ArrayList<>();

    @Override
    public int addDatabase(Database database) {
        //checking if there's already a database with the same name
        if(databases.stream().anyMatch(database1 -> database1.getName().equals(database.getName()))) {
            ZmdbLogger.log("Cannot create database with name " + database.getName() + " as one already exists with the same name.");
            return OPERATION_FAIL_VALUE;
        }
        databases.add(database);
        ZmdbLogger.log("Added database " + database.getName() + ".");
        return OPERATION_SUCCESS_VALUE;
    }

    @Override
    public int getDatabaseCount() {
        return databases.size();
    }

    @Override
    public Optional<Database> getDatabaseByName(String name) {
        return databases.stream().filter(database -> database.getName().equals(name)).findFirst();
    }

    @Override
    public int addTable(Table table, String databaseName) {
        Optional<Database> optionalDatabase = getDatabaseByName(databaseName);
        if(optionalDatabase.isEmpty()) {
            return OPERATION_FAIL_VALUE;
        }
        Database database = optionalDatabase.get();
        if(database.getTable(table.getName()).isPresent()) {
            ZmdbLogger.log("Cannot create table with name " + table.getName() + " in database " + databaseName + " as one already exists with the same name.");
            return OPERATION_FAIL_VALUE;
        }

        database.addTable(table);
        ZmdbLogger.log("Added table " + table.getName() + " in database " + databaseName + ".");
        return OPERATION_SUCCESS_VALUE;
    }

    @Override
    public int getTableCountOfDatabase(String databaseName) {
        Database database = getDatabaseByName(databaseName).orElse(null);
        if(database == null) {
            return OPERATION_FAIL_VALUE;
        }
        return database.getTableCount();
    }

    @Override
    public List<Database> getAllDatabases() {
        return databases;
    }

    @Override
    public List<Table> getTablesFromDatabase(String databaseName) {
        Optional<Database> optionalDatabase = databases.stream().filter(database -> database.getName().equals(databaseName)).findFirst();
        if(optionalDatabase.isEmpty()) {
            return new ArrayList<>();
        }
        return optionalDatabase.get().getTables();
    }

    @Override
    public int addColumnToTable(String databaseName, String tableName, Column<?> column) {
        Optional<Database> db = databases.stream().filter(database -> database.getName().equals(databaseName)).findFirst();
        if(db.isEmpty()) return OPERATION_FAIL_VALUE;
        Optional<Table> tableOptional = db.get().getTables().stream().filter(table -> table.getName().equals(tableName)).findFirst();
        if(tableOptional.isEmpty()) return OPERATION_FAIL_VALUE;
        if(tableOptional.get().getColumnByName(column.getName()).isPresent()) {
            ZmdbLogger.log("Cannot create table with name " + column.getName() + " in table " + tableName + " in database " + databaseName + " as one already exists with the same name.");
            return OPERATION_FAIL_VALUE;
        }
        tableOptional.get().addColumn(column);
        return OPERATION_SUCCESS_VALUE;
    }

    @Override
    public int addRowToTable(String databaseName, String tableName, ArrayList<Object> data, ArrayList<String> order) {
        Optional<Table> optionalTable = getTable(databaseName, tableName);
        if(optionalTable.isEmpty()) {
            return OPERATION_FAIL_VALUE;
        }
        Table table = optionalTable.get();
        if(table.getAllColumns().size() != data.size()) {
            ZmdbLogger.log("Unable to add row to table " + table.getName() + ": input is not the same length as row length.");
            return OPERATION_FAIL_VALUE;
        }
        return table.addRow(data, order);
    }

    @Override
    public Optional<Table> getTable(String databaseName, String tableName) {
        Optional<Database> optionalDatabase = getDatabaseByName(databaseName);
        if(optionalDatabase.isEmpty()) {
            ZmdbLogger.log("Cannot find database " + databaseName);
            return Optional.empty();
        }
        return optionalDatabase.get().getTables().stream().filter(table -> table.getName().equals(tableName)).findFirst();
    }

    @Override
    public int includeRowInColumn(String databaseName, String tableName, String columnName, Object data) {

        Optional<Table> optionalTable = getTablesFromDatabase(databaseName).stream().filter(table -> table.getName().equals(tableName)).findFirst();
        if(optionalTable.isEmpty()) {
            ZmdbLogger.log("Unable to include row including " + data.toString() + " because table " + tableName + " could not be found.");
            return OPERATION_FAIL_VALUE;
        }
        Table table = optionalTable.get();

        Optional<Column<?>> optionalColumn = table.getColumnByName(columnName);
        if(optionalColumn.isEmpty()) {
            ZmdbLogger.log("Unable to include row including " + data.toString() + " because column " + columnName + " could not be found");
            return OPERATION_FAIL_VALUE;
        }
        Column<?> column = optionalColumn.get();
        return column.includeRow(data);
    }

    @Override
    public Optional<Column<?>> getColumn(String databaseName, String tableName, String columnName) {
        Optional<Table> optionalTable = getTable(databaseName, tableName);
        if(optionalTable.isEmpty()) {
            ZmdbLogger.log("Unable to get column " + columnName + " from table " + tableName + " from database " + databaseName + " because the table could not be found.");
            return Optional.empty();
        }
        Table table = optionalTable.get();
        return table.getColumnByName(columnName);
    }

    @Override
    public boolean tableContains(String databaseName, String tableName, ArrayList<Object> data, ArrayList<String> order) {
        Optional<Table> optionalTable = getTable(databaseName, tableName);
        if(optionalTable.isEmpty()) {
            ZmdbLogger.log("Cannot check for row in table " + tableName + " in database " + databaseName + " because the table couldn't be found.");
            return false;
        }
        Table table = optionalTable.get();
        return table.containsRow(data, order);
    }

    @Override
    public int deleteDatabaseByName(String databaseName) {
        Optional<Database> optionalDatabase = getDatabaseByName(databaseName);
        if(optionalDatabase.isEmpty()) {
            ZmdbLogger.log("Couldn't delete database " + databaseName + " because it couldn't be found.");
            return 0;
        }
        databases.remove(optionalDatabase.get());
        return 1;
    }

    @Override
    public int deleteTableByName(String databaseName, String tableName) {
        Optional<Database> optionalDatabase = getDatabaseByName(databaseName);
        if(optionalDatabase.isEmpty()) {
            ZmdbLogger.log("Couldn't delete table " + tableName + " in database " + databaseName + " because the database couldn't be found.");
            return 0;
        }
        optionalDatabase.get().removeTable(tableName);
        return 1;
    }

    @Override
    public int deleteColumnByName(String databaseName, String tableName, String columnName) {
        Optional<Database> optionalDatabase = getDatabaseByName(databaseName);
        if(optionalDatabase.isEmpty()) {
            ZmdbLogger.log("Couldn't delete column " + columnName + " in table " + tableName + " in database " + databaseName + " because the database could not be found.");
            return 0;
        }
        Optional<Table> optionalTable = optionalDatabase.get().getTable(tableName);
        if(optionalTable.isEmpty()) {
            ZmdbLogger.log("Couldn't delete column " + columnName + " in table " + tableName + " in database " + databaseName + " because the table could not be found.");
            return 0;
        }
        optionalTable.get().removeColumn(columnName);
        return 1;
    }

    @Override
    public int deleteRow(String databaseName, String tableName, ProtoRow protoRow) {
        Optional<Table> optionalTable = getTable(databaseName, tableName);
        if(optionalTable.isEmpty()) {
            ZmdbLogger.log("Could not delete row from table " + tableName + " in database " + databaseName + " because the table could not be found.");
            return 0;
        }
        return optionalTable.get().removeRow(protoRow.getData(), protoRow.getOrder());
    }


}

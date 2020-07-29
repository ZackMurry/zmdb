package com.zackmurry.zmdb.dao;

import com.zackmurry.zmdb.tools.RequestPathHelper;
import com.zackmurry.zmdb.tools.ZmdbLogger;
import com.zackmurry.zmdb.controller.proto.ProtoRow;
import com.zackmurry.zmdb.entities.Column;
import com.zackmurry.zmdb.entities.Database;
import com.zackmurry.zmdb.entities.Table;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * reserved words:
 *
 * contains
 * NULL
 * cannot contain '/'
 * must be at least one char
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
            ZmdbLogger.log("Cannot create column with name " + column.getName() + " in table " + tableName + " in database " + databaseName + " as one already exists with the same name.");
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

    @Override
    public int deleteRowByIndex(String databaseName, String tableName, int index) {
        Optional<Table> optionalTable = getTable(databaseName, tableName);
        if(optionalTable.isEmpty()) {
            ZmdbLogger.log("Could not delete row " + index + " in table " + tableName + " in database " + databaseName + " because the table could not be found");
            return 0;
        }
        optionalTable.get().removeRow(index);
        return 1;
    }


    @Override
    public boolean databaseExists(String databaseName) {
        return databases.stream().anyMatch(database -> database.getName().equals(databaseName));
    }

    @Override
    public boolean tableExists(String databaseName, String tableName) {
        return getTable(databaseName, tableName).isPresent();
    }

    @Override
    public boolean columnExists(String databaseName, String tableName, String columnName) {
        return getColumn(databaseName, tableName, columnName).isPresent();
    }

    @Override
    public int changeTableIndex(String databaseName, String tableName, String columnName) {
        Optional<Table> optionalTable = getTable(databaseName, tableName);
        if(optionalTable.isEmpty()) {
            ZmdbLogger.log("Could not change index column of table " + tableName + " in database " + databaseName + " because the table file couldn't be found.");
            return 0;
        }
        return optionalTable.get().setIndexColumn(columnName);
    }

    @Override
    public String getTableIndex(String databaseName, String tableName) {
        Optional<Table> optionalTable = getTable(databaseName, tableName);
        if(optionalTable.isEmpty()) {
            ZmdbLogger.log("Unable to get index of table " + tableName + " in database " + databaseName + " because the table could not be found.");
            return "NULL";
        }
        return optionalTable.get().getIndexColumnName();
    }

    @Override
    public int deleteAllDatabases() {
        if(databases.isEmpty()) {
            ZmdbLogger.log("No databases to delete.");
            return 0;
        }
        databases.clear();
        return 1;
    }

    @Override
    public int deleteAllTablesInDatabase(String databaseName) {
        Optional<Database> optionalDatabase = getDatabaseByName(databaseName);
        if(optionalDatabase.isEmpty()) {
            ZmdbLogger.log("Couldn't delete all tables in database " + databaseName + " because the database doesn't exist.");
            return 0;
        }
        optionalDatabase.get().removeAllTables();
        return 1;
    }

    @Override
    public int deleteAllColumnsInTable(String databaseName, String tableName) {
        Optional<Table> optionalTable = getTable(databaseName, tableName);
        if(optionalTable.isEmpty()) {
            ZmdbLogger.log("Couldn't delete all columns of table " + tableName + " in database " + databaseName + " because the table doesn't exist.");
            return 0;
        }
        optionalTable.get().removeAllColumns();
        return 1;
    }

    @Override
    public ArrayList<?> getAllRowsInColumn(String databaseName, String tableName, String columnName) {
        Optional<Column<?>> optionalColumn = getColumn(databaseName, tableName, columnName);
        if(optionalColumn.isEmpty()) {
            ZmdbLogger.log("Couldn't get all rows from column " + columnName + " from table " + tableName + " in database " + databaseName + " because the column could not be found.");
            return new ArrayList<>();
        }
        return optionalColumn.get().getAllRows();
    }

    /**
     * gets a column from a specified request path
     * @param requestPath the path that you would enter into the http request
     * @return a column if it finds one
     */
    @Override
    public Optional<Column<?>> getColumnFromRequestPath(String requestPath) {
        //getting database from request path
        String databaseName = RequestPathHelper.getDatabaseNameFromRequestPath(requestPath);
        String tableName = RequestPathHelper.getTableNameFromRequestPath(requestPath, databaseName);
        String columnName = RequestPathHelper.getColumnNameFromRequestPath(requestPath, databaseName, tableName);
        return getColumn(databaseName, tableName, columnName);
    }

    @Override
    public int renameDatabase(String databaseName, String newDatabaseName) {
        Optional<Database> optionalDatabase = getDatabaseByName(databaseName);
        if(optionalDatabase.isEmpty()) {
            ZmdbLogger.log("Couldn't rename database " + databaseName + " because the database couldn't be found.");
            return 0;
        }
        optionalDatabase.get().setName(newDatabaseName);
        for(Table table : optionalDatabase.get().getTables()) {
            //todo this would be a place i'd need to add databaseName in table

            for(Column<?> column : table.getAllColumns()) {
                column.setDatabaseName(databaseName);
            }
        }
        return 1;
    }

    @Override
    public int renameTable(String databaseName, String tableName, String newTableName) {
        Optional<Table> optionalTable = getTable(databaseName, tableName);
        if(optionalTable.isEmpty()) {
            ZmdbLogger.log("Couldn't rename table " + tableName + " in database " + databaseName + " because the file couldn't be found.");
            return 0;
        }
        optionalTable.get().setName(newTableName);
        for(Column<?> column : optionalTable.get().getAllColumns()) {
            column.setTableName(newTableName);
        }
        return 1;
    }

    @Override
    public int renameColumn(String databaseName, String tableName, String columnName, String newColumnName) {
        Optional<Column<?>> optionalColumn = getColumn(databaseName, tableName, columnName);
        if(optionalColumn.isEmpty()) {
            ZmdbLogger.log("Couldn't rename column " + columnName + " in table " + tableName + " in database " + databaseName + " because the file couldn't be found.");
            return 0;
        }
        optionalColumn.get().setName(newColumnName);
        return 1;
    }


}

package com.zackmurry.zmdb.dao;

import com.zackmurry.zmdb.entities.Column;
import com.zackmurry.zmdb.entities.Database;
import com.zackmurry.zmdb.entities.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface DatabaseDao {

    int addDatabase(Database database);

    int getDatabaseCount();

    Optional<Database> getDatabaseByName(String name);

    int addTable(Table table, String databaseName);

    int getTableCountOfDatabase(String databaseName);

    List<Database> getAllDatabases();

    List<Table> getTablesFromDatabase(String databaseName);

    int addColumnToTable(String databaseName, String tableName, Column<?> column);

    int addRowToTable(String databaseName, String tableName, ArrayList<Object> data);

    Optional<Table> getTable(String databaseName, String tableName);

    int includeRowInColumn(String databaseName, String tableName, String columnName, Object data);

    Optional<Column<?>> getColumn(String databaseName, String tableName, String columnName);

    boolean tableContains(String databaseName, String tableName, ArrayList<Object> data);

    int deleteDatabaseByName(String databaseName);

}

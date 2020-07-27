package com.zackmurry.zmdb.controller;

import com.zackmurry.zmdb.controller.proto.ProtoRow;
import com.zackmurry.zmdb.controller.proto.ProtoString;
import com.zackmurry.zmdb.entities.Column;
import com.zackmurry.zmdb.entities.Database;
import com.zackmurry.zmdb.controller.proto.ProtoColumn;
import com.zackmurry.zmdb.entities.Table;
import com.zackmurry.zmdb.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * todo:
 * getTable
 *
 */

@RestController
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;


    @PostMapping("/databases")
    public int addDatabase(@RequestBody Database database) {
        return databaseService.addDatabase(database);
    }

    @GetMapping("/databases")
    public List<Database> getAllDatabases() {
        return databaseService.getAllDatabases();
    }

    @GetMapping("/databases/count")
    public int getDatabaseCount() {
        return databaseService.getDatabaseCount();
    }

    //todo this returns tableCount, but not the tables
    @GetMapping("/databases/{name}")
    public Optional<Database> getDatabaseByName(@PathVariable(name="name") String name) {
        return databaseService.getDatabaseByName(name);
    }


    @PostMapping("/databases/{databaseName}/tables")
    public int addTable(@PathVariable String databaseName, @RequestBody Table table) {
        return databaseService.addTable(table, databaseName);
    }

    @GetMapping("databases/{databaseName}/tables/count")
    public int getTableCountOfDatabase(@PathVariable String databaseName) {
        return databaseService.getTableCountOfDatabase(databaseName);
    }

    @GetMapping("/databases/{databaseName}/tables")
    public List<Table> getTablesFromDatabase(@PathVariable(name="databaseName") String databaseName) {
        return databaseService.getTablesFromDatabase(databaseName);
    }

    @PostMapping("/databases/{databaseName}/tables/{tableName}/columns")
    public int addColumnToTable(@PathVariable(name="databaseName") String databaseName, @PathVariable(name="tableName") String tableName, @RequestBody ProtoColumn protoColumn) {
        return databaseService.addColumnToTable(databaseName, tableName, protoColumn);
    }

    @PostMapping("/databases/{databaseName}/tables/{tableName}")
    public int addRowToTable(@PathVariable(name="databaseName") String databaseName, @PathVariable(name="tableName") String tableName, @RequestBody ProtoRow protoRow) {
        return databaseService.addRowToTable(databaseName, tableName, protoRow.getData(), protoRow.getOrder());
    }

    @GetMapping("/databases/{databaseName}/tables/{tableName}")
    public Table getTableByName(@PathVariable(name="databaseName") String databaseName, @PathVariable(name="tableName") String tableName) {
        return databaseService.getTableByName(databaseName, tableName);
    }

    @GetMapping("/databases/{databaseName}/tables/{tableName}/columns/{columnName}")
    public Column<?> getColumnByName(@PathVariable(name="databaseName") String databaseName, @PathVariable(name="tableName") String tableName, @PathVariable("columnName") String columnName) {
        return databaseService.getColumnByName(databaseName, tableName, columnName);
    }

    //todo probly change this to include order
    @GetMapping("/databases/{databaseName}/tables/{tableName}/contains")
    public boolean tableContains(@PathVariable(name="databaseName") String databaseName, @PathVariable(name="tableName") String tableName, @RequestBody ProtoRow protoRow) {
        return databaseService.tableContains(databaseName, tableName, protoRow);
    }

    @DeleteMapping("/databases/{databaseName}")
    public int deleteDatabaseByName(@PathVariable(name="databaseName") String databaseName) {
        return databaseService.deleteDatabaseByName(databaseName);
    }

    @DeleteMapping("/databases/{databaseName}/tables/{tableName}")
    public int deleteTableByName(@PathVariable(name="databaseName") String databaseName, @PathVariable(name="tableName") String tableName) {
        return databaseService.deleteTableByName(databaseName, tableName);
    }

    @DeleteMapping("/databases/{databaseName}/tables/{tableName}/columns/{columnName}")
    public int deleteColumnByName(@PathVariable(name="databaseName") String databaseName, @PathVariable(name="tableName") String tableName, @PathVariable(name="columnName") String columnName) {
        return databaseService.deleteColumnByName(databaseName, tableName, columnName);
    }

    @DeleteMapping("/databases/{databaseName}/tables/{tableName}/rows")
    public int deleteRow(@PathVariable("databaseName") String databaseName, @PathVariable("tableName") String tableName, @RequestBody ProtoRow protoRow) {
        return databaseService.deleteRow(databaseName, tableName, protoRow);
    }

    @DeleteMapping("/databases/{databaseName}/tables/{tableName}/rows/{index}")
    public int deleteRowByIndex(@PathVariable("databaseName") String databaseName, @PathVariable("tableName") String tableName, @PathVariable("index") String index) {
        return databaseService.deleteRowByIndex(databaseName, tableName, index);
    }

    @PostMapping("/databases/{databaseName}/tables/{tableName}/index")
    public int changeTableIndex(@PathVariable("databaseName") String databaseName, @PathVariable("tableName") String tableName, @RequestBody ProtoString columnName) {
        return databaseService.changeTableIndex(databaseName, tableName, columnName.getName());
    }

    @GetMapping("/databases/{databaseName}/tables/{tableName}/index")
    public String getTableIndex(@PathVariable("databaseName") String databaseName, @PathVariable("tableName") String tableName) {
        return databaseService.getTableIndex(databaseName, tableName);
    }
}

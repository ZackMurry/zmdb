package com.zackmurry.zmdb.controller;

import com.zackmurry.zmdb.controller.proto.ProtoRequestPath;
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

    @DeleteMapping("/databases")
    public int deleteAllDatabases() {
        return databaseService.deleteAllDatabases();
    }

    @DeleteMapping("/databases/{databaseName}/tables")
    public int deleteAllTablesInDatabase(@PathVariable("databaseName") String databaseName) {
        return databaseService.deleteAllTablesInDatabase(databaseName);
    }

    @DeleteMapping("/databases/{databaseName}/tables/{tableName}/columns")
    public int deleteAllColumnsInTable(@PathVariable("databaseName") String databaseName, @PathVariable("tableName") String tableName) {
        return databaseService.deleteAllColumnsInTable(databaseName, tableName);
    }

    @GetMapping("/databases/{databaseName}/tables/{tableName}/columns/{columnName}/rows")
    public ArrayList<?> getAllRowsInColumn(@PathVariable("databaseName") String databaseName, @PathVariable("tableName") String tableName, @PathVariable("columnName") String columnName) {
        return databaseService.getAllRowsInColumn(databaseName, tableName, columnName);
    }

    @PostMapping("/databases/{databaseName}/tables/{tableName}/columns/{columnName}/paste")
    public int copyPasteColumn(@PathVariable("databaseName") String databaseName, @PathVariable("tableName") String tableName, @PathVariable("columnName") String columnName, @RequestBody ProtoRequestPath protoRequestPath) {
        return databaseService.copyPasteColumn(databaseName, tableName, columnName, protoRequestPath.getPath());
    }

    @PostMapping("/databases/{databaseName}/tables/{tableName}/paste")
    public int copyPasteTable(@PathVariable("databaseName") String databaseName, @PathVariable("tableName") String tableName, @RequestBody ProtoRequestPath protoRequestPath) {
        return databaseService.copyPasteTable(databaseName, tableName, protoRequestPath.getPath());
    }

    @PostMapping("/databases/{databaseName}/paste")
    public int copyPasteDatabase(@PathVariable("databaseName") String databaseName, @RequestBody ProtoRequestPath protoRequestPath) {
        return databaseService.copyPasteDatabase(databaseName, protoRequestPath.getPath());
    }

    @RequestMapping(value = "/databases/{databaseName}", method = RequestMethod.PATCH)
    public int renameDatabase(@PathVariable String databaseName, @RequestBody ProtoString protoString) {
        return databaseService.renameDatabase(databaseName, protoString.getName());
    }

    @RequestMapping(value = "/databases/{databaseName}/tables/{tableName}", method = RequestMethod.PATCH)
    public int renameTable(@PathVariable String databaseName, @PathVariable("tableName") String tableName, @RequestBody ProtoString protoString) {
        return databaseService.renameTable(databaseName, tableName, protoString.getName());
    }

    @RequestMapping(value = "/databases/{databaseName}/tables/{tableName}/columns/{columnName}", method = RequestMethod.PATCH)
    public int renameColumn(@PathVariable String databaseName, @PathVariable("tableName") String tableName, @PathVariable("columnName") String columnName, @RequestBody ProtoString protoString) {
        return databaseService.renameColumn(databaseName, tableName, columnName, protoString.getName());
    }


}

package com.zackmurry.zmdb.entities;

import com.sun.jdi.Type;
import com.zackmurry.zmdb.ZmdbLogger;
import com.zackmurry.zmdb.controller.files.FileEditor;

import java.util.ArrayList;
import java.util.Arrays;


public class Column<T> {

    private String name;
    private ArrayList<T> rows = new ArrayList<>();
    private String databaseName;
    private String tableName;



    public Column(String name) {
        this.name = name;
    }

    public Column() {

    }

    public int getNumberOfRows() {
        return rows.size();
    }

    public int addRow(Object item) {

        try {
            var item2 = (T) item;
            rows.add(item2);
            return FileEditor.writeToColumn(item.toString(), databaseName, tableName, name);
        } catch (Exception e) {
            ZmdbLogger.log("Error: cannot convert " + item + "to the appropriate type");
            e.printStackTrace();
            return 0;
        }
        //if(!rows.get(0).getClass().getSimpleName().equals(item.getClass().getSimpleName())) return;
    }

    public int includeRow(Object item) {
        try{
            var item2 = (T) item;
            rows.add(item2);
            return 1;
        } catch (Exception e) {
            ZmdbLogger.log("Error: cannot convert " + item + "to the appropriate type");
            e.printStackTrace();
            return 0;
        }

    }

    public T getItemFromRow(int index) {
        return rows.get(index);
    }

    public ArrayList<T> getAllRows() {
        return rows;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean nameIs(String name) {
        return this.name.equals(name);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}

package com.zackmurry.zmdb.entities;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Database {

    private String name;
    private List<Table> tables = new ArrayList<>();

    public Database(String name) {
        this.name = name;
    }

    public Database() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addTable(Table table) {
        tables.add(table);
    }

    public void removeTable(int index) {
        tables.remove(index);
    }

    public void removeTable(String name) {
        tables.removeIf(table -> table.nameIs(name));
    }

    public void removeAllTables() {
        tables.clear();
    }

    public int getTableCount() {
        return tables.size();
    }

    public List<Table> getTables() {
        return this.tables;
    }

    public Optional<Table> getTable(String tableName) {
        return tables.stream().filter(table -> table.getName().equals(tableName)).findFirst();
    }

}

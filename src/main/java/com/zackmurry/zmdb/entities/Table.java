package com.zackmurry.zmdb.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Table {

    private String name;

    private List<Column<?>> columns = new ArrayList<>();

    public Table(String name, Column<?>... columns) {
        this.name = name;
        this.columns.addAll(Arrays.asList(columns));
    }

    public Table(String name) {
        this.name = name;
    }

    public Table() {

    }

    public void addColumn(Column<?> column) {
        this.columns.add(column);
    }

    public List<Column<?>> getAllColumns() {
        return columns;
    }

    public List<String> getColumnNames() {
        return columns.stream().map(Column::getName).collect(Collectors.toList());
    }

    public Column<?> getColumnByIndex(int index) {
        return columns.get(index);
    }

    public Optional<Column<?>> getColumnByName(String name) {
        return columns.stream().filter(column -> column.nameIs(name)).findFirst();
    }

    public int getNumberOfColumns() {
        return columns.size();
    }

    public void removeColumn(int index) {
        columns.remove(index);
    }

    public void removeColumn(String name) {
        columns.removeIf(column -> column.nameIs(name));
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

    public void removeAllColumns() {
        columns.clear();
    }

    public int addRow(ArrayList<Object> data) {
        int out = 1;
        for (int i = 0; i < data.size(); i++) {
            if(columns.get(i).addRow(data.get(i)) != 1) {
                out = 0;
            }
        }
        return out;
    }

}

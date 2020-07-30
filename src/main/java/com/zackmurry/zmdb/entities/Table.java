package com.zackmurry.zmdb.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zackmurry.zmdb.tools.ZmdbLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Table implements Cloneable {

    private String name;

    private List<Column<?>> columns = new ArrayList<>();

    private String databaseName;

    private boolean hasIndexColumn = true;
    private int indexOfIndexColumn = 0; //todo require index columns to have all unique values (useful for searching values in tables)

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
        for(Column<?> column : columns) {
            column.setTableName(name);
        }
    }

    public boolean nameIs(String name) {
        return this.name.equals(name);
    }

    public void removeAllColumns() {
        columns.clear();
    }

    public int addRow(ArrayList<Object> data, ArrayList<String> order) {
        //making sure index columns don't contain duplicates
        if(hasIndexColumn) {
            if(containsRow(data, order)) {
                ZmdbLogger.log("Cannot add duplicate rows to an index column.");
                return 0;
            }
        }

        for(Column<?> column : columns) {
            int index = -1;
            for (int i = 0; i < order.size(); i++) {
                if(order.get(i).equals(column.getName())) index = i;
            }
            if(index == -1) {
                ZmdbLogger.log("Couldn't add row to table " + this.name + " because the order doesn't have the same names as the column names.");
                return 0;
            }
            if(column.addRow(data.get(index)) != 1) return 0;
        }
        return 1;
    }

    public boolean containsRow(ArrayList<Object> data, ArrayList<String> order) {
        if(columns.size() < 1) return false;

        int orderIndex = -1;
        //find the index of the indexColumn in the order arraylist
        for (int j = 0; j < order.size(); j++) {
            if(order.get(j).equals(columns.get(indexOfIndexColumn).getName())) orderIndex = j;
        }

        if(orderIndex == -1) {
            ZmdbLogger.log("Unable to determine if table " + this.name + " contains a row because the order entered was wrong.");
            return false;
        }

        //finding the row that we're looking for
        int rowIndex = -1;
        for (int i = 0; i < columns.get(indexOfIndexColumn).getNumberOfRows(); i++) {
            if(columns.get(indexOfIndexColumn).getItemFromRow(i).equals(data.get(orderIndex))) {
                rowIndex = i;
                break;
            }
        }
        if(rowIndex == -1) {
            return false;
        }

        //replace 0+1 with 0 and an if statement with continue; if i == indexColumnIndex
        for (int i = 0; i < columns.size(); i++) {

            if(i == indexOfIndexColumn) continue;

            //getting the index of the 'data' inputted based on the 'order' input
            int dataIndex = -1;
            for (int j = 0; j < data.size(); j++) {
                if(columns.get(i).getName().equals(order.get(j))) {
                    dataIndex = j;
                    break;
                }
            }
            if(dataIndex == -1) {
                ZmdbLogger.log("Could not find the appropriate index in order for column " + columns.get(i));
            }

            //if the data is not equal to the data from the column at the row
            if(!columns.get(i).getItemFromRow(rowIndex).equals(data.get(dataIndex))) {
                return false;
            }
            //else it is correct for that column so we continue

        }

        return true;

    }

    public int removeRow(ArrayList<Object> data, ArrayList<String> order) {
        //finding data index that corresponds with indexColumn
        int orderIndex = -1;
        for (int i = 0; i < order.size(); i++) {
            if(columns.get(indexOfIndexColumn).getName().equals(order.get(i))) {
                orderIndex = i;
                break;
            }
        }

        int deleteIndex = columns.get(indexOfIndexColumn).findIndex(data.get(orderIndex));
        for(Column<?> column : columns) {
            column.removeRow(deleteIndex);
        }
        return 1;
    }

    public void removeRow(int index) {
        for(Column<?> column : columns) {
            column.removeRow(index);
        }
    }

    public int getIndexOfIndexColumn() {
        return indexOfIndexColumn;
    }

    public void setIndexOfIndexColumn(int indexOfIndexColumn) {
        this.indexOfIndexColumn = indexOfIndexColumn;
    }

    public int setIndexColumn(String columnName) {
        if(columnName.isBlank()) {
            return 0;
        }
        if(columnName.equals("OFF")) {
            setHasIndexColumn(false);
            return 1;
        }

        for (int i = 0; i < columns.size(); i++) {
            if(columns.get(i).getName().equals(columnName)) {
                ZmdbLogger.log("Set index column of table " + this.name + " in database " + databaseName + " to " + columnName + ".");
                indexOfIndexColumn = i;
                columns.get(i).setIndexColumn(true);
                hasIndexColumn = true;
                return 1;
            }
        }
        ZmdbLogger.log("Could not set index column of table " + this.name + " because the column " + columnName + " could not be found.");
        return 0;
    }

    public String getIndexColumnName() {
        if(!hasIndexColumn) {
            return "OFF";
        }
        return columns.get(indexOfIndexColumn).getName();
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        for(Column<?> column : columns) {
            column.setDatabaseName(databaseName);
        }
    }

    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }

    public boolean hasIndexColumn() {
        return hasIndexColumn;
    }

    public void setHasIndexColumn(boolean hasIndexColumn) {
        if(hasIndexColumn) {
            ZmdbLogger.log("Table.setHasIndexColumn() should only be used for disabling index columns.");
            return;
        }
        ZmdbLogger.log("Disabled index columns for table " + this.name + " in database " + databaseName + ".");
        this.hasIndexColumn = false;
        indexOfIndexColumn = -1;
        //finding the current index column and telling it that it is no longer an index column
        for(Column<?> column : columns) {
            if(column.isIndexColumn()) {
                column.setIndexColumn(false);
                return;
            }
        }
    }

}

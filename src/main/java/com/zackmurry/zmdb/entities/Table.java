package com.zackmurry.zmdb.entities;

import com.zackmurry.zmdb.ZmdbLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Table {

    private String name;

    private List<Column<?>> columns = new ArrayList<>(); //the first added column is the index and each element must be unique todo: implement
    //todo allow changing of the index column

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

    public int addRow(ArrayList<Object> data, ArrayList<String> order) {
        int out = 1;

        for(Column<?> column : columns) {
            int index = -1;
            System.out.println("here");
            System.out.println(order.size());
            for (int i = 0; i < order.size(); i++) {
                System.out.println("h");
                System.out.println(column.getName());
                if(order.get(i).equals(column.getName())) index = i;

            }
            if(index == -1) {
                ZmdbLogger.log("Couldn't add row to table " + this.name + " because the order was wrong.");
                return 0;
            }
            if(column.addRow(data.get(index)) != 1) return 0;


        }

        /*for (int i = 0; i < data.size(); i++) {
            if(columns.get(i).addRow(data.get(i)) != 1) {
                out = 0;
            }
        }*/
        return out;
    }

    public boolean containsRow(ArrayList<Object> data, ArrayList<String> order) {
        if(columns.size() < 1) return false;

        //todo change 0 to var (of index column index)

        int orderIndex = -1;
        //find the index of the indexColumn in the order arraylist
        for (int j = 0; j < order.size(); j++) {
            if(order.get(j).equals(columns.get(0).getName())) orderIndex = j;
        }

        if(orderIndex == -1) {
            ZmdbLogger.log("Unable to determine if table " + this.name + " contains a row because the order entered was wrong.");
            return false;
        }

        //finding the row that we're looking for
        int rowIndex = -1;
        for (int i = 0; i < columns.get(0).getNumberOfRows(); i++) {
            if(columns.get(0).getItemFromRow(i).equals(data.get(orderIndex))) {
                rowIndex = i;
                break;
            }
        }
        if(rowIndex == -1) {
            System.out.println("Unfound index.");
            return false;
        }

        //replace 0+1 with 0 and an if statement with continue; if i == indexColumnIndex
        for (int i = 0+1; i < columns.size(); i++) {

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
            //todo change 0 to indexColumn index
            if(columns.get(0).getName().equals(order.get(i))) {
                orderIndex = i;
                break;
            }
        }

        int deleteIndex = columns.get(0).findIndex(data.get(orderIndex));
        for(Column<?> column : columns) {
            column.removeRow(deleteIndex);
        }
        return 1;
    }

}

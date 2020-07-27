package com.zackmurry.zmdb.controller.proto;

import java.util.ArrayList;

public class ProtoRow {

    private ArrayList<Object> data = new ArrayList<>();
    private ArrayList<String> order = new ArrayList<>();

    public ProtoRow(ArrayList<Object> data, ArrayList<String> order) {
        this.data = data;
        this.order = order;
    }

    public ProtoRow() {

    }

    public ArrayList<Object> getData() {
        return data;
    }

    public void setData(ArrayList<Object> data) {
        this.data = data;
    }

    public ArrayList<String> getOrder() {
        return order;
    }

    public void setOrder(ArrayList<String> order) {
        this.order = order;
    }

}

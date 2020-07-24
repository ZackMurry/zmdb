package com.zackmurry.zmdb.controller.proto;

import java.util.ArrayList;

public class ProtoRow {

    private ArrayList<Object> data = new ArrayList<>();

    public ProtoRow(ArrayList<Object> data) {
        this.data = data;
    }

    public ProtoRow() {

    }

    public ArrayList<Object> getData() {
        return data;
    }

    public void setData(ArrayList<Object> data) {
        this.data = data;
    }
}

package com.zackmurry.zmdb.controller.proto;

/**
 * this class only exists to make JSON input easier
 */
public class ProtoColumn {


    private String name;
    private String type;

    public ProtoColumn(String name, String objectType) {
        this.name = name;
        this.type = objectType;
    }

    public ProtoColumn() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setTType(String type) {
        this.type = type;
    }
}

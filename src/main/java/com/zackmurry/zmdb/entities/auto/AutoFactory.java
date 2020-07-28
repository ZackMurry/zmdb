package com.zackmurry.zmdb.entities.auto;

import java.util.UUID;

/**
 * used for auto-generation of rows
 * @param <T> type of object to instantiate
 */
public class AutoFactory<T> {

    private final Class<T> type;

    //a bit redundant but it's the only way i could get it to work
    public AutoFactory(Class<T> type) {
        this.type = type;
    }

    public T getObject() {
        if(UUID.class.equals(type)) {
            return (T) UUID.randomUUID();
        }


        //if nothing matches
        return null;
    }
}

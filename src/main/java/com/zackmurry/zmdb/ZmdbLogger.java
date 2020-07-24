package com.zackmurry.zmdb;

public class ZmdbLogger {

    public static void log(String text) {
        System.out.println(text);
    }

    public static void log(String title, String text) {
        System.out.println(title);
        System.out.println(text);
    }

}

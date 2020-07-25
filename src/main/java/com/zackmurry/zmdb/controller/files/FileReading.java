package com.zackmurry.zmdb.controller.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * shout out: https://www.w3schools.com/java/java_files_read.asp
 */
public class FileReading {

    public String readFile(File file) {
        StringBuilder out = new StringBuilder();
        try {
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                out.append(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return out.toString();
    }

    public static String readFirstLine(File file) {
        String out = "";
        try {
            Scanner myReader = new Scanner(file);
            if (myReader.hasNextLine()) {
                out = myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return out;
    }

    public static String getAllLinesButFirst(File file) {
        StringBuilder out = new StringBuilder();
        try {
            Scanner myReader = new Scanner(file);
            myReader.nextLine();
            while (myReader.hasNextLine()) {
                out.append(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return out.toString();
    }



}

package com.zackmurry.zmdb;

import com.zackmurry.zmdb.files.DataLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * not done:
 * password encoding
 * put mapping
 * delete column by type
 * copying tables and databases
 * maybe adding cutting and pasting too
 * renaming stuff
 * make index rows unique
 * remove row by id from index column
 * maybe make index columns optional
 * add databaseName for each table
 */



@SpringBootApplication
public class ZmdbApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZmdbApplication.class, args);
		new DataLoader(); //loads all necessary data
	}


}

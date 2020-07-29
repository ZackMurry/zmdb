package com.zackmurry.zmdb;

import com.zackmurry.zmdb.entities.auto.AutoFactory;
import com.zackmurry.zmdb.files.DataLoader;
import com.zackmurry.zmdb.tools.RequestPathHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

/**
 * not done:
 * password encoding
 * put mapping
 * delete column by type
 * copying tables and databases
 * maybe adding cutting and pasting too
 * renaming stuff
 */



@SpringBootApplication
public class ZmdbApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZmdbApplication.class, args);
		DataLoader dl = new DataLoader();
	}


}

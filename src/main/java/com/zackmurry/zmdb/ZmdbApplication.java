package com.zackmurry.zmdb;

import com.zackmurry.zmdb.controller.files.DataLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * not done:
 * rows
 *
 *
 *
 */



@SpringBootApplication
public class ZmdbApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZmdbApplication.class, args);
		DataLoader dl = new DataLoader();
	}


}

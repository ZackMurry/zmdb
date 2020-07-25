package com.zackmurry.zmdb.controller.files;

import com.zackmurry.zmdb.ZmdbLogger;
import com.zackmurry.zmdb.controller.proto.ProtoColumn;
import com.zackmurry.zmdb.entities.Database;
import com.zackmurry.zmdb.entities.Table;
import com.zackmurry.zmdb.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

@Service
public class DataLoader {

    @Autowired
    private DatabaseService databaseService;

    /**
     * gets all the data from the files and sends it to DatabaseService
     * @return 1 is success, 0 is fail
     */
    @PostConstruct
    public int loadAllData() {

        //getting databases

        File databasesFile = new File("data/databases");

        //getting databases
        File[] databases = databasesFile.listFiles();

        if(databaseService == null) System.out.println("nully boi");


        if (databases != null) {
            //looping through the databases
            for (File databaseFile : databases) {

                System.out.println(databaseFile.getName());


                //adding each database to databaseService
                if (databaseService.includeDatabase(new Database(databaseFile.getName())) != 1) {
                    ZmdbLogger.log("Failed to load database: " + databaseFile.getName());
                    return 0;
                }
                ZmdbLogger.log("Loading database: " + databaseFile.getName());

                //adding each table to databaseService
                File[] tableFiles = databaseFile.listFiles();
                if (tableFiles == null) continue;

                for (File tableFile : tableFiles) {

                    if (databaseService.includeTable(new Table(tableFile.getName()), databaseFile.getName()) != 1) {
                        ZmdbLogger.log("Failed to load table: " + tableFile.getName());
                        return 0;
                    }
                    ZmdbLogger.log("Loading table: " + tableFile.getName());

                    //adding each column to databaseService
                    File[] columnFiles = tableFile.listFiles();
                    if (columnFiles == null) continue;

                    for (File columnFile : columnFiles) {

                        String firstLine = FileReading.readFirstLine(columnFile); //getting the first line of columnFile (first line is where type info is stored)

                        if (firstLine.contains("@Type=")) {
                            firstLine = firstLine.substring(6); //cut the @Type= off
                        } else {
                            ZmdbLogger.log("Error: first line of " + columnFile.getName() + " does not contain '@Type='");
                            return 0;
                        }

                        databaseService.includeColumnInTable(databaseFile.getName(), tableFile.getName(), new ProtoColumn(columnFile.getName(), firstLine));
                        ZmdbLogger.log("Loading column: " + columnFile.getName());


                        //reading rows of columns
                        String[] rows = FileReading.getAllLinesButFirst(columnFile).split(FileEditor.VALUE_SEPARATOR);
                        for (String row : rows) {
                            if(row.equals(" ")) continue;
                            while(row.startsWith(" ")) row = row.substring(1);
                            if(databaseService.includeRowInColumn(databaseFile.getName(), tableFile.getName(), columnFile.getName(), row) != 1) {
                                ZmdbLogger.log("Failed to include row with data " + row + ".");
                                return 0;
                            }
                        }

                    }

                    ZmdbLogger.log("Table loaded: " + tableFile.getName());

                }

                ZmdbLogger.log("Database loaded: " + databaseFile.getName());

            }
        }


        return 1;
    }


}

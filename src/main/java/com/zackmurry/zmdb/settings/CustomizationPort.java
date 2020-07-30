package com.zackmurry.zmdb.settings;

import com.zackmurry.zmdb.tools.ZmdbLogger;
import com.zackmurry.zmdb.files.FileEditor;
import com.zackmurry.zmdb.files.FileReading;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CustomizationPort implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    public static final int DEFAULT_PORT = 9001;

    public static int PORT = DEFAULT_PORT;

    @Autowired
    ConfigurableServletWebServerFactory configurableServletWebServerFactory;

    //setting default port
    @Override
    public void customize(ConfigurableServletWebServerFactory server) {
        PORT = FileReading.readIntFromIndex(new File("settings.txt"), FileEditor.PORT_INDICATOR); //throws an error if there's no settings.txt file, but that fixes itself
        if(PORT != -1) {
            ZmdbLogger.log("PORT: " + PORT);
            server.setPort(PORT);
        }
        else {
            ZmdbLogger.log("Error setting/reading port from settings.txt. Setting port to 9001.");
            server.setPort(DEFAULT_PORT);
        }
    }

    public int setPort(int number) {
        try{
            configurableServletWebServerFactory.setPort(number);
            System.out.println(configurableServletWebServerFactory.getWebServer().getPort());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            ZmdbLogger.log("Unable to set port to port " + number + ". It is probably already occupied.");
            return 0;
        }
    }

}

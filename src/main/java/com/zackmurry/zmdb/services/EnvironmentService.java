package com.zackmurry.zmdb.services;

import com.zackmurry.zmdb.ZmdbLogger;
import com.zackmurry.zmdb.files.FileEditor;
import com.zackmurry.zmdb.settings.CustomizationPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentService {

    @Autowired
    Environment environment;

    @Autowired
    CustomizationPort customizationPort;

    public int getPort() {
        String port = environment.getProperty("local.server.port");
        if(port == null) {
            ZmdbLogger.log("Unable to retrieve port as it is null.");
            return -1;
        }
        try{
            return Integer.parseInt(port);
        } catch (Exception e) {
            e.printStackTrace();
            ZmdbLogger.log("Unable to retrieve port as it is not a valid number.");
            return -1;
        }
    }

    public int setPort(int number) {
        if(number <= 0) {
            ZmdbLogger.log("Couldn't set port to port " + number + " because the port must be greater than 0.");
            return 0;
        }
        if(customizationPort.setPort(number) != 1) {
            return 0;
        }
        FileEditor.updatePortInSettings(number);
        return 1;

    }


}

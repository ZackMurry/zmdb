package com.zackmurry.zmdb.controller;

import com.zackmurry.zmdb.controller.proto.ProtoInteger;
import com.zackmurry.zmdb.files.FileEditor;
import com.zackmurry.zmdb.files.FileReading;
import com.zackmurry.zmdb.services.EnvironmentService;
import com.zackmurry.zmdb.tools.ZmdbLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.File;

@RestController
public class MiscController {

    @Autowired
    private EnvironmentService environmentService;

    @DeleteMapping("/log")
    public int deleteLogData() {
        return FileEditor.silentRemoveAllTextFromFile(new File("log.txt"));
    }

    @GetMapping("/log")
    public String getLogData() {
        return FileReading.readLogData();
    }

    /**
     * closes application. will not return a response as it's dead.
     */
    @GetMapping("/exit")
    public void exitApplication() {
        ZmdbLogger.log("Exited.");
        System.exit(0);
    }

    /**
     * literally might be the most useless request.
     * @return active port if found. else -1
     */
    @GetMapping("/port")
    public int getPort() {
        return environmentService.getPort();
    }

    @PutMapping("/port")
    public int setPort(@RequestBody ProtoInteger protoInteger) {
        return environmentService.setPort(protoInteger.getNumber());
    }

}

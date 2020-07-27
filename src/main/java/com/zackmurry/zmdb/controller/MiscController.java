package com.zackmurry.zmdb.controller;

import com.zackmurry.zmdb.files.FileEditor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class MiscController {

    @DeleteMapping("/log")
    public int deleteLogData() {
        return FileEditor.silentRemoveAllTextFromFile(new File("log.txt"));
    }

}

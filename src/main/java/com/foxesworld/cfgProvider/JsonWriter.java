package com.foxesworld.cfgProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author AidenFox
 */
public class JsonWriter {
    
    
    public JsonWriter(File path,  Map<String, Object> cfgFileContent) {
        if(!path.exists()) {
            path.getParentFile().mkdirs();
        }
        writeJson(path, cfgFileContent);
    }
    
    
    private static void writeJson(File path, Map<String, Object> contents) {
 
    ObjectMapper mapper = new ObjectMapper();
    try {  
        mapper.writeValue(path, contents);
    } catch (IOException e) {}  

  }  
}

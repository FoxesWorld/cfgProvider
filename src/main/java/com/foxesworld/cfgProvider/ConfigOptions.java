package com.foxesworld.cfgProvider;

/**
 *
 * @author AidenFox
 */
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConfigOptions {

    private static ConfigUtils configInstance;

    protected ConfigOptions(ConfigUtils configInstance, Boolean fillFile) throws IOException {
        ConfigOptions.configInstance = configInstance;
        if(fillFile.equals(true)) {;
            setDefaults(ConfigUtils.cfgTemplate);
        }
    }
    
    public static HashMap<String, Object> readJsonCfg(InputStream is){
        HashMap<String, Object> map = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
            };
            map = mapper.readValue(is, typeRef);
        } catch (IOException ex) {}
        return map;
    }

    private static void setDefaults(String template) throws IOException {
        if (configInstance.getLineCount() <= 0) {
            System.out.println("    - Filling " + ConfigUtils.classFullPath + " file, with " + ConfigUtils.cfgTemplate + " contents");
        }
        for (Map.Entry<String, Object> entry : readJsonCfg(ConfigOptions.class.getClassLoader().getResourceAsStream(template)).entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            setProperty(key, value);
        }
        setProperty("created", new Date());
    }
    
    public static Color HexToColor(String hex) {
        hex = hex.replace("#", "");
        switch (hex.length()) {
            case 6:
                return new Color(
                        Integer.valueOf(hex.substring(0, 2), 16),
                        Integer.valueOf(hex.substring(2, 4), 16),
                        Integer.valueOf(hex.substring(4, 6), 16));
            case 8:
                return new Color(
                        Integer.valueOf(hex.substring(0, 2), 16),
                        Integer.valueOf(hex.substring(2, 4), 16),
                        Integer.valueOf(hex.substring(4, 6), 16),
                        Integer.valueOf(hex.substring(6, 8), 16));
        }
        return new Color(0,0,0);
    }

    public static Object getProperty(String key, String type) {
        Object property = "";
        if (configInstance.checkProperty(key)) {
            switch (type) {
                case "Int":
                    property = configInstance.getPropertyInteger(key);
                    break;

                case "String":
                    property = configInstance.getPropertyString(key);
                    break;

                case "Bool":
                    property = configInstance.getPropertyBoolean(key);
                    break;

            }
        } else {
            return false;
        }
        return property;
    }

    protected static void setProperty(String key, Object value) {
        if (!configInstance.checkProperty(key) || configInstance.getPropertyString(key) == null) {
            configInstance.put(key, value);
            //LOG.info("  - Recording a missing key `" + key + "` with value `" + value + "`");
        }
    }
}

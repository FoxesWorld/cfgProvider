package com.foxesworld.cfgProvider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AidenFox
 */
public class cfgProvider {

    /*INPUT*/
    private final String cfgTemplate;
    private final Boolean externalFile;

    /*ROOT cfg*/
    private final static Map CFG = readJsonCfg(cfgProvider.class.getClassLoader().getResourceAsStream("assets/cfg/cfgRoot.json"));
    private final static String baseDirConst = getWorkdir((Integer) CFG.get("baseDirIndex"));
    public final static String homeDirConst = (String) CFG.get("homeDir");
    private final String tplBaseDirConst = (String) CFG.get("tplBaseDir");
    private final String cfgExportDirConst = (String) CFG.get("cfgExportDir");

    /*ENVIRONMENT PATHs*/
    public final static String GAMEFULLPATH = baseDirConst + File.separator + cfgProvider.homeDirConst + File.separator;
    public static String readNote;
    public static final Integer MONTH = Calendar.getInstance().get(Calendar.MONTH) + 1;
    public static String PROTOCOL;
    public static String HOST;
    public static String SITELOC;

    public static Boolean DEBUG = false;

    /*OUTPUT*/
    public static Map<String, Object> cfgContent = new HashMap<>();
    public static Map<String, Map> cfgMaps = new HashMap<>();

    public cfgProvider(String template, Boolean external, String... args) {
        this.cfgTemplate = template;
        this.externalFile = external;
        String inputCfgPath = this.tplBaseDirConst + template;
        String cfgName = template.split("\\.")[0];
        String filePath = GAMEFULLPATH + File.separator + cfgExportDirConst + File.separator + template.split("\\.")[0] + ".cfg";
        Map<String, Object> configLines = new HashMap<>();
         Map<String, Object> cfgFileContents = configLines = readJsonCfg(cfgProvider.class.getClassLoader().getResourceAsStream(inputCfgPath));
        if (external.equals(true)) {
            
            File fullFilePath = new File(filePath);
            if(fullFilePath.exists()) {
                 readNote = "    - Reading `" + cfgName + "` from external storage " + fullFilePath;
            } else {
                
                 readNote = "    - Creating `" + cfgName + "` from inputStream " + fullFilePath;
                 JsonWriter jsonWriter = new JsonWriter(new File(filePath), cfgFileContents);
                 
            }
            configLines = readJsonCfg(new File(filePath));

        } else {
            readNote = "    - Reading `" + cfgName + "` from inputStream " + inputCfgPath;
            configLines = cfgFileContents;
        
        }
        
                if (configLines.get("debug") != null) {
                    if ("true".equals(configLines.get("debug").toString())) {
                        cfgProvider.DEBUG = true;
                    }
                System.out.println(readNote);
                }
        
        

        cfgProvider.cfgMaps.put(cfgName, configLines);
    }

    private static String getWorkdir(Integer index) {
        String path;
        switch (index) {
            case 1:
                //In user's HOMEDIR
                path = System.getProperty("user.home", "");
                break;

            case 2:
                //On user's SYSTEM-DRIVE
                path = System.getenv("SYSTEMDRIVE");
                break;

            default:
                //In a folder launched from
                path = "";
                break;

        }
        return path;
    }

    protected static HashMap<String, Object> readJsonCfg(InputStream is) {
        HashMap<String, Object> map = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
            };
            map = mapper.readValue(is, typeRef);
        } catch (IOException ignored) {
        }

        return map;
    }
    
    protected static HashMap<String, Object> readJsonCfg(File path) {
        HashMap<String, Object> map = null;
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
        };
        try {
            map = mapper.readValue(path, typeRef);
        } catch (IOException ex) {
            Logger.getLogger(cfgProvider.class.getName()).log(Level.SEVERE, null, ex);
        }

        return map;
    }
}

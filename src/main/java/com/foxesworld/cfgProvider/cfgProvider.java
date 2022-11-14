package com.foxesworld.cfgProvider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
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


    /*ROOT cfg*/
    private final static Map defaultConfig = readJsonCfg(cfgProvider.class.getClassLoader().getResourceAsStream("assets/cfg/defaultCfg.json"));
    
        private static String baseDirPath = getWorkdir((Integer) defaultConfig.get("baseDirIndex"));
        private static String homeDirName = (String) defaultConfig.get("homeDir");
        private static String defaultConfFilesDir = (String) defaultConfig.get("tplBaseDir");
        private static String cfgFileExtension = (String) defaultConfig.get("cfgExtension");
        private static String cfgExportDirName = (String) defaultConfig.get("cfgExportDir");
        private static Boolean debug = (Boolean) defaultConfig.get("debug");

    /*ENVIRONMENT PATHs*/
    public final static String GAMEFULLPATH = baseDirPath + File.separator + cfgProvider.homeDirName + File.separator;
    private static String readNote;
    private static final Integer MONTH = Calendar.getInstance().get(Calendar.MONTH) + 1;

    /*OUTPUT*/
    public static Map<String, Object> cfgContent = new HashMap<>();
    public static Map<String, Map> cfgMaps = new HashMap<>();

    public cfgProvider(String template, Boolean external, String... args) {
        String inputCfgPath = cfgProvider.defaultConfFilesDir + template;
        String cfgName = template.split("\\.")[0];
        String absoluteCfgPath = cfgProvider.getFileAbsolutePath(cfgName);
        Map<String, Object> configLines;
        Map<String, Object> cfgFileContents = readJsonCfg(cfgProvider.class.getClassLoader().getResourceAsStream(inputCfgPath));
        if (external.equals(true)) {
            File absoluteFileCfgPath = new File(absoluteCfgPath);
            if(absoluteFileCfgPath.exists()) {
                 readNote = "    - Reading `" + cfgName + "` from external storage " + absoluteFileCfgPath;
            } else {
                 readNote = "    - Creating `" + absoluteCfgPath + "` from inputStream " + absoluteFileCfgPath;
                 JsonWriter jsonWriter = new JsonWriter(new File(absoluteCfgPath), cfgFileContents); 
            }
            configLines = readJsonCfg(new File(absoluteCfgPath));
        } else {
            readNote = "    - Reading `" + cfgName + "` from inputStream " + inputCfgPath;
            configLines = cfgFileContents;
        }
        
            if ("true".equals(debug)) {
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
    
    private static String getFileAbsolutePath(String cfgName) {
        return GAMEFULLPATH + File.separator + cfgExportDirName + File.separator + cfgName + cfgFileExtension;
    }
    
    public static void setHomeDir(String homeDir){
        cfgProvider.homeDirName = homeDir;
    }
    
    public static void setBaseDirPathIndex(int index){
        cfgProvider.baseDirPath = getWorkdir(index);
    }
    
    public static void setDefaultConfFilesDir(String directory) {
        cfgProvider.defaultConfFilesDir = directory;
    }
    
    public static void setCfgFileExtension(String extension) {
        cfgProvider.cfgFileExtension = extension;
    }
    
    public static void setCfgExportDirName(String dirName) {
        cfgProvider.cfgExportDirName = dirName;
    }
    
    public static void setDebug(Boolean debug){
        cfgProvider.debug = debug;
    }
    
    public static String getReadNote(){
        return readNote;
    }
    
    public static Integer getMonth() {
        return MONTH;
    }
}

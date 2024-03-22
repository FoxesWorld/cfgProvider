package org.foxesworld.cfgProvider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author AidenFox
 */
public class CfgProvider {


    /*ROOT cfg*/
    private final static Map<String, Object> defaultConfig = readJsonCfg(CfgProvider.class.getClassLoader().getResourceAsStream("assets/cfg/defaultCfg.json"));
    
        private static String baseDirPath = getWorkdir((Integer) defaultConfig.get("baseDirIndex"));
        private static String homeDirName = (String) defaultConfig.get("homeDir");
        private static String currentCfg;
        private static String defaultConfFilesDir = (String) defaultConfig.get("tplBaseDir");
        private static String cfgFileExtension = (String) defaultConfig.get("cfgExtension");
        private static String cfgExportDirName = (String) defaultConfig.get("cfgExportDir");
        private static Boolean debug = (Boolean) defaultConfig.get("debug");

    /*ENVIRONMENT PATHs*/
    private final static String GAMEFULLPATH = baseDirPath + File.separator + CfgProvider.homeDirName + File.separator;
    private static String readNote;
    private static final Integer MONTH = Calendar.getInstance().get(Calendar.MONTH) + 1;

    /*OUTPUT*/
    private static Map<String, Object> configLines = new HashMap<>();
    private static Map<String, Map> cfgMaps = new HashMap<>();

    public CfgProvider(String template) {
        String inputCfgPath = CfgProvider.defaultConfFilesDir + template;
        Map<String, Object> cfgFileContents = readJsonCfg(CfgProvider.class.getClassLoader().getResourceAsStream(inputCfgPath));
        currentCfg = template.split("\\.")[0];
        String absoluteCfgPath = getFileAbsolutePath(currentCfg);
        
        if(!template.contains("internal")){
            File absoluteFileCfgPath = new File(absoluteCfgPath);
            if(absoluteFileCfgPath.exists()) {
                 readNote = "    - Reading `" + currentCfg + "` from external storage " + absoluteFileCfgPath;
            } else {
                 readNote = "    - Creating `" + absoluteCfgPath + "` from inputStream " + absoluteFileCfgPath;
                 JsonWriter jsonWriter = new JsonWriter(new File(absoluteCfgPath), cfgFileContents); 
            }
            setConfigLines(readJsonCfg(new File(absoluteCfgPath)));
        } else {
            readNote = "    - Reading `" + currentCfg + "` from inputStream " + inputCfgPath;
            setConfigLines(cfgFileContents);
        }
        
            if ("true".equals(debug)) {
                System.out.println(readNote);
            }
        putCfgMap();
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
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
            };
            map = mapper.readValue(is, typeRef);
        } catch (IOException ignored) {}

        return map;
    }

    protected static HashMap<String, Object> readJsonCfg(File path) {
        HashMap<String, Object> map = null;
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
        };
        try {
            map = mapper.readValue(path, typeRef);
        } catch (IOException ex) {}

        return map;
    }
    
    private static String getFileAbsolutePath(String cfgName) {
        return GAMEFULLPATH + File.separator + cfgExportDirName + File.separator + cfgName + cfgFileExtension;
    }
    
    private static void putCfgMap() {
        cfgMaps.put(getCurrentCfgName(), getConfigLines());
    }
    
    private static void setConfigLines(Map configLines){
        CfgProvider.configLines = configLines;
    }
    @SuppressWarnings("unused")
    public static void setHomeDir(String homeDir){
        CfgProvider.homeDirName = homeDir;
    }
    @SuppressWarnings("unused")
    public static void setBaseDirPathIndex(int index){
        CfgProvider.baseDirPath = getWorkdir(index);
    }
    @SuppressWarnings("unused")
    public static void setDefaultConfFilesDir(String directory) {
        CfgProvider.defaultConfFilesDir = directory;
    }
    @SuppressWarnings("unused")
    public static void setCfgFileExtension(String extension) {
        CfgProvider.cfgFileExtension = extension;
    }
    @SuppressWarnings("unused")
    public static void setCfgExportDirName(String dirName) {
        CfgProvider.cfgExportDirName = dirName;
    }
    @SuppressWarnings("unused")
    public static void setDebug(Boolean debug){
        CfgProvider.debug = debug;
    }
    
    public static String getReadNote(){
        return readNote;
    }
    
    public static String getCurrentCfgName() {
        return currentCfg;
    }
    
    public static Map getConfigLines() {
        return configLines;
    }
    @SuppressWarnings("unused")
    public static Map<String, Map> getAllCfgMaps() {
        return cfgMaps;
    }
    
    public static HashMap<String, List<Object>> getCfgMap(String mapName) {
        return (HashMap<String, List<Object>>) cfgMaps.get(mapName);
    }
    @SuppressWarnings("unused")
    public static Integer getMonth() {
        return MONTH;
    }

    @SuppressWarnings("unused")
    public static String getGameFullPath() {
        return GAMEFULLPATH;
    }
}

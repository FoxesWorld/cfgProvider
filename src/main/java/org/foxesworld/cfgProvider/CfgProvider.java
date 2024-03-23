package org.foxesworld.cfgProvider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CfgProvider {

    private static final Map<String, Object> defaultConfig;

    private static String baseDirPath;
    private static String homeDirName;
    private static String currentCfg;
    private static String defaultConfFilesDir;
    private static String cfgFileExtension;
    private static String cfgExportDirName;
    private static Boolean debug;
    private static final String GAMEFULLPATH;
    private static String readNote;
    private static final Integer MONTH;

    private static final Map<String, Object> configLines = new HashMap<>();
    private static final Map<String, Map<String, Object>> cfgMaps = new HashMap<>();

    static {
        defaultConfig = readJsonCfg(CfgProvider.class.getClassLoader().getResourceAsStream("assets/cfg/defaultCfg.json"));
        baseDirPath = getWorkdir((Integer) defaultConfig.get("baseDirIndex"));
        homeDirName = (String) defaultConfig.get("homeDir");
        defaultConfFilesDir = (String) defaultConfig.get("tplBaseDir");
        cfgFileExtension = (String) defaultConfig.get("cfgExtension");
        cfgExportDirName = (String) defaultConfig.get("cfgExportDir");
        debug = (Boolean) defaultConfig.get("debug");

        GAMEFULLPATH = baseDirPath + File.separator + homeDirName;
        MONTH = Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public CfgProvider(String template) {
        String inputCfgPath = defaultConfFilesDir + template;
        Map<String, Object> cfgFileContents = readJsonCfg(CfgProvider.class.getClassLoader().getResourceAsStream(inputCfgPath));
        currentCfg = template.split("\\.")[0];
        String absoluteCfgPath = getFileAbsolutePath(currentCfg);

        if (!template.contains("internal")) {
            File absoluteFileCfgPath = new File(absoluteCfgPath);
            if (absoluteFileCfgPath.exists()) {
                readNote = "    - Reading `" + currentCfg + "` from external storage " + absoluteFileCfgPath;
            } else {
                readNote = "    - Creating `" + absoluteCfgPath + "` from inputStream " + inputCfgPath;
                JsonWriter jsonWriter = new JsonWriter(new File(absoluteCfgPath), cfgFileContents);
            }
            setConfigLines(readJsonCfg(new File(absoluteCfgPath)));
        } else {
            readNote = "    - Reading `" + currentCfg + "` from inputStream " + inputCfgPath;
            setConfigLines(cfgFileContents);
        }

        if (debug) {
            System.out.println(readNote);
        }
        putCfgMap();
    }

    private static String getWorkdir(Integer index) {
        String path;
        switch (index) {
            case 1:
                path = System.getProperty("user.home", "");
                break;
            case 2:
                path = System.getenv("SYSTEMDRIVE");
                break;
            case 3:
                path = System.getenv("APPDATA");
                break;
            default:
                path = "";
                break;
        }
        return path;
    }

    protected static Map<String, Object> readJsonCfg(InputStream is) {
        Map<String, Object> map = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
            map = mapper.readValue(is, typeRef);
        } catch (IOException ignored) {
        }
        return map;
    }

    protected static Map<String, Object> readJsonCfg(File path) {
        Map<String, Object> map = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
            map = mapper.readValue(path, typeRef);
        } catch (IOException ex) {
        }
        return map;
    }

    private static String getFileAbsolutePath(String cfgName) {
        return GAMEFULLPATH + File.separator + cfgExportDirName + File.separator + cfgName + cfgFileExtension;
    }

    private static void putCfgMap() {
        cfgMaps.put(getCurrentCfgName(), getConfigLines());
    }

    private static void setConfigLines(Map<String, Object> configLines) {
        CfgProvider.configLines.clear();
        CfgProvider.configLines.putAll(configLines);
    }

    @SuppressWarnings("unused")
    public static void setHomeDir(String homeDir) {
        homeDirName = homeDir;
    }

    @SuppressWarnings("unused")
    public static void setBaseDirPathIndex(int index) {
        baseDirPath = getWorkdir(index);
    }

    @SuppressWarnings("unused")
    public static void setDefaultConfFilesDir(String directory) {
        defaultConfFilesDir = directory;
    }

    @SuppressWarnings("unused")
    public static void setCfgFileExtension(String extension) {
        cfgFileExtension = extension;
    }

    @SuppressWarnings("unused")
    public static void setCfgExportDirName(String dirName) {
        cfgExportDirName = dirName;
    }

    @SuppressWarnings("unused")
    public static void setDebug(Boolean debug) {
        CfgProvider.debug = debug;
    }

    public static String getReadNote() {
        return readNote;
    }

    public static String getCurrentCfgName() {
        return currentCfg;
    }

    public static Map<String, Object> getConfigLines() {
        return new HashMap<>(configLines);
    }

    @SuppressWarnings("unused")
    public static Map<String, Map<String, Object>> getAllCfgMaps() {
        return new HashMap<>(cfgMaps);
    }

    @SuppressWarnings("unchecked")
    public static Map getCfgMap(String mapName) {
        return cfgMaps.get(mapName);
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

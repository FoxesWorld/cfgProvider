package com.foxesworld.cfgProvider;

import com.foxesworld.cfgProvider.defaultCfg.DefaultCfg;
import com.foxesworld.cfgProvider.defaultCfg.DefaultCfgAttributes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class CfgProvider {

    private static final String DEFAULT_CFG_PATH = "assets/cfg/defaultCfg.json";
    private static final DefaultCfgAttributes CONSTANTS = new DefaultCfg(DEFAULT_CFG_PATH).getCONSTANTS();
    private static String baseDirPath = getWorkdir(CONSTANTS.getBaseDirIndex());
    private static String currentCfg;
    private static final String GAMEFULLPATH = baseDirPath + File.separator + CONSTANTS.getHomeDir() + File.separator;
    private String readNote;

    /*OUTPUT*/
    private Map<String, Object> configLines = new HashMap<>();
    private Map<String, Map<String, Object>> cfgMaps = new HashMap<>();

    public CfgProvider(String template) {
        String inputCfgPath = CONSTANTS.getTplBaseDir() + template;
        Map<String, Object> cfgFileContents = readJsonCfg(
                CfgProvider.class.getClassLoader().getResourceAsStream(inputCfgPath)
        );
        currentCfg = template.split("\\.")[0];
        String absoluteCfgPath = getFileAbsolutePath(currentCfg);

        if (!template.contains("internal")) {
            File absoluteFileCfgPath = new File(absoluteCfgPath);
            if (absoluteFileCfgPath.exists()) {
                readNote = "    - Reading `" + currentCfg + "` from external storage " + absoluteFileCfgPath;
            } else {
                readNote = "    - Creating `" + absoluteCfgPath + "` from inputStream " + absoluteFileCfgPath;
                JsonWriter jsonWriter = new JsonWriter(absoluteFileCfgPath, cfgFileContents);
            }
            setConfigLines(readJsonCfg(CfgProvider.class.getClassLoader().getResourceAsStream(inputCfgPath)));
        } else {
            readNote = "    - Reading `" + currentCfg + "` from inputStream " + inputCfgPath;
            setConfigLines(cfgFileContents);
        }

        if (CONSTANTS.isDebug()) {
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

            case 3:
                path = System.getenv("APPDATA");
                break;

            default:
                //In a folder launched from
                path = "";
                break;

        }
        return path;
    }

    protected static Map<String, Object> readJsonCfg(InputStream is) {
        try (Reader reader = new InputStreamReader(is)) {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            return new Gson().fromJson(reader, type);
        } catch (IOException ignored) {
            return new HashMap<>();
        }
    }

    protected static void writeJsonCfg(File path, Map<String, Object> map) {
        try (Writer writer = new FileWriter(path)) {
            new Gson().toJson(map, writer);
        } catch (IOException ignored) {
        }
    }

    private static String getFileAbsolutePath(String cfgName) {
        return GAMEFULLPATH + File.separator + CONSTANTS.getCfgExportDir() + File.separator + cfgName + CONSTANTS.getCfgExtension();
    }

    private void putCfgMap() {
        cfgMaps.put(getCurrentCfgName(), getConfigLines());
    }

    private void setConfigLines(Map<String, Object> configLines) {
        this.configLines = configLines;
    }

    public static void setHomeDir(String homeDir) {
        CONSTANTS.setHomeDir(homeDir);
    }

    public static void setBaseDirPathIndex(int index) {
        CONSTANTS.setBaseDirIndex(index);
    }

    public static void setDefaultConfFilesDir(String directory) {
        CONSTANTS.setCfgExportDir(directory);
    }

    public static void setCfgFileExtension(String extension) {
       CONSTANTS.setCfgExtension(extension);
    }

    public static void setCfgExportDirName(String dirName) {
        CONSTANTS.setCfgExportDir(dirName);
    }

    public void setDebug(Boolean debug) {
        CONSTANTS.setDebug(debug);
    }

    public String getReadNote() {
        return readNote;
    }

    public static String getCurrentCfgName() {
        return currentCfg;
    }

    public Map<String, Object> getConfigLines() {
        return configLines;
    }

    public Map<String, Map<String, Object>> getAllCfgMaps() {
        return cfgMaps;
    }

    public Map<String, Object> getCfgMap(String mapName) {
        return cfgMaps.get(mapName);
    }

    public static String getGameFullPath() {
        return GAMEFULLPATH;
    }
}

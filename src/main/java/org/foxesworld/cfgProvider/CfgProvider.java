package org.foxesworld.cfgProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CfgProvider {
    private static final Map<String, Object> defaultConfig;
    public static Logger LOGGER;
    private static String baseDirPath;
    private static String homeDirName;
    private static String currentCfg;
    private static String defaultConfFilesDir;
    private static String cfgFileExtension;
    private static String cfgExportDirName;
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
                readNote = "Reading `" + currentCfg + "` from external storage " + absoluteFileCfgPath;
            } else {
                readNote = "Creating `" + absoluteCfgPath + "` from inputStream " + inputCfgPath;
                JsonWriter jsonWriter = new JsonWriter(new File(absoluteCfgPath), cfgFileContents);
            }
            setConfigLines(readJsonCfg(new File(absoluteCfgPath)));
        } else {
            readNote = "Reading `" + currentCfg + "` from inputStream " + inputCfgPath;
            setConfigLines(cfgFileContents);
        }

        LOGGER.debug(readNote);
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
    protected static Map<String, Object> readJsonCfg(InputStream source) {
        Map<String, Object> map = parseJson(source);
        return handleNumericValues(map);
    }

    protected static Map<String, Object> readJsonCfg(File source) {
        Map<String, Object> map = parseJson(source);
        return handleNumericValues(map);
    }

    private static Map<String, Object> parseJson(Object source) {
        Map<String, Object> map = new HashMap<>();
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();

        try (InputStreamReader reader = new InputStreamReader(getInputStream(source))) {
            map = gson.fromJson(reader, type);
        } catch (IOException ignored) {
        }
        return map;
    }

    private static InputStream getInputStream(Object source) throws IOException {
        if (source instanceof InputStream) {
            return (InputStream) source;
        } else if (source instanceof File) {
            URL url = ((File) source).toURI().toURL();
            return url.openStream();
        } else {
            throw new IllegalArgumentException("Invalid source type");
        }
    }

    private static Map<String, Object> handleNumericValues(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Double) {
                // Convert Double to Integer if it's a whole number
                double doubleValue = (Double) value;
                if (doubleValue == Math.floor(doubleValue)) {
                    entry.setValue((int) doubleValue);
                }
            }
            // Handle nested maps recursively
            if (value instanceof Map) {
                entry.setValue(handleNumericValues((Map<String, Object>) value));
            }
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

    public static void setLOGGER(Logger LOGGER) {
        CfgProvider.LOGGER = LOGGER;
    }
}

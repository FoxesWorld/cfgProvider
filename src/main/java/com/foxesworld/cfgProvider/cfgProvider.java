package com.foxesworld.cfgProvider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 *
 * @author AidenFox
 */
public class cfgProvider {

    /*INPUT*/
    private final String cfgTemplate;
    private final Boolean externalFile;

    /*CONSTANTS*/
    private final static Map CFG = readJsonCfg(cfgProvider.class.getClassLoader().getResourceAsStream("assets/cfg/cfgRoot.json"));
    private final static String baseDirConst = getWorkdir((Integer) CFG.get("baseDirIndex"));
    public final static String homeDirConst = (String) CFG.get("homeDir");
    private final String tplBaseDirConst = (String) CFG.get("tplBaseDir");
    private final String cfgExportDirConst = (String) CFG.get("cfgExportDir");

    /*ENVIRONMENT PATHs*/
    public final static String GAMEFULLPATH = baseDirConst + File.separator + cfgProvider.homeDirConst + File.separator;
    public static final Integer MONTH = Calendar.getInstance().get(Calendar.MONTH) + 1;
    public static String PROTOCOL;
    public static String HOST;
    public static String SITELOC;

    public static Boolean DEBUG = false;

    /*OUTPUT*/
    public static Map<String, Object> cfgContent = new HashMap<>();
    public static Map<String, Map> cfgMaps = new HashMap<>();

    public cfgProvider(String template, Boolean external) {
        this.cfgTemplate = template;
        this.externalFile = external;
        String cfgFilePath = this.tplBaseDirConst + template;
        String cfgName = template.split("\\.")[0];
        Map<String, Object> configLines = new HashMap<>();
        if (external.equals(true)) {
            File fullFilePath = new File(GAMEFULLPATH + File.separator + cfgExportDirConst + File.separator + template.split("\\.")[0] + ".cfg");
            try {
                ConfigUtils config = new ConfigUtils(fullFilePath, cfgFilePath) {
                };
                ConfigOptions cfo = new ConfigOptions(config, true);
                System.out.println("    - Reading `" + cfgName + "` from external storage " + fullFilePath);
                configLines = loadHashMap(fullFilePath);
            } catch (IOException ex) {
            }
        } else {
            System.out.println("    - Reading `" + cfgName + "` from local storage " + cfgFilePath);
            configLines = cfgProvider.readJsonCfg(cfgProvider.class.getClassLoader().getResourceAsStream(cfgFilePath));
        }
        if (configLines.get("debug") != null) {
            if ("true".equals(configLines.get("debug").toString())) {
                cfgProvider.DEBUG = true;
            }
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
                //On user's SYSTEMDRIVE
                path = System.getenv("SYSTEMDRIVE");
                break;

            default:
                //In a folder launched from
                path = "";
                break;

        }
        return path;
    }

    private static HashMap<String, Object> readJsonCfg(InputStream is) {
        HashMap<String, Object> map = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
            };
            map = mapper.readValue(is, typeRef);
        } catch (IOException ex) {
        }

        return map;
    }

    private HashMap<String, Object> loadHashMap(File cfgFile) {
        HashMap<String, Object> result = new HashMap<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(cfgFile));
            String line;
            while ((line = br.readLine()) != null) {
                if ((line.isEmpty()) || (line.startsWith("#")) || (!line.contains(": "))) {
                    continue;
                }
                String[] args = line.split(": ");
                if (args.length < 2) {
                    result.put(args[0], null);
                    continue;
                }
                result.put(args[0], args[1]);
            }
        } catch (IOException ex) {
        } finally {

            try {
                br.close();
            } catch (IOException e) {
            }
        }
        return result;
    }

    private String getVersion() {
        Attributes attr = null;
        try {
            URLClassLoader cl = (URLClassLoader) cfgProvider.class.getClassLoader();
            URL url = cl.findResource("META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(url.openStream());
            attr = manifest.getMainAttributes();
            System.out.println(attr.values());
        } catch (IOException ex) {
        }
        return attr.getValue("Implementation-Version");
    }
}

package org.foxesworld.cfgProvider.defaultCfg;

public class DefaultCfgAttributes {

    private int baseDirIndex;
    private String homeDir;
    private String tplBaseDir;
    private String cfgExportDir;
    private String cfgExtension;
    private boolean debug;

    public int getBaseDirIndex() {
        return baseDirIndex;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public String getTplBaseDir() {
        return tplBaseDir;
    }

    public String getCfgExportDir() {
        return cfgExportDir;
    }

    public String getCfgExtension() {
        return cfgExtension;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setBaseDirIndex(int baseDirIndex) {
        this.baseDirIndex = baseDirIndex;
    }

    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }

    public void setTplBaseDir(String tplBaseDir) {
        this.tplBaseDir = tplBaseDir;
    }

    public void setCfgExportDir(String cfgExportDir) {
        this.cfgExportDir = cfgExportDir;
    }

    public void setCfgExtension(String cfgExtension) {
        this.cfgExtension = cfgExtension;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}

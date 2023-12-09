package com.foxesworld.cfgProvider.defaultCfg;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.util.Objects;

public class DefaultCfg {
    private DefaultCfgAttributes CONSTANTS;

    public DefaultCfg(String path){
        this.parseConstants(path);
    }

    private void parseConstants(String path){
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(DefaultCfg.class.getClassLoader().getResourceAsStream(path)));
        this.CONSTANTS = new Gson().fromJson(reader, DefaultCfgAttributes.class);
    }

    public DefaultCfgAttributes getCONSTANTS() {
        return CONSTANTS;
    }
}

package com.foxesworld.cfgProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class JsonWriter {

    public JsonWriter(File path, Map<String, Object> cfgFileContent) {
        if (!path.exists()) {
            path.getParentFile().mkdirs();
        }
        writeJson(path, cfgFileContent);
    }

    private static void writeJson(File path, Map<String, Object> contents) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(contents, writer);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
    }
}

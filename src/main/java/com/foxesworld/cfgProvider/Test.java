package com.foxesworld.cfgProvider;

import java.util.Map;

/**
 *
 * @author AidenFox
 */
public class Test {

    private static cfgProvider test;
    private static Map testMap = null;

    public static void main(String[] args) {
        test = new cfgProvider("test.json", false);
        testMap = test.cfgMaps.get("test");
        System.out.println(testMap);
    }

}

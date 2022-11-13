package com.foxesworld.cfgProvider;

import java.util.Map;

/**
 * @author AidenFox
 */
public class Test {

    private static cfgProvider test;
    private static Map testMap = null;

    public static void main(String[] args) {
        test = new cfgProvider("test.json", true);
        testMap = test.cfgMaps.get("test");
        mapList();
    }
    
    private static void mapList() {
        Object inputArray;
         for (Object inputArrName : testMap.keySet()) {
           inputArray = testMap.get(inputArrName);
             System.out.println(testMap.get(inputArrName));
         }
         System.out.println(cfgProvider.readNote);
         

    }

}

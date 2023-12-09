package com.foxesworld.cfgProvider;

import java.util.Map;

/**
 * @author AidenFox
 */
public class Test {

    private static CfgProvider test;
    private static Map testMap = null;

    public static void main(String[] args) {
        test = new CfgProvider("test.json");
        testMap = test.getCfgMap(CfgProvider.getCurrentCfgName());
        mapList();
    }
    
    private static void mapList() {
        Object inputArray;
         for (Object inputArrName : testMap.keySet()) {
           inputArray = testMap.get(inputArrName);
             System.out.println(testMap.get(inputArrName));
         }
         System.out.println(test.getReadNote());
    }

}

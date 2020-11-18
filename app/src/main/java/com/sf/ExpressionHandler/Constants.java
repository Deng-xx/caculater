package com.sf.ExpressionHandler;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    private static Map<String, String> constants;

    public static Map<String, String> load() {
        if (constants == null) {
            Map map = new HashMap<>();
            map.put("ans", "0");
            constants = map;
        }
        return constants;
    }

    public static void setAns(String value) {
        constants.put("ans", value);
    }
}
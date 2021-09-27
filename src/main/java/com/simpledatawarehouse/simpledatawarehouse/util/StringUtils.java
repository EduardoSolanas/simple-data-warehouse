package com.simpledatawarehouse.simpledatawarehouse.util;

public class StringUtils {

    public static String cleanse(String valueToCleanse) {
        return valueToCleanse.toLowerCase().replaceAll("\\s+","");
    }
}

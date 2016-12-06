package me.fernandodominguez.zenmap.helpers;

/**
 * Coded by fernando on 08/02/16.
 */
public class StringHelper {
    public static String truncate(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len-3) + "...";
        } else {
            return str;
        }
    }

    public static String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static String toCamelCase(String str) {
        String[] parts = str.split("_");
        String camelCaseString = "";
        for (String part : parts){
            camelCaseString += capitalize(part);
        }
        return camelCaseString;
    }
}

package me.fernandodominguez.zenmap.helpers;

/**
 * Created by fernando on 08/02/16.
 */
public class StringHelper {
    public static String truncate(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len-3) + "...";
        } else {
            return str;
        }
    }
}

package me.fernandodominguez.zenmap.helpers;


import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by fernando on 14/01/16.
 */
public class DateHelper {

    public static String getDate(long timeStamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStamp*1000);
        return DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();
    }
}

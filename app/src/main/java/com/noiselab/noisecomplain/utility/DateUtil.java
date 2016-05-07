package com.noiselab.noisecomplain.utility;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shawn on 7/4/2016.
 */
public class DateUtil {

    public static Date toDate(String pattern, String string) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            return formatter.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isToday(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String todayFormat = formatter.format(System.currentTimeMillis());
        String dateFormat = formatter.format(date);
        return dateFormat.equals(todayFormat);
    }

}

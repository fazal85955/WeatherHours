package com.apps.sfrcreativity.weatherhours.utils;



import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Clock {


    public Clock() { }




    private static Date getFormat(String text) {
        DateFormat OWM_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = OWM_format.parse(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getSec(String text) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("ss");
        return formatter.format(getFormat(text).getTime());
    }


    public static String getClock(String text) {
        SimpleDateFormat formatter = new SimpleDateFormat("aa");
        String clock = formatter.format(getFormat(text).getTime());
        return clock;
    }

    public static String getDay(String text, boolean full) {
        SimpleDateFormat formatter;
        String strDateFormat = full ? "EEEE" : "EE";
        formatter = new SimpleDateFormat(strDateFormat);
        return formatter.format(getFormat(text).getTime());
    }

    public static String getFormattedDate(String date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(getFormat(date));
    }

    public static String getFormattedTime(String date, String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(getFormat(date).getTime());
    }
/*
    public static String getFormattedTodayDate(String format) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }
    public static String getFormattedTodayTime(String format) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date.getTime());
    }
*/
    public static String getDate(String text) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("dd");
        return formatter.format(getFormat(text).getTime());
    }

    public static String getNowHour() {
        Date date = new Date();
        String strDateFormat = "hh";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);
    }
    public static String getNowHour(boolean hour12) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat(hour12 ? "hh" : "kk");
        return dateFormat.format(date);
    }


    public static String getDayTime(String date) {
        int hour;
        if(date==null) {
            hour = Integer.valueOf(getNowHour(false));
        } else {
            hour = Integer.valueOf(getHour(date,false));
        }
        if(hour>=6 && hour<18) {
            return "DAY";
        } else {
            return "NIGHT";
        }
    }



    public static String getNowMin() {
        Date date = new Date();
        String strDateFormat = "mm";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);
    }

    public static String getNowSec() {
        Date date = new Date();
        String strDateFormat = "ss";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);
    }

    public static String getNowClock() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("aa");
        String clock = formatter.format(date);
        return clock;
    }

    public static String getTodayDate() {
        Date date = new Date();
        String strDateFormat = "dd";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);
    }

    public static String getToday(boolean full) {
        Date date = new Date();
        String strDateFormat = full ? "EEEE" : "EE";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);
    }

    public static String getTodayMonth(boolean full) {
        Date date = new Date();
        String strDateFormat = full ? "MMMM" : "MM";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);
    }


    private static String getMonth(String text) {
        SimpleDateFormat formatter;
        String strDateFormat = "MMMM";
        formatter = new SimpleDateFormat(strDateFormat);
        return formatter.format(getFormat(text).getTime());
    }

    public static String getTodayYear(boolean full) {
        Date date = new Date();
        String strDateFormat = full ? "yyyy" : "yy";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);
    }

    public static String getHour(String text, boolean hour12) {
        SimpleDateFormat formatter = new SimpleDateFormat(hour12 ? "hh" : "kk");
        return formatter.format(getFormat(text).getTime());
    }

    private static String getYear(String text) {
        SimpleDateFormat formatter;
        String strDateFormat = "yyyy";
        formatter = new SimpleDateFormat(strDateFormat);
        return formatter.format(getFormat(text).getTime());
    }

    private static String getMin(String text) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("mm");
        return formatter.format(getFormat(text).getTime());
    }


}

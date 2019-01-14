package com.apps.sfrcreativity.weatherhours.utils;

import com.apps.sfrcreativity.weatherhours.models.Spot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class OWMDataParser {

    // These are the names of the JSON objects that need to be extracted.
    public final static String OWM_RESULT = "cod";
    public final static int OWM_SUCCESS_CODE = 200;
    /*
    public final static int OWM_ERROR_CODE = 404;
    public final static int OWM_INVALID_API_CODE = 401;
    public final static int OWM_REQUEST_EXCEED_CODE = 429;
    public final static int OWM_NO_GEO_CODE = 400;
*/
    // Geo information
    // objects
    public final static String OWM_OBJ_GEO_HRS = "city"; //major object
    private final static String OWM_OBJ_COORDINATES = "coord"; //minor object
    // in the city obj
    public final static String OWM_VAR_CITY = "name"; //minor variable
    public final static String OWM_VAR_COUNTRY = "country"; //minor variable
    public final static String OWM_VAR_POPULATION = "population"; //minor variable
    // in the coord obj
    public final static String OWM_VAR_LATTITUDE = "lat"; //minor variable
    public final static String OWM_VAR_LONGTITUDE = "lon"; //minor variable

    // Other details
    // arrays and objects
    public final static String OWM_ARRAY_LIST = "list"; //major array
    public final static String OWM_OBJ_WEATHER = "weather"; //major object
    private final static String OWM_OBJ_MAIN = "main"; //major object
    private final static String OWM_OBJ_WIND = "wind"; //major object
    private final static String OWM_OBJ_SYS = "sys"; //major object
    public final static String OWM_OBJ_RAIN = "rain"; //major object
    public final static String OWM_OBJ_SNOW = "snow"; //major object
    public final static String OWM_OBJ_CLOUDS = "clouds"; //major object
    // Detailed variables
    // in the weather obj
    public final static String OWM_VAR_MAIN = "main"; //major object
    public final static String OWM_VAR_ICON = "icon"; //minor var
    // in the main obj
    public final static String OWM_VAR_TEMPERATURE = "temp"; //minor var
    public final static String OWM_VAR_MAX = "temp_max"; //minor var
    public final static String OWM_VAR_MIN = "temp_min"; //minor var
    public final static String OWM_VAR_HUMIDITY = "humidity"; //minor var
    public final static String OWM_VAR_PRESSURE = "pressure"; //minor var
    public final static String OWM_VAR_SEA_LVL = "sea_level"; //minor var
    public final static String OWM_VAR_GRND_LVL = "grnd_level"; //minor var
    public final static String OWM_VAR_VISIBILITY = "visibility"; //minor var
    // in the sys obj
    public final static String OWM_VAR_SUNRISE = "sunrise"; //minor var
    public final static String OWM_VAR_SUNSET = "sunset"; //minor var
    public final static String OWM_VAR_DATE_TXT = "dt_txt"; //minor var
    // in the wind obj
    public final static String OWM_VAR_WIND_DEGREE = "deg"; //minor var
    public final static String OWM_VAR_WIND_SPEED = "speed"; //minor var

    private final static String OWM_VAR_3H = "3h"; //minor var

    // in the clouds obj
    private final static String OWM_VAR_CLOUDS = "all"; //minor var

     public static Spot.GeoReport getGeoDataFromJson(JSONObject forecastJson)
             throws JSONException {


         if(forecastJson.has(OWM_RESULT)) {
             if (forecastJson.getString(OWM_RESULT).equals(String.valueOf(OWM_SUCCESS_CODE))) {
                 Spot.GeoReport geoReport = new Spot.GeoReport();
                 // city obj
                 JSONObject inGEO = forecastJson.getJSONObject(OWM_OBJ_GEO_HRS);
                 geoReport.setGeoData(
                         getString(inGEO, OWM_VAR_CITY),
                         getString(inGEO, OWM_VAR_COUNTRY),
                         getLong(inGEO, OWM_VAR_POPULATION),
                         getDouble(getJObject(inGEO,OWM_OBJ_COORDINATES),OWM_VAR_LATTITUDE),
                         getDouble(getJObject(inGEO,OWM_OBJ_COORDINATES),OWM_VAR_LONGTITUDE));

                 return geoReport;
             }
         } else {
            return null;
         }

         return null;
     }

     private static String getString(JSONObject object, String cons) {
         try {
             return object.has(cons) ? object.getString(cons) : "";
         } catch (JSONException e) {
             e.printStackTrace();
             return "";
         }
     }

    private static long getLong(JSONObject object, String cons) {
        try {
            return object.has(cons) ? object.getLong(cons) : 0;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static int getInt(JSONObject object, String cons) {
        try {
            return object.has(cons) ? object.getInt(cons) : 0;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static double getDouble(JSONObject object, String cons) {
        try {
            return object.has(cons) ? object.getDouble(cons) : 0.0;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private static JSONArray getJArray(JSONObject object, String cons) {
        try {
            return object.has(cons) ? object.getJSONArray(cons) : new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static JSONObject getJObject(JSONObject object, String cons) {
        try {
            return object.has(cons) ? object.getJSONObject(cons) : new JSONObject();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }



    public static Spot.Report getSingleDataFromJson(JSONObject forecastJson)
            throws JSONException {

            if (getString(forecastJson, OWM_RESULT).equals(String.valueOf(OWM_SUCCESS_CODE))) {
                Spot.Report report = new Spot.Report();
                // objects
                JSONObject weatherObject = getJArray(forecastJson, OWM_OBJ_WEATHER).getJSONObject(0);
                JSONObject mainObject = getJObject(forecastJson, OWM_OBJ_MAIN);
                JSONObject windObject = getJObject(forecastJson, OWM_OBJ_WIND);
                JSONObject sysObject = getJObject(forecastJson, OWM_OBJ_SYS);
                JSONObject cloudObject = getJObject(forecastJson, OWM_OBJ_CLOUDS);

                report.setVisibility(getLong(forecastJson, OWM_VAR_VISIBILITY));
                // weather
                report.setIconCode(getString(weatherObject, OWM_VAR_ICON));
                report.setWeatherDescription(getString(weatherObject, OWM_VAR_MAIN));
                // main
                report.setTemp(getDouble(mainObject, OWM_VAR_TEMPERATURE));
                report.setTempMax(getDouble(mainObject, OWM_VAR_MAX));
                report.setTempMin(getDouble(mainObject, OWM_VAR_MIN));
                report.setHumidity(getDouble(mainObject, OWM_VAR_HUMIDITY));
                report.setPressure(getDouble(mainObject, OWM_VAR_PRESSURE));
                report.setSeaLevel(getDouble(mainObject, OWM_VAR_SEA_LVL));
                report.setGroundLevel(getDouble(mainObject, OWM_VAR_GRND_LVL));
                // sys
                report.setSunRise(getLong(sysObject, OWM_VAR_SUNRISE));
                report.setSunSet(getLong(sysObject, OWM_VAR_SUNSET));
                // wind
                report.setWind(getDouble(windObject, OWM_VAR_WIND_SPEED));
                report.setWindDegree(getDouble(windObject, OWM_VAR_WIND_DEGREE));
                // clouds
                report.setClouds(getInt(cloudObject, OWM_VAR_CLOUDS));


                return report;
            }
        return null;
    }



    public static ArrayList<Spot.Day> getDaysDataFromJson(JSONObject forecastJson)
            throws JSONException {



            if (getString(forecastJson, OWM_RESULT).equals(String.valueOf(OWM_SUCCESS_CODE))) {
                // list array
                JSONArray weatherArray = getJArray(forecastJson, OWM_ARRAY_LIST);
                ArrayList<Spot.Day> days = new ArrayList<>();

                String lastDate = "";
                int FIRST = -1;
                int AFTER = 1;
                int DONT = 0;
                int add = DONT;
                Spot.Day day = new Spot.Day();

                for (int i = 0; i < weatherArray.length(); i++) {
                    boolean lastIndex = ((i + 1) == weatherArray.length());

                    Spot.Report hour = getHour(weatherArray, i);
                    String date = Clock.getDate(hour.getDateText());

                    if (!lastDate.isEmpty() || !lastDate.equals("")) {
                        if (lastDate.equals(date)) {
                            if (lastIndex) {
                                add = AFTER;
                            }
                        } else {
                            add = FIRST;
                        }
                    }
                    if (add == FIRST) {
                        days.add(day);
                        day = new Spot.Day();
                        add = DONT;
                    }
                    day.addHour(hour);
                    if (add == AFTER) {
                        if (lastIndex) {
                            days.add(day);
                            add = DONT;
                        }
                    }
                    lastDate = date;
                }

                return days;
            }
        return null;
    }

    private static Spot.Report getHour(JSONArray weatherArray, int index) throws JSONException{

        Spot.Report report = new Spot.Report();
        JSONObject dayForecast = weatherArray.getJSONObject(index);
        if(dayForecast!=null) {
            // direct vars
            report.setDateText(getString(dayForecast, OWM_VAR_DATE_TXT));
            // objects
            JSONObject mainObject = getJObject(dayForecast, OWM_OBJ_MAIN);
            JSONObject cloudsObject = getJObject(dayForecast, OWM_OBJ_CLOUDS);
            JSONObject windObject = getJObject(dayForecast, OWM_OBJ_WIND);
            JSONObject weatherObject = getJArray(dayForecast, OWM_OBJ_WEATHER).getJSONObject(0);
            // weather obj
            report.setIconCode(getString(weatherObject, OWM_VAR_ICON));
            report.setWeatherDescription(getString(weatherObject, OWM_VAR_MAIN));
            // main obj
            report.setTemp(getDouble(mainObject, OWM_VAR_TEMPERATURE));
            report.setTempMax(getDouble(mainObject, OWM_VAR_MAX));
            report.setTempMin(getDouble(mainObject, OWM_VAR_MIN));
            report.setHumidity(getDouble(mainObject, OWM_VAR_HUMIDITY));
            report.setPressure(getDouble(mainObject, OWM_VAR_PRESSURE));
            report.setSeaLevel(getDouble(mainObject, OWM_VAR_SEA_LVL));
            report.setGroundLevel(getDouble(mainObject, OWM_VAR_GRND_LVL));
            //GregorianCalendar gc = new GregorianCalendar();
            //gc.add(Calendar.DAY_OF_MONTH, i);
            // wind obj
            report.setWind(getDouble(windObject, OWM_VAR_WIND_SPEED));
            report.setWindDegree(getDouble(windObject, OWM_VAR_WIND_DEGREE));
            // clouds obj
            report.setClouds(getInt(cloudsObject, OWM_VAR_CLOUDS));

            report.setRain(getDouble(getJObject(dayForecast, OWM_OBJ_RAIN), OWM_VAR_3H));

            report.setRain(getDouble(getJObject(dayForecast, OWM_OBJ_SNOW), OWM_VAR_3H));
            // adding in the reports array list

        } else {
        return null;
        }
        return report;
    }


}


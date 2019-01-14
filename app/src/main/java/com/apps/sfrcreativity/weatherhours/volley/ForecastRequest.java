/*
 * Copyright (c) 2015. Tyler McCraw
 */

package com.apps.sfrcreativity.weatherhours.volley;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.apps.sfrcreativity.weatherhours.utils.OWMDataParser;

import org.json.JSONException;
import org.json.JSONObject;



public class ForecastRequest implements Response.Listener<JSONObject>, Response.ErrorListener {

    public interface WeatherListener {
        void onResponse(JSONObject jsonObject);
        void onError(int statusCode);
    }


    private static final String OWM_FORECAST = "forecast";
    private static final String OWM_WEATHER = "weather";
    private RequestQueue queue;
    private JSONObject res;
    private WeatherListener weatherListener;
    private int errorCode;

    public ForecastRequest(Context context, WeatherListener weatherListener) {
        this.queue = Volley.newRequestQueue(context);
        this.weatherListener = weatherListener;
        this.res = new JSONObject();
        errorCode = -1;
    }

    private String buildingUrl(String city, String type) {

        String OWM_BASEURL = "api.openweathermap.org";
        String OWM_VERSION = "2.5";
        String OWM_DATA = "data";
        String OWM_PARAM_QUERY = "q";
        String OWM_PARAM_MODE = "mode";
        String OWM_PARAM_UNITS = "units";
        String OWM_PARAM_APIKEY = "APPID";
        //get weather key from openweathermap.org
        String OWM_APIKEY = "b650a8f681bbc73a5f52ed06ca681a8d";
        String dataProtocol = "http";
        String dataMode = "json";
        String dataUnits = "imperial";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(dataProtocol)
                .authority(OWM_BASEURL)
                .appendPath(OWM_DATA)
                .appendPath(OWM_VERSION)
                .appendPath(type)
                .appendQueryParameter(OWM_PARAM_QUERY, city)
                .appendQueryParameter(OWM_PARAM_MODE, dataMode)
                .appendQueryParameter(OWM_PARAM_UNITS, dataUnits)
                .appendQueryParameter(OWM_PARAM_APIKEY, OWM_APIKEY);

        //String url =  builder.build().toString();
        return builder.build().toString();
    }

    public void makeRequest(String city) {
        queue.add(new JsonObjectRequest(Request.Method.GET, buildingUrl(city, OWM_FORECAST), null, this, this));
        queue.add(new JsonObjectRequest(Request.Method.GET, buildingUrl(city, OWM_WEATHER), null, this, this));
        queue.addRequestFinishedListener((RequestQueue.RequestFinishedListener<JSONObject>) request -> {
            if(res.has("days") && res.has("current")) {
                weatherListener.onResponse(res);
            }
        });
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(errorCode!=error.networkResponse.statusCode) {
            errorCode = error.networkResponse.statusCode;
            weatherListener.onError(errorCode);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        if(response.has(OWMDataParser.OWM_RESULT)) {
            try {
                res.put(response.has(OWMDataParser.OWM_ARRAY_LIST) ? "days" : "current", response);
            } catch (JSONException e) {
                if(errorCode!=-1 && errorCode!=e.hashCode()) {
                    errorCode = e.hashCode();
                    weatherListener.onError(errorCode);
                }
            }
        }
    }

}

/*
 * Copyright (c) 2015. Tyler McCraw
 */

package com.apps.sfrcreativity.weatherhours.utils;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Base64;

import android.view.View;


import com.apps.sfrcreativity.weatherhours.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


/**
 * Created by sfrcreativity on 12/27/2018.
 */
public final class Util {

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static double convertFtoC(double degreesInFahrenheit) {
        return (degreesInFahrenheit - 32) * 5/9;
    }

    public static void printKeyHash(Context ctx) {
        try {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            for(Signature sig: info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(sig.toByteArray());
                String key = Base64.encodeToString(md.digest(),Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public static void rateApp(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static void takeScreenshot(Context ctx) {
        //Date now = new Date();
        //android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        // save bitmap to cache directory
        try {
            File cachePath = new File(ctx.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            View v1 = ((Activity)ctx).getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void  shareScreenshot(Context ctx) {
        File imagePath = new File(ctx.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(ctx, "com.apps.sfrcreativity.weatherhours", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            //shareIntent.setType("image/*");
            //shareIntent.putExtra(Intent.EXTRA_TEXT,"text");
            //shareIntent.putExtra(Intent.EXTRA_TITLE,"#weatherhours");
            //shareIntent.putExtra(Intent.EXTRA_SUBJECT,"#weatherhours");
            shareIntent.setDataAndType(contentUri, ctx.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            ctx.startActivity(Intent.createChooser(shareIntent, "Share with:"));
        }
    }

    public static double convertCelciusToFahrenheit(double degreesInCelcius) {
        return degreesInCelcius * 9/5 + 32;
    }

    public static class Pressure {
        // equals 1 hPa

        // multiply by
        private static final double UNIT_at = 0.0010197162129779;
        private static final double UNIT_atm = 0.000986923266716013;
        private static final double UNIT_Pa = 100;
        private static final double UNIT_kPa = 0.1;
        private static final double UNIT_MPa = 0.0001;
        private static final double UNIT_bar = 0.001;
        private static final double UNIT_mbar = 1;
        private static final double UNIT_mmH2O = 10.197162129779;
        private static final double UNIT_mmHg = 0.75006375541921;
        private static final double UNIT_Torr = UNIT_mmHg;
        private static final double UNIT_kgf_cm2 = 0.001019716212978;
        private static final double UNIT_kgf_m2 = 10.19716212978;
        private static final double UNIT_psi = 0.014503773773022;
        private static final double UNIT_psf = 2.0885;
        private static final double UNIT_inHg = 0.029529983071445;
        private static final double UNIT_Nm_2 = UNIT_Pa;

        public static double get(double pressure, String scale){
            HashMap<String,Double> pressureUnit = new HashMap<>();
            pressureUnit.put("at", UNIT_at);
            pressureUnit.put("atm", UNIT_atm);
            pressureUnit.put("bar", UNIT_bar);
            pressureUnit.put("mbar", UNIT_mbar);
            pressureUnit.put("inHg", UNIT_inHg);
            pressureUnit.put("kgfcm2", UNIT_kgf_cm2);
            pressureUnit.put("kgfm2", UNIT_kgf_m2);
            pressureUnit.put("Pa", UNIT_Pa);
            pressureUnit.put("hPa", UNIT_mbar);
            pressureUnit.put("kPa", UNIT_kPa);
            pressureUnit.put("MPa", UNIT_MPa);
            pressureUnit.put("mmH2O", UNIT_mmH2O);
            pressureUnit.put("mmHg", UNIT_mmHg);
            pressureUnit.put("Nm2", UNIT_Nm_2);
            pressureUnit.put("psi", UNIT_psi);
            pressureUnit.put("psf", UNIT_psf);
            pressureUnit.put("Torr", UNIT_Torr);
            return (pressure * pressureUnit.get(scale));
        }

        public static String getScale(String scale) {
            if(scale.equals("kgfcm2")) {
                return "kgf/cm²";
            } else if(scale.equals("kgfm2")) {
                return "kgf/m²";
            } else if(scale.equals("mmH2O")) {
                return "mmH²O";
            } else if(scale.equals("Nm2")) {
                return "N/m⁻²";
            } else {
                return scale;
            }
        }
    }

    public static class Speed {
        // equals 1 mile per hour

        // multiply by
        private static final double UNIT_knot = 0.86897624;
        private static final double UNIT_mps = 0.44704;
        private static final double UNIT_kph = 1.609344;
        private static final double UNIT_cps = 44.704;
        private static final double UNIT_fps = 1.4666667;

        public static double get(double speed, String scale) {
            HashMap<String,Double> speedUnit = new HashMap<>();
            speedUnit.put("cps", UNIT_cps);
            speedUnit.put("fps", UNIT_fps);
            speedUnit.put("mps", UNIT_mps);
            speedUnit.put("mph", 1.0);
            speedUnit.put("knots", UNIT_knot);
            speedUnit.put("kph", UNIT_kph);
            // b stands for beaufort
            if(scale.equals("b")) {
                return getBeaufort(speed);
            } else {
                return (speed * speedUnit.get(scale));
            }
        }

        public static int getBeaufort(double speed) {
            if(speed>1) {
                return 1;
            } else if(speed>5) {
                return 2;
            } else if(speed>12) {
                return 3;
            } else if(speed>20) {
                return 4;
            } else if(speed>30) {
                return 5;
            } else if(speed>40) {
                return 6;
            } else if(speed>50) {
                return 7;
            } else if(speed>61) {
                return 8;
            } else if(speed>74) {
                return 9;
            } else if(speed>89) {
                return 10;
            } else if(speed>103) {
                return 11;
            } else if(speed>119) {
                return 12;
            } else {
                return 0;
            }
        }

        public static String getBeaufortStatus(Context ctx, double speed) {
            switch (Math.round(getBeaufort(speed))) {
                case 0:
                    return ctx.getResources().getString(R.string.airCalm);
                case 1:
                    return ctx.getResources().getString(R.string.airLightAir);
                case 2:
                    return ctx.getResources().getString(R.string.airLightBreeze);
                case 3:
                    return ctx.getResources().getString(R.string.airGentleBreeze);
                case 4:
                    return ctx.getResources().getString(R.string.airModerateBreeze);
                case 5:
                    return ctx.getResources().getString(R.string.airFreshBreeze);
                case 6:
                    return ctx.getResources().getString(R.string.airStrongBreeze);
                case 7:
                    return ctx.getResources().getString(R.string.airNearGale);
                case 8:
                    return ctx.getResources().getString(R.string.airGale);
                case 9:
                    return ctx.getResources().getString(R.string.airStrongBreeze);
                case 10:
                    return ctx.getResources().getString(R.string.airStorm);
                case 11:
                    return ctx.getResources().getString(R.string.airViolentStorm);
                case 12:
                    return ctx.getResources().getString(R.string.airHurricane);
                    default:
                        return "";
            }
        }







    }


    public static String getTranslatedDescription(Context ctx, String desc) {
        switch(desc) {
            case "Thunderstorm":
                return ctx.getResources().getString(R.string.desThunderstorm);
            case "Drizzle":
                return ctx.getResources().getString(R.string.desDrizzle);
            case "Rain":
                return ctx.getResources().getString(R.string.desRain);
            case "Snow":
                return ctx.getResources().getString(R.string.desSnow);
            case "Atmosphere":
                return ctx.getResources().getString(R.string.desAtmosphere);
            case "Clear":
                return ctx.getResources().getString(R.string.desClear);
            case "Clouds":
                return ctx.getResources().getString(R.string.desClouds);
            default:
                return ctx.getResources().getString(R.string.desClear);
        }

    }


    public static int getItemWeatherIcon(String OWMIconCode, String dayTime) {
        switch (OWMIconCode) {
            case "01d":
            case "01n":
                if(dayTime.equals("DAY")) {
                    return R.drawable.bordered_sunny;
                } else {
                    return R.drawable.bordered_night_clear;
                }
            case "02d":
            case "02n":
                if(dayTime.equals("DAY")) {
                    return R.drawable.bordered_day_cloudy;
                } else {
                    return R.drawable.bordered_night_cloudy;
                }
            case "03d":
            case "03n":
                return R.drawable.bordered_cloudy2;
            case "04d":
            case "04n":
                return R.drawable.bordered_cloudy3;
            case "09d":
            case "09n":
                return R.drawable.bordered_rainy;
            case "10d":
            case "10n":
                return R.drawable.bordered_rainy2;
            case "11d":
            case "11n":
                return R.drawable.bordered_stormy;
            case "13d":
            case "13n":
                return R.drawable.bordered_snowy;
            case "50d":
            case "50n":
                return R.drawable.bordered_mist;
            default:
                return R.drawable.bordered_sunny;
        }
    }

    public static int getFeaturedWeatherIcon(String OWMIconCode, String dayTime) {
        switch (OWMIconCode) {
            case "01d":
            case "01n":
                if(dayTime.equals("DAY")) {
                    return R.drawable.sunny;
                } else {
                    return R.drawable.night_clear;
                }
            case "02d":
            case "02n":
                if(dayTime.equals("DAY")) {
                    return R.drawable.day_cloudy;
                } else {
                    return R.drawable.night_cloudy;
                }
            case "03d":
            case "03n":
                return R.drawable.cloudy2;
            case "04d":
            case "04n":
                return R.drawable.cloudy3;
            case "09d":
            case "09n":
                return R.drawable.rainy;
            case "10d":
            case "10n":
                return R.drawable.rainy2;
            case "11d":
            case "11n":
                return R.drawable.stormy;
            case "13d":
            case "13n":
                return R.drawable.snowy;
            case "50d":
            case "50n":
                return R.drawable.mist;
            default:
                return R.drawable.sunny;
        }
    }

}

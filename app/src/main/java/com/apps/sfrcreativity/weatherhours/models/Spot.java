package com.apps.sfrcreativity.weatherhours.models;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Spot  {

    private GeoReport geoReport;
    private Report now;
    private ArrayList<Day> days;

    public Spot() {
        geoReport = new GeoReport();
        days = new ArrayList<>();
        now = new Report();
    }


    public GeoReport getGeoReport() {
        return geoReport;
    }

    public void setGeoReport(GeoReport geoReport) {
        this.geoReport = geoReport;
    }

    public Report getNow() {
        return now;
    }

    public void setNow(Report now) {
        this.now = now;
    }

    public ArrayList<Day> getDays() {
        return days;
    }


    public void setDays(ArrayList<Day> days) {
        this.days = days;
    }


    public Day getDay(int pos)  {
        return days.get(pos);
    }


    public static class GeoReport  {

        private String country;
        private String city;
        private long population;
        private double lat;
        private double lon;

        public GeoReport() {
            this.country = "";
            this.city = "";
        }


        public void setGeoData(String city, String country, long population, double lat, double lon) {
            this.city = city;
            this.country = country;
            this.population = population;
            this.lat = lat;
            this.lon = lon;
        }



        public String describe() {
            StringBuilder result = new StringBuilder();
            String newLine = System.getProperty("line.separator");

            result.append( this.getClass().getName() );
            result.append( " Object {" );
            result.append(newLine);

            //determine fields declared in this class only (no fields of superclass)
            Field[] fields = this.getClass().getDeclaredFields();

            //print field names paired with their values
            for ( Field field : fields  ) {
                result.append("  ");
                try {
                    result.append( field.getName() );
                    result.append(": ");
                    //requires access to private field:
                    result.append( field.get(this) );
                } catch ( IllegalAccessException ex ) {
                    ex.printStackTrace();
                }
                result.append(newLine);
            }
            result.append("}");

            return result.toString();
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public long getPopulation() {
            return population;
        }

        public void setPopulation(long population) {
            this.population = population;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

    }

    public static class Report {

        private String iconCode;
        private String weatherDescription;

        private int clouds;

        private String dateText;
        private long sunRise;
        private long sunSet;
        private long visibility;

        private double rain;
        private double snow;
        private double temp;
        private double tempMax;
        private double tempMin;
        private double humidity;
        private double pressure;
        private double wind;
        private double windDegree;
        private double seaLevel;
        private double groundLevel;

        public Report() {
            iconCode = "";
            weatherDescription = "";
            dateText = "";
        }


        public int getSnow() {
            return (int) Math.round(snow);
        }



        public void setVisibility(long visibility) {
            this.visibility = visibility;
        }

        public void setSnow(double snow) {
            this.snow = snow;
        }

        public int getRain() {
            return (int) Math.round(rain);
        }

        public void setRain(double rain) {
            this.rain = rain;
        }

        public int getClouds() {
            return clouds;
        }

        public void setClouds(int clouds) {
            this.clouds = clouds;
        }

        public long getSunRise() {
            return sunRise;
        }

        public void setSunRise(long sunRise) {
            this.sunRise = sunRise;
        }

        public long getSunSet() {
            return sunSet;
        }

        public void setSunSet(long sunSet) {
            this.sunSet = sunSet;
        }

        public String getDateText() {
            return dateText;
        }

        public void setDateText(String dateText) {
            this.dateText = dateText;
        }


        public String describe() {
            StringBuilder result = new StringBuilder();
            String newLine = System.getProperty("line.separator");

            result.append( this.getClass().getName() );
            result.append( " Object {" );
            result.append(newLine);

            //determine fields declared in this class only (no fields of superclass)
            Field[] fields = this.getClass().getDeclaredFields();

            //print field names paired with their values
            for ( Field field : fields  ) {
                result.append("  ");
                try {
                    result.append( field.getName() );
                    result.append(": ");
                    //requires access to private field:
                    result.append( field.get(this) );
                } catch ( IllegalAccessException ex ) {
                    ex.printStackTrace();
                }
                result.append(newLine);
            }
            result.append("}");

            return result.toString();
        }

        public String getWeatherDescription() {
            return weatherDescription;
        }

        public void setWeatherDescription(String weatherDescription) { this.weatherDescription = weatherDescription; }

        public String getIconCode() {
            return iconCode;
        }

        public void setIconCode(String iconCode) {
            this.iconCode = iconCode;
        }

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

        public double getTempMax() {
            return tempMax;
        }

        public void setTempMax(double tempMax) {
            this.tempMax = tempMax;
        }

        public double getTempMin() {
            return tempMin;
        }

        public void setTempMin(double tempMin) {
            this.tempMin = tempMin;
        }

        public double getHumidity() {
            return humidity;
        }

        public void setHumidity(double humidity) {
            this.humidity = humidity;
        }

        public double getPressure() {
            return pressure;
        }

        public void setPressure(double pressure) {
            this.pressure = pressure;
        }

        public double getWind() {
            return wind;
        }

        public void setWind(double wind) {
            this.wind = wind;
        }

        public double getWindDegree() {
            return windDegree;
        }

        public void setWindDegree(double windDegree) {
            this.windDegree = windDegree;
        }

        public double getSeaLevel() {
            return seaLevel;
        }

        public void setSeaLevel(double seaLevel) {
            this.seaLevel = seaLevel;
        }

        public double getGroundLevel() {
            return groundLevel;
        }

        public void setGroundLevel(double groundLevel) {
            this.groundLevel = groundLevel;
        }
    }
/*
    dawn        6
    morning     9
    midday      12
    afternoon   3
    dusk        6
    evening     9
    midnight    12
    night       3
*/
    public static class Day {

        private ArrayList<Report> hours;

        public Day() {
            this.hours = new ArrayList<>();
        }

        public ArrayList<Report> getHours() {
            return hours;
        }

        public void addHour(Report hour) {
            this.hours.add(hour);
        }

        public Report getHour(int pos) {
            return this.hours.get(pos);
        }
}

}

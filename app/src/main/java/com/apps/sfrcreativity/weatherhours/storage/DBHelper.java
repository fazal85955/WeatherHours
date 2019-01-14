package com.apps.sfrcreativity.weatherhours.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.apps.sfrcreativity.weatherhours.activities.BossActivity;
import com.apps.sfrcreativity.weatherhours.models.Spot;
import com.apps.sfrcreativity.weatherhours.utils.Clock;

import java.util.ArrayList;
import java.util.List;

import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_OBJ_CLOUDS;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_OBJ_GEO_HRS;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_OBJ_RAIN;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_OBJ_SNOW;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_OBJ_WEATHER;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_CITY;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_COUNTRY;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_DATE_TXT;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_GRND_LVL;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_HUMIDITY;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_ICON;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_LATTITUDE;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_LONGTITUDE;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_MAIN;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_MAX;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_MIN;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_POPULATION;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_PRESSURE;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_SEA_LVL;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_SUNRISE;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_SUNSET;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_TEMPERATURE;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_WIND_DEGREE;
import static com.apps.sfrcreativity.weatherhours.utils.OWMDataParser.OWM_VAR_WIND_SPEED;


public class DBHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = "WOLVERINE";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "spotsManager";

    private static final String KEY_DAY = "day";
    private static final String KEY_HOUR = "hour";

    private static final String[] GEO_COL =
            new String[] {
                    OWM_VAR_CITY,           //string
                    OWM_VAR_COUNTRY,        //string
                    OWM_VAR_POPULATION,     //long
                    OWM_VAR_LATTITUDE,      //double
                    OWM_VAR_LONGTITUDE      //double
    };

    private static final String[] WEATHER_COL =
            new String[] {
                    OWM_VAR_ICON,//0            //string
                    OWM_VAR_MAIN, //1           //string
                    OWM_OBJ_CLOUDS,  //2        //int
                    OWM_VAR_DATE_TXT,//3        //string
                    OWM_VAR_SUNRISE, //4        //long
                    OWM_VAR_SUNSET,//5          //long
                    OWM_OBJ_RAIN, //6           //double
                    OWM_OBJ_SNOW,//7            //double
                    OWM_VAR_TEMPERATURE, //8    //int
                    OWM_VAR_MIN, //9            //int
                    OWM_VAR_MAX,//10            //int
                    OWM_VAR_HUMIDITY,//11       //int
                    OWM_VAR_PRESSURE,//12       //int
                    OWM_VAR_WIND_SPEED, //13    //int
                    OWM_VAR_WIND_DEGREE,//14    //int
                    OWM_VAR_SEA_LVL,//15        //int
                    OWM_VAR_GRND_LVL//16        //int
                    //OWM_VAR_CITY,//17
    };

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance  
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        createGeoTable(db);
        //createCity(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + OWM_OBJ_GEO_HRS);
        //db.execSQL("DROP TABLE IF EXISTS " + OWM_OBJ_WEATHER);
        onCreate(db);
    }
    // table creating operations
    public void createGeoTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + OWM_OBJ_GEO_HRS
                + "("
                //+ KEY_ID + " INTEGER PRIMARY KEY,"
                + GEO_COL[0] + " TEXT,"
                + GEO_COL[1] + " TEXT,"
                + GEO_COL[2] + " INTEGER,"
                + GEO_COL[3] + " REAL,"
                + GEO_COL[4] + " REAL"
                //+ KEY_DAYS + " INTEGER"
                + ")");
    }
    public void createCityTable(SQLiteDatabase db, String city) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + validTableName(city) + "("
                + WEATHER_COL[0] + " TEXT,"
                + WEATHER_COL[1] + " TEXT,"
                + WEATHER_COL[2] + " INTEGER,"
                + WEATHER_COL[3] + " TEXT,"
                + WEATHER_COL[4] + " INTEGER,"
                + WEATHER_COL[5] + " INTEGER,"
                + WEATHER_COL[6] + " REAL,"
                + WEATHER_COL[7] + " REAL,"
                + WEATHER_COL[8] + " REAL,"
                + WEATHER_COL[9] + " REAL,"
                + WEATHER_COL[10] + " REAL,"
                + WEATHER_COL[11] + " REAL,"
                + WEATHER_COL[12] + " REAL,"
                + WEATHER_COL[13] + " REAL,"
                + WEATHER_COL[14] + " REAL,"
                + WEATHER_COL[15] + " REAL,"
                + WEATHER_COL[16] + " REAL"
                + ")");
    }
    // command to write into database
    // SQLiteDatabase database = this.getWritableDatabase();
    // data inserting operations
    private void insertGeoData(SQLiteDatabase db, Spot.GeoReport geoReport) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(GEO_COL[0],geoReport.getCity());
        contentValues.put(GEO_COL[1],geoReport.getCountry());
        contentValues.put(GEO_COL[2],geoReport.getPopulation());
        contentValues.put(GEO_COL[3],geoReport.getLat());
        contentValues.put(GEO_COL[4],geoReport.getLon());
        long result = db.insert(OWM_OBJ_GEO_HRS,null , contentValues);
        if(result != -1) {
            createCityTable(db,geoReport.getCity());
        }
    }
    private ContentValues insertHourData(Spot.Report hour) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEATHER_COL[0],hour.getIconCode());
        contentValues.put(WEATHER_COL[1],hour.getWeatherDescription());
        contentValues.put(WEATHER_COL[2],hour.getClouds());
        contentValues.put(WEATHER_COL[3],hour.getDateText());
        contentValues.put(WEATHER_COL[4],hour.getSunRise());
        contentValues.put(WEATHER_COL[5],hour.getSunSet());
        contentValues.put(WEATHER_COL[6],hour.getRain());
        contentValues.put(WEATHER_COL[7],hour.getSnow());
        contentValues.put(WEATHER_COL[8],hour.getTemp());
        contentValues.put(WEATHER_COL[9],hour.getTempMin());
        contentValues.put(WEATHER_COL[10],hour.getTempMax());
        contentValues.put(WEATHER_COL[11],hour.getHumidity());
        contentValues.put(WEATHER_COL[12],hour.getPressure());
        contentValues.put(WEATHER_COL[13],hour.getWind());
        contentValues.put(WEATHER_COL[14],hour.getWindDegree());
        contentValues.put(WEATHER_COL[15],hour.getSeaLevel());
        contentValues.put(WEATHER_COL[16],hour.getGroundLevel());
        return contentValues;
    }
    private void insertSpot(SQLiteDatabase db, Spot spot) {
        createCityTable(db,validTableName(spot.getGeoReport().getCity()));
        ContentValues cv = insertHourData(spot.getNow());
        long result = db.insert(validTableName(spot.getGeoReport().getCity()), null, cv);

        int day = 0;
        for (Spot.Day d : spot.getDays()) {
            int hour = 0;
            for (Spot.Report h : d.getHours()) {
                cv = insertHourData(h);
                result = db.insert(validTableName(spot.getGeoReport().getCity()), null, cv);

                hour++;
            }
            day++;
        }
    }
    // data updating operations
    private void updateSpot(SQLiteDatabase db, Spot spot) {
        resetCityTable(db,spot.getGeoReport().getCity());
        insertSpot(db,spot);
    }
    private int updateGeo(SQLiteDatabase db,Spot.GeoReport geoReport) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(GEO_COL[0], geoReport.getCity());
        contentValues.put(GEO_COL[1], geoReport.getCountry());
        contentValues.put(GEO_COL[2], geoReport.getPopulation());
        contentValues.put(GEO_COL[3], geoReport.getLat());
        contentValues.put(GEO_COL[4], geoReport.getLon());
        int count = db.update(OWM_OBJ_GEO_HRS, contentValues, GEO_COL[0] + " = ?", new String[]{geoReport.getCity()});

        return count;
    }
    // data insert or update operations
    private boolean insertedOrUpdateGeo(Spot.GeoReport geoReport) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean hasRecord = hasCity(db,geoReport.getCity());
        db.close();
        db = this.getWritableDatabase();
        createGeoTable(db);
        if(hasRecord) {
            updateGeo(db,geoReport);
            db.close();
            return true;
        }
        insertGeoData(db,geoReport);
        db.close();
        return true;
    }

    private static String validTableName(String city) {
        return city.replaceAll("\\s+","").replaceAll("\\.+","").replaceAll("\\-+","");
    }

    private boolean insertedOrUpdateSpotHours(Spot spot) {
        SQLiteDatabase mDB = this.getReadableDatabase();
        boolean has = hasTable(mDB, validTableName(spot.getGeoReport().getCity()));
        mDB.close();
        mDB = this.getWritableDatabase();
        if(has) {
            updateSpot(mDB,spot);
            mDB.close();
            return false;
        }
        insertSpot(mDB,spot);
        mDB.close();
        return true;
    }
    // data deleting operations
    private void deleteGeoData(SQLiteDatabase db, String city) {
        int deletedRows = db.delete(OWM_OBJ_GEO_HRS,  GEO_COL[0]+" = ?", new String[]{city});
        if (deletedRows > 0) {
            deleteCityTable(db,validTableName(city));
        }
    }
    // table deleting operations
    private void deleteCityTable(SQLiteDatabase db, String city) {
        db.execSQL("DROP TABLE IF EXISTS " + validTableName(city));
    }
    private void resetCityTable(SQLiteDatabase db, String city) {
        db.delete(validTableName(city),null,null);
    }
    private void deleteAllTables(SQLiteDatabase db) {
        // query to obtain the names of all tables in your database
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tables = new ArrayList<>();
        while (c.moveToNext()) {
            tables.add(c.getString(0));
        }
        for (String table : tables) {
            String dropQuery = "DROP TABLE IF EXISTS " + table;
            db.execSQL(dropQuery);
        }
        c.close();
    }
    // command to read from database
    // SQLiteDatabase database = this.getReadableDatabase();
    // data retrieving operations
    private ArrayList<Spot.GeoReport> getAllGeoData(SQLiteDatabase db) {
        Cursor res = db.rawQuery("select * from " + OWM_OBJ_GEO_HRS,null);
        ArrayList<Spot.GeoReport> gr = new ArrayList<>();
        if(res.getCount()>0) {
            while (res.moveToNext()) {
                Spot.GeoReport geoReport = new Spot.GeoReport();
                geoReport.setGeoData(
                        res.getString(0),
                        res.getString(1),
                        res.getLong(2),
                        res.getDouble(3),
                        res.getDouble(4)
                );
                gr.add(geoReport);
            }
        } else {
            res.close();
            return null;
        }
        res.close();
        return gr;
    }

    private Spot.Report getHourData(Cursor res) {
        Spot.Report hour = new Spot.Report();
        hour.setIconCode(res.getString(0));
        hour.setWeatherDescription(res.getString(1));
        hour.setClouds(res.getInt(2));
        hour.setDateText(res.getString(3));
        hour.setSunRise(res.getInt(4));
        hour.setSunSet(res.getInt(5));
        hour.setRain(res.getDouble(6));
        hour.setSnow(res.getDouble(7));
        hour.setTemp(res.getDouble(8));
        hour.setTempMin(res.getDouble(9));
        hour.setTempMax(res.getDouble(10));
        hour.setHumidity(res.getDouble(11));
        hour.setPressure(res.getDouble(12));
        hour.setWind(res.getDouble(13));
        hour.setWindDegree(res.getDouble(14));
        hour.setSeaLevel(res.getDouble(15));
        hour.setGroundLevel(res.getDouble(16));
        return hour;
    }

    private Spot getSpotWeather(SQLiteDatabase db, String city) {
        Spot spot = new Spot();
        Cursor res = db.rawQuery("select * from " + validTableName(city), null);
        ArrayList<Spot.Day> days = new ArrayList<>();
        String lastDate = "";
        int FIRST = -1;
        int AFTER = 1;
        int DONT = 0;
        int add = DONT;
        Spot.Day day = new Spot.Day();
        int i=0;
        if (res.getCount() > 0) {
            while (res.moveToNext()) {
                boolean lastIndex = ((i + 1) == res.getCount());
                Spot.Report hour = getHourData(res);

                if(hour.getDateText().length()>0) {
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
                } else {
                    spot.setNow(hour);
                }
                i++;
            }
        } else {
            res.close();
            return null;
        }
        spot.setDays(days);



        res.close();
        return spot;
    }
    private Spot.GeoReport getGeoData(SQLiteDatabase db, String city) {
        Cursor cursor = db.query(OWM_OBJ_GEO_HRS,
                    GEO_COL, GEO_COL[0] + "=?",
                    new String[]{city}, null, null, null, null);
        if(cursor.getCount()==0) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        Spot.GeoReport geoReport = new Spot.GeoReport();
        geoReport.setCity(cursor.getString(0));
        geoReport.setCountry(cursor.getString(1));
        geoReport.setPopulation(cursor.getLong(2));
        geoReport.setLat(cursor.getDouble(3));
        geoReport.setLon(cursor.getDouble(4));
        cursor.close();
        return geoReport;
    }
    private boolean hasCity(SQLiteDatabase db, String city) {
        Cursor cursor = db.query(OWM_OBJ_GEO_HRS,
                GEO_COL, GEO_COL[0] + "=?",
                new String[]{city}, null, null, null, null);

        if(cursor.getCount()>0) {
            cursor.close();
            return true;
        }

        cursor.close();
        return false;
    }
    // retrieving all tables
    private void getAllTables(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        //while (c.moveToNext()) {
//            Log.d(LOG_TAG,"table: "+c.getString(0));
  //      }
        c.close();
    }
    private boolean hasTable(SQLiteDatabase db, String table){
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='"+table+"'";
        Cursor mCursor = db.rawQuery(query, null);
        if (mCursor.getCount() > 0) {
            mCursor.close();
            return true;
        }
        mCursor.close();
        return false;
    }

    // squence behaviours accessed globally
    public boolean insertedOrUpdateSpot(Spot spot) {
        boolean insertedGeo = insertedOrUpdateGeo(spot.getGeoReport());
        boolean insertedSpotHours = insertedOrUpdateSpotHours(spot);
        if(insertedGeo && insertedSpotHours) {
            return true;
        }
        return false;
    }
    public boolean deleteSpot(String city) {
        if(isExists(city)) {
            SQLiteDatabase db = this.getWritableDatabase();
            deleteGeoData(db, city);
            db.close();
            return true;
        } else {
            return false;
        }
    }
    public ArrayList<String> getAllGeoCities() {
        SQLiteDatabase db = this.getWritableDatabase();
        createGeoTable(db);
        db.close();
        db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + OWM_OBJ_GEO_HRS,null);
        if(res.getCount()>0) {
            ArrayList<String> cityList = new ArrayList();
            while (res.moveToNext()) {
                String cityName = res.getString(0);
                cityList.add(cityName);
            }
            res.close();
            db.close();
            return cityList;
        } else {
            res.close();
            db.close();
            return null;
        }
    }
    public boolean isExists(String city) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean hasCity = hasCity(db,city);
        boolean hasTable = hasTable(db,validTableName(city));
        db.close();
        if(hasCity && hasTable) {
            return true;
        }
        return false;
    }
    public Spot getSpot(String city) {
        if(isExists(city)) {
            SQLiteDatabase db = this.getReadableDatabase();
            Spot spot = getSpotWeather(db,city);
            spot.setGeoReport(getGeoData(db,city));
            db.close();
            return spot;
        }
        return null;
    }
    public ArrayList<Spot> getAllSpots() {
        ArrayList<String> cities = getAllGeoCities();
        if(cities!=null) {
            ArrayList<Spot> spot = new ArrayList<>();
            for (String city: cities) {
            Spot s = getSpot(city);
                if(s!=null) {
                spot.add(s);
                }
            }
            return spot;
        }
        return null;
    }

}  
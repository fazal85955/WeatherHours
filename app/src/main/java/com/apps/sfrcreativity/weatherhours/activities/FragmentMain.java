package com.apps.sfrcreativity.weatherhours.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.sfrcreativity.weatherhours.R;
import com.apps.sfrcreativity.weatherhours.adapter.ReportRecyclerViewAdapter;
import com.apps.sfrcreativity.weatherhours.models.Spot;
import com.apps.sfrcreativity.weatherhours.storage.DBHelper;
import com.apps.sfrcreativity.weatherhours.utils.Clock;
import com.apps.sfrcreativity.weatherhours.utils.OWMDataParser;
import com.apps.sfrcreativity.weatherhours.utils.Sync;
import com.apps.sfrcreativity.weatherhours.utils.Util;
import com.apps.sfrcreativity.weatherhours.volley.ForecastRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMain extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int MAX_SETUP_DURATION = 1000;
    public static final String ARG_CITY = "city";
    public static final String ARG_RESPONSE = "response";
    public static final String ARG_ACTION = "action";
    public static final String PARAM_SQLITE = "sqlite";
    public static final String PARAM_JSON = "json";
    private static final int NONE = -1;
    private static final String KEY_PREF_TIME = "hh";
    private static final String KEY_PREF_INTERVAL = "15m";
    private static final String KEY_PREF_DATE = "EEEE, MMMM dd, yyyy";
    private static final String KEY_PREF_WIND = "kph";
    private static final String KEY_PREF_PRESSURE = "hPa";
    private static final String KEY_PREF_TEMP = "c";
    private long lastTimeReportUpdate;
    private ReportRecyclerViewAdapter viewAdapterDay,viewAdapterHour;
    private SharedPreferences prefs;
    private Spot currentCity;
    private Sync time, updateRate;
    private Spot.Report selectedHour;
    private Context ctx;
    private String cityName;
    private KProgressHUD progressHUD;

    private WeatherFragmentListener weatherFragmentListener;

    public interface WeatherFragmentListener {
        void onToolbarTitleUpdate(String title, String subtitle);
        void onDayTimeChange(String dayTime, Button nowButton);
        void onOptionsMenuItemClick(int id);
    }

    @BindView(R.id.txtTime) TextView txtTime;
    @BindView(R.id.txtClock) TextView txtClock;
    @BindView(R.id.txtDate) TextView txtDate;
    @BindView(R.id.txtTmp) TextView txtTmp;
    @BindView(R.id.txtTmpType) TextView txtTmpType;
    @BindView(R.id.txtTmpMin) TextView txtTmpMin;
    @BindView(R.id.txtTmpMax) TextView txtTmpMax;
    @BindView(R.id.txtDesc) TextView txtDesc;
    @BindView(R.id.txtPressure) TextView txtPressure;
    @BindView(R.id.txtHumidity) TextView txtHumidity;
    @BindView(R.id.txtWind) TextView txtWindSpeed;
    @BindView(R.id.txtWindStatus) TextView txtWindStatus;
    @BindView(R.id.txtWindScale) TextView txtWScale;
    @BindView(R.id.txtDirectionShort) TextView txtWindDirShort;
    @BindView(R.id.txtDirectionLong) TextView txtWindDirLong;
    @BindView(R.id.txtDateSunrise) TextView txtSunriseDate;
    @BindView(R.id.txtTimeSunrise) TextView txtSunriseTime;
    @BindView(R.id.txtDateSunset) TextView txtSunsetDate;
    @BindView(R.id.txtTimeSunset) TextView txtSunsetTime;
    @BindView(R.id.txtSeaLvl) TextView txtSeaLvl;
    @BindView(R.id.txtGrndLvl) TextView txtGrndLvl;
    @BindView(R.id.imgTmp) ImageView imgTmp;
    @BindView(R.id.imgDirection) ImageView imgDirection;
    @BindView(R.id.hour_now) Button btnNow;
    @BindView(R.id.lst_day) RecyclerView dayRecyclerView;
    @BindView(R.id.lst_time) RecyclerView timeRecyclerView;
    @BindView(R.id.fragment_main) SwipeRefreshLayout swipeRefreshLayout;

    public FragmentMain() {
        currentCity = new Spot();
        time = new Sync();
        updateRate = new Sync();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_main, container, false);
        this.ctx = view.getContext();

        progressHUD = new KProgressHUD(ctx);
        progressHUD
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel(ctx.getResources().getString(R.string.proLabelPW))
                .setDetailsLabel(ctx.getResources().getString(R.string.proDetailPW))
                .setMaxProgress(100)
                .setAutoDismiss(true)
                .setBackgroundColor(ctx.getResources().getColor(R.color.cardViews))
                .setDimAmount(.7f);
        ButterKnife.bind(this,view);
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        prefs.registerOnSharedPreferenceChangeListener(this);
        cityName = getArguments().getString(ARG_CITY,ctx.getResources().getString(R.string.app_name));
        String action = getArguments().getString(ARG_ACTION, PARAM_JSON);
        lastTimeReportUpdate = getArguments().getLong("update",0);
        if(action.equals(PARAM_SQLITE)) {
            progressHUD.show();
            DBHelper dbHelper = new DBHelper(ctx);
            currentCity = dbHelper.getSpot(cityName);
            new Handler().postDelayed(() -> {
                selectedHour = currentCity.getNow();
                updateUI();
            }, 1000);
        } else {
            String response = getArguments().getString(ARG_RESPONSE, null);
            try {
                JSONObject object = new JSONObject(response);
                updateWeather(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        time.setMilliSeconds(1000);
        time.setOnUpdateListener(this::currentTimeDate);
        time.turnOnSync();

        InterstitialAd mInterstitialAd = new InterstitialAd(ctx);
        mInterstitialAd.setAdUnitId(ctx.getResources().getString(R.string.adINTERSTITIAL));
        updateRate.setMilliSeconds(getUpdateRate(prefs));
        updateRate.setOnUpdateListener(() -> {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            callForData(cityName);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> callForData(cityName));

        updateRate.turnOnSync();
        weatherFragmentListener.onDayTimeChange(Clock.getDayTime(null), btnNow);
        btnNow.setOnClickListener(v -> {
            viewAdapterDay.setSelected(0);
            viewAdapterDay.notifyDataSetChanged();
            viewAdapterHour.setSelected(NONE);
            viewAdapterHour.notifyDataSetChanged();
            time.turnOnSync();
            selectedHour=currentCity.getNow();
            setMainCardUI(selectedHour);
            weatherFragmentListener.onDayTimeChange(Clock.getDayTime(null), btnNow);
        });
        txtTmp.setOnClickListener(v -> {
            Util.takeScreenshot(ctx);
            Util.shareScreenshot(ctx);
        });
        currentTimeDate();
        return view;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case KEY_PREF_TEMP:
                initializeDayRecyclerView(viewAdapterDay.getLastSelected(), sharedPreferences);
                initializeHourRecyclerView(viewAdapterDay.getLastSelected(), sharedPreferences);
                changeTemperature(selectedHour, sharedPreferences);
                break;
            case KEY_PREF_WIND:
                changeWind(sharedPreferences);
                break;
            case KEY_PREF_PRESSURE:
                changePressure(selectedHour, sharedPreferences);
                break;
            case KEY_PREF_INTERVAL:
                updateRate.setMilliSeconds(getUpdateRate(sharedPreferences));
                break;
            case KEY_PREF_TIME:
                initializeDayRecyclerView(viewAdapterDay.getLastSelected(), sharedPreferences);
                initializeHourRecyclerView(viewAdapterDay.getLastSelected(), sharedPreferences);
                changeTime(selectedHour, sharedPreferences);
                break;
            case KEY_PREF_DATE:
                changeDate(selectedHour, sharedPreferences);
        }
    }

    private void updateWeather(JSONObject response) {
        try {
            progressHUD.show();
            JSONObject current = response.getJSONObject("current");
            JSONObject days = response.getJSONObject("days");
            currentCity.setGeoReport(OWMDataParser.getGeoDataFromJson(days));
            currentCity.setDays(OWMDataParser.getDaysDataFromJson(days));
            currentCity.setNow(OWMDataParser.getSingleDataFromJson(current));
            selectedHour = currentCity.getNow();
            DBHelper dbHelper = new DBHelper(ctx);
            dbHelper.insertedOrUpdateSpot(currentCity);
            updateUI();
        } catch (JSONException e) {
            e.printStackTrace();
            progressHUD.dismiss();
        }
    }

    private void updateUI() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            setMainCardUI(selectedHour);
            initializeDayRecyclerView(0, prefs);
            initializeHourRecyclerView(0, prefs);
            Locale country = new Locale("", currentCity.getGeoReport().getCountry());
            weatherFragmentListener.onToolbarTitleUpdate(currentCity.getGeoReport().getCity(), country.getDisplayCountry());
            progressUpdate();
        }, MAX_SETUP_DURATION);
    }

    private void progressUpdate() {
        int MAX_PROGRESS = 100;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            int currentProgress;
            @Override
            public void run() {
                currentProgress += 5;
                progressHUD.setProgress(currentProgress);
                if (currentProgress == 80) {
                    progressHUD.setLabel(ctx.getResources().getString(R.string.proLabelAF));
                }

                if (currentProgress < MAX_PROGRESS) {
                    handler.postDelayed(this, 1);
                }
            }
        }, MAX_PROGRESS);
    }

    private void changeTemperature(Spot.Report r, SharedPreferences prefs) {
        String scale =  prefs.getString(KEY_PREF_TEMP, KEY_PREF_TEMP);
        boolean celsius = scale.equals(KEY_PREF_TEMP);
        txtTmp.setText(accurate(celsius ? Util.convertFtoC(r.getTemp()) :
                        r.getTemp()));
        String degreeSign = ctx.getResources().getString(R.string.signDegree);
        txtTmpType.setText(degreeSign.concat(scale.toUpperCase()));
        txtTmpMin.setText(accurate(celsius ? Util.convertFtoC(r.getTempMin()) : r.getTempMin()));
        txtTmpMax.setText(accurate(celsius ? Util.convertFtoC(r.getTempMax()) : r.getTempMax()));
    }

    private void initializeHourRecyclerView(int index, SharedPreferences prefs) {
        LinearLayoutManager recyclerLayoutManager = new
                LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
        timeRecyclerView.setLayoutManager(recyclerLayoutManager);
        List<ReportRecyclerViewAdapter.RVModel> modelList = new ArrayList<ReportRecyclerViewAdapter.RVModel>();
        boolean celsius = prefs.getString(KEY_PREF_TEMP, KEY_PREF_TEMP).equals(KEY_PREF_TEMP);
        for (Spot.Report r:currentCity.getDay(index).getHours()) {
            String date = r.getDateText();
            String clock = " " + Clock.getClock(r.getDateText());
            date = Clock.getFormattedTime(date, KEY_PREF_TIME) + clock;
            int tmp = (int) Math.round(celsius ? Util.convertFtoC(r.getTemp()) :
                    r.getTemp());
            modelList.add(new ReportRecyclerViewAdapter.RVModel(tmp, Util.getItemWeatherIcon(r.getIconCode(), Clock.getDayTime(r.getDateText())), date));
        }
        viewAdapterHour = new
                ReportRecyclerViewAdapter(modelList, (position, rvModel) -> {
                    time.turnOffSync();
                    selectedHour = currentCity.getDays().get(viewAdapterDay.getLastSelected()).getHour(position);
                    setMainCardUI(selectedHour);
                    weatherFragmentListener.onDayTimeChange(Clock.getDayTime(selectedHour.getDateText()), btnNow);
                });
        timeRecyclerView.setAdapter(viewAdapterHour);
        timeRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private void initializeDayRecyclerView(int index, SharedPreferences prefs) {
        LinearLayoutManager recyclerLayoutManager = new
                LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
        dayRecyclerView.setLayoutManager(recyclerLayoutManager);
        List<ReportRecyclerViewAdapter.RVModel> modelList = new ArrayList<ReportRecyclerViewAdapter.RVModel>();
        boolean celsius = prefs.getString(KEY_PREF_TEMP,KEY_PREF_TEMP).equals(KEY_PREF_TEMP);
        int i=0;
        String today = ctx.getResources().getString(R.string.keyToday);
        for (Spot.Day d:currentCity.getDays()) {
            Spot.Report hour  = d.getHour(0);
            String date = hour.getDateText();
            int tmp = (int) Math.round(celsius ? Util.convertFtoC(hour.getTemp()) :
                    hour.getTemp());
            modelList.add(new ReportRecyclerViewAdapter.RVModel(tmp,Util.getItemWeatherIcon(hour.getIconCode(),
                    Clock.getDayTime(hour.getDateText())), i++ == 0 ? today : Clock.getDay(date,false), Clock.getDate(date)));
        }
        viewAdapterDay = new
                ReportRecyclerViewAdapter(modelList, (position, rvModel) -> {
                    time.turnOffSync();
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
                    selectedHour = currentCity.getDays().get(position).getHour(0);
                    initializeHourRecyclerView(position, pref);
                    setMainCardUI(selectedHour);
                    weatherFragmentListener.onDayTimeChange(Clock.getDayTime(selectedHour.getDateText()), btnNow);
                });
        dayRecyclerView.setAdapter(viewAdapterDay);
        viewAdapterDay.setSelected(index);
        dayRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        time.turnOffSync();
        updateRate.turnOffSync();
        super.onPause();
    }

    @Override
    public void onResume() {
        time.turnOnSync();
        updateRate.turnOnSync();
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            weatherFragmentListener = (WeatherFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement FragmentChangeListener, UIUpdateListener");
        }
    }

    @Override
    public void onDetach() {

        time.turnOffSync();
        updateRate.turnOffSync();
        super.onDetach();
    }

    private void callForData(String city) {
        if(Util.isNetworkAvailable(ctx)) {
            new ForecastRequest(ctx, new ForecastRequest.WeatherListener() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    updateWeather(jsonObject);
                }
                @Override
                public void onError(int statusCode) {
                    BossActivity.errorDialog(ctx, statusCode);
                }
            }).makeRequest(city);
        } else {
            BossActivity.errorDialog(ctx, 0);
        }
    }

    private long getUpdateRate(SharedPreferences prefs) {
        long MILLISECOND = 1;
        long SECOND = MILLISECOND * 1000;
        long MINUTE = SECOND * 60;
        long HOUR = MINUTE * 60;
        long DAY = HOUR * 24;
        String rate = prefs.getString(KEY_PREF_INTERVAL, KEY_PREF_INTERVAL);
        switch (rate) {
            case "30m":
                return MINUTE*30;
            case "1h":
                return HOUR;
            case "2h":
                return HOUR*2;
            case "3h":
                return HOUR*3;
            case "6h":
                return HOUR*6;
            case "1d":
                return DAY;
            default:
                return MINUTE*15;
        }
    }


    public static void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

    public static void TextViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);

                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

    private void setMainCardUI(Spot.Report r) {
        changeTime(r, prefs);
        changeDate(r, prefs);
        changeTemperature(r, prefs);
        int id = Util.getFeaturedWeatherIcon(r.getIconCode(), Clock.getDayTime(r.getDateText()));
        if(Integer.valueOf((String) imgTmp.getTag())!=id) {
            Bitmap adaptiveIcon = ((BitmapDrawable) ctx.getResources().getDrawable(id)).getBitmap();
            ImageViewAnimatedChange(ctx,imgTmp, adaptiveIcon);
            imgTmp.setTag(String.valueOf(id));
        }
        txtDesc.setText(Util.getTranslatedDescription(ctx, r.getWeatherDescription()));
        changePressure(r, prefs);
        txtHumidity.setText(accurate(r.getHumidity()).concat(" %"));
        // wind
        changeWind(prefs);
        // sunset & sunrise
        long sunRise = currentCity.getNow().getSunRise();
        long sunSet = currentCity.getNow().getSunSet();
        txtSunriseTime.setText(milliToTime(sunRise));
        txtSunsetTime.setText(milliToTime(sunSet));
        txtSunriseDate.setText(milliToDate(sunRise));
        txtSunsetDate.setText(milliToDate(sunSet));
    }

    private Drawable getRotateDrawable(final Bitmap b, double angle) {
        return new BitmapDrawable(ctx.getResources(), b) {
            @Override
            public void draw(final Canvas canvas) {
                canvas.save();
                canvas.rotate((float)angle, b.getWidth() / 2, b.getHeight() / 2);
                super.draw(canvas);
                canvas.restore();
            }
        };
    }


    private String milliToTime(long millis) {
        Date date = new Date(1000*millis);
        DateFormat formatter = new SimpleDateFormat("hh:mm aa");
        String dateFormatted = formatter.format(date);
        return dateFormatted.replace(".","").toUpperCase();
    }

    private String milliToDate(long millis) {
        Date date = new Date(1000*millis);
        DateFormat formatter = new SimpleDateFormat("dd MMM");
        return formatter.format(date);
    }

    private static String accurate(double var) {
        return String.valueOf(Math.round(var));
    }

    private void currentTimeDate() {
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String timeFormat = prefs.getString(KEY_PREF_TIME,KEY_PREF_TIME);
        String date = new Date().toString();
        txtTime.setText(Clock.getFormattedTime(date,timeFormat)
                .concat(":")
                .concat(Clock.getNowMin()));
                //.concat(":")
                //.concat(Clock.getNowSec()));
        txtClock.setText(timeFormat.equals(KEY_PREF_TIME) ?
                Clock.getNowClock() : "");
        txtDate.setText(Clock.getFormattedDate(new Date().toString(),
                prefs.getString(KEY_PREF_DATE, KEY_PREF_DATE)));

        weatherFragmentListener.onDayTimeChange(Clock.getDayTime(null), btnNow);
    }

    private void changeTime(Spot.Report r, SharedPreferences prefs) {
        String date;
        String timeFormat = prefs.getString(KEY_PREF_TIME, KEY_PREF_TIME);
        if(time.isRunning()) {
            date = new Date().toString();
            txtTime.setText(Clock.getFormattedTime(date,timeFormat)
                    .concat(":")
                    .concat(Clock.getNowMin())
                    .concat(":")
                    .concat(Clock.getNowSec()));
        } else {
            date = r.getDateText();
            txtTime.setText(Clock.getFormattedTime(date, KEY_PREF_TIME)
                    .concat(":00"));
        }

        txtClock.setText(timeFormat.equals(KEY_PREF_TIME) ?
                (time.isRunning() ? Clock.getNowClock() : Clock.getClock(r.getDateText())) : "");
    }

    private void changeDate(Spot.Report r, SharedPreferences prefs) {
        txtDate.setText(Clock.getFormattedDate(
                time.isRunning() ? new Date().toString() : r.getDateText(),
                prefs.getString(KEY_PREF_DATE, KEY_PREF_DATE)));
    }

    private void changeWind(SharedPreferences prefs) {
        String sKey = prefs.getString(KEY_PREF_WIND, KEY_PREF_WIND);
        double speed = Util.Speed.get(currentCity.getNow().getWind(),sKey);
        txtWScale.setText(sKey);
        txtWindStatus.setText(Util.Speed.getBeaufortStatus(ctx, currentCity.getNow().getWind()));
        txtWindSpeed.setText(accurate(speed));
        Drawable myDrawable = ctx.getResources().getDrawable(R.drawable.navigation);
        Bitmap b = ((BitmapDrawable) myDrawable).getBitmap();
        double angle=currentCity.getNow().getWindDegree();
        final Drawable rotatedDrawable = getRotateDrawable(b,angle);
        imgDirection.setImageDrawable(rotatedDrawable);
        String[] dir = getDirection(ctx, currentCity.getNow().getWindDegree());
        txtWindDirShort.setText(dir[0]);
        txtWindDirLong.setText(dir[1]);
    }

    private static String[] getDirection(Context con, double degree) {
        long angle = Math.round(degree);
        String[] direction = new String[2];
        if (angle > 350) {
            direction[0]="N";
            direction[1]=getString(con,R.string.directionNorth);
        } else if (angle > 280) {
            direction[0]="NW";
            direction[1]=getString(con,R.string.directionNorth).concat(" ").concat(getString(con,R.string.directionWest));
        } else if (angle > 260) {
            direction[0]="W";
            direction[1]=getString(con,R.string.directionWest);
        } else if (angle > 190) {
            direction[0]="SW";
            direction[1]=getString(con,R.string.directionSouth).concat(" ").concat(getString(con,R.string.directionWest));
        } else if (angle > 170) {
            direction[0]="S";
            direction[1]=getString(con,R.string.directionSouth);
        } else if (angle > 100) {
            direction[0]="SE";
            direction[1]=getString(con,R.string.directionSouth).concat(" ").concat(getString(con,R.string.directionEast));
        } else if (angle > 80) {
            direction[0]="E";
            direction[1]=getString(con,R.string.directionEast);
        } else if (angle > 10) {
            direction[0]="NE";
            direction[1]=getString(con,R.string.directionNorth).concat(" ").concat(getString(con,R.string.directionEast));
        } else {
            direction[0]="N";
            direction[1]=getString(con,R.string.directionNorth);
        }
        return direction;
    }

    private static String getString(Context con, @StringRes int id) {
        return con.getResources().getString(id);
    }

    private void changePressure(Spot.Report r, SharedPreferences prefs) {
        String pKey = prefs.getString(KEY_PREF_PRESSURE, KEY_PREF_PRESSURE);
        double unit = Util.Pressure.get(r.getPressure(),pKey);
        String pressure = accurate(unit).concat(" ").concat(Util.Pressure.getScale(pKey));
        txtPressure.setText(pressure);
        // Pressure
        unit = Util.Pressure.get(currentCity.getNow().getSeaLevel(),pKey);
        pressure = accurate(unit).concat(" ").concat(Util.Pressure.getScale(pKey));
        txtSeaLvl.setText(pressure);
        unit = Util.Pressure.get(currentCity.getNow().getGroundLevel(),pKey);
        pressure = accurate(unit).concat(" ").concat(Util.Pressure.getScale(pKey));
        txtGrndLvl.setText(pressure);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_update:
                long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastTimeReportUpdate);
                if (seconds > 5) {
                    callForData(cityName);
                    lastTimeReportUpdate = System.currentTimeMillis();
                } else {
                    if(Util.isNetworkAvailable(ctx)) {
                        Toast.makeText(ctx, ctx.getResources().getString(R.string.success100), Toast.LENGTH_SHORT).show();
                    } else {
                        BossActivity.errorDialog(ctx,0);
                    }
                }
                break;
            case R.id.action_share:
                Util.takeScreenshot(ctx);
                Util.shareScreenshot(ctx);
                break;
            case R.id.action_add:
                weatherFragmentListener.onOptionsMenuItemClick(R.id.action_add);
                break;
            case R.id.action_delete:
                weatherFragmentListener.onOptionsMenuItemClick(R.id.action_delete);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}

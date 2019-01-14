package com.apps.sfrcreativity.weatherhours.activities;

//TODO: implementing firebase analytics for tracking
//TODO: implementing firebase ads
//TODO: creating app link for google search results
/*TODO: testing require:
        TODO:   orientation change
        TODO:   internet connection on/off
        TODO:   system kills app
        TODO:   database reset
 */
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.sfrcreativity.weatherhours.R;
import com.apps.sfrcreativity.weatherhours.adapter.CityAdapter;
import com.apps.sfrcreativity.weatherhours.storage.DBHelper;
import com.apps.sfrcreativity.weatherhours.utils.LocationProviders;
import com.apps.sfrcreativity.weatherhours.utils.Sync;
import com.apps.sfrcreativity.weatherhours.utils.Util;
import com.apps.sfrcreativity.weatherhours.volley.ForecastRequest;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BossActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentMain.WeatherFragmentListener, CityListFragment.CityListFragmentListener {

    private static final String DAYTIME_NIGHT = "NIGHT";
    private static final String DAYTIME_DAY = "DAY";

    private LocationProviders locationProviders;
    private DBHelper dbHelper;
    private String cityName;
    private boolean backgroundColorNightState;
    private TransitionDrawable toolbarTransition, backgroundTransition, nowButtonTransition;
    private String DAYTIME_STATE;
    private static final int duration = 1000;
    private FirebaseAnalytics mFirebaseAnalytics;


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.parentPanel) ConstraintLayout mPanel;
    @BindView(R.id.adView) AdView adview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        ButterKnife.bind(this);
        ((AppBarLayout)findViewById(R.id.appbar)).bringToFront();
        dbHelper = new DBHelper(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        DAYTIME_STATE = "DAY";
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.mainContainer, new CityListFragment()).commit();

        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setSubtitle(getResources().getString(R.string.app_version));
        setSupportActionBar(toolbar);
        initiliazeTransitions();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        menu.getItem(0).setChecked(true);
        MenuItem item = menu.getItem(0);
        ArrayList<String> cityList = dbHelper.getAllGeoCities();

        if(cityList!=null && cityList.size()>0) {
            for (final String c: cityList) {
                addItem(c);
            }
        }
        disableNavigationViewScrollbars(navigationView);
        navigationView.invalidate();
        adview.loadAd(new AdRequest.Builder().build());
    }

    private void initiliazeTransitions() {
        Drawable backgrounds[] = new Drawable[2];
        backgrounds[0] = getResources().getDrawable(R.drawable.day_background);
        backgrounds[1] = getResources().getDrawable(R.drawable.night_background);
        backgroundTransition = new TransitionDrawable(backgrounds);
        Drawable nowBackgrounds[] = new Drawable[2];
        nowBackgrounds[0] = getResources().getDrawable(R.drawable.current_hour_day);
        nowBackgrounds[1] = getResources().getDrawable(R.drawable.current_hour_night);
        nowButtonTransition = new TransitionDrawable(nowBackgrounds);
        ColorDrawable[] color = {new ColorDrawable(getResources().getColor(R.color.dayColor)),
                new ColorDrawable(getResources().getColor(R.color.nightColor))};
        toolbarTransition = new TransitionDrawable(color);

    }


    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(!item.isChecked()) {
            if (id == R.id.nav_settings) {
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
            } else if (id == R.id.nav_home) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.mainContainer, new CityListFragment()).commit();
                toolbar.setTitle(getResources().getString(R.string.app_name));
                toolbar.setSubtitle(getResources().getString(R.string.app_version));
                toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
            } else if (id == R.id.nav_remove) {
                AlertDialog.Builder sayWindows = new AlertDialog.Builder(
                        this);
                sayWindows.setIcon(R.drawable.ic_location_on_black_24dp);
                sayWindows.setTitle(getResources().getString(R.string.dialogSub));
                ArrayList<String> cityList = dbHelper.getAllGeoCities();
                if(cityList!=null && cityList.size()>0) {
                    ArrayAdapter arrayAdapter =
                            new ArrayAdapter(this, android.R.layout.simple_list_item_1, cityList);
                    sayWindows.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = cityList.get(which);
                            deleteItem(name);
                        }
                    });
                }

                sayWindows.setNegativeButton(getResources().getString(R.string.dialogCancel), null);

                final AlertDialog mAlertDialog = sayWindows.create();
                mAlertDialog.show();
            } else if (id == R.id.nav_add) {
                showCityDialog();
            } else if (id == R.id.nav_share) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        getResources().getString(R.string.shareLink).concat(" ").concat(getResources().getString(R.string.addressPlayStore)));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            } else if (id == R.id.nav_rate) {
                Util.rateApp(this);
            } else if(id == R.id.nav_about) {
                AlertDialog.Builder sayWindows = new AlertDialog.Builder(
                        this);
                sayWindows.setIcon(R.drawable.ic_info_black_24dp);
                sayWindows.setTitle(getResources().getString(R.string.pref_header_about));
                sayWindows.setMessage(getResources().getString(R.string.summary_about)+"\n"+
                        getResources().getString(R.string.title_version)+": "+
                        getResources().getString(R.string.app_version));
                sayWindows.setPositiveButton(getResources().getString(R.string.action_RateUs), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Util.rateApp(BossActivity.this);
                    }
                });

                final AlertDialog mAlertDialog = sayWindows.create();
                mAlertDialog.show();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeLocale(String language_code) {
        Resources res = getResources();
// Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language_code.toLowerCase())); // API 17+ only.
// Use conf.locale = new Locale(...) if targeting lower versions
        res.updateConfiguration(conf, dm);
    }

    public void showCityDialog() {
        AlertDialog.Builder sayWindows = new AlertDialog.Builder(
                this, R.style.CustomAlertDialog);
        final EditText etCity = new EditText(this);
        final TextView tvCity = new TextView(this);
        final LinearLayout rootLayout=new LinearLayout(this);
        etCity.setHint(getResources().getString(R.string.dialogCC));
        cityName = "";
        locationProviders = new LocationProviders(this);
        final KProgressHUD progressHUD = new KProgressHUD(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getResources().getString(R.string.proLabelPW))
                .setDetailsLabel(getResources().getString(R.string.proDetailGL))
                .setCancellable(true)
                .setAutoDismiss(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.addView(tvCity);
        rootLayout.addView(etCity);
        sayWindows.setView(rootLayout);
        sayWindows.setTitle(getResources().getString(R.string.dialogAC));
        sayWindows.setIcon(R.drawable.ic_my_location_black_24dp);

        locationProviders.setOnLocationListener(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location!=null) {
                    Geocoder geocoder = new Geocoder(BossActivity.this, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String city = addresses.get(0).getLocality();
                    String countryName = addresses.get(0).getCountryName();
                    String response = city.concat(", ").concat(countryName);
                    etCity.setText(toTitleCase(response));
                    progressHUD.dismiss();
                }
                locationProviders.removeLocationUpdates();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
        sayWindows.setNeutralButton(getResources().getString(R.string.dialogUGPS), null);
        sayWindows.setPositiveButton(getResources().getString(R.string.menu_Add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(Util.isNetworkAvailable(BossActivity.this)) {

                    cityName = toTitleCase(etCity.getText().toString().split(",", 2)[0]);

                    new ForecastRequest(getApplicationContext(), new ForecastRequest.WeatherListener() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            drawFragmentMain(cityName, FragmentMain.PARAM_JSON, jsonObject.toString());

                        }

                        @Override
                        public void onError(int statusCode) {
                            errorDialog(BossActivity.this,statusCode);
                        }

                    }).makeRequest(toTitleCase(etCity.getText().toString()));
                } else {
                    errorDialog(BossActivity.this, 0);
                }
            }
        });
        sayWindows.setNegativeButton(getResources().getString(R.string.dialogCancel), null);
        final AlertDialog mAlertDialog = sayWindows.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = mAlertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Util.isNetworkAvailable(BossActivity.this)) {
                            progressHUD.show();
                            locationProviders.getLocation();
                        } else {
                            errorDialog(BossActivity.this, 0);
                        }
                    }
                });
            }
        });
        mAlertDialog.show();
    }


    public static String toTitleCase(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder converted = new StringBuilder();

        boolean convertNext = true;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }

        return converted.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK) {
            if(requestCode==LocationProviders.REQUEST_CHECK_SETTINGS) {
                locationProviders.getLocation();
            }
        }
    }

    private int searchForItem(String title) {
        title = title.replaceAll("\\s+","").replaceAll("\\.+","").replaceAll("\\-+","");
        Menu menu = navigationView.getMenu();
        for (int i=0;i<menu.size();i++) {
            MenuItem item = menu.getItem(i);
            String itemName = item.getTitle().toString().replaceAll("\\s+","").replaceAll("\\.+","").replaceAll("\\-+","");
            if (item.getGroupId() == R.id.cityGroup
                    && title.equals(itemName)) {

                return item.getItemId();
            }
        }
        return -1;
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    private MenuItem addItem(String name) {
        if (searchForItem(name)==-1) {
            Menu menu = navigationView.getMenu();
            int itemId = new Random().nextInt((1000 - 1) + 1) + 1;
            MenuItem item = menu.add(R.id.cityGroup, itemId, 1, name)
                    .setIcon(R.drawable.ic_my_location_black_24dp)
                    .setCheckable(true)
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            drawFragmentMain(name, FragmentMain.PARAM_SQLITE, null);
                            return false;
                        }
                    });
            return item;
        }
        return null;
    }

    public static void errorDialog(Context ctx, int statusCode) {
        AlertDialog.Builder sayWindows = new AlertDialog.Builder(
                ctx);
        sayWindows.setIcon(R.drawable.ic_error_black_24dp);
        sayWindows.setTitle(ctx.getResources().getString(R.string.dialogError));
        if(statusCode==0) {
            sayWindows.setMessage(ctx.getResources().getString(R.string.error404));
        } else {
            sayWindows.setMessage(ctx.getResources().getString(R.string.city404));
        }
        sayWindows.setPositiveButton(ctx.getResources().getString(R.string.dialogOK),null);
        final AlertDialog mAlertDialog = sayWindows.create();
        mAlertDialog.show();
    }


    private void drawFragmentMain(String cityName, String action, String response) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentMain f = new FragmentMain();
        Bundle bundle = new Bundle();
        bundle.putString(FragmentMain.ARG_CITY, cityName);
        bundle.putString(FragmentMain.ARG_RESPONSE, response);
        bundle.putString(FragmentMain.ARG_ACTION, action);
        f.setArguments(bundle);
        ft.replace(R.id.mainContainer, f).commit();
        toolbarTransition.resetTransition();
        backgroundTransition.resetTransition();
        mPanel.setBackground(backgroundTransition);
        toolbar.setBackground(toolbarTransition);
        DAYTIME_STATE = DAYTIME_DAY;
        mFirebaseAnalytics.setUserProperty("City",cityName);
        adview.loadAd(new AdRequest.Builder().build());
    }


    @Override
    public void onToolbarTitleUpdate(String title, String subtitle) {
        toolbar.setTitle(title);
        toolbar.setSubtitle(subtitle);
        MenuItem item = addItem(title);
        if(item!=null)
            item.setChecked(true);
    }

    @Override
    public void onDayTimeChange(String dayTime, Button button) {

        if(!DAYTIME_STATE.equals(dayTime)) {
            switch (dayTime) {
                case DAYTIME_NIGHT:
                    toolbarTransition.resetTransition();
                    toolbarTransition.startTransition(duration);
                    backgroundTransition.resetTransition();
                    backgroundTransition.startTransition(duration);
                    nowButtonTransition.resetTransition();
                    nowButtonTransition.startTransition(duration);
                    break;
                case DAYTIME_DAY:
                    toolbarTransition.reverseTransition(duration);
                    backgroundTransition.reverseTransition(duration);
                    nowButtonTransition.reverseTransition(duration);
            }

            DAYTIME_STATE = dayTime;

            mPanel.setBackground(backgroundTransition);
            toolbar.setBackground(toolbarTransition);
            button.setBackground(nowButtonTransition);
        }


    }

    @Override
    public void onOptionsMenuItemClick(int id) {
        switch(id) {
        case R.id.action_add:
            showCityDialog();
            break;
        case R.id.action_delete:
            String name = toolbar.getTitle().toString();
            deleteItem(name);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.mainContainer, new CityListFragment()).commit();
            toolbar.setTitle(getResources().getString(R.string.app_name));
            toolbar.setSubtitle(getResources().getString(R.string.app_version));
            toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
            break;
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
        }
    }

    @Override
    public void resetBackground(int color) {
        mPanel.setBackground(new ColorDrawable(color));
    }

    private void deleteItem(String name) {
        int deleteItem = searchForItem(name);
        if(deleteItem!=-1) {
            navigationView.getMenu().removeItem(deleteItem);
            Menu menu = navigationView.getMenu();
        }

        dbHelper.deleteSpot(name);

    }


    @Override
    public void onClickListener(String name, int pos) {
        drawFragmentMain(name, FragmentMain.PARAM_SQLITE, null);
        navigationView.getMenu().getItem(0).setChecked(false);
        navigationView.getMenu().getItem(pos+2).setChecked(true);
    }

    @Override
    public void onDeleteClickListener(String name, int pos) {
        deleteItem(name);
    }


    @Override
    public void showDialog() {
        showCityDialog();
    }
}

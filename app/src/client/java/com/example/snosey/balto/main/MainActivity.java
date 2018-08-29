package com.example.snosey.balto;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.snosey.balto.Support.image.CircleTransform;
import com.example.snosey.balto.Support.notification.NotifyService;
import com.example.snosey.balto.Support.webservice.GetData;
import com.example.snosey.balto.Support.webservice.UrlData;
import com.example.snosey.balto.Support.webservice.WebService;
import com.example.snosey.balto.login.RegistrationActivity;
import com.example.snosey.balto.main.ClientProfile;
import com.example.snosey.balto.main.Help;
import com.example.snosey.balto.main.HomeAndOnline;
import com.example.snosey.balto.main.Promotions;
import com.example.snosey.balto.main.reservation.Reservations;
import com.example.snosey.balto.payment.PaymentSlider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Snosey on 2/7/2018.
 */

public class MainActivity extends FragmentActivity {
    public static JSONObject jsonObject;


    @InjectView(R.id.clientName)
    com.example.snosey.balto.Support.CustomTextView clientName;
    @InjectView(R.id.clientRate)
    com.example.snosey.balto.Support.CustomTextView clientRate;

    @InjectView(R.id.drawer)
    ScrollView drawer;
    @InjectView(R.id.DrawerLayout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.progress)
    ProgressBar progress;
    @InjectView(R.id.logo)
    ImageView logo;

    @InjectView(R.id.menu)
    ImageView menu;


    @InjectView(R.id.home)
    LinearLayout home;
    @InjectView(R.id.agenda)
    LinearLayout agenda;
    @InjectView(R.id.waller)
    LinearLayout waller;
    @InjectView(R.id.add_payment)
    LinearLayout addPayment;
    @InjectView(R.id.online)
    Switch online;


    LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    boolean doubleBackToExitPressedOnce = false;
    @InjectView(R.id.title)
    com.example.snosey.balto.Support.CustomTextView title;
    @InjectView(R.id.HomeText)
    com.example.snosey.balto.Support.CustomTextView HomeText;
    @InjectView(R.id.reservationText)
    com.example.snosey.balto.Support.CustomTextView reservationText;
    @InjectView(R.id.paymentWayText)
    com.example.snosey.balto.Support.CustomTextView paymentWayText;
    @InjectView(R.id.promotionsText)
    com.example.snosey.balto.Support.CustomTextView promotionsText;
    @InjectView(R.id.languageText)
    com.example.snosey.balto.Support.CustomTextView languageText;
    @InjectView(R.id.termsAndConditionsText)
    com.example.snosey.balto.Support.CustomTextView termsAndConditionsText;
    @InjectView(R.id.shareText)
    com.example.snosey.balto.Support.CustomTextView shareText;
    @InjectView(R.id.logoutText)
    com.example.snosey.balto.Support.CustomTextView logoutText;

    @InjectView(R.id.helpText)
    com.example.snosey.balto.Support.CustomTextView helpText;

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 0) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.exit), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else
            super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = new Intent(MainActivity.this, NotifyService.class);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                22, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);

        checkLang();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/arial.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/arial.ttf");
        title.setTypeface(font, Typeface.BOLD);
        clientName.setTypeface(font, Typeface.BOLD);
        logoutText.setTypeface(font, Typeface.BOLD);
        termsAndConditionsText.setTypeface(font, Typeface.BOLD);
        languageText.setTypeface(font, Typeface.BOLD);
        promotionsText.setTypeface(font, Typeface.BOLD);
        shareText.setTypeface(font, Typeface.BOLD);
        paymentWayText.setTypeface(font, Typeface.BOLD);
        reservationText.setTypeface(font, Typeface.BOLD);
        HomeText.setTypeface(font, Typeface.BOLD);
        helpText.setTypeface(font, Typeface.BOLD);

        clientRate.setVisibility(View.GONE);


        try {
            jsonObject = new JSONObject(getIntent().getStringExtra("userData"));
            if (RegistrationActivity.sharedPreferences.getString("lang", "en").equals("ar"))
                clientName.setText(jsonObject.getString("first_name_ar"));
            else
                clientName.setText(jsonObject.getString("first_name_en"));

            if (!jsonObject.getString("image").equals("")) {
                String imageLink = jsonObject.getString("image");
                if (!imageLink.startsWith("https://"))
                    imageLink = WebService.Image.fullPathImage + imageLink;
                Picasso.with(this).load(imageLink).transform(new CircleTransform()).into((ImageView) findViewById(R.id.logo));
            }
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    {
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putString(WebService.HomeVisit.id_user, jsonObject.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        FragmentManager fm = getSupportFragmentManager();
                        ClientProfile fragment = new ClientProfile();
                        FragmentTransaction ft = fm.beginTransaction();
                        fragment.setArguments(bundle);
                        ft.replace(R.id.fragment, fragment, "ClientProfile");
                        ft.addToBackStack("ClientProfile");
                        ft.commit();

                        if (drawerLayout.isDrawerOpen(drawer))
                            drawerLayout.closeDrawer(drawer);
                    }
                }
            });
            UrlData urlData = new UrlData();
            urlData.add(WebService.Slider.id_user, jsonObject.getString("id"));
            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    if (output.contains("true")) {
                        try {
                            JSONObject jsonObject = new JSONObject(output);
                            if (!jsonObject.getString(WebService.Slider.total_rate).equals("0") && !jsonObject.getString(WebService.Slider.total_rate).equals("null"))
                                clientRate.setText(jsonObject.getString(WebService.Slider.total_rate));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, this, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Slider.userRateApi, urlData.get());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (BuildConfig.APPLICATION_ID.contains("doctor")) {
            home.setVisibility(View.GONE);
            addPayment.setVisibility(View.GONE);
            online.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean onlineBool) {
                    UrlData urlData = new UrlData();
                    if (onlineBool) {
                        urlData.add(WebService.Slider.online, WebService.Slider.on);
                        online.setText(getString(R.string.available));
                    } else {
                        urlData.add(WebService.Slider.online, WebService.Slider.off);
                        online.setText(getString(R.string.offline));
                    }
                    try {
                        urlData.add(WebService.Slider.id_user, jsonObject.getString("id"));
                        new GetData(new GetData.AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                            }
                        }, MainActivity.this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Location.updateLocationApi, urlData.get());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else

        {
            online.setVisibility(View.GONE);
            agenda.setVisibility(View.GONE);
            waller.setVisibility(View.GONE);
            MainHomeOfClient();
        }

        updateLocationInDB();

        if (getIntent().hasExtra("data")) {
            new NotificationTransaction(MainActivity.this, getIntent().getStringExtra("data"));
        }

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu();
            }
        });

    }

    private void MainHomeOfClient() {

        Bundle bundle = new Bundle();
        try {
            bundle.putString(WebService.HomeVisit.id_user, jsonObject.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        HomeAndOnline fragment = new HomeAndOnline();
        fragment.setArguments(bundle);
        ft.replace(R.id.fragment, fragment);
        ft.commit();

    }

    public void setting(View view) {
        if (drawerLayout.isDrawerOpen(drawer))
            drawerLayout.closeDrawer(drawer);
    }

    public void home(View view) {

        if (drawerLayout.isDrawerOpen(drawer))
            drawerLayout.closeDrawer(drawer);

        Fragment myFragment = (Fragment) getSupportFragmentManager().findFragmentByTag("HomeAndOnline");
        if (myFragment == null || !myFragment.isVisible()) {


            FragmentManager fm = getSupportFragmentManager();
            HomeAndOnline fragment = new HomeAndOnline();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment, fragment, "HomeAndOnline");
            ft.commit();

        }
    }

    public void reservations(View view) {

        if (drawerLayout.isDrawerOpen(drawer))
            drawerLayout.closeDrawer(drawer);

        Fragment myFragment = (Fragment) getSupportFragmentManager().findFragmentByTag("ReservationsMain");
        if (myFragment == null || !myFragment.isVisible()) {
            FragmentManager fm = getSupportFragmentManager();
            Reservations fragment = new Reservations();
            FragmentTransaction ft = fm.beginTransaction();
            ft.addToBackStack(null);
            ft.replace(R.id.fragment, fragment, "ReservationsMain");
            ft.commit();

        }
    }

    void checkLang() {
        Locale locale;

        if (RegistrationActivity.sharedPreferences.getString("lang", "en").equals("ar"))
            locale = new Locale("ar");
        else
            locale = new Locale("en");

        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        onConfigurationChanged(config);
    }

    public void payment(View view) {
        if (drawerLayout.isDrawerOpen(drawer))
            drawerLayout.closeDrawer(drawer);

        Fragment myFragment = (Fragment) getSupportFragmentManager().findFragmentByTag("payment");
        if (myFragment == null || !myFragment.isVisible()) {

            FragmentManager fm = getSupportFragmentManager();
            PaymentSlider fragment = new PaymentSlider();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment, fragment, "payment");
            ft.addToBackStack("payment");
            ft.commit();
        }

    }

    public void promotions(View view) {

        if (drawerLayout.isDrawerOpen(drawer))
            drawerLayout.closeDrawer(drawer);

        Fragment myFragment = (Fragment) getSupportFragmentManager().findFragmentByTag("promo");
        if (myFragment == null || !myFragment.isVisible()) {


            FragmentManager fm = getSupportFragmentManager();
            Bundle bundle = new Bundle();
            try {
                bundle.putString(WebService.PromoCode.id_user, jsonObject.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Promotions fragment = new Promotions();
            FragmentTransaction ft = fm.beginTransaction();
            fragment.setArguments(bundle);
            ft.replace(R.id.fragment, fragment, "promo");
            ft.addToBackStack("promo");
            ft.commit();
        }
    }

    public void share(View view) {
        UrlData urlData = new UrlData();
        try {
            urlData.add(WebService.PromoCode.added_by, jsonObject.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output.contains("true")) {
                    String code = "";
                    try {
                        code = new JSONObject(output).getString("code");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareOfferPatient) + code + "\nhttps://play.google.com/store/apps/details?id=" +
                            BuildConfig.APPLICATION_ID);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);

                }
            }
        }, this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.PromoCode.newPromoCodeApi, urlData.get());

    }

    public void langauge(View view) {

        {

            if (drawerLayout.isDrawerOpen(drawer))
                drawerLayout.closeDrawer(drawer);
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage(getString(R.string.chooseLang));
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ("english"),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            recreate();
                            Locale locale = new Locale("en");
                            SharedPreferences sharedPreferences = getSharedPreferences("login_client", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("lang", "en");
                            editor.commit();
                            Locale.setDefault(locale);
                            Configuration config = new Configuration();
                            config.locale = locale;
                            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                            onConfigurationChanged(config);

                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, ("عربي"),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            recreate();
                            Locale locale = new Locale("ar");
                            SharedPreferences sharedPreferences = getSharedPreferences("login_client", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("lang", "ar");
                            editor.commit();
                            Locale.setDefault(locale);
                            Configuration config = new Configuration();
                            config.locale = locale;
                            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                            onConfigurationChanged(config);

                        }
                    });
            alertDialog.show();
        }

    }

    public void termAndConditions(View view) {
        if (drawerLayout.isDrawerOpen(drawer))
            drawerLayout.closeDrawer(drawer);

        String url = "";
        if (RegistrationActivity.sharedPreferences.getString("lang", "en").equals("ar"))
            url = "https://drive.google.com/open?id=1eSaJK6ZJS4N8BRCpiwIwki0DZqkJZ6UY";
        else
            url = "https://drive.google.com/open?id=1EsOM9r5vDV-QYLb_nCvZNbWonv1L6xeA";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public void logout(View view) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage(getString(R.string.areYouSure));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences sharedPreferences = getSharedPreferences("login_client", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
                        finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void menu() {
        if (drawerLayout.isDrawerOpen(drawer))
            drawerLayout.closeDrawer(drawer);
        else
            drawerLayout.openDrawer(drawer);

    }

    public void help(View view) {
        if (drawerLayout.isDrawerOpen(drawer))
            drawerLayout.closeDrawer(drawer);

        Fragment myFragment = (Fragment) getSupportFragmentManager().findFragmentByTag("Help");
        if (myFragment != null && myFragment.isVisible())
            return;

        FragmentManager fm = getSupportFragmentManager();
        Help fragment = new Help();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment, fragment, "Help");
        if (view != null)
            ft.addToBackStack("Help");

        ft.commit();
    }

    public void back(View view) {

        if (drawerLayout.isDrawerOpen(drawer)) {
            drawerLayout.closeDrawer(drawer);
        } else {
            super.onBackPressed();
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                UrlData urlData = new UrlData();
                try {
                    urlData.add(WebService.Location.id, jsonObject.getString("id"));
                    urlData.add(WebService.Location.latitude, location.getLatitude() + "");
                    urlData.add(WebService.Location.longitude, location.getLongitude() + "");
                    new GetData(new GetData.AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                        }
                    }, MainActivity.this, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.Location.updateLocationApi, urlData.get());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    };

    void updateLocationInDB() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }


    @OnClick(R.id.back)
    public void onViewClicked() {
        onBackPressed();
    }

}

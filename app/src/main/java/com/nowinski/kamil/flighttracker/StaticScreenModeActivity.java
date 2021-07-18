package com.nowinski.kamil.flighttracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.nowinski.kamil.flighttracker.Entities.FlightInfo;
import com.nowinski.kamil.flighttracker.Entities.Radar;
import com.nowinski.kamil.flighttracker.Services.AsyncResponse;
import com.nowinski.kamil.flighttracker.Services.CompassService;
import com.nowinski.kamil.flighttracker.Services.DownloadImageResponse;
import com.nowinski.kamil.flighttracker.Services.DownloadImageTask;
import com.nowinski.kamil.flighttracker.Services.JsonTask;
import com.nowinski.kamil.flighttracker.Utils.AngleArithm;
import com.nowinski.kamil.flighttracker.Utils.DialogWindow;
import com.nowinski.kamil.flighttracker.Utils.GpsNetworkCheckStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class StaticScreenModeActivity extends AppCompatActivity implements AsyncResponse<Radar>, DownloadImageResponse<Drawable> {

    private static final int REQUEST_ID_ALL_PERMISSIONS = 1;
    private boolean isRequiredPermissionsGranted = false;
    private static final double boundary = 0.9;
    private JsonTask flightRadarTask;
    private Geocoder geocoder;
    private Location actualLocation;
    private int actualVerticalAngle;
    private int actualHorizontalAngle;
    private BroadcastReceiver mMessageReceiver;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    AsyncResponse<Radar> thisContext = this;
    private boolean firstTime = true;

    private ImageView searchIcon, aircraftPng;
    private TextView searchText, icaoTxt, regTxt, latTxt, lonTxt,
            altTxt, speedTxt, fromTxt, toTxt, airLineTxt;
    private RelativeLayout rootView, afterAnimationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_static_screen_mode);
        initViews();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            isRequiredPermissionsGranted = checkAndRequestPermissions();
        } else {
            isRequiredPermissionsGranted = true;
        }

        if(isRequiredPermissionsGranted && GpsNetworkCheckStatus.isNetworkEnabled(this)){
            startLocation();
            geocoder = new Geocoder(this, Locale.getDefault());
            //start compass service
            Intent i = new Intent(StaticScreenModeActivity.this, CompassService.class);
            startService(i);

            //receiver
            mMessageReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    actualHorizontalAngle = intent.getIntExtra("compassValue", 0);
                    actualVerticalAngle = intent.getIntExtra("angleValue", 0);
                }
            };

            //register receives msg from service
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter("sensor-axis"));
        } else {
            Toast.makeText(this, "Nie nadano uprawnień lub brak sieci", Toast.LENGTH_SHORT).show();
        }
    }

    private  boolean checkAndRequestPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]),REQUEST_ID_ALL_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_ID_ALL_PERMISSIONS) {
            Map<String, Integer> perms = new HashMap<>();
            perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Uprawnienia", "Nadano uprawienia lokalizacji i internetu");
                    this.recreate();
                } else {
                    Log.d("Uprawnienia", "Któreś z uprawnień nie zostało nadane, zapytaj ponownie ");
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)) {
                        DialogWindow.showDialogOK(this,"Lokalizacja i dostęp do internetu wymagany do prawidłowego działania aplikacji",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                checkAndRequestPermissions();
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                finish();
                                                System.exit(0);
                                                break;
                                        }
                                    }
                                });
                    } else {
                        Log.d("Uprawnienia", "Uprawnienia zabronione (nigdy nie pytaj jest zaznaczone");
                        Toast.makeText(this, "Włącz w ustawieniach telefonu uprawienia lokalizacji i internetu", Toast.LENGTH_LONG)
                                .show();
                        finish();
                        System.exit(0);
                    }
                }
            }
        }
    }

    private void startLocation(){
        this.locationRequest = new LocationRequest();
        this.locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        this.locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(this.locationRequest);
        this.locationSettingsRequest = builder.build();

        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                actualLocation = location;
                if(firstTime){
                    flightRadarTask = new JsonTask();
                    flightRadarTask.setDelegate(thisContext);
                    flightRadarTask.setDeviceLocation(actualLocation);
                    flightRadarTask.execute(location.getLatitude() + boundary, location.getLatitude() - boundary, location.getLongitude() - boundary, location.getLongitude() + boundary);
                    firstTime = false;
                }
            }
        };

        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Uprawnienia", "Brak uprawnien - GPS");
            return;
        }
        this.mFusedLocationClient.requestLocationUpdates(this.locationRequest,
                this.locationCallback, Looper.myLooper());
    }

    @Override
    public void processFinish(Radar response) {
        if(response != null) {
            List<FlightInfo> flightInfoList = response.getFlightInfoList();
            for (FlightInfo f : flightInfoList) {

                if((f.getAngle1() >= (actualVerticalAngle - 30)) && (f.getAngle1() <= (actualVerticalAngle + 30))
                        && AngleArithm.close_angles(f.getAngle2(), actualHorizontalAngle, 10)) {
                    icaoTxt.setText("ICAO 24-BIT ADDRESS: " + f.getIcao24bitAddr());
                    regTxt.setText("REJESTRACJA: " + f.getRegistrationNumber());
                    latTxt.setText("SZEROKOŚĆ GEOGRAFICZNA: " + f.getActualLatitude());
                    lonTxt.setText("DŁUGOŚĆ GEOGRAFICZNA: " + f.getActualLongitude());
                    altTxt.setText("WYSOKOŚĆ: " + f.getAltitude() + " ft");
                    speedTxt.setText("PRĘDKOŚĆ: " + f.getSpeed() + " km/h");
                    fromTxt.setText("SKĄD LECI: " + f.getCountryFrom());
                    toTxt.setText("DOKĄD LECI: " + f.getCountryTo());
                    airLineTxt.setText("NAZWA LINII LOTNICZEJ: " + f.getAirLine());
                    new CountDownTimer(5000, 1500) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            searchText.setVisibility(GONE);
                            rootView.setBackgroundColor(ContextCompat.getColor(StaticScreenModeActivity.this, R.color.colorSplashText));
                            searchIcon.setImageResource(R.drawable.rocket);
                            startAnimation();
                        }
                        @Override
                        public void onFinish() {
                        }
                    }.start();
                    setAircraftImage(f.getId());
                    return;
                }
            }
        }
        flightRadarTask = new JsonTask();
        flightRadarTask.setDelegate(this);
        flightRadarTask.setDeviceLocation(actualLocation);
        flightRadarTask.execute(actualLocation.getLatitude() + boundary, actualLocation.getLatitude() - boundary, actualLocation.getLongitude() - boundary, actualLocation.getLongitude() + boundary);
    }

    private void setAircraftImage(String aircraftId){
        DownloadImageTask downloadImageTask = new DownloadImageTask();
        downloadImageTask.setDelegate(this);
        downloadImageTask.execute(aircraftId);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        if(isRequiredPermissionsGranted) {
            this.mFusedLocationClient.removeLocationUpdates(this.locationCallback);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed(){
        this.mFusedLocationClient.removeLocationUpdates(this.locationCallback);
        this.finish();
    }

    @Override
    public void onDestroy(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private void initViews() {
        searchIcon = findViewById(R.id.searchIcon);
        searchText= findViewById(R.id.searchText);
        rootView = findViewById(R.id.rootView);
        afterAnimationView = findViewById(R.id.afterAnimationView);
        icaoTxt = findViewById(R.id.icaoTxt);
        regTxt = findViewById(R.id.regTxt);
        latTxt = findViewById(R.id.latTxt);
        lonTxt = findViewById(R.id.lonTxt);
        altTxt = findViewById(R.id.altTxt);
        speedTxt = findViewById(R.id.speedTxt);
        fromTxt = findViewById(R.id.fromTxt);
        toTxt = findViewById(R.id.toTxt);
        airLineTxt = findViewById(R.id.airLineTxt);
        aircraftPng = findViewById(R.id.aircraftPng);
    }

    private void startAnimation() {
        ViewPropertyAnimator viewPropertyAnimator = searchIcon.animate();
        searchIcon.animate().scaleY(0.5f).scaleX(0.5f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1500);
        viewPropertyAnimator.x(5f);
        viewPropertyAnimator.y(10f);
        viewPropertyAnimator.setDuration(1500);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                afterAnimationView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void downloadImageFinish(Drawable response) {
        if(response != null) {
            aircraftPng.setImageDrawable(response);
        }
    }

    public void returnToStaticScreenMode(View view){
        this.recreate();
    }
}

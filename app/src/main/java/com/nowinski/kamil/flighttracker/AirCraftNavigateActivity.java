package com.nowinski.kamil.flighttracker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Bundle;
import android.util.Log;
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
import com.nowinski.kamil.flighttracker.Services.JsonTask;
import com.nowinski.kamil.flighttracker.Utils.DialogWindow;
import com.nowinski.kamil.flighttracker.Utils.GpsNetworkCheckStatus;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.map.Icon;
import com.tomtom.online.sdk.map.MapConstants;
import com.tomtom.online.sdk.map.MapFragment;
import com.tomtom.online.sdk.map.MarkerBuilder;
import com.tomtom.online.sdk.map.OnMapReadyCallback;
import com.tomtom.online.sdk.map.SimpleMarkerBalloon;
import com.tomtom.online.sdk.map.TomtomMap;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AirCraftNavigateActivity extends AppCompatActivity implements AsyncResponse<Radar> {

    private static final int REQUEST_ID_ALL_PERMISSIONS = 1;
    private boolean isRequiredPermissionsGranted = false;

    //Map field
    private TomtomMap tomtomMap;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_craft_navigate);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            isRequiredPermissionsGranted = checkAndRequestPermissions();
        } else {
            isRequiredPermissionsGranted = true;
        }

        if(isRequiredPermissionsGranted && GpsNetworkCheckStatus.isNetworkEnabled(this)){
            startLocation();
            geocoder = new Geocoder(this, Locale.getDefault());
            //init tomtomMap
            MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
            mapFragment.getAsyncMap(onMapReadyCallback);
            //start compass service
            Intent i = new Intent(AirCraftNavigateActivity.this, CompassService.class);
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

    private final OnMapReadyCallback onMapReadyCallback =
            new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull TomtomMap map) {
                    tomtomMap = map;
                    tomtomMap.setMyLocationEnabled(true);
                    /*tomtomMap.addOnMarkerClickListener(new TomtomMapCallback.OnMarkerClickListener() {
                        @Override
                        public void onMarkerClick(@NonNull Marker marker) {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(AirCraftNavigateActivity.this);
                            View view = getLayoutInflater().inflate(R.layout.custom_dialog, null);
                            mBuilder.setView(view);
                            AlertDialog dialog = mBuilder.create();
                            dialog.show();
                        }
                    });*/
                }
            };

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
                    //tomtomcenteron
                    tomtomMap.centerOn(location.getLatitude(), location.getLongitude(),
                            9.0, MapConstants.ORIENTATION_NORTH);
                    tomtomMap.set3DMode();
                    tomtomMap.getUiSettings().setStyleUrl("asset://mapstyles/main.json");
                    tomtomMap.getLogoSettings().applyInvertedLogo();
                    flightRadarTask = new JsonTask();
                    flightRadarTask.setDelegate(thisContext);
                    flightRadarTask.setDeviceLocation(actualLocation);
                    flightRadarTask.execute(location.getLatitude() + 0.5, location.getLatitude() - 0.5, location.getLongitude() - 0.5, location.getLongitude() + 0.5);
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
            tomtomMap.removeMarkers();
            List<FlightInfo> flightInfoList = response.getFlightInfoList();
            for (FlightInfo f : flightInfoList) {
                if((f.getAngle1() >= (actualVerticalAngle - 10)) && (f.getAngle1() <= (actualVerticalAngle + 10))
                && (f.getAngle2() >= (actualHorizontalAngle - 10)) && (f.getAngle2() <= (actualHorizontalAngle + 10))) {
                    LatLng lTemp = new LatLng(f.getActualLatitude(), f.getActualLongitude());
                    tomtomMap.addMarker(new MarkerBuilder(lTemp)
                            .icon(Icon.Factory.fromResources(getApplicationContext(), R.drawable.flight_ico))
                            .tag(f.getId())
                            .markerBalloon(new SimpleMarkerBalloon("Id: " + f.getId() + "\n"
                                    + "Icao: " + f.getIcao24bitAddr() + "\n"
                                    + "Reg: " + f.getRegistrationNumber() + "\n"
                                    + "Lat: " + f.getActualLatitude() + "\n"
                                    + "Lon: " + f.getActualLongitude() + "\n"
                                    + "Alt: " + f.getAltitude() + "\n"
                                    + "Speed: " + f.getSpeed() + "\n"
                                    + "From: " + f.getCountryFrom() + "\n"
                                    + "To: " + f.getCountryTo() + "\n"
                                    + "Airline: " + f.getAirLine() + "\n"
                                    + "Angle: " + f.getAngle1() + "\n"
                                    + "Angle2: " + f.getAngle2())));
                }
            }
        }
        flightRadarTask = new JsonTask();
        flightRadarTask.setDelegate(this);
        flightRadarTask.setDeviceLocation(actualLocation);
        flightRadarTask.execute(actualLocation.getLatitude() + 0.5, actualLocation.getLatitude() - 0.5, actualLocation.getLongitude() - 0.5, actualLocation.getLongitude() + 0.5);
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
}

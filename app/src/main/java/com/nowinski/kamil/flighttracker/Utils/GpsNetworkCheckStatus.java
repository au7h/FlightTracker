package com.nowinski.kamil.flighttracker.Utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class GpsNetworkCheckStatus {
    public static boolean isNetworkEnabled(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isGpsEnabled(LocationManager locationManager){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}

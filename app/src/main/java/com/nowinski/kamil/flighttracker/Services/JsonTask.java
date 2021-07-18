package com.nowinski.kamil.flighttracker.Services;

import android.location.Location;
import android.os.AsyncTask;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.nowinski.kamil.flighttracker.Entities.FlightInfo;
import com.nowinski.kamil.flighttracker.Entities.Radar;
import com.nowinski.kamil.flighttracker.Utils.AngleArithm;
import com.nowinski.kamil.flighttracker.Utils.RadarParser;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;

public class JsonTask extends AsyncTask<Double, Void, Radar> {
    private AsyncResponse<Radar> delegate = null;
    private Location deviceLocation = null;

    @Override
    protected Radar doInBackground(Double... params) {
        Double northBoundary = params[0], southBoundary = params[1], westBoundary = params[2], eastBoundary = params[3];
        StringBuilder baseUrl = new StringBuilder().append("https:/data-live.flightradar24.com/zones/fcgi/feed.js?bounds=")
                .append(northBoundary).append(",")
                .append(southBoundary).append(",")
                .append(westBoundary).append(",")
                .append(eastBoundary);
        String USER_AGENT = "Mozilla//5.0 (Windows NT 10.0; WOW64; rv:65.0) Gecko//20100101 Firefox//65.0";
        Connection.Response response;
        Radar radar = null;
        try {
            response = Jsoup.connect(baseUrl.toString())
                    .ignoreContentType(true)
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .timeout(10000)
                    .execute();
            Thread.sleep(5000);
            String json = response.parse().body().text();
            radar = RadarParser.parseJsonToRadarObj(json);

            if(radar != null) {
                List<FlightInfo> flightInfoList = radar.getFlightInfoList();
                for (FlightInfo f : flightInfoList) {
                    long angle1 = AngleArithm.computeAngle(f.getActualLatitude(), f.getActualLongitude(), deviceLocation.getLatitude(), deviceLocation.getLongitude(), f.getAltitude());
                    long angle2 = AngleArithm.angleFromCoordinate(deviceLocation.getLatitude(), deviceLocation.getLongitude(), f.getActualLatitude(), f.getActualLongitude());
                    //angle1 -> vertical, angle2 -> horizontal
                    f.setAngle1(angle1);
                    f.setAngle2(angle2);
                }
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
        return radar;
    }

    @Override
    protected void onPostExecute(Radar result) {
        delegate.processFinish(result);
    }
    public void setDelegate(AsyncResponse<Radar> delegate) { this.delegate = delegate; }
    public void setDeviceLocation(Location deviceLocation){this.deviceLocation = deviceLocation;}
}

package com.nowinski.kamil.flighttracker.Utils;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;

public class AngleArithm {
    static final long FullAngle = 360;

    private static long normalize_angle(long a)
    {
        return a < 0 ? FullAngle + a : a;
    }

    public static Boolean close_angles(long a, long b, long epsilon)
    {
        long diff = normalize_angle(a - b);
        return diff <= epsilon || diff >= FullAngle - epsilon;
    }

    public static long computeAngle(double aircraftLat, double aircraftLon, double deviceLat, double deviceLon, double aircraftAlt) {
        Point flightPoint = new Point(aircraftLon, aircraftLat, 0.0, SpatialReferences.getWgs84());
        Point userPoint = new Point(deviceLon, deviceLat, 0.0, SpatialReferences.getWgs84());
        SpatialReference spatialReference = SpatialReference.create(54002);
        Point flightPointProjected = (Point) GeometryEngine.project(flightPoint, spatialReference);
        Point userPointProjected = (Point) GeometryEngine.project(userPoint, spatialReference);
        return Math.round(Math.toDegrees(Math.atan((aircraftAlt * 0.3048) / GeometryEngine.distanceBetween(flightPointProjected, userPointProjected))));
    }

    public static long angleFromCoordinate(double lat1, double long1, double lat2,
                                       double long2) {
        double dLon = (long2 - long1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);
        double brng = Math.atan2(y, x);
        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        //brng = 360 - brng; // - remove this line to make clockwise
        return Math.round(brng);
    }
}

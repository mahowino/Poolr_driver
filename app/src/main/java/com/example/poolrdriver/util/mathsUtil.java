package com.example.poolrdriver.util;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class mathsUtil {
    public static double getDistanceFromUserPoints(LatLng latLng, LatLng userPoint) {
        //longitudinal difference
        double longDiff=userPoint.longitude-latLng.longitude;
        Log.d("distance", "destinationLatLong: "+latLng);
        Log.d("distance", "sourceLatLong: "+userPoint);

        double distance=Math.sin(deg2rad(latLng.latitude))
                *Math.sin(deg2rad(userPoint.latitude))
                +Math.cos(deg2rad(latLng.latitude))
                *Math.cos(deg2rad(userPoint.latitude))
                *Math.cos(deg2rad(longDiff));

        distance=Math.acos(distance);

        //convert distance into degree
        distance=rad2degree(distance);

        //distance in miles
        distance=distance*60*1.1515;

        //distance in Kilometers

        distance=distance*1.609344;
        Log.d("distance", "getDistanceFromUserPoints: "+distance);
        return distance;
    }

    public static double rad2degree(double distance) {
        return (distance*180.0/Math.PI);
    }

    //convert degree to radians
    public static double deg2rad(double distance) {
        return (distance*Math.PI/180.0);
    }
}


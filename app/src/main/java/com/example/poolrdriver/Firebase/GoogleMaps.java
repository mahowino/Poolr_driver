package com.example.poolrdriver.Firebase;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public abstract class GoogleMaps {
    public static String getUrl(LatLng source, LatLng destination, String mode) {
        String origin="origin="+source.latitude+","+source.longitude;

        String str_destination="destination="+destination.latitude+","+destination.longitude;

        String str_mode="mode="+mode;

        String parameter=origin+"&"+str_destination+"&"+str_mode;

        String format="json";

        String url="https://maps.googleapis.com/maps/api/directions/"
                +format
                +"?"
                +parameter
                +"&key=AIzaSyAJnDBJM8t5SL3tzrSfucXgvYsIAcSZJJE";
        return url;

    }

    public  static LatLng getLocationFromAddress(String strAddress, Context context){

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng((double) (location.getLatitude()),
                    (double) (location.getLongitude()));

            return p1;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
    public void getAddress(double lat, double lng,Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}

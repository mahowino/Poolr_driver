package com.example.poolrdriver.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.models.TimePickerObject;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trips  {

    private int tripPrice, seats;
    private String driverSource, driverDestination, driverUid, privacy;

    DocumentSnapshot snapshot;

    public Trips(DocumentSnapshot snapshot) {
        this.snapshot = snapshot;
    }


    public String getTripUID() {
        return snapshot.getId();
    }

    public Date getDate() {
        return snapshot.getDate(FirebaseFields.DEPARTURETIME);
    }
    public boolean isRidePublic() {
        return Boolean.TRUE.equals(snapshot.getBoolean(FirebaseFields.PRIVACY));
    }

    public Long getPassengerBookingFee(){return snapshot.getLong(FirebaseFields.PASSENGER_BOOKING_FEE);}

    public String getPrivacy() {
        return String.valueOf(snapshot.get(FirebaseFields.PRIVACY));
    }
    public String getLuggage() {
        return String.valueOf(snapshot.get(FirebaseFields.LUGGAGE));
    }

    public int getSeats() {
        return Math.toIntExact(snapshot.getLong(FirebaseFields.SEATS));
    }

    public Long getTripPrice() {
        return snapshot.getLong(FirebaseFields.P_TRIP_PRICE);
    }

    public String getTripID() {
        return snapshot.getId();
    }

    public String getDriverSource() {
        return (String) snapshot.get(FirebaseFields.P_LOCATION_FROM);
    }

    public String getDriverDestination() {
        return (String) snapshot.get(FirebaseFields.P_LOCATION_TO);
    }

    public String getDriverUid() {
        return (String) snapshot.get(FirebaseFields.DRIVER);
    }

    public List<LatLng> getDriverRoute() {

            List<GeoPoint> Route = (List<GeoPoint>) snapshot.getGeoPoint(FirebaseFields.DRIVER_ROUTE);
            List<LatLng> driverRouteList = new ArrayList<>();

            if (Route != null)
                for (GeoPoint point : Route)
                    driverRouteList.add(new com.google.android.gms.maps.model.LatLng(point.getLatitude(), point.getLongitude()));


            return driverRouteList;
    }



}


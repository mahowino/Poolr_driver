package com.example.poolrdriver.classes.other;

import com.example.poolrdriver.Abstract.Constants.FirebaseFields;
import com.example.poolrdriver.classes.models.TimePickerObject;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Trips  {

    private int tripPrice, seats;
    private String driverSource, driverDestination, driverUid, privacy;

    DocumentSnapshot snapshot;

    public Trips(DocumentSnapshot snapshot) {
        this.snapshot = snapshot;
    }


    public String getTripNetwork(){
        return snapshot.getString(FirebaseFields.NETWORK_UID);
    }
    public String getTripUID() {
        return snapshot.getId();
    }

    public Date getDate() {
        return snapshot.getDate(FirebaseFields.DEPARTURETIME);
    }

    public TimePickerObject getTimePickerObjectDate(){
        Date date=getDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int month=cal.get(Calendar.MONTH);
        int year=cal.get(Calendar.YEAR);
        int day=cal.get(Calendar.DAY_OF_MONTH);
        int hour=cal.get(Calendar.HOUR_OF_DAY);
        int minute=cal.get(Calendar.MINUTE);
        return new TimePickerObject(hour,minute,day,month,year);

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

    public GeoPoint getTripStartGeopoint(){
        return snapshot.getGeoPoint(FirebaseFields.LOCATION_FROM_GEOPOINT);
    }
    public GeoPoint getTripEndGeopoint(){
        return snapshot.getGeoPoint(FirebaseFields.LOCATION_TO_GEOPOINT);
    }



}


package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.*;
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.classes.Network;
import com.example.poolrdriver.models.TripModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.GeoPoint;
import com.ncorti.slidetoact.SlideToActView;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class price_split extends AppCompatActivity {

    SlideToActView  post_ride;
    TripModel trip;
    private static final String TRIP_EXTRA="trip";
    private final String documentID=generateRandomId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_split);
        initializeData();
        setUpListeners();

    }

    private void setUpListeners() {
        post_ride.setOnSlideCompleteListener(slideToActView -> postTrip());

    }

    private void postTrip() {
        getSetPrice();

        if (trip.isPrivacy()) postTripOnPublicTrips();
        else postTripOnNetworkTrips();

        redirectActivity(price_split.this,MapsActivity.class);
    }


    private void getSetPrice() {

    }

    private void initializeData() {
        post_ride = findViewById(R.id.post_ride_confirm);
        trip=getIntent().getParcelableExtra(TRIP_EXTRA);
    }
    private void postTripOnPublicTrips(){
        String path= FirebaseConstants.RIDES+"/"+documentID;

        setDocument(getMapData(), createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
               // Toast.makeText(price_split.this, "Your ride has been successfully posted", Toast.LENGTH_SHORT).show();
                getRouteData();
            }

            @Override
            public void onError(Object object) {
                Toast.makeText(price_split.this, "error ", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void postTripRoute(Map map){
        String path= FirebaseConstants.RIDES+"/"+documentID+"/"+FirebaseFields.DRIVER_ROUTE;
        setDocument(map, createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {
                Toast.makeText(price_split.this, "error ", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void postTripOnNetworkTrips() {

        List<Network> networks=trip.getNetworks();
        if (networks==null)postTripOnFirebaseNetwork(trip.getNetworkId());
        else
            for (Network network:networks)
                postTripOnFirebaseNetwork(network.getNetworkUID());
    }

    private void postTripOnFirebaseNetwork(String networkUID) {
        String path= FirebaseConstants.NETWORKS+"/"+networkUID;
        setDocument(getMapData(), createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(price_split.this, "Your ride has been successfully posted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Object object) {
                Toast.makeText(price_split.this, "error ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Map<String,Object> getMapData() {
        //document creation
        Map<String, Object> map = new HashMap<>();
        map.put(FirebaseFields.DRIVER, new User().getUID());
        map.put(FirebaseFields.P_LOCATION_FROM, trip.getDriverSource());
        map.put(FirebaseFields.P_LOCATION_TO,trip.getDriverDestination());

        LatLng source=((LatLng)getIntent().getExtras().get("sourcePoint"));
        LatLng destination=((LatLng)getIntent().getExtras().get("destinationPoint"));

        GeoPoint sourceGeopoint=new GeoPoint(source.latitude,source.longitude);
        GeoPoint destinationGeopoint=new GeoPoint(destination.latitude,destination.longitude);

        map.put(FirebaseFields.LOCATION_TO_GEOPOINT,destinationGeopoint);
        map.put(FirebaseFields.LOCATION_FROM_GEOPOINT,sourceGeopoint);
        map.put(FirebaseFields.SEATS,trip.getSeats());
        map.put(FirebaseFields.P_TRIP_PRICE, trip.getTripPrice());
        map.put(FirebaseFields.PRIVACY,trip.isPrivacy());
        map.put(FirebaseFields.DRIVER,trip.getDriverUid());
        map.put(FirebaseFields.DEPARTURETIME,new Date());

        return map;
    }
    private void getRouteData(){
        List<LatLng> driverRouteList =((PolylineOptions)getIntent().getExtras().get("POLYLINE")).getPoints();

        for (LatLng latLng:driverRouteList){
        Map<String, Object> map = new HashMap<>();
        map.put(FirebaseFields.LATITUDE,latLng.latitude);
        map.put(FirebaseFields.LONGTITUDE,latLng.longitude);
        postTripRoute(map);
        }
        Toast.makeText(price_split.this, "Your ride has been successfully posted", Toast.LENGTH_SHORT).show();
    }

    private String generateRandomId(){
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(20);
        for(int i = 0; i < 20; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
}
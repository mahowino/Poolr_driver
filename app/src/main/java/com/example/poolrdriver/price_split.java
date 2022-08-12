package com.example.poolrdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.classes.Route;
import com.example.poolrdriver.classes.Trips;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.tabs.TabLayout;
import com.ncorti.slidetoact.SlideToActView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class price_split extends AppCompatActivity {

    SlideToActView  post_ride;
    Trips trip;
    private static final String TRIP_EXTRA="trip";

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
        if (trip.isRidePublic()) postTripOnNetworkTrips();
        else postTripOnPublicTrips();
    }


    private void getSetPrice() {

    }

    private void initializeData() {
        post_ride = findViewById(R.id.post_ride_confirm);
        trip=getIntent().getParcelableExtra(TRIP_EXTRA);
    }
    private void postTripOnPublicTrips(){
        String path= FirebaseConstants.RIDES;

        FirebaseRepository.setDocument(getMapData(), FirebaseRepository.createCollectionReference(path), new Callback() {
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
    private void postTripOnNetworkTrips() {
        String path= FirebaseConstants.NETWORKS+"/"+trip.getNetworkID();

        FirebaseRepository.setDocument(getMapData(), FirebaseRepository.createCollectionReference(path), new Callback() {
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
        map.put(FirebaseFields.SEATS,trip.getSeats());
        map.put(FirebaseFields.P_TRIP_PRICE, trip.getTripPrice());
        map.put(FirebaseFields.PRIVACY,trip.isRidePublic());
        map.put(FirebaseFields.DRIVER,trip.getDriverUid());
        map.put(FirebaseFields.DRIVER_ROUTE,trip.getDriverRoute());
        map.put(FirebaseFields.DEPARTURETIME,trip.getDate());

        return map;
    }

}
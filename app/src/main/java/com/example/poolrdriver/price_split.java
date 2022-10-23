package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.*;
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.classes.Network;
import com.example.poolrdriver.models.TimePickerObject;
import com.example.poolrdriver.models.TripModel;
import com.example.poolrdriver.util.mathsUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.tabs.TabLayout;
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
    String Luggage;
    TextView max_amount;
    TabLayout amount_of_luggage;
    EditText priceToPay;
    Long maxPrice;
    TimePickerObject date;
    private static final String TRIP_EXTRA="trip";
    private static final String date_selected="time_picker";

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
        getLuggage();
        getSetPrice();

    }

    private void getLuggage() {
        int tab=amount_of_luggage.getSelectedTabPosition();
        switch (tab){
            case 0:
                Luggage="none";
                break;
            case 1:
                Luggage="small";
                break;
            case 2:
                Luggage="medium";
                break;
            case 3:
                Luggage="large";
                break;
        }

    }


    private void getSetPrice() {
        Long priceInput=Long.valueOf(priceToPay.getText().toString().trim());

        if (priceInput>maxPrice){
            Toast.makeText(getApplicationContext(), "You cannot charge above the legal carpool limit", Toast.LENGTH_SHORT).show();
        }
        else {
            trip.setTripPrice((Long.parseLong(String.valueOf(priceInput))));

            if (trip.isPrivacy()) postTripOnPublicTrips();
            else postTripOnNetworkTrips();

            redirectActivity(price_split.this,MapsActivity.class);
        }

    }

    private void initializeData() {
        post_ride = findViewById(R.id.post_ride_confirm);
        trip=getIntent().getParcelableExtra(TRIP_EXTRA);
        date=getIntent().getParcelableExtra(date_selected);

        max_amount=findViewById(R.id.txt_maximum_to_charge);
        priceToPay=findViewById(R.id.editTextPriceToPay);
        amount_of_luggage=findViewById(R.id.tab_LuggageAllowed);
        double tripDistance= mathsUtil.getDistanceFromUserPoints(trip.getSourcePoint(),trip.getDestinationpoint());
        maxPrice=Math.round(tripDistance*FirebaseConstants.FIXED_RATE_PER_KILOMETER);
        maxPrice=maxPrice/trip.getSeats();
        max_amount.setText("KSH "+maxPrice);
    }
    private void postTripOnPublicTrips(){
        String path= FirebaseConstants.RIDES+"/"+documentID;

        setDocument(getMapData(), createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
               // Toast.makeText(price_split.this, "Your ride has been successfully posted", Toast.LENGTH_SHORT).show();
                Toast.makeText(price_split.this, "Your ride has been successfully posted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Object object) {
                Toast.makeText(price_split.this, "error ", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void postTripOnNetworkTrips() {

        List<String> networkIDS=getIntent().getStringArrayListExtra("networks");
        if (networkIDS==null)postTripOnFirebaseNetwork(trip.getNetworkId());
        else
            for (String networkID:networkIDS)
                postTripOnFirebaseNetwork(networkID);
    }

    private void postTripOnFirebaseNetwork(String networkUID) {
        String path= FirebaseConstants.NETWORKS+"/"+networkUID+"/"+FirebaseConstants.RIDES;
        setDocument(getMapData(), createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

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
        map.put(FirebaseFields.PASSENGER_BOOKING_FEE,Math.ceil(trip.getTripPrice()*FirebaseConstants.FIXED_RATE_PASSENGER_CUT/ trip.getSeats()));
        map.put(FirebaseFields.LUGGAGE,Luggage);
        map.put(FirebaseFields.PRIVACY,trip.isPrivacy());
        map.put(FirebaseFields.DRIVER,trip.getDriverUid());
        map.put(FirebaseFields.DEPARTURETIME, date.getDate(true));

        //when putting price, add passenger booking fee to the trip cost.

        return map;
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
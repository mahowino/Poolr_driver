package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createDocumentReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.deleteDocument;
import static com.example.poolrdriver.Firebase.FirebaseRepository.setDocument;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.Constants.FirebaseConstants;
import com.example.poolrdriver.Firebase.Constants.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.adapters.OngoingTripsAdapter;
import com.example.poolrdriver.models.Requests;
import com.example.poolrdriver.models.TripModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.GeoPoint;
import com.ncorti.slidetoact.SlideToActView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OngoingTrip extends AppCompatActivity {

    ArrayList<String> passengers;
    List<Requests> requests;
    TripModel trip;
    private static final String STARTING_LOCATION = "starting_location";
    TextView source,destination,driverName;
    ImageView imageDriver;
    RecyclerView recyclerView;
    Location currentLocation,endLocation;
    double passengerCut,booking_fee;
    FloatingActionButton panic,trips, finish_trips;
    private final String CHOSEN_TRIP="chosen_trip";
    private final String PASSENGERS="passengers";
    private final String LOCATIONS="locations";
    private  String userDetailsPath;
    private double pricePerPassenger;
    private final String  USER_ACCOUNT="signed_in_user";

    private String walletUid;
    private GeoPoint startLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_trip);
        initializeVariables();
        setListeners();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }



    private void setListeners() {
        panic.setOnClickListener(v -> openSafety());
        //trips.setOnClickListener(v -> openTrips());
        finish_trips.setOnClickListener(v -> closeTrip());

    }


    private void closeTrip() {
        new AlertDialog.Builder(OngoingTrip.this)
                .setTitle("Confirmation")
                .setIcon(R.drawable.icons8_bus_ticket_20px)
                .setMessage("Are you sure you would like to end this trip. This should be after ending trips for every passenger individually")
                .setPositiveButton("yes", (dialog1, which) -> {
                    finishTrip();
                })
                .setNegativeButton("no",(dialog1,which)->{

                })
                .show();

    }

    private void finishTrip() {

        //checkIfTripWas an regular one
        String ongoingTripPath=FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/"+FirebaseConstants.ONGOING_TRIP+"/"+trip.getTripID();
        String booking_on_trip=FirebaseConstants.RIDES+"/"+trip.getTripID();

        deleteFromDatabase(ongoingTripPath);
        deleteFromDatabase(booking_on_trip);
        updateTrips();

        Toast.makeText(this, "Trip successfully ended", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(OngoingTrip.this,MapsActivity.class);
        startActivity(intent);
        finish();
    }

    private void deleteFromDatabase(String path) {
        deleteDocument(createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {

            }
        });
    }
    private void updateTrips() {
        String path= FirebaseConstants.DRIVERS+"/"+new User().getUID()+"/"+FirebaseConstants.PAST_RIDES;
        setDocument(getMapData(),createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {

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
        map.put(FirebaseFields.PASSENGER_BOOKING_FEE,Math.ceil(trip.getTripPrice()*FirebaseConstants.FIXED_RATE_PASSENGER_CUT/ trip.getSeats()));
        map.put(FirebaseFields.PRIVACY,trip.isPrivacy());
        map.put(FirebaseFields.DRIVER,trip.getDriverUid());
        map.put(FirebaseFields.DEPARTURETIME,new Date());


        //when putting price, add passenger booking fee to the trip cost.

        return map;
    }

    private void openTrips() {
        Intent myTrips=new Intent(OngoingTrip.this, reviewActivity.class);

        myTrips.putExtra("chosen_trip",trip);
        startActivity(myTrips);

    }

    private void openSafety() {
        String number="999";
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
        startActivity(callIntent);

    }

    private void initializeVariables() {
        source=findViewById(R.id.TripSource_expanded_ongoing);
        destination=findViewById(R.id.TripDestination_expanded_ongoing);
        //imageDriver=findViewById(R.id.imgDriverProfilePicture_ongoing);
        panic=findViewById(R.id.panic_ongoing);
        finish_trips =findViewById(R.id.finish_trip_btn);
        recyclerView=findViewById(R.id.passengers_ongoing_trip_recyclerView);
        getIntentData();
    }

    private void getIntentData() {
        //HAS TO BE GOTTEN FROM DB;
        trip=getIntent().getExtras().getParcelable(CHOSEN_TRIP);
        Bundle bundle = getIntent().getParcelableExtra(STARTING_LOCATION);
        LatLng tempStartLocation=bundle.getParcelable("start");
        startLocation=new GeoPoint(tempStartLocation.latitude,tempStartLocation.longitude);
        passengers=getIntent().getStringArrayListExtra(PASSENGERS);

        source.setText(trip.getDriverSource());
        destination.setText(trip.getDriverDestination());



        OngoingTripsAdapter adapter=new OngoingTripsAdapter(getApplicationContext(),OngoingTrip.this,passengers,trip);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));

    }
}
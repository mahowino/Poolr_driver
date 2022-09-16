package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createDocumentReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocument;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsInCollection;
import static com.example.poolrdriver.Firebase.FirebaseRepository.setDocument;
import static com.example.poolrdriver.util.AppSystem.getMyDefaultLocation;
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.classes.Passenger;
import com.example.poolrdriver.models.Requests;
import com.example.poolrdriver.models.TripModel;
import com.example.poolrdriver.util.mathsUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
    TextView source,destination,driverName;
    ImageView imageDriver;
    Location currentLocation,endLocation;
    double passengerCut,booking_fee;
    FloatingActionButton panic,trips,passenger_details;
    private final String CHOSEN_TRIP="chosen_trip";
    private final String PASSENGERS="passengers";
    private final String LOCATIONS="locations";
    private  String userDetailsPath;
    private double pricePerPassenger;
    private final String  USER_ACCOUNT="signed_in_user";
    private SlideToActView endTrip;
    private String walletUid;

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
        trips.setOnClickListener(v -> openTrips());
        passenger_details.setOnClickListener(v -> viewPassengers());
        endTrip.setOnSlideCompleteListener(slideToActView -> calculateTripCost());


    }

    private void calculateTripCost() {
        getMyDefaultLocation(this, new Callback() {
            @Override
            public void onSuccess(Object object) {
                endLocation=(Location) object;
                checkIfTripIsComplete();

            }

            @Override
            public void onError(Object object) {}});

    }

    private void checkIfTripIsComplete() {
        LatLng source=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        LatLng tripSource=trip.getSourcePoint();
        LatLng destination=new LatLng(endLocation.getLatitude(),endLocation.getLongitude());
        LatLng tripDestination=trip.getDestinationpoint();
        double distance,price,pricePerSeat;


        if (mathsUtil.getDistanceFromUserPoints(source,tripSource)<1
            && mathsUtil.getDistanceFromUserPoints(destination,tripDestination)<1){
            pricePerSeat=trip.getTripPrice();

        }
        else {
            distance=mathsUtil.getDistanceFromUserPoints(source,destination);
            pricePerSeat=(distance*FirebaseConstants.FIXED_RATE_PER_KILOMETER/trip.getSeats());

        }
        price=pricePerSeat*trip.getSeats();

        chargeBookingFee(price);
    }

    private void chargeBookingFee(double price) {
         booking_fee=price*FirebaseConstants.FIXED_RATE_DRIVER_CUT;
         double driverFee=price-booking_fee;
        booking_fee=booking_fee+trip.getPassengerBookingFee();
        postBookingFee();
        chargePassengerForTrip();
        updateDriversWallet(driverFee);

    }


    private void updateDriversWallet(double price){
        String path=FirebaseConstants.PASSENGERS+"/"+new User().getUID()+ "/"+FirebaseConstants.DRIVER_WALLET;
        getDocumentsInCollection(createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

                Task<QuerySnapshot> task=(Task<QuerySnapshot>) object;
                for (DocumentSnapshot snapshot:task.getResult()){
                    setUpDriverWallet(snapshot.getId(),price);
                }
            }

            @Override
            public void onError(Object object) {
                Log.d(TAG.TAG, "onFailure: failure "+((Exception)object).getMessage());}
        });
    }
    private void setUpDriverWallet(String walletUid,double price) {
        String path=FirebaseConstants.PASSENGERS+"/"+new User().getUID()+ "/"+FirebaseConstants.DRIVER_WALLET+"/"+walletUid;
        setDocument(createWallet(price), createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                redirectActivity(OngoingTrip.this,reviewActivity.class);
                //trip ended successfully
            }

            @Override
            public void onError(Object object) {

            }
        });

    }

    private void postBookingFee() {
        String path=FirebaseConstants.ADMIN+"/"+FirebaseConstants.ROOT_ADMIN_ID+"/"+FirebaseConstants.ADMIN_WALLET;
        String path2=FirebaseConstants.ADMIN+"/"+FirebaseConstants.ROOT_ADMIN_ID+"/"+FirebaseConstants.TRANSACTIONS;

        setDocument(updateAdminWallet(),createDocumentReference(path2), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {

            }
        });

        setDocument(createBookingTransaction(), createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {

            }
        });


    }

    private void chargePassengerForTrip() {
        for (String passenger:passengers) {
            getWalletData(passenger);
            updateTrips(passenger);
        }

    }

    private void updateTrips(String passengerID) {
        String path= FirebaseConstants.PASSENGERS+"/"+passengerID+"/"+FirebaseConstants.PAST_RIDES;
        setDocument(createBookingTransaction(),createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                updateDriverPastTrips();
            }

            @Override
            public void onError(Object object) {

            }
        });

    }
    private void updateDriverPastTrips() {
        String path= FirebaseConstants.DRIVERS+"/"+new User().getUID()+"/"+FirebaseConstants.PAST_RIDES;
        setDocument(createBookingTransaction(),createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {

            }
        });

    }

    private Map updateAdminWallet() {
        Map<String,Object> map=new HashMap<>();
        map.put(FirebaseFields.CASH,booking_fee);
        return map;
    }
    private void getWalletData(String passengerID){
        String path= FirebaseConstants.PASSENGERS+"/"+passengerID+"/"+FirebaseConstants.PASSENGER_WALLET;
        getDocumentsInCollection(createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                String walletUid;
                double wallet_balance;
                Task<QuerySnapshot> task=(Task<QuerySnapshot>) object;
                for (DocumentSnapshot snapshot:task.getResult()){
                    walletUid=snapshot.getId();
                    String cash= String.valueOf(snapshot.get(FirebaseFields.CASH));
                    wallet_balance=Double.valueOf(cash);
                    wallet_balance=wallet_balance-trip.getTripPrice();
                    topUpCash(walletUid,wallet_balance);
                }
            }

            @Override
            public void onError(Object object) {
                Log.d("tag", "onFailure: failure "+((Exception)object).getMessage());}
        });
    }

    private void topUpCash(String walletUid,double newBalance) {
        String path=FirebaseConstants.PASSENGERS+"/"+new User().getUID()+ "/"+FirebaseConstants.PASSENGER_WALLET+"/"+walletUid;
        setDocument(createWallet(newBalance), createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {

            }
        });
    }

    private Map createWallet(double amountToUpdate) {
        Map<String,Object> wallet_update_object=new HashMap<>();

        wallet_update_object.put(FirebaseFields.CASH,amountToUpdate);
        wallet_update_object.put(FirebaseFields.UPDATE_TIME,new Date());
        return wallet_update_object;

    }
    private Map createBookingTransaction() {
        Map<String,Object> map=new HashMap<>();
        map.put(FirebaseFields.TRIP_ID,trip);
        map.put(FirebaseFields.CASH,booking_fee);
        return map;
    }

    private void viewPassengers() {
        Intent myTrips=new Intent(OngoingTrip.this, Passengers.class);

        myTrips.putExtra("chosen_trip",trip);
        startActivity(myTrips);
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
        driverName=findViewById(R.id.driver_name_ongoing);
        imageDriver=findViewById(R.id.imgDriverProfilePicture_ongoing);
        panic=findViewById(R.id.panic_ongoing);
        trips=findViewById(R.id.stops_ongoing);
        passenger_details=findViewById(R.id.passengers_ongoing);
        getIntentData();
    }

    private void getIntentData() {
        trip=getIntent().getExtras().getParcelable(CHOSEN_TRIP);
        passengers=getIntent().getStringArrayListExtra(PASSENGERS);
        endTrip=findViewById(R.id.end_trip_slider);
        source.setText(trip.getDriverSource());
        destination.setText(trip.getDriverDestination());
        driverName.setText(new User().getName());
        Glide.with(OngoingTrip.this).load(new User().getProfilePic()).into(imageDriver);
    }
}
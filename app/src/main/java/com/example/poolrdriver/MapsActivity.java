package com.example.poolrdriver;


import static com.example.poolrdriver.Firebase.FirebaseRepository.createDocumentReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.setDocument;
import static com.example.poolrdriver.My_trips_expanded.STARTING_LOCATION;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.Constants.FirebaseConstants;
import com.example.poolrdriver.Firebase.Constants.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.classes.Trips;
import com.example.poolrdriver.databinding.ActivityMapsBinding;
import com.example.poolrdriver.models.TripModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity{

    private BottomNavigationView bottomNavigationView;
    private CollectionReference collectionReference;
    private final String CHOSEN_TRIP="chosen_trip";
    private final String PASSENGERS="passengers";


    Fragment fragment = null;
    FragmentTransaction fragmentTransaction;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        fragment = new fragment_where_to();
                        switchFragment(fragment);
                        return true;
                    case R.id.nav_schedule:
                        fragment = new more_items();
                        switchFragment(fragment);
                        return true;

                }
                return false;
            };
    private void setFirebaseToken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                   // Timber.tag("app_token").d(token);
                    updateTokenInDatabase(getTokenData(token));

                });
    }


    private Map<String, Object> getTokenData(String token) {
        Map<String,Object> map=new HashMap<>();
        map.put("token",token);
        return map;
    }

    private void updateTokenInDatabase(Map<String,Object> map) {
        String path=FirebaseConstants.DRIVERS+"/"+new User().getUID();
        setDocument(map, createDocumentReference(path), SetOptions.merge(), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            setFirebaseToken();
            String path= FirebaseConstants.PASSENGERS+"/"+user.getUid()+"/"+FirebaseConstants.ONGOING_TRIP;
            collectionReference= FirebaseRepository.createCollectionReference(path);
            collectionReference.addSnapshotListener((value, error) -> {
                if (error != null) {
                    Log.d("error", "Listen failed.", error);
                    return;
                }

                //get Time

                for (DocumentSnapshot snapshot:value){
                    //check how you store transaction time in your database

                    if (snapshot.exists()){
                        boolean isTripOn=snapshot.getBoolean(FirebaseFields.IS_TRIP_ACTIVE);
                        ArrayList<String> passengers;

                        if(isTripOn){
                            passengers= (ArrayList<String>) snapshot.get(FirebaseConstants.PASSENGERS);
                            Intent intent=new Intent(getApplicationContext(),OngoingTrip.class);
                            TripModel trip=setUpTripObject(new Trips(snapshot));
                            intent.putExtra(CHOSEN_TRIP,trip);

                            Bundle startingPoint = new Bundle();
                            startingPoint.putParcelable("start", trip.getSourcePoint());

                            intent.putExtra(STARTING_LOCATION,startingPoint);
                            intent.putStringArrayListExtra(PASSENGERS,passengers);
                            //put extra a trip object from snapshot
                            startActivity(intent);
                        }
                    }



                }


            });

        }
        else{
            
        }


    }
    private TripModel setUpTripObject(Trips trip) {
        TripModel tripModel=new TripModel();
        tripModel.setDriverUid(trip.getDriverUid());
        tripModel.setDriverSource(trip.getDriverSource());
        tripModel.setDriverDestination(trip.getDriverDestination());
        tripModel.setPrivacy(trip.isRidePublic());
        tripModel.setTripPrice(trip.getTripPrice());
        tripModel.setSeats(trip.getSeats());
        tripModel.setTripID(trip.getTripID());
        tripModel.setPassengerBookingFee(trip.getPassengerBookingFee());

        LatLng point=new LatLng(trip.getTripStartGeopoint().getLatitude(),trip.getTripStartGeopoint().getLongitude());
        tripModel.setSourcePoint(point);
        //todo:continue from here
        LatLng destination=new LatLng(trip.getTripEndGeopoint().getLatitude(),trip.getTripEndGeopoint().getLongitude());
        tripModel.setDestinationpoint(destination);
        // tripModel.setDestinationpoint(trip.getTripDestinationGeopoint());



        return tripModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.poolrdriver.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigationView= findViewById(R.id.main_bottomNavigation_switcher);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);


    }
    private void switchFragment(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.where_to, fragment);
        fragmentTransaction.commit();
    }
}

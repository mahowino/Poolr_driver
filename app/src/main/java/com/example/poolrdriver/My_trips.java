package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.*;

import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.adapters.MyTripsAdapter;
import com.example.poolrdriver.classes.Network;
import com.example.poolrdriver.classes.Trips;
import com.example.poolrdriver.models.TripModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class My_trips extends AppCompatActivity {
    RecyclerView upcomingTrips;
    private List<TripModel> tripsList;
    private TextView viewPastTrips;
    private String network_trips_path,public_trips_path;
    private List<Network> networks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);
        initializeVariables();
        setListeners();
        getPostsDataFromDriver();

    }

    private void setListeners() {
        viewPastTrips.setOnClickListener(v -> redirectActivity(My_trips.this,PastTrips.class));
    }

    private void initializeVariables() {
        //initializations
        tripsList=new ArrayList<>();
        networks=new ArrayList<>();
        upcomingTrips=findViewById(R.id.upcomingtripsRecyclerView);
        viewPastTrips=findViewById(R.id.lnk_view_past_trips);
        getUserNetworks();

        //public trips
        public_trips_path=FirebaseConstants.RIDES;

        //network trips
        network_trips_path=FirebaseConstants.NETWORKS;
    }
    private void getUserNetworks() {
        String path= FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/"+FirebaseConstants.NETWORKS;
        getDocumentsInCollection(createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                QuerySnapshot snapshot=((Task<QuerySnapshot>) object).getResult();

                getNetworks(snapshot);

            }

            @Override
            public void onError(Object object) {
                Toast.makeText(My_trips.this, "error ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getNetworks(QuerySnapshot querysnapshot) {

        for (DocumentSnapshot snapshot:querysnapshot){
            Network network=new Network();
            network.setNetworkName(String.valueOf(snapshot.get(FirebaseFields.NETWORK_NAME)));
            network.setNetworkUID(String.valueOf(snapshot.get(FirebaseFields.NETWORK_UID)));
            network.setNetworkTravelAdminUID(String.valueOf(snapshot.get(FirebaseFields.NETWORK_TRAVEL_ADMIN)));
            networks.add(network);
        }





    }
    private void getPostsDataFromDriver() {
        //get user networks



        getDocumentsFromQueryInCollection(createQuery(createCollectionReference(network_trips_path),FirebaseFields.DRIVER,new User().getUID()), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<QuerySnapshot> task=(Task<QuerySnapshot>) object;
                if(task.isSuccessful()) {
                    for (DocumentSnapshot snapshot:task.getResult()) {
                        Log.d("tag", "onSuccess: "+snapshot.get(FirebaseFields.DRIVER).toString());
                        tripsList.add(setUpTripObject(new Trips(snapshot)));
                    }
                    getPublicTripsFromDatabase();
                }
            }

            @Override
            public void onError(Object object) {Log.d(TAG.TAG, "onFailure: "+((Exception)object).getMessage());}
        });

    }

    private void getPublicTripsFromDatabase() {
        Log.d("tag", "onSuccess: "+new User().getUID());
        getDocumentsFromQueryInCollection(createQuery(createCollectionReference(public_trips_path),FirebaseFields.DRIVER,new User().getUID()), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<QuerySnapshot> task=(Task<QuerySnapshot>) object;
                if(task.isSuccessful()) {
                    for (DocumentSnapshot snapshot:task.getResult()) {
                        Log.d("tag", "onSuccess: "+snapshot.get(FirebaseFields.DRIVER).toString());
                        tripsList.add(setUpTripObject(new Trips(snapshot)));
                    }
                    initializeRecyclerView();
                }
            }

            @Override
            public void onError(Object object) {Log.d(TAG.TAG, "onFailure: "+((Exception)object).getMessage());}
        });
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
        tripModel.setLuggage(trip.getLuggage());
        LatLng point=new LatLng(trip.getTripStartGeopoint().getLatitude(),trip.getTripStartGeopoint().getLongitude());
        tripModel.setSourcePoint(point);
        LatLng destination=new LatLng(trip.getTripEndGeopoint().getLatitude(),trip.getTripEndGeopoint().getLongitude());
        tripModel.setDestinationpoint(destination);
        tripModel.setTimePickerObject(trip.getTimePickerObjectDate());

        return tripModel;
    }


    private void initializeRecyclerView() {

        MyTripsAdapter adapter=new MyTripsAdapter(tripsList,getApplicationContext(),My_trips.this);
        upcomingTrips.setAdapter(adapter);
        upcomingTrips.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));

    }
}
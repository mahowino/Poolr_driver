package com.example.poolrdriver.ui.activities.other;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createQuery;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsFromQueryInCollection;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsInCollection;
import static com.example.poolrdriver.ui.activities.other.My_trips.FROM_NETWORK;
import static com.example.poolrdriver.ui.activities.other.onLocationPressedActivity.CAR_FOR_TRIP;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.poolrdriver.Abstract.Callback;
import com.example.poolrdriver.Abstract.FirebaseConstants;
import com.example.poolrdriver.Abstract.Constants.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.R;
import com.example.poolrdriver.adapters.MyTripsAdapter;
import com.example.poolrdriver.classes.other.Trips;
import com.example.poolrdriver.classes.models.CarTypes;
import com.example.poolrdriver.classes.models.Network;
import com.example.poolrdriver.classes.models.TripModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyRoutes extends AppCompatActivity {
    List<TripModel> tripsList;
    List<Network> networks;
    String public_trips_path,network_trips_path;
    RecyclerView routesRecyclerView;
    FloatingActionButton addRoute;
    CarTypes chosenCarForTrip;
    private int counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_routes);
        initializeVariables();

        setListeners();
    }

    private void setListeners() {
        addRoute.setOnClickListener(view -> {
            Intent intent=new Intent(getApplicationContext(),onLocationPressedActivity.class);
            intent.putExtra(CAR_FOR_TRIP,chosenCarForTrip);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
        });
    }

    private void initializeVariables() {
        //initializations
        tripsList=new ArrayList<>();
        networks=new ArrayList<>();
        routesRecyclerView=findViewById(R.id.upcomingRoutesRecyclerView);
        addRoute=findViewById(R.id.btnAddRoute);

        getUserNetworks();


        //public trips
        public_trips_path= FirebaseConstants.ROUTES;
        chosenCarForTrip=getIntent().getParcelableExtra(CAR_FOR_TRIP);



    }

    private void getUserNetworks() {
        String path= FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/"+FirebaseConstants.NETWORKS;
        getDocumentsInCollection(createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                QuerySnapshot snapshot=((Task<QuerySnapshot>) object).getResult();

                Log.d("path", "onSuccess: "+path);
                getNetworks(snapshot);

            }

            @Override
            public void onError(Object object) {
                Toast.makeText(MyRoutes.this, "error ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getNetworks(QuerySnapshot querysnapshot) {
        for (DocumentSnapshot snapshot:querysnapshot){
            Network network=new Network();
            network.setNetworkName(String.valueOf(snapshot.get(FirebaseFields.NETWORK_NAME)));
            network.setNetworkUID(snapshot.getId());
            network.setNetworkTravelAdminUID(String.valueOf(snapshot.get(FirebaseFields.NETWORK_TRAVEL_ADMIN)));
            Log.d("path", "onSuccess: "+network.getNetworkUID());
            networks.add(network);
        }
        getPostsDataFromDriver();
    }
    private void getPostsDataFromDriver() {
        //get user networks
        //get networks
        if (networks.size()>0){
            counter=0;
            for (Network network:networks)
                getTripsFromNetwork(network.getNetworkUID());
            Log.d("path", "onSuccess: "+counter);
        }
        else if (!getIntent().getBooleanExtra(FROM_NETWORK,false))
            getPublicTripsFromDatabase();
        else initializeRecyclerView();

    }

    private void getTripsFromNetwork(String networkID) {
        String path=FirebaseConstants.NETWORKS+"/"+networkID+"/"+FirebaseConstants.ROUTES;

        getDocumentsFromQueryInCollection(createQuery(createCollectionReference(path),FirebaseFields.DRIVER,new User().getUID()), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<QuerySnapshot> task=(Task<QuerySnapshot>) object;
                if(task.isSuccessful()) {
                    counter++;
                    for (DocumentSnapshot snapshot:task.getResult()) {
                        Log.d("tag", "onSuccess: ggg"+snapshot.get(FirebaseFields.DRIVER).toString());
                        tripsList.add(setUpTripObject(new Trips(snapshot)));
                    }

                    if (networks.size()==counter){
                        if (!getIntent().getBooleanExtra(FROM_NETWORK,false))
                            getPublicTripsFromDatabase();
                        else  initializeRecyclerView();
                    }

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

        if (!tripModel.isPrivacy())
            tripModel.setNetworkId(trip.getTripNetwork());
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

        MyTripsAdapter adapter=new MyTripsAdapter(tripsList,getApplicationContext(),MyRoutes.this,true);
        routesRecyclerView.setAdapter(adapter);
        routesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));

    }
}
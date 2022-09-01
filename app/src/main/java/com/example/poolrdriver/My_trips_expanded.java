package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.adapters.PassengersAdapter;
import com.example.poolrdriver.classes.Passenger;
import com.example.poolrdriver.classes.Trips;
import com.example.poolrdriver.models.TripModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class My_trips_expanded extends AppCompatActivity {
    TextView source,destination,departure_time,privacy,no_of_passengers,no_of_seats_offered,cash_to_be_paid,no_of_requests,no_passengers_booked;
    Button btnRequests;
    TripModel trip;
    User user;

    List<Passenger> passengers;
    private final String CHOSEN_TRIP="chosen_trip";
    private  String userDetailsPath;
    private final String  USER_ACCOUNT="signed_in_user";
    private QuerySnapshot snapshot;
    RecyclerView passengersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips_expanded);
        initializeVariables();
        setListeners();
        getBookedPassengers();

    }

    private void getBookedPassengers() {
        FirebaseRepository.getDocumentsInCollection(createCollectionReference(userDetailsPath), new Callback() {
            @Override
            public void onSuccess(Object object) {
                snapshot=((Task<QuerySnapshot>) object).getResult();
                if (snapshot!=null)
                    displayPassengers();
                else
                    displayNoPassengers();
            }

            @Override
            public void onError(Object object) {}
        });

    }

    private void displayPassengers() {
        if(arePassengersAvailable())displayPassengersAdapter();
        else displayNoPassengers();
    }

    private void displayNoPassengers() {
        passengersList.setVisibility(View.INVISIBLE);
        no_passengers_booked.setVisibility(View.VISIBLE);
        no_of_passengers.setText("none");


    }

    private void displayPassengersAdapter() {
        passengersList.setVisibility(View.VISIBLE);
        no_passengers_booked.setVisibility(View.INVISIBLE);
        displayAdapter();

    }

    private void displayAdapter() {
        PassengersAdapter adapter=new PassengersAdapter(this,passengers);
        passengersList.setAdapter(adapter);
        passengersList.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
        no_of_passengers.setText(passengers.size());
    }

    private boolean arePassengersAvailable() {
        for (DocumentSnapshot snapshot:snapshot)
           passengers.add(new Passenger(snapshot));
        return passengers.size() > 0;
    }

    private void setListeners() {
        //btnRequests.setOnClickListener(v -> );
    }

    private void setTextData() {
        source.setText(trip.getDriverSource());
        destination.setText(trip.getDriverDestination());
        departure_time.setText(SimpleDateFormat.getInstance().format(new Date()));//trip.getTimePickerObject().getDate()
        privacy.setText("trip.isPrivacy()");
        no_of_seats_offered.setText(String.valueOf(trip.getSeats()));
        cash_to_be_paid.setText(String.valueOf(trip.getTripPrice()));
        //no_of_requests.setText(trip.getNumberOfRequests());

    }

    private void initializeVariables() {
        linkViews();
        getValues();
        setTextData();


    }

    private void linkViews() {
        source=findViewById(R.id.TripSource_expanded);
        destination=findViewById(R.id.TripDestination_expanded);
        departure_time=findViewById(R.id.Trip_time_expanded);
        privacy=findViewById(R.id.privacy_setting_expanded);
        no_of_passengers=findViewById(R.id.passengers_no__expanded);
        no_of_seats_offered=findViewById(R.id.seats_booked_expanded);
        cash_to_be_paid=findViewById(R.id.CashPaid_expanded);
        btnRequests=findViewById(R.id.btn_viewRequests);
        no_of_requests=findViewById(R.id.requests_no_expanded);
        passengersList=findViewById(R.id.passengers_list);
        no_passengers_booked=findViewById(R.id.no_passengers_booked_label);
        passengers=new ArrayList<>();
    }

    private void getValues() {
        trip=getIntent().getParcelableExtra(CHOSEN_TRIP);
        user=getIntent().getParcelableExtra(USER_ACCOUNT);

        if (trip.isPrivacy())
            userDetailsPath= FirebaseConstants.NETWORKS+"/"+trip.getNetworkId()+"/"+FirebaseConstants.RIDES+"/"+trip.getTripID()+"/"+FirebaseConstants.BOOKINGS;
        else
            userDetailsPath= FirebaseConstants.RIDES+"/"+trip.getTripID()+"/"+FirebaseConstants.BOOKINGS;

    }
}
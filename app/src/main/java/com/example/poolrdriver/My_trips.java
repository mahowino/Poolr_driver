package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.*;

import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.adapters.MyTripsAdapter;
import com.example.poolrdriver.classes.Notifications;
import com.example.poolrdriver.classes.SignedUpDriver;
import com.example.poolrdriver.classes.Trips;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class My_trips extends AppCompatActivity {
    RecyclerView upcomingTrips;
    private List<Trips> tripsList;
    private TextView viewPastTrips;


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
        upcomingTrips=findViewById(R.id.upcomingtripsRecyclerView);
        viewPastTrips=findViewById(R.id.lnk_view_past_trips);
    }

    private void getPostsDataFromDriver() {
        String path=FirebaseConstants.PASSENGERS+"/"+new User().getUID() +"/"+FirebaseConstants.POSTED_RIDES;

        getDocumentsInCollection(createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<QuerySnapshot> task=(Task<QuerySnapshot>) object;
                if(task.isSuccessful()) {
                    for (DocumentSnapshot snapshot:task.getResult())
                        tripsList.add(snapshot.toObject(Trips.class));
                    initializeRecyclerView();
                }
            }

            @Override
            public void onError(Object object) {Log.d(TAG.TAG, "onFailure: "+((Exception)object).getMessage());}
        });

    }


    private void initializeRecyclerView() {

        MyTripsAdapter adapter=new MyTripsAdapter(tripsList,getApplicationContext());
        upcomingTrips.setAdapter(adapter);
        upcomingTrips.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));

    }
}
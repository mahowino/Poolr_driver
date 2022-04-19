package com.example.poolrdriver;

import static com.example.poolrdriver.SignUpScreen.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.adapters.MyTripsAdapter;
import com.example.poolrdriver.adapters.NotificationsAdapter;
import com.example.poolrdriver.classes.Notifications;
import com.example.poolrdriver.classes.SignedUpDriver;
import com.example.poolrdriver.classes.Trips;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class My_trips extends AppCompatActivity {
    RecyclerView upcomingTrips,notificationsView;
    private List<Trips> tripsList;
    private List<Notifications> notifications;
    private FirebaseFirestore db;
    private TextView viewPastTrips;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);

        //initializations
        tripsList=new ArrayList<>();
        notifications=new ArrayList<>();
        db=FirebaseFirestore.getInstance();
        upcomingTrips=findViewById(R.id.upcomingtripsRecyclerView);
        viewPastTrips=findViewById(R.id.lnk_view_past_trips);



        //onclick
        viewPastTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getApplicationContext(),PastTrips.class);
                startActivity(intent);
            }
        });

        getPostsDataFromDriver();

        //test declarations
        //todo: remove declarations with actual data

        tripsList.add(new Trips("Qwetu students residence","Kenyatta University","my location","KM Market",300,R.drawable.profile_pic_example,"Mahalon"));
        tripsList.add(new Trips("Qwetu students residence","Kenyatta ","my location","KM Market",7500,R.drawable.profile_second_choice,"James"));
        tripsList.add(new Trips("Qwetu students residence"," University","my location","KM Market",200,R.drawable.profile_pic_example,"Samuel"));



    }

    private void getPostsDataFromDriver() {
        db.collection(FirebaseConstants.DRIVERS)
                .document(SignedUpDriver.getUsername())
                .collection(FirebaseConstants.POSTED_RIDES)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        //get information from posted ride

                        if(task.isSuccessful()) {
                            String locationFrom, locationTo;
                            Long cash;
                            Timestamp time;

                            for (DocumentSnapshot snapshot:task.getResult()){

                                /** todo: get number of passengers booked from accepted passengers
                                 * todo: get number of requests
                                 */

                                locationFrom=snapshot.getString(FirebaseFields.P_LOCATION_FROM);
                                locationTo=snapshot.getString(FirebaseFields.P_LOCATION_TO);
                                cash=snapshot.getLong(FirebaseFields.CASH);
                                time=snapshot.getTimestamp(FirebaseFields.DEPARTURETIME);

                            }

                            //set up recycler

                            initializeRecyclerView();


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getMessage());
                        Toast.makeText(getApplicationContext(),"error accessing driver posts",Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void initializeRecyclerView() {

        //RecyclerView initializations
        //todo: declare adapter well
        MyTripsAdapter adapter=new MyTripsAdapter(tripsList,getApplicationContext());
        upcomingTrips.setAdapter(adapter);
        upcomingTrips.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false));

    }
}
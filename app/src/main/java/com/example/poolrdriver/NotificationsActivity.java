package com.example.poolrdriver;

import static com.example.poolrdriver.SignUpScreen.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    RecyclerView notificationsView;
    private List<com.example.poolrdriver.classes.Notifications> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        //initialization
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        notifications=new ArrayList<>();
        notifications=findViewById(R.id.notifications_recycler);


        getNotificationFromFirebase();
    }

    private void getSpecificRequests() {

        db.collection(FirebaseConstants.DRIVERS)
                .document(mAuth.getCurrentUser().getUid())
                .collection(FirebaseConstants.REQUESTS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()) {
                            String locationFrom, locationTo, passengerName;
                            Timestamp time;
                            boolean tripAcceptanceStatus;

                            for (DocumentSnapshot snapshot:task.getResult()){

                                locationFrom=snapshot.getString(FirebaseFields.P_LOCATION_FROM);
                                locationTo=snapshot.getString(FirebaseFields.P_LOCATION_TO);
                                passengerName=snapshot.getString(FirebaseFields.P_NAME);
                                time=snapshot.getTimestamp(FirebaseFields.DEPARTURETIME);
                                tripAcceptanceStatus=snapshot.getBoolean(FirebaseFields.STATUS);

                            }



                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getMessage());
                    }
                });
    }

    private void getNotificationFromFirebase() {
        db.collection(FirebaseConstants.DRIVERS)
                .document(SignedUpDriver.getUsername())
                .collection(FirebaseConstants.NOTIFICATIONS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot snapshot:task.getResult()){
                                String message=snapshot.getString(FirebaseFields.MESSAGE);
                                int type=Integer.parseInt(snapshot.get(FirebaseFields.TYPE).toString());

                                com.example.poolrdriver.classes.Notifications notification=new com.example.poolrdriver.classes.Notifications(message,type);

                                //if notification requires picture, get from database
                                if (snapshot.getString(FirebaseFields.IMAGE_URI)!=null){

                                    //parse the URI
                                    String uri=snapshot.getString(FirebaseFields.IMAGE_URI);
                                    Uri imageUri=Uri.parse(uri);
                                    notification.setImageUri(imageUri);
                                }

                                notifications.add(notification);

                                initializeRecyclerView();
                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailure: "+e.getMessage());
                        Toast.makeText(getApplicationContext(),"error getting notifications",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initializeRecyclerView() {

        //RecyclerView initializations
        NotificationsAdapter adapter=new NotificationsAdapter(getApplicationContext(),notifications);
        notificationsView.setAdapter(adapter);
        notificationsView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }
}
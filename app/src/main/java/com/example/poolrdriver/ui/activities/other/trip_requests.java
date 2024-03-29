package com.example.poolrdriver.ui.activities.other;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsInCollection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Abstract.Callback;
import com.example.poolrdriver.Abstract.FirebaseConstants;
import com.example.poolrdriver.Abstract.Constants.FirebaseFields;
import com.example.poolrdriver.R;
import com.example.poolrdriver.adapters.RequestsAdapter;
import com.example.poolrdriver.classes.models.Requests;
import com.example.poolrdriver.classes.models.TripModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class trip_requests extends AppCompatActivity {

    RecyclerView requestsView;
    private List<Requests> tripRequest;
    TripModel trip;
    ImageView no_requests_image;
    TextView no_requests_text;
    boolean is_request_available;
    boolean isNetworkTrip;
    String tripId;
    private final String CHOSEN_TRIP="chosen_trip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_requests);
        initializeVariables();
        getRequestsFromFirebase(isNetworkTrip);

    }

    private void initializeVariables() {
        //initialization
        tripRequest=new ArrayList<>();
        requestsView=findViewById(R.id.recyclerViewRequests);
        no_requests_image=findViewById(R.id.no_requests_image);
        no_requests_text=findViewById(R.id.no_requests_text);
        is_request_available=false;
        trip=getIntent().getParcelableExtra(CHOSEN_TRIP);
        Log.d("rides", "initializeVariables: "+trip.getTripID());
        isNetworkTrip=getIntent().getBooleanExtra("isNetworkTrip",false);
    }


    private void getRequestsFromFirebase(boolean isNetworkTrip) {
        //query
        String path;
        if (!isNetworkTrip)
            path= FirebaseConstants.RIDES+"/"+trip.getTripID()+"/"+FirebaseConstants.REQUESTS;

        else {
            //get from network
            String networkId=getIntent().getExtras().getString("networkID");
            path= FirebaseConstants.NETWORKS+"/"+networkId+"/"+FirebaseConstants.RIDES+"/"+trip.getTripID()+"/"+FirebaseConstants.REQUESTS;

        }
        getTrip(path);
    }

    private void getTrip(String path) {
        getDocumentsInCollection(createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<QuerySnapshot> task=(Task<QuerySnapshot>)object;
                for (DocumentSnapshot snapshot:task.getResult())

                    if (snapshot.exists()){createNotification(snapshot);is_request_available=true;}

                if (is_request_available)initializeRecyclerView();
                else initializeNoNotificationView();

            }

            @Override
            public void onError(Object object) {
                Log.d(TAG.TAG, "onFailure: "+((Exception)object).getMessage());
                Toast.makeText(getApplicationContext(),"error getting notifications",Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("rides", "initializeVariables: "+path);
    }

    private void initializeNoNotificationView() {
        no_requests_image.setVisibility(View.VISIBLE);
        no_requests_text.setVisibility(View.VISIBLE);
        requestsView.setVisibility(View.INVISIBLE);
    }

    private void createNotification(DocumentSnapshot snapshot) {

        Requests requests=new Requests();

        //trip date can be gotten from trip object
        //requests.setTripDate(trip.getTimePickerObject().getDate());
        requests.setTripPrice(Double.parseDouble(String.valueOf(snapshot.get(FirebaseFields.P_TRIP_PRICE))));
        requests.setLocationFrom(String.valueOf(snapshot.get(FirebaseFields.P_LOCATION_FROM)));
        requests.setLocationTo(String.valueOf(snapshot.get(FirebaseFields.P_LOCATION_TO)));
        requests.setSeats(Integer.parseInt(String.valueOf(snapshot.get(FirebaseFields.SEATS))));
        requests.setPassengerUID(String.valueOf(snapshot.get(FirebaseConstants.PASSENGERS)));
        requests.setTripUID(String.valueOf(snapshot.get(FirebaseFields.TRIP_ID)));
        requests.setSourceGeopoint(snapshot.getGeoPoint(FirebaseFields.LOCATION_FROM_GEOPOINT));
        requests.setDestinationGeopoint(snapshot.getGeoPoint(FirebaseFields.LOCATION_TO_GEOPOINT));
        requests.setRequestID(snapshot.getId());

        tripRequest.add(requests);

    }

    private void initializeRecyclerView() {
        no_requests_image.setVisibility(View.INVISIBLE);
        no_requests_text.setVisibility(View.INVISIBLE);
        requestsView.setVisibility(View.VISIBLE);

        //RecyclerView initializations
        RequestsAdapter adapter=new RequestsAdapter(trip_requests.this,tripRequest);
        requestsView.setAdapter(adapter);
        requestsView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }
}
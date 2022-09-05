package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsInCollection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.adapters.NotificationsAdapter;
import com.example.poolrdriver.adapters.RequestsAdapter;
import com.example.poolrdriver.classes.Notifications;
import com.example.poolrdriver.databinding.RequestCardBinding;
import com.example.poolrdriver.models.Requests;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class trip_requests extends AppCompatActivity {

    RecyclerView requestsView;
    private List<Requests> tripRequest;
    ImageView no_requests_image;
    TextView no_requests_text;
    boolean is_request_available;
    boolean isNetworkTrip;
    String tripId;

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
        tripId=getIntent().getExtras().getString("tridID");
        isNetworkTrip=getIntent().getBooleanExtra("isNetworkTrip",false);
    }


    private void getRequestsFromFirebase(boolean isNetworkTrip) {
        //query
        String path;
        if (isNetworkTrip)
            path= FirebaseConstants.RIDES+"/"+tripId+"/"+FirebaseConstants.REQUESTS;
        else {
            //get from network
            String networkId=getIntent().getExtras().getString("networkID");
            path= FirebaseConstants.NETWORKS+"/"+networkId+"/"+FirebaseConstants.RIDES+"/"+tripId+"/"+FirebaseConstants.REQUESTS;
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

    }

    private void initializeNoNotificationView() {
        no_requests_image.setVisibility(View.VISIBLE);
        no_requests_text.setVisibility(View.VISIBLE);
        requestsView.setVisibility(View.INVISIBLE);
    }

    private void createNotification(DocumentSnapshot snapshot) {

        Requests requests=new Requests();

        requests.setTripPrice(Double.parseDouble(String.valueOf(snapshot.get(FirebaseFields.P_TRIP_PRICE))));
        requests.setLocationFrom(String.valueOf(snapshot.get(FirebaseFields.P_LOCATION_FROM)));
        requests.setLocationTo(String.valueOf(snapshot.get(FirebaseFields.P_LOCATION_TO)));
        requests.setSeats(Integer.parseInt(String.valueOf(snapshot.get(FirebaseFields.SEATS))));
        requests.setPassengerUID(String.valueOf(snapshot.get(FirebaseFields.DRIVER)));
        requests.setTripUID(snapshot.getId());

        tripRequest.add(requests);

    }

    private void initializeRecyclerView() {
        no_requests_image.setVisibility(View.INVISIBLE);
        no_requests_text.setVisibility(View.INVISIBLE);
        requestsView.setVisibility(View.VISIBLE);

        //RecyclerView initializations
        RequestsAdapter adapter=new RequestsAdapter(getApplicationContext(),tripRequest);
        requestsView.setAdapter(adapter);
        requestsView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }
}
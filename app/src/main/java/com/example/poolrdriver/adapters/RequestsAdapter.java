package com.example.poolrdriver.adapters;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createDocumentReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocument;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsInCollection;
import static com.example.poolrdriver.Firebase.FirebaseRepository.setDocument;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.My_trips;
import com.example.poolrdriver.R;
import com.example.poolrdriver.TAG;
import com.example.poolrdriver.classes.Passenger;
import com.example.poolrdriver.models.Requests;
import com.example.poolrdriver.price_split;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.HolderView> {
    Context context;
    List<Requests> requests;


    public RequestsAdapter(Context context, List<Requests> requests) {
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public RequestsAdapter.HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestsAdapter.HolderView(LayoutInflater.from(context).inflate(R.layout.request_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsAdapter.HolderView holder, int position) {
        Requests request=requests.get(position);
        setPassengerInformationFromDatabase(request,holder);
        setTexts(request,holder);
        setListener(request,holder);
    }

    private void setListener(Requests requests, RequestsAdapter.HolderView holder) {
        holder.btnAcceptPassenger.setOnClickListener(v -> acceptPassengerRequest(requests));
        holder.btnRejectPassenger.setOnClickListener(v -> rejectPassengerRequest(requests));
    }

    private void setTexts(Requests requests, RequestsAdapter.HolderView holderView) {

        holderView.passengerSource.setText(requests.getPassengerUID());
        holderView.passengerDestination.setText(requests.getPassengerUID());
    }
    private void setPassengerInformationFromDatabase(Requests requests, RequestsAdapter.HolderView holderView){
        String path= FirebaseConstants.PASSENGERS+"/"+requests.getPassengerUID();
        getDocument(createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<QuerySnapshot> task=(Task<QuerySnapshot>)object;
                for (DocumentSnapshot snapshot:task.getResult()){
                    //get text information from the database
                    holderView.passengerName.setText(String.valueOf(snapshot.get(FirebaseFields.FULL_NAMES)));
                    holderView.passengerRating.setText(String.valueOf(snapshot.get(FirebaseFields.RATING)));

                }

            }

            @Override
            public void onError(Object object) {
                Log.d(TAG.TAG, "onFailure: "+((Exception)object).getMessage());
                Toast.makeText(context,"error getting requests",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void acceptPassengerRequest(Requests requests) {
    }

    private void rejectPassengerRequest(Requests requests) {
    }

    private void acceptRequestOnDatabase(Requests requests) {
       String userDetailsPath= FirebaseConstants.RIDES+"/"+requests.getTripUID()+"/"+FirebaseConstants.BOOKINGS;
        setDocument(getMapData(requests), createCollectionReference(userDetailsPath), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(context, "Passenger has been successfully accepted", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context, My_trips.class);
                context.startActivity(intent);
                //delete record

                //show my trips expanded

            }

            @Override
            public void onError(Object object) {
                Toast.makeText(context, "error ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Map getMapData(Requests requests) {
        Map<String,Object> map=new HashMap<>();
        map.put(FirebaseConstants.PASSENGERS, requests.getPassengerUID());
        map.put(FirebaseFields.P_LOCATION_FROM, requests.getUserSource());
        map.put(FirebaseFields.P_LOCATION_TO,requests.getUserDestination());
        map.put(FirebaseFields.SEATS,requests.getSeats());
        map.put(FirebaseFields.P_TRIP_PRICE, requests.getTripPrice());


        return map;
    }


    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class HolderView extends RecyclerView.ViewHolder {
        TextView passengerName, passengerRating, passengerSource, passengerDestination,price;
        ImageView passengerDisplayPicture;
        Button btnAcceptPassenger;
        FloatingActionButton btnRejectPassenger;
       // CardView passengerCard;

        public HolderView(@NonNull View itemView) {
            super(itemView);
            initializeViews(itemView);
        }

        private void initializeViews(View itemView) {
            passengerName = itemView.findViewById(R.id.TripPassengerNameRequests);
            passengerRating = itemView.findViewById(R.id.ratingRequests);
            passengerSource = itemView.findViewById(R.id.TripSourceRequests);
            passengerDestination = itemView.findViewById(R.id.TripDestinationRequests);
            passengerDisplayPicture = itemView.findViewById(R.id.TripProfilePictureRequests);
            btnAcceptPassenger = itemView.findViewById(R.id.btn_book_public_rideRequests);
            price= itemView.findViewById(R.id.tripPriceRequests);
           // passengerCard = itemView.findViewById(R.id.call_card);
        }
    }
}

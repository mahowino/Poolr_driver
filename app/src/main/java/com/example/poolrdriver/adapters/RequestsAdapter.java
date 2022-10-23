package com.example.poolrdriver.adapters;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createDocumentReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.deleteDocument;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocument;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsInCollection;
import static com.example.poolrdriver.Firebase.FirebaseRepository.setDocument;
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import android.app.AlertDialog;
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
import com.example.poolrdriver.classes.Notifications;
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
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.request_card,parent,false);
        return new RequestsAdapter.HolderView(view);
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

        holderView.passengerSource.setText(requests.getLocationFrom());
        holderView.passengerDestination.setText(requests.getLocationTo());
    }
    private void setPassengerInformationFromDatabase(Requests requests, RequestsAdapter.HolderView holderView){
        String path= FirebaseConstants.PASSENGERS+"/"+requests.getPassengerUID();
        getDocument(createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<DocumentSnapshot> task=(Task<DocumentSnapshot>)object;
                DocumentSnapshot snapshot=task.getResult();
                    //get text information from the database
                    holderView.passengerName.setText(String.valueOf(snapshot.get(FirebaseFields.FULL_NAMES)));
                    holderView.passengerRating.setText(String.valueOf(snapshot.get(FirebaseFields.RATING)));



            }

            @Override
            public void onError(Object object) {
                Log.d(TAG.TAG, "onFailure: "+((Exception)object).getMessage());
                Toast.makeText(context,"error getting requests",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void acceptPassengerRequest(Requests requests) {
        new AlertDialog.Builder(context)
                .setTitle("Confirmation")
                .setIcon(R.drawable.icons8_bus_ticket_20px)
                .setMessage("Are you sure you would like to request this ride?")
                .setPositiveButton("yes", (dialog1, which) -> {
                    //what of network trips?
                    String path= FirebaseConstants.RIDES+"/"+requests.getTripUID()+"/"+FirebaseConstants.BOOKINGS+"/"+requests.getPassengerUID();
                    acceptPassenger(path,requests);
                })
                .setNegativeButton("no",(dialog1,which)->{

                })
                .show();
    }

    private void acceptPassenger(String Path,Requests requests) {
        setDocument(createRequestMap(requests), createDocumentReference(Path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                String path3=FirebaseConstants.PASSENGERS+"/"+requests.getPassengerUID()+"/"+FirebaseConstants.TRIPS+"/"+requests.getPassengerUID();
                setTripOnDatabase(path3,requests);

            }

            @Override
            public void onError(Object object) {
                Toast.makeText(context,"Error accepting request",Toast.LENGTH_LONG).show();
            }
        });

    }
    private void rejectPassenger(String path, Requests requests) {
        setDocument(createRequestMap(requests), createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(context,"Trip has been successfully removed",Toast.LENGTH_LONG).show();
                String path= FirebaseConstants.RIDES+"/"+requests.getTripUID()+"/"+FirebaseConstants.REQUESTS+"/"+requests.getRequestID();
                String path2= FirebaseConstants.PASSENGERS+"/"+requests.getPassengerUID()+"/"+FirebaseConstants.REQUESTS+"/"+requests.getRequestID();
                //todo:add to passenger collection called rejected trips;
               // possibly ask for reason in future
                deleteRequestFromDatabase(path);
                deleteRequestFromDatabase(path2);
                //displayNotificationOnPHone()
                Notifications notification=new Notifications("Your trip request has been rejected",1,"Trip Rejected");
                showNotification(requests,notification);

            }

            @Override
            public void onError(Object object) {
                Toast.makeText(context,"Error accepting request",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void setTripOnDatabase(String path, Requests requests) {
        setDocument(createRequestMap(requests), createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                //todo:convert to batch write
                Toast.makeText(context,"rider has been successfully accepted",Toast.LENGTH_LONG).show();
                String path= FirebaseConstants.RIDES+"/"+requests.getTripUID()+"/"+FirebaseConstants.REQUESTS+"/"+requests.getRequestID();
                String path2= FirebaseConstants.PASSENGERS+"/"+requests.getPassengerUID()+"/"+FirebaseConstants.REQUESTS+"/"+requests.getRequestID();
                deleteRequestFromDatabase(path);
                deleteRequestFromDatabase(path2);
                //displayNotificationOnPHone()
                Notifications notification=new Notifications("Your trip request has been accepted",1,"Trip Accepted");
                showNotification(requests,notification);

                redirect();
            }

            @Override
            public void onError(Object object) {
                Toast.makeText(context,"Error accepting request",Toast.LENGTH_LONG).show();
            }
        });
    }


    private void deleteRequestFromDatabase(String path) {
        deleteDocument(createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(context,"Trip has been successfully requested",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Object object) {
                Toast.makeText(context,"Error accepting request",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void redirect() {
        Intent intent=new Intent(context,My_trips.class);
        context.startActivity(intent);
    }

    private void showNotification(Requests requests,Notifications notification ) {
        String path=FirebaseConstants.PASSENGERS+"/"+requests.getPassengerUID()+"/"+FirebaseConstants.NOTIFICATIONS;
        setDocument(createNotification(notification), createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

                //todo:fill in details about trip
                Intent intent=new Intent(context, My_trips.class);
                context.startActivity(intent);
            }

            @Override
            public void onError(Object object) {

            }
        });
    }

    private Map createRequestMap(Requests requests) {
        Map<String,Object> map=new HashMap<>();

        //FOR PASSENGER
        map.put(FirebaseFields.P_LOCATION_FROM, requests.getLocationFrom());
        map.put(FirebaseFields.P_LOCATION_TO,requests.getLocationTo());
        map.put(FirebaseFields.LOCATION_TO_GEOPOINT,requests.getDestinationGeopoint());
        map.put(FirebaseFields.LOCATION_FROM_GEOPOINT,requests.getSourceGeopoint());
        map.put(FirebaseFields.SEATS,requests.getSeats());
        map.put(FirebaseFields.P_TRIP_PRICE, requests.getTripPrice());
        map.put(FirebaseConstants.PASSENGERS,requests.getPassengerUID());
        map.put(FirebaseFields.TRIP_ID,requests.getTripUID());
        map.put(FirebaseFields.TRiP_DATE,requests.getTripDate());

        return map;
    }
    private Map<String,Object> createNotification(Notifications notifications) {
        Map<String,Object> map=new HashMap<>();
        map.put(FirebaseFields.MESSAGE,notifications.getMessage());
        map.put(FirebaseFields.TYPE,1);
        map.put(FirebaseFields.TITLE,notifications.getTitle());

        return map;
    }

    private void rejectPassengerRequest(Requests requests) {
        new AlertDialog.Builder(context)
                .setTitle("Confirmation")
                .setIcon(R.drawable.icons8_bus_ticket_20px)
                .setMessage("Are you sure you would like to reject this ridee?")
                .setPositiveButton("yes", (dialog1, which) -> {
                    String path= FirebaseConstants.PASSENGERS+"/"+requests.getPassengerUID()+"/"+FirebaseConstants.REQUESTS_REJECTED+"/"+requests.getTripUID();
                    rejectPassenger(path,requests);
                })
                .setNegativeButton("no",(dialog1,which)->{

                })
                .show();
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
            btnRejectPassenger=itemView.findViewById(R.id.btnRejectRideRequests);
            price= itemView.findViewById(R.id.tripPriceRequests);
           // passengerCard = itemView.findViewById(R.id.call_card);
        }
    }
}

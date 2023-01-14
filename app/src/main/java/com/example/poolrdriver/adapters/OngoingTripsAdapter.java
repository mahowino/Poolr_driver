package com.example.poolrdriver.adapters;

import static com.example.poolrdriver.Abstract.Constants.FirebaseInitVariables.db;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createDocumentReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createQuery;
import static com.example.poolrdriver.Firebase.FirebaseRepository.deleteDocument;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocument;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsFromQueryInCollection;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsInCollection;
import static com.example.poolrdriver.Firebase.FirebaseRepository.setDocument;
import static com.example.poolrdriver.util.AppSystem.getMyDefaultLocation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.poolrdriver.Abstract.Callback;
import com.example.poolrdriver.Abstract.FirebaseConstants;
import com.example.poolrdriver.Abstract.Constants.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.Interfaces.OnTripEndedListener;
import com.example.poolrdriver.R;
import com.example.poolrdriver.ui.activities.other.TAG;
import com.example.poolrdriver.classes.other.Passenger;
import com.example.poolrdriver.classes.models.TripModel;
import com.example.poolrdriver.util.RatingDialog;
import com.example.poolrdriver.util.mathsUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OngoingTripsAdapter extends RecyclerView.Adapter<OngoingTripsAdapter.ViewHolder > {
    private  GeoPoint startLocation;
    Context mContext;
    List<String> passengerIDs;
    TripModel trip;
    private Location endLocation;
    Activity activity;
    double driverAmount,passengerAmount,adminAmount;
    OnTripEndedListener listener;

    public OngoingTripsAdapter(Context mContext, Activity activity, List<String> passengerIDs, TripModel trip, OnTripEndedListener listener) {
        this.mContext = mContext;
        this.passengerIDs = passengerIDs;
        this.trip = trip;
        this.activity=activity;
        startLocation=new GeoPoint(trip.getSourcePoint().latitude,trip.getSourcePoint().longitude);
        driverAmount=0;
        passengerAmount=0;
        adminAmount=0;
        this.listener=listener;
    }

    @NonNull
    @Override
    public OngoingTripsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.ongoing_trips_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull OngoingTripsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String passengerId=passengerIDs.get(position);
        String path= FirebaseConstants.PASSENGERS+"/"+passengerId;
        getDocument(createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<DocumentSnapshot> task=(Task<DocumentSnapshot>)object;
                Passenger passenger=new Passenger(task.getResult());
                //reviews=passenger.getReviews();
                setTexts(passenger,holder);
                setListener(passenger,position,holder);

            }

            @Override
            public void onError(Object object) {

            }
        });

    }
    private void navigateUser(){
        Uri navigationIntentUri = Uri.parse("google.navigation:q=" + trip.getDestinationpoint().latitude +"," + trip.getDestinationpoint().longitude);//creating intent with latlng
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        activity.startActivity(mapIntent);
    }

    private void setListener(Passenger passenger,int position, ViewHolder holder) {
      holder.btnEndTrip.setOnClickListener(v -> calculateTripCost(passenger,position));
      holder.btnMapNavigator.setOnClickListener(v -> navigateUser());
    }

    private void setTexts(Passenger passenger, ViewHolder holder) {
        //name
        holder.passengerName.setText(passenger.getNames());
        //image
        Glide.with(mContext).load(passenger.getProfilePic()).into(holder.profilePicture);

    }

    @Override
    public int getItemCount() {
        return passengerIDs.size();
    }
    private void calculateTripCost(Passenger passenger,int position) {



        getMyDefaultLocation(activity, new Callback() {
            @Override
            public void onSuccess(Object object) {
                endLocation=(Location) object;
                checkIfTripIsComplete(passenger,position);
                Log.d("tag", "checkIfTripIsComplete: "+endLocation);

            }

            @Override
            public void onError(Object object) {

            }
        });

    }
    private void getTripCharges(boolean isTripComplete,double distance,Passenger passenger){
        double cash_for_trip= trip.getTripPrice();
        double bookingFee=trip.getPassengerBookingFee();
        double pricePerSeat;

        if (isTripComplete) {
            //charge driver cut
            double driverFee=cash_for_trip*FirebaseConstants.FIXED_RATE_DRIVER_CUT;

            adminAmount=bookingFee;
            passengerAmount=0;
            driverAmount=cash_for_trip;
            listener.onTripEnded(driverAmount,adminAmount,passengerAmount,passenger);



        }
        else if(distance<1){
            new AlertDialog.Builder(activity)
                    .setTitle("Cancel trip")
                    .setMessage("You haven't traveled for the minimum distance to warant a carpool. Do you still want to cancel?")
                    .setPositiveButton(android.R.string.yes, (dialog1, which) -> {
                        //cancelled ride

                        listener.onTripEnded(0,bookingFee,cash_for_trip,passenger);


                    })
                    .setNegativeButton(android.R.string.no, (dialog12, which) -> dialog12.dismiss())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }

        else {
            pricePerSeat=(distance*FirebaseConstants.FIXED_RATE_PER_KILOMETER)/trip.getSeats();

            //get driver cut
            double driverFee=pricePerSeat*FirebaseConstants.FIXED_RATE_DRIVER_CUT;

            //update new price information
            double priceToGiveDriver=pricePerSeat-driverFee;

            //update balance to add back to passenger account
            double balanceToTopUpPassenger=cash_for_trip-(pricePerSeat)-bookingFee;

            adminAmount=bookingFee;
            passengerAmount=balanceToTopUpPassenger;
            driverAmount=pricePerSeat;


            Log.d("ongoing", "onSuccess: ongoing trip driver fee"+priceToGiveDriver);
            Log.d("ongoing", "onSuccess: ongoing trip booking fee"+(bookingFee+driverFee));
            Log.d("ongoing", "onSuccess: ongoing trip passenger fee"+balanceToTopUpPassenger);
            listener.onTripEnded(driverAmount,adminAmount,passengerAmount,passenger);

        }

    }



    private void checkIfTripIsComplete(Passenger passenger,int position) {
        LatLng source = new LatLng(startLocation.getLatitude(), startLocation.getLongitude());

        //get destinationPoint from app
        LatLng destination = new LatLng(endLocation.getLatitude(), endLocation.getLongitude());
        LatLng tripDestination = trip.getDestinationpoint();
        getTripCharges(mathsUtil.getDistanceFromUserPoints(destination, tripDestination) <= 1, mathsUtil.getDistanceFromUserPoints(source, destination), passenger);
       /* mathsUtil.getDistanceFromUserPoints(destination, tripDestination) <= 100000*/
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView passengerName;
        ImageView profilePicture;
        TextView btnMapNavigator;
        Button btnEndTrip;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnEndTrip=itemView.findViewById(R.id.btn_endTrip);
            btnMapNavigator=itemView.findViewById(R.id.txtViewDirections);
            passengerName=itemView.findViewById(R.id.passenger_name_ongoing);
            profilePicture=itemView.findViewById(R.id.imgPassengerProfilePicture_ongoing);
        }
    }
}

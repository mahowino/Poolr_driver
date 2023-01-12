package com.example.poolrdriver.adapters;

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
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.R;
import com.example.poolrdriver.ui.activities.other.TAG;
import com.example.poolrdriver.classes.other.Passenger;
import com.example.poolrdriver.classes.models.TripModel;
import com.example.poolrdriver.util.RatingDialog;
import com.example.poolrdriver.util.mathsUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

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

    public OngoingTripsAdapter(Context mContext, Activity activity, List<String> passengerIDs, TripModel trip) {
        this.mContext = mContext;
        this.passengerIDs = passengerIDs;
        this.trip = trip;
        this.activity=activity;
        startLocation=new GeoPoint(trip.getSourcePoint().latitude,trip.getSourcePoint().longitude);
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


    private void checkIfTripIsComplete(Passenger passenger,int position) {
        LatLng source=new LatLng(startLocation.getLatitude(),startLocation.getLongitude());
        LatLng tripSource=trip.getSourcePoint();


        //get destinationPoint from app

        LatLng destination=new LatLng(endLocation.getLatitude(),endLocation.getLongitude());
        LatLng tripDestination=trip.getDestinationpoint();
        Log.d("tag", "checkIfTripIsComplete: "+source);
        Log.d("tag", "checkIfTripIsComplete: "+tripSource);
        Log.d("tag", "checkIfTripIsComplete: "+destination);
        Log.d("tag", "checkIfTripIsComplete: "+tripDestination);


        double pricePerSeat=0;
        boolean isTripComplete=false;


        //if trip is within limits of driver
        if (mathsUtil.getDistanceFromUserPoints(destination,tripDestination)<1){
            pricePerSeat=trip.getTripPrice();
            isTripComplete=true;
            chargeBookingFee(pricePerSeat,passenger,isTripComplete,position);

        }

            //trip canceled prematurely
        else  {
            double distance=mathsUtil.getDistanceFromUserPoints(source,destination);
            if(distance<1){
                new AlertDialog.Builder(activity)
                        .setTitle("Cancel trip")
                        .setMessage("You haven't traveled for the minimum distance to warant a carpool. Do you still want to cancel?")
                        .setPositiveButton(android.R.string.yes, (dialog1, which) -> {
                            //cancelled ride

                            RatingDialog rate=new RatingDialog(activity,passenger,trip.getTripID());
                            rate.startRatingAlertDialog();

                        })
                        .setNegativeButton(android.R.string.no, (dialog12, which) -> dialog12.dismiss())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
            else {
                Toast.makeText(mContext, "you are cancelling the trip early", Toast.LENGTH_SHORT).show();
                pricePerSeat=(distance*FirebaseConstants.FIXED_RATE_PER_KILOMETER)/trip.getSeats();
                chargeBookingFee(pricePerSeat,passenger,false,position);
            }


        }



    }

    private void chargeBookingFee(double pricePerSeat,Passenger passenger,boolean isTripComplete,int pos) {


        movePledgedFundsToDriverWallet(passenger,pricePerSeat,isTripComplete,pos);

    }

    private void movePledgedFundsToDriverWallet(Passenger passenger,double pricePerSeat, boolean isTripComplete,int pos) {
        getPledgeWalletForTheSpecificTrip(passenger,pricePerSeat,isTripComplete,pos);
    }

    private void getPledgeWalletForTheSpecificTrip(Passenger passenger,double pricePerSeat,boolean isTripComplete,int position) {
        String path=FirebaseConstants.PASSENGERS+"/"+passenger.getUsername()+ "/"+FirebaseConstants.PLEDGED_FUNDS_WALLET+"/"+trip.getTripID();
        getDocument(
                createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

                Task<DocumentSnapshot> task=(Task<DocumentSnapshot>) object;
                DocumentSnapshot snapshot=task.getResult();


                    Toast.makeText(mContext, "You have.", Toast.LENGTH_SHORT).show();
                    double cash_for_trip=snapshot.getDouble(FirebaseFields.CASH);
                    double bookingFee=snapshot.getDouble(FirebaseFields.PASSENGER_BOOKING_FEE);

                    //if trip is complete
                    if (isTripComplete) {
                        //charge driver cut
                        //reconsider
                        double driverFee=cash_for_trip*FirebaseConstants.FIXED_RATE_DRIVER_CUT;

                        //update new price information
                        cash_for_trip=cash_for_trip-driverFee;

                        //top up wallets
                        addWalletAmountToDriverWallet(cash_for_trip);
                        addBookingFeeToAdminWallet(driverFee+bookingFee);

                    }

                    else {


                        //get driver cut
                        double driverFee=pricePerSeat*FirebaseConstants.FIXED_RATE_DRIVER_CUT;


                        //update new price information
                        double priceToGiveDriver=pricePerSeat-driverFee;

                        //update balance to add back to passenger account
                        double balanceToTopUpPassenger=cash_for_trip-(pricePerSeat);

                        Log.d("ongoing", "onSuccess: ongoing trip driver fee"+priceToGiveDriver);
                        Log.d("ongoing", "onSuccess: ongoing trip booking fee"+(bookingFee+driverFee));
                        Log.d("ongoing", "onSuccess: ongoing trip passenger fee"+balanceToTopUpPassenger);

                        Toast.makeText(mContext, "Trip  for "+passenger.getNames(), Toast.LENGTH_SHORT).show();
                        //top up wallets
                        addWalletAmountToDriverWallet(priceToGiveDriver);
                        addBookingFeeToAdminWallet(bookingFee+driverFee);
                        updatePassengersWallet(balanceToTopUpPassenger,passenger);

                    }

                    //update trip information
                    updateTrips(passenger.getUsername());//todo: fix error
                    deleteWalletEntry(snapshot.getId(),passenger.getUsername());
                    passengerIDs.remove(position);
                    notifyItemRemoved(position);

                    //review passenger
                    RatingDialog rate=new RatingDialog(activity,passenger,trip.getTripID());
                    rate.startRatingAlertDialog();
                    //Toast.makeText(mContext, "Trip successfully ended for "+passenger.getNames(), Toast.LENGTH_SHORT).show();

                }



            @Override
            public void onError(Object object) {
                Log.d(TAG.TAG, "onFailure: failure "+((Exception)object).getMessage());}
        });


    }

    private void updatePassengersWallet(double balanceToTopUpPassenger, Passenger passenger) {
        String path=FirebaseConstants.PASSENGERS+"/"+passenger.getUsername()+ "/"+FirebaseConstants.PASSENGER_WALLET;
        getDocumentsFromQueryInCollection(
                createQuery(createCollectionReference(path),
                        FirebaseFields.TRIP_ID,
                        trip.getTripID()), new Callback() {
            @Override
            public void onSuccess(Object object) {

                double wallet_balance;
                Task<QuerySnapshot> task=(Task<QuerySnapshot>) object;
                for (DocumentSnapshot snapshot:task.getResult()){
                String cash= String.valueOf(snapshot.get(FirebaseFields.CASH));
                wallet_balance=Double.valueOf(cash);
                wallet_balance=wallet_balance+balanceToTopUpPassenger;

                topUpPassengerCash(snapshot.getId(),wallet_balance,passenger.getUsername());
            }


            }

            @Override
            public void onError(Object object) {
                Log.d(TAG.TAG, "onFailure: failure "+((Exception)object).getMessage());
            }
        });

    }

    private void addBookingFeeToAdminWallet(double driverFee) {
        String path=FirebaseConstants.ADMIN+"/"+FirebaseConstants.ROOT_ADMIN_ID+"/"+FirebaseConstants.ADMIN_WALLET;
        getDocumentsInCollection(createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                String walletUid;
                double wallet_balance;
                Task<QuerySnapshot> task=(Task<QuerySnapshot>) object;
                for (DocumentSnapshot snapshot:task.getResult()){
                    walletUid=snapshot.getId();
                    String cash= String.valueOf(snapshot.get(FirebaseFields.CASH));
                    wallet_balance=Double.valueOf(cash);
                    wallet_balance=wallet_balance+driverFee;
                    topUpAdminCash(walletUid,wallet_balance);
                    logTransaction();
                }
            }

            @Override
            public void onError(Object object) {
                Log.d("tag", "onFailure: failure "+((Exception)object).getMessage());}
        });

    }

    private void addWalletAmountToDriverWallet(double cash_for_trip) {
        String path= FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/"+FirebaseConstants.DRIVER_WALLET;
        getDocumentsInCollection(createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                String walletUid;
                double wallet_balance;
                Task<QuerySnapshot> task=(Task<QuerySnapshot>) object;
                for (DocumentSnapshot snapshot:task.getResult()){
                    walletUid=snapshot.getId();
                    String cash= String.valueOf(snapshot.get(FirebaseFields.CASH));
                    wallet_balance=Double.valueOf(cash);
                    wallet_balance=wallet_balance+cash_for_trip;
                    topUpCash(walletUid,wallet_balance);

                }
            }

            @Override
            public void onError(Object object) {
                Log.d("tag", "onFailure: failure "+((Exception)object).getMessage());}
        });

    }


    private void deleteWalletEntry(String walletId,String passengerID) {
        //move trip to past trips
        String pledgeWalletPath=FirebaseConstants.PASSENGERS+"/"+passengerID+"/"+FirebaseConstants.PLEDGED_FUNDS_WALLET+"/"+walletId;
        String upcomingTripPath=FirebaseConstants.PASSENGERS+"/"+passengerID+"/"+FirebaseConstants.TRIPS+"/"+trip.getTripID();
        String ongoingTripPath=FirebaseConstants.PASSENGERS+"/"+passengerID+"/"+FirebaseConstants.ONGOING_TRIP+"/"+trip.getTripID();
        String booking_on_trip=FirebaseConstants.RIDES+"/"+trip.getTripID()+"/"+FirebaseConstants.BOOKINGS+"/"+passengerID;


        deleteFromDatabase(pledgeWalletPath);
        deleteFromDatabase(upcomingTripPath);
        deleteFromDatabase(ongoingTripPath);
        deleteFromDatabase(booking_on_trip);


    }
    private void deleteFromDatabase(String path) {
        deleteDocument(createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {
                Log.d(TAG.TAG, "onFailure: failure "+((Exception)object).getMessage());
            }
        });
    }



    private void logTransaction() {

        String path2=FirebaseConstants.ADMIN+"/"+FirebaseConstants.ROOT_ADMIN_ID+"/"+FirebaseConstants.TRANSACTIONS;

        setDocument(createBookingTransaction(), createCollectionReference(path2), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {
        Log.d(TAG.TAG, "onFailure: failure "+((Exception)object).getMessage());
            }
        });


    }


    private void updateTrips(String passengerID) {
        String path= FirebaseConstants.PASSENGERS+"/"+passengerID+"/"+FirebaseConstants.PAST_RIDES;
        setDocument(createBookingTransaction(),createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {
                Log.d(TAG.TAG, "onFailure: failure "+((Exception)object).getMessage());
            }
        });

    }

    private void topUpAdminCash(String walletUid,double newBalance) {
        String path=FirebaseConstants.ADMIN+"/"+FirebaseConstants.ROOT_ADMIN_ID+"/"+FirebaseConstants.ADMIN_WALLET+"/"+walletUid;
        setDocument(createWallet(newBalance), createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {
                Log.d(TAG.TAG, "onFailure: failure "+((Exception)object).getMessage());
            }
        });
    }


    private void topUpCash(String walletUid,double newBalance) {
        String path=FirebaseConstants.PASSENGERS+"/"+new User().getUID()+ "/"+FirebaseConstants.DRIVER_WALLET+"/"+walletUid;
        setDocument(createWallet(newBalance), createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {
                Log.d(TAG.TAG, "onFailure: failure "+((Exception)object).getMessage());
            }
        });
    }
    private void topUpPassengerCash(String walletUid,double newBalance,String passenger) {
        String path=FirebaseConstants.PASSENGERS+"/"+passenger+ "/"+FirebaseConstants.PASSENGER_WALLET+"/"+walletUid;
        setDocument(createWallet(newBalance), createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {
                Log.d(TAG.TAG, "onFailure: failure "+((Exception)object).getMessage());
            }
        });
    }

    private Map createWallet(double amountToUpdate) {
        Map<String,Object> wallet_update_object=new HashMap<>();

        wallet_update_object.put(FirebaseFields.CASH,amountToUpdate);
        wallet_update_object.put(FirebaseFields.UPDATE_TIME,new Date());
        return wallet_update_object;

    }
    private Map createBookingTransaction() {
        //todo:populateTripValues
        Map<String,Object> map=new HashMap<>();
        map.put(FirebaseFields.TRIP_ID,trip);
        map.put(FirebaseFields.CASH,trip.getTripPrice());
        return map;
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

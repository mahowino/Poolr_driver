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
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.OngoingTrip;
import com.example.poolrdriver.R;
import com.example.poolrdriver.TAG;
import com.example.poolrdriver.classes.Passenger;
import com.example.poolrdriver.models.Requests;
import com.example.poolrdriver.models.TripModel;
import com.example.poolrdriver.reviewActivity;
import com.example.poolrdriver.util.mathsUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.ncorti.slidetoact.SlideToActView;

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
    public void onBindViewHolder(@NonNull OngoingTripsAdapter.ViewHolder holder, int position) {
        String passengerId=passengerIDs.get(position);
        String path= FirebaseConstants.PASSENGERS+"/"+passengerId;
        getDocument(createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<DocumentSnapshot> task=(Task<DocumentSnapshot>)object;
                Passenger passenger=new Passenger(task.getResult());
                //reviews=passenger.getReviews();
                setTexts(passenger,holder);
                setListener(passenger,holder);

            }

            @Override
            public void onError(Object object) {

            }
        });

    }

    private void setListener(Passenger passenger, ViewHolder holder) {
      holder.endTrip.setOnSlideCompleteListener(slideToActView -> calculateTripCost(passenger));
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
    private void calculateTripCost(Passenger passenger) {
        getMyDefaultLocation(activity, new Callback() {
            @Override
            public void onSuccess(Object object) {
                endLocation=(Location) object;
                checkIfTripIsComplete(passenger);
                Log.d("tag", "checkIfTripIsComplete: "+endLocation);

            }

            @Override
            public void onError(Object object) {}});

    }


    private void checkIfTripIsComplete(Passenger passenger) {
        LatLng source=new LatLng(startLocation.getLatitude(),startLocation.getLongitude());
        LatLng tripSource=trip.getSourcePoint();


        //get destinationPoint from app

        LatLng destination=new LatLng(endLocation.getLatitude(),endLocation.getLongitude());
        LatLng tripDestination=trip.getDestinationpoint();
        Log.d("tag", "checkIfTripIsComplete: "+source);
        Log.d("tag", "checkIfTripIsComplete: "+tripSource);
        Log.d("tag", "checkIfTripIsComplete: "+destination);
        Log.d("tag", "checkIfTripIsComplete: "+tripDestination);


        double distance,pricePerSeat;
        boolean isTripComplete;


        //if trip is within limits of driver
        if (mathsUtil.getDistanceFromUserPoints(source,tripSource)<1
                && mathsUtil.getDistanceFromUserPoints(destination,tripDestination)<1){
            pricePerSeat=trip.getTripPrice();
            isTripComplete=true;

        }
        //trip canceled prematurely
        else {
            distance=mathsUtil.getDistanceFromUserPoints(source,destination);
            pricePerSeat=(distance*FirebaseConstants.FIXED_RATE_PER_KILOMETER)/trip.getSeats();
            isTripComplete=false;

        }



        chargeBookingFee(pricePerSeat,passenger,isTripComplete);
    }

    private void chargeBookingFee(double pricePerSeat,Passenger passenger,boolean isTripComplete) {


        movePledgedFundsToDriverWallet(passenger,pricePerSeat,isTripComplete);

    }

    private void movePledgedFundsToDriverWallet(Passenger passenger,double pricePerSeat, boolean isTripComplete) {
        getPledgeWalletForTheSpecificTrip(passenger,pricePerSeat,isTripComplete);
    }

    private void getPledgeWalletForTheSpecificTrip(Passenger passenger,double pricePerSeat,boolean isTripComplete) {
        String path=FirebaseConstants.PASSENGERS+"/"+passenger.getUsername()+ "/"+FirebaseConstants.PLEDGED_FUNDS_WALLET;
        getDocumentsFromQueryInCollection(
                createQuery(
                        createCollectionReference(path),
                        FirebaseFields.TRIP_ID,
                        trip.getTripID()), new Callback() {
            @Override
            public void onSuccess(Object object) {

                Task<QuerySnapshot> task=(Task<QuerySnapshot>) object;

                for (DocumentSnapshot snapshot:task.getResult()){
                    double cash_for_trip=snapshot.getDouble(FirebaseFields.CASH);
                    double bookingFee=snapshot.getDouble(FirebaseFields.PASSENGER_BOOKING_FEE);

                    //if trip is complete
                    if (isTripComplete) {
                        //charge driver cut
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
                    //Toast.makeText(mContext, "Trip successfully ended for "+passenger.getNames(), Toast.LENGTH_SHORT).show();

                }
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
                    topUpCash(walletUid,wallet_balance);
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
        String ongoingTripPath=FirebaseConstants.PASSENGERS+"/"+passengerID+"/"+FirebaseConstants.ONGOING_TRIP+"/"+new User().getUID();
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
        SlideToActView endTrip;
        TextView passengerName;
        ImageView profilePicture;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            endTrip=itemView.findViewById(R.id.end_trip_slider);
            passengerName=itemView.findViewById(R.id.passenger_name_ongoing);
            profilePicture=itemView.findViewById(R.id.imgPassengerProfilePicture_ongoing);
        }
    }
}

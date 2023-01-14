package com.example.poolrdriver.ui.activities.other;

import static com.example.poolrdriver.Abstract.Constants.FirebaseInitVariables.db;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createDocumentReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createQuery;
import static com.example.poolrdriver.Firebase.FirebaseRepository.deleteDocument;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocument;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsFromQueryInCollection;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsInCollection;
import static com.example.poolrdriver.Firebase.FirebaseRepository.setDocument;
import static com.example.poolrdriver.classes.other.userLogInAttempt.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Abstract.Callback;
import com.example.poolrdriver.Abstract.Constants.FirebaseInitVariables;
import com.example.poolrdriver.Abstract.FirebaseConstants;
import com.example.poolrdriver.Abstract.Constants.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.Interfaces.OnTripEndedListener;
import com.example.poolrdriver.R;
import com.example.poolrdriver.adapters.OngoingTripsAdapter;
import com.example.poolrdriver.classes.models.Requests;
import com.example.poolrdriver.classes.models.TripModel;
import com.example.poolrdriver.classes.other.Passenger;
import com.example.poolrdriver.util.LoadingDialog;
import com.example.poolrdriver.util.RatingDialog;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OngoingTrip extends AppCompatActivity {

    ArrayList<String> passengers;
    List<Requests> requests;
    TripModel trip;
    private static final String STARTING_LOCATION = "starting_location";
    TextView source,destination,driverName;
    ImageView imageDriver;
    RecyclerView recyclerView;
    Location currentLocation,endLocation;
    double amountForDriver,amountForAdmin;
    FloatingActionButton panic,trips, finish_trips;
    private final String CHOSEN_TRIP="chosen_trip";
    private final String PASSENGERS="passengers";
    private final String LOCATIONS="locations";
    private  String userDetailsPath;
    private double pricePerPassenger;
    private final String  USER_ACCOUNT="signed_in_user";
    LoadingDialog loadingDialog;
    boolean isTripDone;

    private String walletUid;
    private GeoPoint startLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_trip);
        initializeVariables();
        setListeners();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }



    private void setListeners() {
        panic.setOnClickListener(v -> openSafety());
        //trips.setOnClickListener(v -> openTrips());
        finish_trips.setOnClickListener(v -> closeTrip());

    }


    private void closeTrip() {
        new AlertDialog.Builder(OngoingTrip.this)
                .setTitle("Confirmation")
                .setIcon(R.drawable.icons8_bus_ticket_20px)
                .setMessage("Are you sure you would like to end this trip. This should be after ending trips for every passenger individually")
                .setPositiveButton("yes", (dialog1, which) -> {
                    finishTrip();
                })
                .setNegativeButton("no",(dialog1,which)->{

                })
                .show();

    }

    private void finishTrip() {

       /* //checkIfTripWas an regular one
        String ongoingTripPath=FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/"+FirebaseConstants.ONGOING_TRIP+"/"+trip.getTripID();
        String booking_on_trip=FirebaseConstants.RIDES+"/"+trip.getTripID();

        deleteFromDatabase(ongoingTripPath);
        deleteFromDatabase(booking_on_trip);
        updateTrips();*/


        Intent intent=new Intent(OngoingTrip.this,MapsActivity.class);
        startActivity(intent);
        finish();
    }


    private Map<String,Object> getMapData() {
        //document creation
        Map<String, Object> map = new HashMap<>();
        map.put(FirebaseFields.DRIVER, new User().getUID());
        map.put(FirebaseFields.P_LOCATION_FROM, trip.getDriverSource());
        map.put(FirebaseFields.P_LOCATION_TO,trip.getDriverDestination());
        map.put(FirebaseFields.SEATS,trip.getSeats());
        map.put(FirebaseFields.P_TRIP_PRICE, trip.getTripPrice());
        map.put(FirebaseFields.PASSENGER_BOOKING_FEE,Math.ceil(trip.getTripPrice()*FirebaseConstants.FIXED_RATE_PASSENGER_CUT/ trip.getSeats()));
        map.put(FirebaseFields.PRIVACY,trip.isPrivacy());
        map.put(FirebaseFields.DRIVER,trip.getDriverUid());
        map.put(FirebaseFields.DEPARTURETIME,new Date());


        //when putting price, add passenger booking fee to the trip cost.

        return map;
    }

    private void openTrips() {
        Intent myTrips=new Intent(OngoingTrip.this, reviewActivity.class);

        myTrips.putExtra("chosen_trip",trip);
        startActivity(myTrips);

    }

    private void openSafety() {
        String number="999";
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
        startActivity(callIntent);

    }

    private void initializeVariables() {
        source=findViewById(R.id.TripSource_expanded_ongoing);
        destination=findViewById(R.id.TripDestination_expanded_ongoing);
        loadingDialog=new LoadingDialog(this);
        panic=findViewById(R.id.panic_ongoing);
        finish_trips =findViewById(R.id.finish_trip_btn);
        recyclerView=findViewById(R.id.passengers_ongoing_trip_recyclerView);
        isTripDone=false;
        getIntentData();
    }

    private void getIntentData() {
        //HAS TO BE GOTTEN FROM DB;
        trip=getIntent().getExtras().getParcelable(CHOSEN_TRIP);
        Bundle bundle = getIntent().getParcelableExtra(STARTING_LOCATION);
        LatLng tempStartLocation=bundle.getParcelable("start");
        startLocation=new GeoPoint(tempStartLocation.latitude,tempStartLocation.longitude);
        passengers=getIntent().getStringArrayListExtra(PASSENGERS);

        source.setText(trip.getDriverSource());
        destination.setText(trip.getDriverDestination());



        OngoingTripsAdapter adapter=new OngoingTripsAdapter(getApplicationContext(), OngoingTrip.this, passengers, trip, new OnTripEndedListener() {
            @Override
            public void onTripEnded(double driverCharge, double adminCharge, double valueToTopPassengerWallet, Passenger passenger) {
                loadingDialog.startLoadingAlertDialog();
                endTripForPassenger(driverCharge,adminCharge,valueToTopPassengerWallet,passenger);
            }

        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));

    }

    private void endTripForPassenger(double driverCharge, double adminCharge, double valueToTopPassengerWallet,Passenger passenger) {
        db.runTransaction(transaction -> {

            String passengerID=passenger.getUsername();

            DocumentSnapshot driverWallet =
                    transaction.get(
                            createDocumentReference(FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/"+FirebaseConstants.DRIVER_WALLET+"/"+new User().getUID()));

            DocumentSnapshot adminWallet =
                    transaction.get(
                            (createDocumentReference(FirebaseConstants.ADMIN+"/"+FirebaseConstants.ROOT_ADMIN_ID+"/"+FirebaseConstants.ADMIN_WALLET+"/"+FirebaseConstants.ADMIN_WALLET_ID)));

            DocumentSnapshot passengerWallet =
                    transaction.get(createDocumentReference(
                            (FirebaseConstants.PASSENGERS+"/"+passengerID+"/"+FirebaseConstants.PASSENGER_WALLET+"/"+passengerID)));

            DocumentSnapshot OngoingTrip = transaction.get(createDocumentReference(
                    FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/"+FirebaseConstants.ONGOING_TRIP+"/"+trip.getTripID()));


            // Note: this could be done without a transaction
            //       by updating the population using FieldValue.increment()
            double passengerWalletAmount = passengerWallet.getDouble(FirebaseFields.CASH)+valueToTopPassengerWallet;
            double adminWalletAmount = adminWallet.getDouble(FirebaseFields.CASH)+adminCharge;
            double driverWalletAmount = driverWallet.getDouble(FirebaseFields.CASH)+driverCharge;
            passengers= (ArrayList<String>) OngoingTrip.get(FirebaseConstants.PASSENGERS);

            transaction.update(
                    createDocumentReference(
                            FirebaseConstants.PASSENGERS+"/"+passengerID+"/"+FirebaseConstants.PASSENGER_WALLET+"/"+passengerID),FirebaseFields.CASH, passengerWalletAmount);

            transaction.update(
                    createDocumentReference(
                            FirebaseConstants.ADMIN+"/"+FirebaseConstants.ROOT_ADMIN_ID+"/"+FirebaseConstants.ADMIN_WALLET+"/"+FirebaseConstants.ADMIN_WALLET_ID), FirebaseFields.CASH, adminWalletAmount);

            transaction.update(
                    createDocumentReference(
                                    FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/"+FirebaseConstants.DRIVER_WALLET+"/"+new User().getUID()), FirebaseFields.CASH, driverWalletAmount);


            for (int x=0;x<passengers.size();x++)
                if (passengers.get(x).equals(passengerID))
                    passengers.remove(x);


                if (passengers.size()<=0){

                    isTripDone=true;
                    transaction.delete(createDocumentReference(FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/"+FirebaseConstants.ONGOING_TRIP+"/"+trip.getTripID()));
                    transaction.delete(createDocumentReference(FirebaseConstants.RIDES+"/"+trip.getTripID()));
                }

                else

                    transaction.update(
                            createDocumentReference(
                                    FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/"+FirebaseConstants.TRIP_WALLET+"/"+trip.getTripID()), FirebaseConstants.PASSENGERS, passengers);

                transaction.delete(createDocumentReference(FirebaseConstants.PASSENGERS+"/"+passengerID+"/"+FirebaseConstants.PLEDGED_FUNDS_WALLET+"/"+trip.getTripID()));
                transaction.delete(createDocumentReference(FirebaseConstants.PASSENGERS+"/"+passengerID+"/"+FirebaseConstants.TRIPS+"/"+trip.getTripID()));
                transaction.delete(createDocumentReference(FirebaseConstants.PASSENGERS+"/"+passengerID+"/"+FirebaseConstants.ONGOING_TRIP+"/"+trip.getTripID()));
                transaction.delete(createDocumentReference(FirebaseConstants.RIDES+"/"+trip.getTripID()+"/"+FirebaseConstants.BOOKINGS+"/"+passengerID));

            return null;
        }).addOnSuccessListener(o -> {
            loadingDialog.dismissDialog();


            RatingDialog rate=new RatingDialog(OngoingTrip.this,passenger,trip.getTripID(),isTripDone);
            rate.startRatingAlertDialog();

        }).addOnFailureListener(e -> Log.w("TAG", "Transaction failure.", e));


    }
    private void deleteEntries(String passengerID) {

        List<DocumentReference> references=new ArrayList<>();
/*
        references.add(createDocumentReference(FirebaseConstants.PASSENGERS+"/"+passengerID+"/"+FirebaseConstants.PLEDGED_FUNDS_WALLET+"/"+trip.getTripID()));
        references.add();
        references.add(createDocumentReference(FirebaseConstants.PASSENGERS+"/"+passengerID+"/"+FirebaseConstants.ONGOING_TRIP+"/"+trip.getTripID()));
        references.add(createDocumentReference(FirebaseConstants.RIDES+"/"+trip.getTripID()+"/"+FirebaseConstants.BOOKINGS+"/"+passengerID));*/

        FirebaseRepository.deleteBatch(references, new Callback() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(getApplicationContext(), "Trip successfully ended for user", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Object object) {
                Exception exception=(Exception)object;
                Toast.makeText(getApplicationContext(), "error: "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
        });


    }

    private void endTripForEveryone() {
        Toast.makeText(this, "Trip successfully ended", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(OngoingTrip.this,MapsActivity.class);
        startActivity(intent);
        finish();
    }

/*
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
    private void getTemporaryWalletDetails(){
        String path= FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/"+FirebaseConstants.TRIP_WALLET+"/"+trip.getTripID();
        getDocument(createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<DocumentSnapshot> task=(Task<DocumentSnapshot>) object;
                DocumentSnapshot snapshot=task.getResult();
                amountForDriver=snapshot.getDouble(FirebaseFields.CASH);

            }

            @Override
            public void onError(Object object) {
                Log.d("tag", "onFailure: failure "+((Exception)object).getMessage());}
        });
    }




    private void logTransaction(String passengerID,double passengerWallet,double driverWallet,double adminAmount) {

        WriteBatch batch = db.batch();
        batch.set(createDocumentReference(FirebaseConstants.ADMIN+"/"+FirebaseConstants.ROOT_ADMIN_ID+"/"+FirebaseConstants.TRANSACTIONS),createBookingTransaction());
        batch.set(createDocumentReference(FirebaseConstants.PASSENGERS+"/"+passengerID+"/"+FirebaseConstants.PAST_RIDES),createBookingTransaction());
        batch.set(createDocumentReference(FirebaseConstants.ADMIN+"/"+FirebaseConstants.ROOT_ADMIN_ID+"/"+FirebaseConstants.ADMIN_WALLET+"/"+walletUid),createWallet(adminAmount));
        batch.set(createDocumentReference(FirebaseConstants.PASSENGERS+"/"+new User().getUID()+ "/"+FirebaseConstants.DRIVER_WALLET+"/"+walletUid),createWallet(passengerWallet));
        batch.delete(createDocumentReference(FirebaseConstants.PASSENGERS+"/"+passengerID+ "/"+FirebaseConstants.PLEDGED_FUNDS_WALLET+"/"+trip.getTripID()));

    }




    private void topUpPassengerCash(String walletUid,double newBalance,String passenger) {

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
    }*/

}
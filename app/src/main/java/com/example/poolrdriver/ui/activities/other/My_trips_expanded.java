package com.example.poolrdriver.ui.activities.other;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createDocumentReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocument;
import static com.example.poolrdriver.Firebase.FirebaseRepository.setDocument;
import static com.example.poolrdriver.util.AppSystem.getMyDefaultLocation;
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.poolrdriver.Abstract.Callback;
import com.example.poolrdriver.Abstract.FirebaseConstants;
import com.example.poolrdriver.Abstract.Constants.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.R;
import com.example.poolrdriver.adapters.PassengersAdapter;
import com.example.poolrdriver.classes.other.Passenger;
import com.example.poolrdriver.classes.models.Requests;
import com.example.poolrdriver.classes.models.TimePickerObject;
import com.example.poolrdriver.classes.models.TripModel;
import com.example.poolrdriver.ui.activities.userRegistrationJourney.CancelTrip;
import com.example.poolrdriver.util.mathsUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.ncorti.slidetoact.SlideToActView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class My_trips_expanded extends AppCompatActivity {
    public static final String STARTING_LOCATION = "starting_location";
    TextView source,destination,departure_time,privacy,no_of_passengers,no_of_seats_offered,cash_to_be_paid,luggage,no_passengers_booked,cancel;
    Button btnRequests;
    TripModel trip;
    User user;
    int counter;
    List<Passenger> passengers;
    private final String CHOSEN_TRIP="chosen_trip";
    private final String PASSENGERS="passengers";
    private final String TRIP_TIME="trip_time";
    private TimePickerObject timePickerObject;
    private  String userDetailsPath;
    private final String  USER_ACCOUNT="signed_in_user";
    private QuerySnapshot snapshot;
    private List<Requests> booking_requests;
    RecyclerView passengersList;
    SlideToActView startTrip;
    private Location currentLocation;

    @Override
    protected void onResume() {
        super.onResume();
        checkGPS();

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkGPS();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips_expanded);
        initializeVariables();
        setListeners();
        checkGPS();
        getBookedPassengers();

    }

    private void getBookedPassengers() {
        FirebaseRepository.getDocumentsInCollection(createCollectionReference(userDetailsPath), new Callback() {
            @Override
            public void onSuccess(Object object) {
                snapshot=((Task<QuerySnapshot>) object).getResult();
                if (snapshot!=null){
                    booking_requests=new ArrayList<>();
                    displayPassengers();
                }
                else
                    displayNoPassengers();
            }

            @Override
            public void onError(Object object) {}
        });

    }

    private void displayPassengers() {
        if(arePassengersAvailable()){
            counter=0;
            getPassengersAvailable();

        }
        else displayNoPassengers();


    }

    private void displayNoPassengers() {
        startTrip.setVisibility(View.INVISIBLE);
        passengersList.setVisibility(View.INVISIBLE);
        no_passengers_booked.setVisibility(View.VISIBLE);
        no_of_passengers.setText("none");


    }

    private void displayPassengersAdapter() {
        if (isTripWithinTime())
            startTrip.setVisibility(View.VISIBLE);

        passengersList.setVisibility(View.VISIBLE);
        no_passengers_booked.setVisibility(View.INVISIBLE);
        displayAdapter();

    }

    private void displayAdapter() {

        PassengersAdapter adapter=new PassengersAdapter(this,passengers,booking_requests,My_trips_expanded.this);
        passengersList.setAdapter(adapter);
        passengersList.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
        no_of_passengers.setText(passengers.size()+" passengers");
    }

    private void getPassengersAvailable() {
        int numberOfPassengers=snapshot.size();
        for (DocumentSnapshot snapshot:snapshot)
            getPassengersData(snapshot,numberOfPassengers);
    }
    private boolean arePassengersAvailable(){
        return snapshot.size()>0;
}

    private void getPassengersData(DocumentSnapshot snapshot,int NumberOfPassengers) {
        //GET REQUESTS DATA
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

        booking_requests.add(requests);

        String path=FirebaseConstants.PASSENGERS+"/"+snapshot.get(FirebaseConstants.PASSENGERS);
        getDocument(createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<DocumentSnapshot> task=(Task<DocumentSnapshot>) object;
                passengers.add(new Passenger(task.getResult()));
                counter++;
                if (counter==NumberOfPassengers)
                    displayPassengersAdapter();
            }

            @Override
            public void onError(Object object) {

            } });


    }

    private void setListeners() {
        btnRequests.setOnClickListener(v -> viewSentRequests());
        cancel.setOnClickListener(v -> redirectActivity(My_trips_expanded.this, CancelTrip.class));
        startTrip.setOnSlideCompleteListener(slideToActView -> beginTrip());
    }

    private void beginTrip() {


        getStartLocationSnapshot();

    }

    private boolean isTripWithinTime() {
      Date currentDate= Calendar.getInstance().getTime();
      Date tripDate=timePickerObject.getDate(true);
      long diff=Math.abs(tripDate.getTime()-currentDate.getTime());
      long timeInMinutes=TimeUnit.MILLISECONDS.toMinutes(diff);
        Toast.makeText(this, " "+timeInMinutes, Toast.LENGTH_SHORT).show();
      return timeInMinutes <15.0;
    }

    private void viewSentRequests() {
        Intent intent=new Intent(My_trips_expanded.this,trip_requests.class);
        intent.putExtra(CHOSEN_TRIP,trip);
        startActivity(intent);

    }
    private Map<String,Object> createOngoingTrip() {
        //source and destinations not showing
        Map<String,Object> map=new HashMap<>();
        GeoPoint sourceGeopoint=new GeoPoint(currentLocation.getLatitude(),currentLocation.getLongitude());
        map.put(FirebaseFields.START_LOCATION,sourceGeopoint);
        map.put(FirebaseFields.IS_TRIP_NETWORK,trip.isPrivacy());
        ArrayList<String> passengersIds=new ArrayList<>();
        for (Passenger passenger:passengers)
            passengersIds.add(passenger.getUsername());

        map.put(FirebaseConstants.PASSENGERS,passengersIds);
        map.put(FirebaseFields.DRIVER, new User().getUID());
        map.put(FirebaseFields.P_LOCATION_FROM, trip.getDriverSource());
        map.put(FirebaseFields.P_LOCATION_TO,trip.getDriverDestination());
        map.put(FirebaseFields.IS_TRIP_ACTIVE,true);

        GeoPoint locationFromGeopoint=new GeoPoint(trip.getSourceGeopoint().latitude,trip.getSourceGeopoint().longitude);
        GeoPoint locationToGeopoint=new GeoPoint(trip.getDestinationGeopoint().latitude,trip.getDestinationGeopoint().longitude);
        map.put(FirebaseFields.LOCATION_TO_GEOPOINT,locationToGeopoint);
        map.put(FirebaseFields.LOCATION_FROM_GEOPOINT,locationFromGeopoint);
        map.put(FirebaseFields.SEATS,trip.getSeats());
        map.put(FirebaseFields.P_TRIP_PRICE, trip.getTripPrice());
        map.put(FirebaseFields.PASSENGER_BOOKING_FEE,(trip.getTripPrice()*FirebaseConstants.FIXED_RATE_PASSENGER_CUT));
        map.put(FirebaseFields.LUGGAGE,trip.getLuggage());
        map.put(FirebaseFields.PRIVACY,trip.isPrivacy());
        map.put(FirebaseFields.DRIVER,trip.getDriverUid());
        map.put(FirebaseFields.DEPARTURETIME,new Date());




        return map;
        // for end trip
        // String IS_TRIP_NETWORK = "is_trip_network";
        //        public static final String PASSENGER_IDS="passenger_ids";
        //        public static final String START_LOCATION = "start_locaion";
        //        public static final String END_LOCATION = "is_promotion_used";
        //        public static final String TRIP_CASH_PAID = "is_promotion_used";

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent myTrips=new Intent(getApplicationContext(),My_trips.class);
                        startActivity(myTrips);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    private void checkGPS() {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }


    }

    private void getStartLocationSnapshot() {
        getMyDefaultLocation(this, new Callback() {
            @Override
            public void onSuccess(Object object) {
                currentLocation=(Location) object;
                String path= FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/"+FirebaseConstants.ONGOING_TRIP+"/"+trip.getTripID();
                putStartLocationInDatabase(path);
            }

            @Override
            public void onError(Object object) {}});
    }

    private void putStartLocationInDatabase(String path) {
        setDocument(createOngoingTrip(), createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                for (Passenger passenger:passengers){
                    String path= FirebaseConstants.PASSENGERS+"/"+passenger.getUsername()+"/"+FirebaseConstants.ONGOING_TRIP+"/"+trip.getTripID();
                    putStartLocationInPassengerDatabase(path);
                }
                setNotification();
                redirectToOngoingTripPage();


            }

            @Override
            public void onError(Object object) {

            }
        });
    }

    private void redirectToOngoingTripPage() {
        Intent intent=new Intent(My_trips_expanded.this,OngoingTrip.class);
        LatLng point=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        trip.setStartingPoint(point);

        if(mathsUtil.getDistanceFromUserPoints(point,trip.getSourcePoint())<1){
            intent.putExtra(CHOSEN_TRIP,trip);
            Bundle startingPoint = new Bundle();
            startingPoint.putParcelable("start", point);

            intent.putExtra(STARTING_LOCATION,startingPoint);
            ArrayList<String> passengersIds=new ArrayList<>();
            for (Passenger passenger:passengers)
                passengersIds.add(passenger.getUsername());

            intent.putStringArrayListExtra(PASSENGERS, passengersIds);

            startActivity(intent);
        }
        else {
            Toast.makeText(this, "You are not in the starting location, please start the trip there", Toast.LENGTH_SHORT).show();
        }

    }

    private void setNotification() {

    }

    private void putStartLocationInPassengerDatabase(String path) {
        setDocument(createOngoingTrip(), createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
            }

            @Override
            public void onError(Object object) {

            }
        });
    }


    private void setTextData() {
        source.setText(trip.getDriverSource());
        destination.setText(trip.getDriverDestination());
        int day=timePickerObject.getDay();
        int month=timePickerObject.getMonth();
        int year=timePickerObject.getYear();
        int hour=timePickerObject.getHour();
        int minute= timePickerObject.getMinute();
        departure_time.setText(day+"/"+month+"/"+year+" at "+hour+":"+minute+" hours");

        //departure_time.setText(SimpleDateFormat.getInstance().format(new Date()));//trip.getTimePickerObject().getDate()
        if (!trip.isPrivacy())
            privacy.setText("network");
        else
            privacy.setText("Everyone");

        if (!isTripWithinTime())
            startTrip.setVisibility(View.INVISIBLE);
        //else
          //  startTrip.setVisibility(View.VISIBLE);

        no_of_seats_offered.setText(String.valueOf(trip.getSeats()));
        luggage.setText(trip.getLuggage());
        cash_to_be_paid.setText(String.valueOf(trip.getTripPrice()));

        //no_of_requests.setText(trip.getNumberOfRequests());

    }

    private void initializeVariables() {
        linkViews();
        getValues();
        setTextData();


    }

    private void linkViews() {
        source=findViewById(R.id.TripSource_expanded);
        destination=findViewById(R.id.TripDestination_expanded);
        departure_time=findViewById(R.id.Trip_time_expanded);
        privacy=findViewById(R.id.privacy_setting_expanded);
        no_of_passengers=findViewById(R.id.passengers_no__expanded);
        no_of_seats_offered=findViewById(R.id.seats_booked_expanded);
        cash_to_be_paid=findViewById(R.id.CashPaid_expanded);
        btnRequests=findViewById(R.id.btn_viewRequests);
        luggage=findViewById(R.id.luggage);
        passengersList=findViewById(R.id.passengers_list);
        no_passengers_booked=findViewById(R.id.no_passengers_booked_label);
        cancel=findViewById(R.id.txt_cancel_trip);
        startTrip=findViewById(R.id.startTrip);
        passengers=new ArrayList<>();
    }

    private void getValues() {
        trip=getIntent().getExtras().getParcelable(CHOSEN_TRIP);
        user=getIntent().getParcelableExtra(USER_ACCOUNT);
        timePickerObject=getIntent().getParcelableExtra(TRIP_TIME);
        Log.d("trip", "getValues: "+trip.getTripID());
        if (trip.isPrivacy())
            userDetailsPath= FirebaseConstants.RIDES+"/"+trip.getTripID()+"/"+FirebaseConstants.BOOKINGS;
            //userDetailsPath= FirebaseConstants.NETWORKS+"/"+trip.getNetworkId()+"/"+FirebaseConstants.RIDES+"/"+trip.getTripID()+"/"+FirebaseConstants.BOOKINGS;
        else
            userDetailsPath= FirebaseConstants.RIDES+"/"+trip.getTripID()+"/"+FirebaseConstants.BOOKINGS;


    }
}
package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.*;
import static com.example.poolrdriver.Firebase.GoogleMaps.getLocationFromAddress;
import static com.example.poolrdriver.Firebase.GoogleMaps.getUrl;
import static com.example.poolrdriver.util.AppSystem.createDialog;
import static com.example.poolrdriver.util.AppSystem.getMyDefaultLocation;
import static com.example.poolrdriver.util.AppSystem.requestGps;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.DirectionHelpers.FetchURL;
import com.example.poolrdriver.DirectionHelpers.TaskLoadedCallback;
import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.adapters.AutoSuggestionsAdapter;
import com.example.poolrdriver.classes.Network;
import com.example.poolrdriver.classes.Trips;
import com.example.poolrdriver.classes.dateFormat;
import com.example.poolrdriver.classes.private_rides;
import com.example.poolrdriver.models.TimePickerObject;
import com.example.poolrdriver.models.TripModel;
import com.example.poolrdriver.util.LoadingDialog;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class onLocationPressedActivity extends AppCompatActivity implements TaskLoadedCallback {
    private EditText TimePicker,GroupPicker;
    Spinner networks_spinner ;
    private AutoCompleteTextView destination,source;
    private String DateOfTravel;
    private String TimeOfTravel;
    private  TextView datePicked,timePicked,confirmDate,confirmTime,timeFrom,timeTo,privacy;
    Button confirm,btnEveryoneVisibility,btnMyNetworksVisibility,post;
    CalendarView calendarView;
    android.widget.TimePicker timePicker;
    private dateFormat chosenDate,chosenTempDate;
    private boolean isTripPublic;
    private LoadingDialog loadingDialog;
    LatLng sourcePoint, destinationPoint;
    private final List<MarkerOptions> markerOptionsList = new ArrayList<>();
    TabLayout seatsOffered;
    TimePickerObject time;
    TripModel trip;
    private PolylineOptions polylineOptions;
    String locationFromString, locationToString;
    private static final String SOURCE_DEFAULT_TEXT = "My location";
    private static final String PRIVACY="Everyone";
    private static final String TRIP_EXTRA="trip";
    private static final String TAG ="locationSearch" ;
    Location currentLocation;
    private Context mContext;
    private List<Network> networks;
    public static TextView txtNetwork_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_location_pressed);
        initializeData();
        initializeGpsData();
        setListeners();
    }

    private void initializeGpsData() {
        requestGps(this);
        getMyDefaultLocation(this, new Callback() {
            @Override
            public void onSuccess(Object object) {
                currentLocation=(Location) object;
                //put current location as source
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                    source.setText(addresses.get(0).getAddressLine(0));
                    destination.requestFocus();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Object object) {}});
    }

    private void setViews() {
        //define views
        TimePicker=findViewById(R.id.time_picker_post_rides);
        privacy=findViewById(R.id.who_to_post_to);
        timeFrom=findViewById(R.id.textViewTimeChosenFrom);
        timeTo=findViewById(R.id.textViewTimeChosenTo);
        post=findViewById(R.id.btn_post_ride);
        loadingDialog=new LoadingDialog(this);
        source = findViewById(R.id.locationWhereFrom);
        destination = findViewById(R.id.locationWhereTo);
        seatsOffered=findViewById(R.id.seats_offered);
        networks_spinner = findViewById(R.id.network_spinner);
        txtNetwork_spinner=findViewById(R.id.txt_network_spinner);

    }


    private void setAdapters() {
        destination.setAdapter(new AutoSuggestionsAdapter(getApplicationContext(), android.R.layout.simple_list_item_1));
        source.setAdapter(new AutoSuggestionsAdapter(getApplicationContext(), android.R.layout.simple_list_item_1));

    }

    private void setListeners() {
        TimePicker.setOnClickListener(v14 -> {
            DateOfTravel="today"; TimeOfTravel="now";
            showDialogLayout(R.layout.bottom_sheet_layout_time);}
        );

        privacy.setOnClickListener(v -> { showDialogLayout(R.layout.bottom_sheet_layout_trip_visibility);});
        post.setOnClickListener(v ->setPostDetails());
    }

    private void setPostDetails() {
        loadingDialog.startLoadingAlertDialog();
        getTripDetails();
        

    }

    private void getTripDetails() {
        getTextFromUI();
        initializeUIText();
    }


    private void getTextFromUI() {
       locationFromString=source.getText().toString();
       locationToString  = destination.getText().toString();
    }


    private void initializeUIText() {
      try {
          sourcePoint = getLocationFromAddress(locationFromString,mContext);
          destinationPoint = getLocationFromAddress(locationToString,mContext);
          getPolylineValues();
      }
      catch (Exception e){
          e.printStackTrace();
          Toast.makeText(mContext, "write a valid address", Toast.LENGTH_SHORT).show();
      }
      loadingDialog.dismissDialog();
    }



    private void initializeData() {
        setViews();
        setValues();
        setAdapters();
        getUserNetworks();
    }

    private void getUserNetworks() {
        String path= FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/"+FirebaseConstants.NETWORKS;
        getDocumentsInCollection(createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                QuerySnapshot snapshot=((Task<QuerySnapshot>) object).getResult();

                getNetworks(snapshot);

            }

            @Override
            public void onError(Object object) {
                Toast.makeText(onLocationPressedActivity.this, "error ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getNetworks(QuerySnapshot querysnapshot) {

        for (DocumentSnapshot snapshot:querysnapshot){
            Network network=new Network();
            network.setNetworkName(String.valueOf(snapshot.get(FirebaseFields.NETWORK_NAME)));
            network.setNetworkUID(String.valueOf(snapshot.get(FirebaseFields.NETWORK_UID)));
            network.setNetworkTravelAdminUID(String.valueOf(snapshot.get(FirebaseFields.NETWORK_TRAVEL_ADMIN)));
            networks.add(network);
        }


        setUpPrivacyOptions();


    }

    private void setUpPrivacyOptions() {
        setUpPrivacyAdapter(populateArrayList());

    }

    private void setUpNoVisiblePrivacyAdapter() {
        networks_spinner.setVisibility(View.GONE);
        txtNetwork_spinner.setVisibility(View.GONE);
    }

    private void setUpVisiblePrivacyAdapter() {
        networks_spinner.setVisibility(View.VISIBLE);
        txtNetwork_spinner.setVisibility(View.VISIBLE);
        networks_spinner.setSelection(0);
    }

    private void setUpPrivacyAdapter(List<String> Networks) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, Networks);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        networks_spinner.setAdapter(adapter);
        //setUpVisibilityOfDialog

    }

    private List<String> populateArrayList() {
        List<String> Networks =  new ArrayList<>();
        Networks.add("all");
        for (Network network:networks)
            Networks.add(network.getNetworkName());
        Toast.makeText(onLocationPressedActivity.this, "size is "+Networks.size(), Toast.LENGTH_SHORT).show();

        return Networks;
    }

    private void setValues() {
       trip =new TripModel();
       networks=new ArrayList<>();
       mContext=getApplicationContext();
       isTripPublic=true;
       setDefaultDate();

    }

    private void setDefaultDate() {
        setDefaultDateUI();
        setDefaultDateBackend();

    }



    private void setDefaultDateUI() {
        time=new TimePickerObject();
        Calendar calendar=Calendar.getInstance();
        setTime(calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
    }

    private void setDefaultDateBackend() {

        time.setDefaultCalendarDateAndTime();
    }


    private void getPolylineValues() {
        Log.d(TAG, "postRides: "+ sourcePoint);
        Log.d(TAG, "postRides: "+ destinationPoint);
        markerOptionsList.add(new MarkerOptions().position(sourcePoint));
        markerOptionsList.add( new MarkerOptions().position(destinationPoint));
        new FetchURL(onLocationPressedActivity.this)
                .execute(getUrl(sourcePoint,destinationPoint,"driving"),"driving");
    }


    private void initializeVariables(Dialog dialog, int layout) {

        if (layout == R.layout.bottom_sheet_layout_time) {
            datePicked =  dialog.findViewById(R.id.date_picked);
            timePicked =  dialog.findViewById(R.id.time_picked);
            confirm =  dialog.findViewById(R.id.btn_select_dates);
        }
        else if(layout==R.layout.date_picler_ui_screen) {
            confirmDate=dialog.findViewById(R.id.calender_confirm_date);
            calendarView= dialog.findViewById(R.id.calendarView);

            calendarView.setMinDate(System.currentTimeMillis() - 1000);
        }
        else if(layout==R.layout.bottom_sheet_layout_trip_visibility) {
            btnEveryoneVisibility=dialog.findViewById(R.id.btn_select_everyone);
            btnMyNetworksVisibility= dialog.findViewById(R.id.btn_select_contacts);

            if (networks.size()==0)btnMyNetworksVisibility.setVisibility(View.GONE);
            else btnMyNetworksVisibility.setVisibility(View.VISIBLE);


            btnEveryoneVisibility.setOnClickListener(v -> {
                isTripPublic=true;
                setUpNoVisiblePrivacyAdapter();
                privacy.setText("Visible to Everyone");
                dialog.dismiss();});

            btnMyNetworksVisibility.setOnClickListener(v -> {
                isTripPublic=false;
                setUpVisiblePrivacyAdapter();
                privacy.setText("Visible to my networks");
                dialog.dismiss();
            });

        }
        else {
            confirmTime=dialog.findViewById(R.id.confirmTime);
            timePicker=dialog.findViewById(R.id.timeView);
        }

    }

    private void showDialogLayout(int layout) {
        Dialog dialog=createDialog(onLocationPressedActivity.this,layout);
        initializeVariables(dialog,layout);
        setDialogOnClickListeners(dialog,layout);

    }

    @SuppressLint("SetTextI18n")
    private void setDialogOnClickListeners(Dialog dialog, int layout) {
        if (layout == R.layout.bottom_sheet_layout_time) {
            confirm.setOnClickListener(v -> {TimePicker.setText("Leaving on " + DateOfTravel + " at " + TimeOfTravel);dialog.dismiss();});
            datePicked.setOnClickListener(v -> showDialogLayout(R.layout.date_picler_ui_screen));
            timePicked.setOnClickListener(v -> showDialogLayout(R.layout.time_picker_ui_screen));
        }

        if (layout == R.layout.date_picler_ui_screen) {

            calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                DateOfTravel = dayOfMonth + "/" + month + "/" + year;datePicked.setText(DateOfTravel);
                time.setCalendarDate(dayOfMonth,month,year);
            });

            confirmDate.setOnClickListener(v ->dialog.dismiss());
        }

        if (layout==R.layout.time_picker_ui_screen){

            timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> setTime(hourOfDay,minute,true));
            confirmTime.setOnClickListener(v -> dialog.dismiss());
        }

    }

    @SuppressLint("SetTextI18n")
    private void setTime(int hourOfDay, int minute, boolean isFromDialog) {
        //time from and to setup
        if(minute<10 && hourOfDay>0){timeFrom.setText((hourOfDay - 1) + ":" + (60 - (10 - minute)) + "hrs");timeTo.setText((hourOfDay)+":"+(minute+10)+"hrs");}
        else if (minute<10 && hourOfDay==0){timeFrom.setText(23+":"+(60-(10-minute))+"hrs");timeTo.setText((hourOfDay)+":"+(minute+10)+"hrs");}
        else if (minute>=50 && hourOfDay>0 && hourOfDay!=23){timeFrom.setText(hourOfDay+":"+(minute-10)+"hrs");timeTo.setText((hourOfDay+1)+":"+((minute+10)-60)+"hrs");}
        else if(minute>=50 && hourOfDay==23){timeFrom.setText(hourOfDay+":"+(minute-10)+"hrs");timeTo.setText(00+":"+((minute+10)-60)+"hrs");}
        else {timeFrom.setText(hourOfDay+":"+(minute-10)+"hrs");timeTo.setText((hourOfDay)+":"+(minute+10)+"hrs");}

        if (isFromDialog){
            timePicked.setText( hourOfDay+":"+minute+"hrs");
            TimeOfTravel=hourOfDay+":"+minute+"hrs";
        }
        time.setCalendarTime(hourOfDay, minute);

    }




    @Override
    public void onTaskDone(Object... values) {
        loadingDialog.dismissDialog();polylineOptions=(PolylineOptions) values[0];trip.setDriverRoute(polylineOptions);postRides();
    }

    private void postRides() {

        Intent next = new Intent(getApplicationContext(), price_split.class);

        Log.d(TAG, "postRides: "+ polylineOptions.getPoints());
        trip.setDriverSource(locationFromString);
        trip.setDriverDestination(locationToString);
        trip.setDriverUid(new User().getUID());
        trip.setPrivacy(isTripPublic);
        trip.setDestinationpoint(destinationPoint);
        trip.setSourcepoint(sourcePoint);

        if (!isTripPublic && networks_spinner.getSelectedItemPosition()!=0)
           trip.setNetworkId(networks.get(networks_spinner.getSelectedItemPosition()-1).getNetworkUID());
        else if (networks_spinner.getSelectedItemPosition()==0){
            ArrayList<String> networkIDS=new ArrayList<>();
            for (Network network:networks)
                networkIDS.add(network.getNetworkUID());

            next.putStringArrayListExtra("networks",networkIDS);
        }



        trip.setSeats(getSeatsChosen());
        trip.setTimePickerObject(time);


        next.putExtra("POLYLINE",polylineOptions);
        next.putExtra("destinationPoint",destinationPoint);
        next.putExtra("sourcePoint",sourcePoint);
        next.putExtra("trip",(Parcelable) trip);

        startActivity(next);
        
    }



    private int getSeatsChosen() {return seatsOffered.getSelectedTabPosition()+1;}
}
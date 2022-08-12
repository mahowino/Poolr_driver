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
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
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
import com.example.poolrdriver.classes.Trips;
import com.example.poolrdriver.classes.dateFormat;
import com.example.poolrdriver.classes.private_rides;
import com.example.poolrdriver.models.TimePickerObject;
import com.example.poolrdriver.util.LoadingDialog;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class onLocationPressedActivity extends AppCompatActivity implements TaskLoadedCallback {
    private EditText TimePicker,GroupPicker;
    private AutoCompleteTextView destination,source;
    private String DateOfTravel;
    private String TimeOfTravel;
    private  TextView datePicked,timePicked,confirmDate,confirmTime,timeFrom,timeTo,privacy;
    Button confirm,btnEveryoneVisibility,btnMyContactsVisibility,post;
    CalendarView calendarView;
    android.widget.TimePicker timePicker;
    private dateFormat chosenDate,chosenTempDate;
    private LoadingDialog loadingDialog;
    LatLng sourcePoint, destinationPoint;
    private final List<MarkerOptions> markerOptionsList = new ArrayList<>();
    TabLayout seatsOffered;
    TimePickerObject time;
    Trips trip;
    private PolylineOptions polylineOptions;
    String locationFromString, locationToString;
    private static final String SOURCE_DEFAULT_TEXT = "My location";
    private static final String PRIVACY="Everyone";
    private static final String TRIP_EXTRA="trip";
    private static final String TAG ="locationSearch" ;
    Location currentLocation;
    private Context mContext;

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
            public void onSuccess(Object object) {currentLocation=(Location) object;}

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

    }


    private void setAdapters() {
        destination.setAdapter(new AutoSuggestionsAdapter(getApplicationContext(), android.R.layout.simple_list_item_1));
        source.setAdapter(new AutoSuggestionsAdapter(getApplicationContext(), android.R.layout.simple_list_item_1));
        destination.requestFocus();
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
        if (source.getText().toString().isEmpty())
            locationFromString= source.getHint().toString();
        else locationFromString=source.getText().toString();

        locationToString  = destination.getText().toString();
    }


    private void initializeUIText() {
        if (locationFromString.equals(SOURCE_DEFAULT_TEXT)) {
            try {getMyDefaultLocation(this, new Callback() {
                @Override
                public void onSuccess(Object object) {
                    currentLocation=(Location)object;
                    setDefaultLocationLatLong();
                    setDefaultLocationString();
                    getPolylineValues();
                }
                
                @Override
                public void onError(Object object) {

                }
            });} catch (Exception exception) {exception.printStackTrace();}

        }
        else {sourcePoint = getLocationFromAddress(locationToString,mContext);getPolylineValues();}
        destinationPoint = getLocationFromAddress(locationToString,mContext);
        
    }

    private void setDefaultLocationString() {
        Geocoder geocoder = new Geocoder(onLocationPressedActivity.this, Locale.getDefault());
        try {
            List<Address> addresses=  geocoder.getFromLocation(sourcePoint.latitude, sourcePoint.longitude, 1);
            for (Address address:addresses) locationFromString=address.toString();
        }
        catch (IOException e) {e.printStackTrace();}
    }

    private void setDefaultLocationLatLong() {
        //get Adress 
        sourcePoint = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
    }

    private void initializeData() {
        setViews();
        setValues();
        setAdapters();
        getUserNetworks();
    }

    private void getUserNetworks() {
        String path= FirebaseConstants.NETWORKS;
        getDocumentsFromQueryInCollection(createArrayQuery(createCollectionReference(path), FirebaseFields.NETWORK_MEMBERS,new User().getUID()), new Callback() {
            @Override
            public void onSuccess(Object object) {
                //todo: set privacy depending on networks in(if networks exist)
                //trip.setNetworkID((QuerySnapshot) object);
                //network exists

            }

            @Override
            public void onError(Object object) {
                Toast.makeText(onLocationPressedActivity.this, "error ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setValues() {
       trip =new Trips();
        setDefaultDate();
        mContext=getApplicationContext();
    }

    private void setDefaultDate() {
        setDefaultDateUI();
        setDefaultDateBackend();

    }



    private void setDefaultDateUI() {
        Calendar calendar=Calendar.getInstance();
        setTime(calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE));
    }

    private void setDefaultDateBackend() {
        time=new TimePickerObject();
        time.setDefaultCalendarDateAndTime();
    }


    private void getPolylineValues() {
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
            btnMyContactsVisibility= dialog.findViewById(R.id.btn_select_contacts);

            btnEveryoneVisibility.setOnClickListener(v -> {GroupPicker.setText("Visible to Everyone");dialog.dismiss();});
            btnMyContactsVisibility.setOnClickListener(v -> {GroupPicker.setText("Visible to my contacts");dialog.dismiss();});

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

            timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> setTime(hourOfDay,minute));
            confirmTime.setOnClickListener(v -> dialog.dismiss());
        }

    }

    @SuppressLint("SetTextI18n")
    private void setTime(int hourOfDay, int minute) {
        //time from and to setup
        if(minute<10 && hourOfDay>0){timeFrom.setText((hourOfDay - 1) + ":" + (60 - (10 - minute)) + "hrs");timeTo.setText((hourOfDay)+":"+(minute+10)+"hrs");}
        else if (minute<10 && hourOfDay==0){timeFrom.setText(23+":"+(60-(10-minute))+"hrs");timeTo.setText((hourOfDay)+":"+(minute+10)+"hrs");}
        else if (minute>=50 && hourOfDay>0 && hourOfDay!=23){timeFrom.setText(hourOfDay+":"+(minute-10)+"hrs");timeTo.setText((hourOfDay+1)+":"+((minute+10)-60)+"hrs");}
        else if(minute>=50 && hourOfDay==23){timeFrom.setText(hourOfDay+":"+(minute-10)+"hrs");timeTo.setText(00+":"+((minute+10)-60)+"hrs");}
        else {timeFrom.setText(hourOfDay+":"+(minute-10)+"hrs");timeTo.setText((hourOfDay)+":"+(minute+10)+"hrs");}

        timePicked.setText( hourOfDay+":"+minute+"hrs");
        time.setCalendarTime(hourOfDay, minute);

    }




    @Override
    public void onTaskDone(Object... values) {
        loadingDialog.dismissDialog();polylineOptions=(PolylineOptions) values[0];postRides();
    }

    private void postRides() {
        

        trip.setDriverRoute(polylineOptions.getPoints());
        trip.setDriverSource(locationFromString);
        trip.setDriverDestination(locationToString);
        trip.setDriverUid(new User().getUID());
        trip.setRidePublic(privacy.getText().toString().equals(PRIVACY));
        trip.setSeats(getSeatsChosen());
        trip.setDate(time);


        Intent next = new Intent(getApplicationContext(), price_split.class);
        next.putExtra("trip", (Parcelable) trip);
        startActivity(next);
        
    }

    private int getSeatsChosen() {return seatsOffered.getSelectedTabPosition()+1;}
}
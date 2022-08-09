package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.GoogleMaps.getLocationFromAddress;
import static com.example.poolrdriver.Firebase.GoogleMaps.getUrl;
import static com.example.poolrdriver.util.AppSystem.createDialog;
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.poolrdriver.DirectionHelpers.FetchURL;
import com.example.poolrdriver.DirectionHelpers.TaskLoadedCallback;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.adapters.AutoSuggestionsAdapter;
import com.example.poolrdriver.classes.dateFormat;
import com.example.poolrdriver.classes.private_rides;
import com.example.poolrdriver.util.LoadingDialog;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.internal.location.zzz;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

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
    private  TextView datePicked,timePicked,confirmDate,confirmTime,timeFrom,timeTo;
    Button confirm,btnEveryoneVisibility,btnMyContactsVisibility,post;
    CalendarView calendarView;
    android.widget.TimePicker timePicker;
    private dateFormat chosenDate,chosenTempDate;
    private LoadingDialog loadingDialog;
    LatLng sourcePoint, destinationPoint;
    private final List<MarkerOptions> markerOptionsList = new ArrayList<>();
    private PolylineOptions polylineOptions;
    String locationFromString, locationToString;
    private static final String SOURCE_DEFAULT_TEXT = "My location";
    private static final String TAG ="locationSearch" ;
    Location currentLocation;
    private static final int REQUEST_CODE = 1001;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Context mContext;
    private ArrayList<private_rides> chosenRides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_location_pressed);
        initializeData();
        getMyDefaultLocation();
        requestGps();
        setListeners();
    }



    private void setViews() {
        //define views
        TimePicker=findViewById(R.id.edittext_time_picker2);
        GroupPicker=findViewById(R.id.who_to_post_to);
        timeFrom=findViewById(R.id.textViewTimeChosenFrom);
        timeTo=findViewById(R.id.textViewTimeChosenTo);
        post=findViewById(R.id.btn_post_ride);
        loadingDialog=new LoadingDialog(this);

        source = findViewById(R.id.locationWhereFrom);
        destination = findViewById(R.id.locationWhereTo);

    }



    private void requestGps() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices.getSettingsClient(onLocationPressedActivity.this).checkLocationSettings(builder.build()).addOnCompleteListener(task -> {promptUserForGps(task);});
    }

    private void promptUserForGps(Task<LocationSettingsResponse> task) {
        try {task.getResult(ApiException.class);}
        catch (ApiException exception) {
            switch (exception.getStatusCode()) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                    try {
                        // Cast to a resolvable exception.
                        ResolvableApiException resolvable = (ResolvableApiException) exception;
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        resolvable.startResolutionForResult(
                                onLocationPressedActivity.this,
                                LocationRequest.PRIORITY_HIGH_ACCURACY);
                    } catch (IntentSender.SendIntentException | ClassCastException ignored) {}
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: break;
            }
        }
    }

    private void setAdapters() {
        destination.setAdapter(new AutoSuggestionsAdapter(getApplicationContext(), android.R.layout.simple_list_item_1));
        source.setAdapter(new AutoSuggestionsAdapter(getApplicationContext(), android.R.layout.simple_list_item_1));
        destination.requestFocus();
    }


    private void setListeners() {
        TimePicker.setOnClickListener(v14 -> {DateOfTravel="today"; TimeOfTravel="now";showDialogLayout(R.layout.bottom_sheet_layout_time);});
        GroupPicker.setOnClickListener(v -> { showDialogLayout(R.layout.bottom_sheet_layout_trip_visibility);});
        post.setOnClickListener(v ->setPostDetails());

    }


    private void setPostDetails() {
        loadingDialog.startLoadingAlertDialog();
        getTripDetails();
        getPolylineValues();

    }

    private void getTripDetails() {
        getTextFromUI();
        initializeUIText();
    }

    private void getTextFromUI() {

        locationFromString= source.getHint().toString();
        locationToString  = destination.getText().toString();

    }


    private void initializeUIText() {


        if (locationFromString.equals(SOURCE_DEFAULT_TEXT)) {
            try {getMyDefaultLocation();} catch (Exception exception) {exception.printStackTrace();}

            //todo:fix gps issue
            sourcePoint = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
            Geocoder geocoder = new Geocoder(onLocationPressedActivity.this, Locale.getDefault());

            try {geocoder.getFromLocation(sourcePoint.latitude, sourcePoint.longitude, 1);}
            catch (IOException e) {e.printStackTrace();}

        }
        else { String sourceDestination = source.getText().toString();sourcePoint = getLocationFromAddress(sourceDestination,mContext);}

        destinationPoint = getLocationFromAddress(locationToString,mContext);


    }

    private void initializeData() {setViews();setValues();setAdapters();}

    private void setValues() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //todo dates from mainpage
       // BestTimeForTrip = getIntent().getExtras().getParcelable("chosenDate");

        chosenRides = new ArrayList<>();
        mContext=getApplicationContext();
       // user=new User();

        boolean isDestinationPreset = getIntent().getBooleanExtra("isDestinationSelected", false);
        if (isDestinationPreset) destination.setText( getIntent().getExtras().getString("destinationAdress"));

    }




    private void getMyDefaultLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {if (location != null) currentLocation = location;});
    }

    private void getPolylineValues() {

        MarkerOptions place1 = new MarkerOptions().position(sourcePoint);
        MarkerOptions place2 = new MarkerOptions().position(destinationPoint);

        markerOptionsList.add(place1);
        markerOptionsList.add(place2);

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
            datePicked.setOnClickListener(v -> {setDefaultDays();showDialogLayout(R.layout.date_picler_ui_screen);});
            timePicked.setOnClickListener(v -> {setDefaultDays();showDialogLayout(R.layout.time_picker_ui_screen);});
        }

        if (layout == R.layout.date_picler_ui_screen) {

            calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

                DateOfTravel = dayOfMonth + "/" + month + "/" + year;datePicked.setText(DateOfTravel);
                chosenTempDate.setYear(String.valueOf(year));chosenTempDate.setMonth(String.valueOf(month));chosenTempDate.setDay(String.valueOf(dayOfMonth));
            });

            confirmDate.setOnClickListener(v ->dialog.dismiss());
        }

        if (layout==R.layout.time_picker_ui_screen){


            timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
                //time from and to setup
                if(minute<10 && hourOfDay>0){timeFrom.setText((hourOfDay-1)+":"+(60-(10-minute))+"hrs");timeTo.setText((hourOfDay)+":"+(minute+10)+"hrs");}
                else if (minute<10 && hourOfDay==0){timeFrom.setText(23+":"+(60-(10-minute))+"hrs");timeTo.setText((hourOfDay)+":"+(minute+10)+"hrs");}
                else if (minute>=50 && hourOfDay>0){timeFrom.setText(hourOfDay+":"+(minute-10)+"hrs");timeTo.setText((hourOfDay+1)+":"+((minute+10)-60)+"hrs");}
                else if(minute>=50 && hourOfDay==23){timeFrom.setText(hourOfDay+":"+(minute-10)+"hrs");timeTo.setText(00+":"+((minute+10)-60)+"hrs");}
                else {timeFrom.setText(hourOfDay+":"+(minute-10)+"hrs");timeTo.setText((hourOfDay)+":"+(minute+10)+"hrs");}



                TimeOfTravel= hourOfDay+":"+minute+"hrs"; timePicked.setText(TimeOfTravel);
                chosenTempDate.setHour(String.valueOf(hourOfDay));chosenTempDate.setMinute(String.valueOf(minute));
            });

           confirmTime.setOnClickListener(v -> dialog.dismiss());
        }

    }

    private void setDefaultDays(){
        Calendar calendar = Calendar.getInstance();
        chosenDate.setDay(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        chosenDate.setHour(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
        chosenDate.setMinute( String.valueOf(calendar.get(Calendar.MINUTE)));
        chosenDate.setMonth( String.valueOf( calendar.get(Calendar.MONTH)));
        chosenDate.setYear( String.valueOf(calendar.get(Calendar.YEAR)));

    }

    @Override
    public void onTaskDone(Object... values) {
        loadingDialog.dismissDialog();polylineOptions=(PolylineOptions) values[0];postRides();
    }

    private void postRides() {
        //create the intent
        Intent next = new Intent(getApplicationContext(), price_split.class);
        next.putExtra("markerOptionsplace1",markerOptionsList.get(0));
        next.putExtra("markerOptionsplace2",markerOptionsList.get(1));
        next.putExtra("location_from",locationFromString);
        next.putExtra("location_to",locationToString);
       // next.putExtra("routes", (Parcelable) chosenRides);
        next.putExtra("polyline",polylineOptions);
        Log.d(TAG, "postRides: "+polylineOptions.getPoints());

        startActivity(next);
    }
}
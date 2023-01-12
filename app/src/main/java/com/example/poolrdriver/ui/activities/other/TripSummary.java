package com.example.poolrdriver.ui.activities.other;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createDocumentReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.setDocument;
import static com.example.poolrdriver.util.AppSystem.createDialog;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Abstract.Callback;
import com.example.poolrdriver.Abstract.FirebaseConstants;
import com.example.poolrdriver.Abstract.Constants.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.R;
import com.example.poolrdriver.classes.models.TimePickerObject;
import com.example.poolrdriver.classes.models.TripModel;
import com.example.poolrdriver.util.AppSystem;
import com.ncorti.slidetoact.SlideToActView;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TripSummary extends AppCompatActivity {

    TextView tripSource,tripDestination,tripPrice,tripSeats,tripVisibility;
    AutoCompleteTextView tripTime;
    private final String CHOSEN_TRIP="chosen_trip";
    private final String PASSENGERS="passengers";
    private final String TRIP_TIME="trip_time";
    private final String  USER_ACCOUNT="signed_in_user";
    SlideToActView post;
    private String DateOfTravel;
    private String TimeOfTravel;
    TripModel trip;
    User user;
    private  TextView datePicked,timePicked,confirmDate,confirmTime,timeFrom,timeTo;
    Button confirm;
    android.widget.TimePicker timePicker;
    CalendarView calendarView;
    private TimePickerObject timePickerObject;
    private final String documentID=generateRandomId();

    TimePickerObject time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_summary);
        initializeData();
        setData();
        setTextData();
        setDefaultDate();
        setListeners();
    }
    private String generateRandomId(){
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(20);
        for(int i = 0; i < 20; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
    private void setListeners() {
        tripTime.setOnClickListener(v14 -> {
            DateOfTravel="today"; TimeOfTravel="now";
            showDialogLayout(R.layout.bottom_sheet_layout_time);}
        );
        post.setOnSlideCompleteListener(slideToActView -> postTripOnPublicTrips());

    }

    private void setData() {
        trip=getIntent().getExtras().getParcelable(CHOSEN_TRIP);
        user=getIntent().getParcelableExtra(USER_ACCOUNT);
        timePickerObject=getIntent().getParcelableExtra(TRIP_TIME);

    }

    private void setDefaultDate() {
        setDefaultDateUI();
        setDefaultDateBackend();

    }

    private void setDefaultDateBackend() {

        time.setDefaultCalendarDateAndTime();
    }

    private void setDefaultDateUI() {
        time=new TimePickerObject();
        Calendar calendar=Calendar.getInstance();
        setTime(calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
    }

    private void setTextData() {
        tripSource.setText(trip.getDriverSource());
        tripDestination.setText(trip.getDriverDestination());
        int day=timePickerObject.getDay();
        int month=timePickerObject.getMonth();
        int year=timePickerObject.getYear();
        int hour=timePickerObject.getHour();
        int minute= timePickerObject.getMinute();
        tripTime.setText(day+"/"+month+"/"+year+" at "+hour+":"+minute+" hours");

        //departure_time.setText(SimpleDateFormat.getInstance().format(new Date()));//trip.getTimePickerObject().getDate()
        if (!trip.isPrivacy())
            tripVisibility.setText("network");
        else
            tripVisibility.setText("Everyone");



        tripSeats.setText(String.valueOf(trip.getSeats()));
        //luggage.setText(trip.getLuggage());
       tripPrice.setText("KSH "+trip.getTripPrice()+" /= Per seat");

        //no_of_requests.setText(trip.getNumberOfRequests());

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
        else {
            confirmTime=dialog.findViewById(R.id.confirmTime);
            timePicker=dialog.findViewById(R.id.timeView);
        }

    }

    private void showDialogLayout(int layout) {
        Dialog dialog=createDialog(TripSummary.this,layout);
        initializeVariables(dialog,layout);
        setDialogOnClickListeners(dialog,layout);

    }

    @SuppressLint("SetTextI18n")
    private void setDialogOnClickListeners(Dialog dialog, int layout) {
        if (layout == R.layout.bottom_sheet_layout_time) {
            confirm.setOnClickListener(v -> {tripTime.setText("Leaving on " + DateOfTravel + " at " + TimeOfTravel);dialog.dismiss();});
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
     /*   //time from and to setup
        if(minute<10 && hourOfDay>0){timeFrom.setText((hourOfDay - 1) + ":" + (60 - (10 - minute)) + "hrs");timeTo.setText((hourOfDay)+":"+(minute+10)+"hrs");}
        else if (minute<10 && hourOfDay==0){timeFrom.setText(23+":"+(60-(10-minute))+"hrs");timeTo.setText((hourOfDay)+":"+(minute+10)+"hrs");}
        else if (minute>=50 && hourOfDay>0 && hourOfDay!=23){timeFrom.setText(hourOfDay+":"+(minute-10)+"hrs");timeTo.setText((hourOfDay+1)+":"+((minute+10)-60)+"hrs");}
        else if(minute>=50 && hourOfDay==23){timeFrom.setText(hourOfDay+":"+(minute-10)+"hrs");timeTo.setText(00+":"+((minute+10)-60)+"hrs");}
        else {timeFrom.setText(hourOfDay+":"+(minute-10)+"hrs");timeTo.setText((hourOfDay)+":"+(minute+10)+"hrs");}
*/
        if (isFromDialog){
            timePicked.setText( hourOfDay+":"+minute+"hrs");
            TimeOfTravel=hourOfDay+":"+minute+"hrs";
        }
        time.setCalendarTime(hourOfDay, minute);

    }
    private void postRouteOnNetworkTrips() {

       postTripOnNetworkTrips(trip.getNetworkId());

    }

    private void postTripOnNetworkTrips(String networkID){
        String path= FirebaseConstants.RIDES+"/"+documentID;

        setDocument(getMapData(), createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                // Toast.makeText(price_split.this, "Your ride has been successfully posted", Toast.LENGTH_SHORT).show();
                Toast.makeText(TripSummary.this, "Your ride has been successfully posted", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Object object) {
                Toast.makeText(TripSummary.this, "error ", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void postTripOnPublicTrips(){
        String path= FirebaseConstants.RIDES+"/"+documentID;

        setDocument(getMapData(), createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                // Toast.makeText(price_split.this, "Your ride has been successfully posted", Toast.LENGTH_SHORT).show();
                Toast.makeText(TripSummary.this, "Your ride has been successfully posted", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Object object) {
                Toast.makeText(TripSummary.this, "error ", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private Map<String,Object> getMapData() {

        //document creation
        Map<String, Object> map = new HashMap<>();
        map.put(FirebaseFields.DRIVER, new User().getUID());
        map.put(FirebaseFields.P_LOCATION_FROM, trip.getDriverSource());
        map.put(FirebaseFields.P_LOCATION_TO,trip.getDriverDestination());
        map.put(FirebaseFields.LOCATION_TO_GEOPOINT, AppSystem.convertLatLongToGeopoint(trip.getDestinationGeopoint()));
        map.put(FirebaseFields.LOCATION_FROM_GEOPOINT, AppSystem.convertLatLongToGeopoint(trip.getSourceGeopoint()));
        map.put(FirebaseFields.SEATS,trip.getSeats());
        map.put(FirebaseFields.P_TRIP_PRICE, trip.getTripPrice());
        map.put(FirebaseFields.PASSENGER_BOOKING_FEE,Math.ceil(trip.getTripPrice()*FirebaseConstants.FIXED_RATE_PASSENGER_CUT/ trip.getSeats()));
        map.put(FirebaseFields.LUGGAGE,trip.getLuggage());
        map.put(FirebaseFields.PRIVACY,trip.isPrivacy());
        map.put(FirebaseFields.DRIVER,trip.getDriverUid());
        map.put(FirebaseFields.DEPARTURETIME,time.getDate());
       // map.put(FirebaseFields.NUMBER_PLATE,chosenCarForTrip.getNumberplate());

        return map;
    }




    private void initializeData() {
        //view linking
        tripDestination=findViewById(R.id.txtTripDestinationSummary);
        tripSeats=findViewById(R.id.txtSeatsOfferedSummary);
        tripSource=findViewById(R.id.txtTripSourceSummary);
        tripPrice=findViewById(R.id.txtTripPriceSummary);
        tripVisibility=findViewById(R.id.txtVisibilityTripSummary);
        tripTime=findViewById(R.id.tripTimeTripSummary);
        timeFrom=findViewById(R.id.textViewTimeChosenFrom);
        timeTo=findViewById(R.id.textViewTimeChosenTo);
        post=findViewById(R.id.post_ride_confirm2);

        //intent retrieval
    }
}
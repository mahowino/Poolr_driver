package com.example.poolrdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.classes.Route;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.tabs.TabLayout;
import com.ncorti.slidetoact.SlideToActView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class price_split extends AppCompatActivity {

    TabLayout mTabLayout;
    ViewPager viewPager;
    TextView timePicker,datePicker;
    private String DateOfTravel;
    private String TimeOfTravel;
    private  TextView datePicked,timePicked,LocationFrom,LocationTo;
    private PolylineOptions polylineOptions;
    private MarkerOptions place1,place2;
    private List<Route> chosenRoutes;
    String location_from,location_to;
    SlideToActView  post_ride;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_split);
        initializeData();
        setUpListeners();



    }

    private void setUpListeners() {
        post_ride.setOnSlideCompleteListener(slideToActView -> {
            postTripOnDatabase();
        });

    }

    private void initializeData() {
        //initializations
        location_from=getIntent().getExtras().get("location_from").toString();
        location_to=getIntent().getExtras().get("location_to").toString();
       // chosenRoutes=getIntent().getExtras().getParcelable("routes");
        //mTabLayout.setupWithViewPager(viewPager);
        polylineOptions= (PolylineOptions) getIntent().getExtras().get("polyline");
        place1= (MarkerOptions) getIntent().getExtras().get("markerOptionsplace1");
        place2= (MarkerOptions) getIntent().getExtras().get("markerOptionsplace2");
        post_ride = findViewById(R.id.post_ride_confirm);
    }
    private void postTripOnDatabase(){
        String path= FirebaseConstants.RIDES;

        FirebaseRepository.setDocument(getMapData(), FirebaseRepository.createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(price_split.this, "Your ride has been successfully posted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Object object) {
                Toast.makeText(price_split.this, "error ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Map getMapData() {
        //document creation
        Map<String, Object> map = new HashMap<>();
        map.put(FirebaseFields.DRIVER, new User().getUID());
        map.put(FirebaseFields.P_LOCATION_FROM, location_from);
        map.put(FirebaseFields.P_LOCATION_TO,location_to);

        return map;
    }

}
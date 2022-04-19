package com.example.poolrdriver;


import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.example.poolrdriver.databinding.ActivityMapsBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapsActivity extends FragmentActivity{

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient client;
    private BottomSheetBehavior bottomSheetBehavior;
    private FrameLayout bottomSheet;
    private FloatingActionButton menuDrawer;
    private FloatingActionButton requests,wallet,trips;
    private ConstraintLayout profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initializations
        requests = (FloatingActionButton) findViewById(R.id.requests);
        wallet = (FloatingActionButton) findViewById(R.id.wallet);
        trips = (FloatingActionButton) findViewById(R.id.trips);
        //profile=findViewById(R.id.my_profile);

        //bottomSheet=findViewById(sheet_up_down);
        //menuDrawer=(FloatingActionButton)findViewById(R.id.menuDrawerBtn);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.mDrawerLayout);

        //bottomSheetBehavior=BottomSheetBehavior.from(bottomSheet);

        //onclick listeners


        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
                startActivity(intent);

            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), wallet.class);
                startActivity(intent);


            }
        });

        trips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), My_trips.class);
                startActivity(intent);

            }
        });


        // click event for show-dismiss bottom sheet
        //bottomSheetBehavior.setPeekHeight(500);
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        /*bottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                   // btn_bottom_sheet.setText("Close sheet");
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                   // btn_bottom_sheet.setText("Expand sheet");
                }
            }
        });
*/
// callback for do something
     /*   bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                       // btn_bottom_sheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                       // btn_bottom_sheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }


            }



            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
 */
    }
}

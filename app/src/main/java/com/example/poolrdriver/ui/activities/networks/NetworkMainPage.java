package com.example.poolrdriver.ui.activities.networks;

import static com.example.poolrdriver.Helpers.NetworksHelper.*;
import static com.example.poolrdriver.ui.activities.other.My_trips.FROM_NETWORK;
import static com.example.poolrdriver.classes.other.Passenger.getProfilePicture;
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.poolrdriver.Abstract.Callback;
import com.example.poolrdriver.Helpers.NetworksHelper;
import com.example.poolrdriver.Interfaces.FirebaseDocumentCount;
import com.example.poolrdriver.ui.activities.other.My_trips;
import com.example.poolrdriver.R;
import com.example.poolrdriver.classes.other.Passenger;
import com.example.poolrdriver.classes.models.Network;
import com.example.poolrdriver.ui.activities.other.NetworkViewMembers;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

public class NetworkMainPage extends AppCompatActivity {
    TextView networkName,btnViewRequests,txtAdminName,txtNoOfMembers,txtNoOfTrips;
    ImageView adminProfilePic;
    FloatingActionButton btnMembersList,btnTripsList,btnCallAdmin;
    Button viewTrips;
    Network network;
    Passenger admin;
    public static final String NETWORK="Network";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_main_page);
        initializeViews();
        setTexts();
        setListeners();
    }

    private void setListeners() {
        btnTripsList.setOnClickListener(v -> redirect(NetworkMainPage.this, My_trips.class));
        viewTrips.setOnClickListener(v -> redirectActivity(NetworkMainPage.this,My_trips.class));
        btnMembersList.setOnClickListener(v-> viewMembers());
        btnCallAdmin.setOnClickListener(v -> callAdmin());
    }

    private void callAdmin() {
        String number=admin.getPhoneNumber();
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
        startActivity(callIntent);
    }

    private void redirect(NetworkMainPage networkMainPage, Class<My_trips> my_tripsClass) {
        Intent intent=new Intent(networkMainPage,my_tripsClass);
        intent.putExtra(FROM_NETWORK,true);
        startActivity(intent);
    }

    private void viewMembers() {
        Intent intent=new Intent(NetworkMainPage.this, NetworkViewMembers.class);
        intent.putExtra(FROM_NETWORK,network);
        startActivity(intent);
    }

    private void initializeViews() {
        networkName=findViewById(R.id.txtNetworkNameMainPage);
        btnViewRequests=findViewById(R.id.btnViewRequests);
        txtAdminName=findViewById(R.id.txtTravelAdminName);
        btnMembersList=findViewById(R.id.btnCheckMembers);
        btnTripsList=findViewById(R.id.btnCheckUpcomingTrip);
        btnCallAdmin=findViewById(R.id.btnCallTravelAdmin);
        viewTrips=findViewById(R.id.btnViewTrips);
        txtNoOfTrips=findViewById(R.id.txtNoOfUpcomingTrips);
        txtNoOfMembers=findViewById(R.id.txtNoOfMembersInNetwork);
        adminProfilePic=findViewById(R.id.adminProfilePic);

        network=getIntent().getParcelableExtra(NETWORK);

    }

    private void setTexts() {
        networkName.setText(network.getNetworkName());
        getNumberOfMembersInNetwork(network, new FirebaseDocumentCount() {
            @Override
            public void onCount(long count) {
                txtNoOfMembers.setText(String.valueOf(count));
            }

            @Override
            public void onError(String error) {

            }
        });

        getNumberOfPostedTripsInNetwork(network, new FirebaseDocumentCount() {
            @Override
            public void onCount(long count) {
                txtNoOfTrips.setText(String.valueOf(count));
            }

            @Override
            public void onError(String error) {

            }
        });

        NetworksHelper.getTravelAdminDetails(network.getNetworkTravelAdminUID(), new Callback() {
            @Override
            public void onSuccess(Object object) {
                admin=(Passenger) object;
                txtAdminName.setText(admin.getNames());

                getProfilePicture(new Callback() {
                    @Override
                    public void onSuccess(Object object) {
                        Uri uri=(Uri)object;
                        Picasso.with(getApplicationContext())
                                .load(uri)
                                .into(adminProfilePic);
                    }

                    @Override
                    public void onError(Object object) {

                    }
                });

            }

            @Override
            public void onError(Object object) {

            }
        });

    }


}
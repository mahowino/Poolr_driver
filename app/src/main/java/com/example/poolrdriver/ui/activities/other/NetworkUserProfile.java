package com.example.poolrdriver.ui.activities.other;

import static com.example.poolrdriver.classes.other.Passenger.getProfilePicture;
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.poolrdriver.Abstract.Callback;
import com.example.poolrdriver.Helpers.NetworksHelper;
import com.example.poolrdriver.R;
import com.example.poolrdriver.classes.other.Passenger;
import com.example.poolrdriver.classes.models.Network;
import com.example.poolrdriver.ui.activities.networks.NetworkMainPage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class NetworkUserProfile extends AppCompatActivity {

    public static final String NETWORK_PASSENGER ="Network_passenger" ;
    public static final String NETWORK_FROM ="Network" ;
    ImageView profilePic;
    FloatingActionButton btnSuspendUser,btnDeleteUser;
    Network network;
    Passenger passenger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_user_profile);

        //set toolbar
        Toolbar toolbar=findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initializeViews();
        setTexts();
        setListeners();

    }

    private void setTexts() {
        getProfilePicture(passenger.getUsername(),new Callback() {
            @Override
            public void onSuccess(Object object) {
                Uri uri=(Uri)object;
                Picasso.with(getApplicationContext())
                        .load(uri)
                        .into(profilePic);
            }

            @Override
            public void onError(Object object) {

            }
        });

        profilePic.setContentDescription(passenger.getUsername());
    }

    private void setListeners() {
    btnSuspendUser.setVisibility(View.INVISIBLE);
    btnDeleteUser.setOnClickListener(v -> NetworksHelper.removeUserFromNetwork(passenger, network, NetworkUserProfile.this, new Callback() {
        @Override
        public void onSuccess(Object object) {
            Toast.makeText(NetworkUserProfile.this, "User removed from network", Toast.LENGTH_SHORT).show();
            redirectActivity(NetworkUserProfile.this, NetworkMainPage.class);
        }

        @Override
        public void onError(Object object) {
            Toast.makeText(NetworkUserProfile.this, "error removing user", Toast.LENGTH_SHORT).show();
        }
    }));

    }

    private void initializeViews() {
        profilePic=findViewById(R.id.networkUserImageView);
        btnDeleteUser=findViewById(R.id.btnRemoveUser);
        btnSuspendUser=findViewById(R.id.btnSuspendUser);
        passenger=getIntent().getParcelableExtra(NETWORK_PASSENGER);
        network=getIntent().getParcelableExtra(NETWORK_FROM);
    }
}
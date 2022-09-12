package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.adapters.ReviewAdapter;
import com.example.poolrdriver.classes.Passenger;
import com.example.poolrdriver.classes.Reviews;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PassengerProfile extends AppCompatActivity {
    TextView passengerName,bio,noReviews,noBio,rating;
    List<Reviews> reviews;
    Passenger passenger;
    String passengerID;
    RecyclerView reviewRecyclerView;
    ImageView passengerProfilePicture;
    FloatingActionButton btnCall;
    private final String CHOSEN_PASSENGER="chosen_passenger";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_profile);
        initializeViews();

        setOnClickListeners();


    }

    private void setOnClickListeners() {
        btnCall.setOnClickListener(v -> callSpecificUser());
    }

    private void callSpecificUser() {
        String number=passenger.getPhoneNumber();
        Intent callIntent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+number));
        startActivity(callIntent);
    }

    private void setUpProfile() {
        //name
        passengerName.setText(passenger.getNames());

        //Bio
        if (passenger.getBio()!=null)setUpBio();
        else setUpNoBio();

        //image
        Glide.with(this).load(passenger.getProfilePic()).into(passengerProfilePicture);

        //
    }

    private void setUpNoBio() {
        noBio.setVisibility(View.VISIBLE);
        bio.setVisibility(View.INVISIBLE);

    }

    private void setUpBio() {
        noBio.setVisibility(View.INVISIBLE);
        bio.setVisibility(View.VISIBLE);
        bio.setText(passenger.getBio());
    }

    private void setUpReviews() {
        if (reviews.size()>0)setUpReviewsAdapter();
        else setUpNoReviews();
    }

    private void setUpNoReviews() {
        noReviews.setVisibility(View.VISIBLE);
        reviewRecyclerView.setVisibility(View.INVISIBLE);

    }

    private void setUpReviewsAdapter() {
        noReviews.setVisibility(View.INVISIBLE);

        ReviewAdapter adapter=new ReviewAdapter(this,reviews);
        reviewRecyclerView.setAdapter(adapter);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        reviewRecyclerView.setVisibility(View.VISIBLE);
    }

    private void getExtraDataFromIntent() {
        passengerID=getIntent().getExtras().getString(CHOSEN_PASSENGER);
        getPassengerInformation();

    }

    private void getPassengerInformation() {
        String path= FirebaseConstants.PASSENGERS+"/"+passengerID;
        getDocument(createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<DocumentSnapshot> task=(Task<DocumentSnapshot>)object;
                passenger=new Passenger(task.getResult());
                //reviews=passenger.getReviews();
                setUpProfile();
                setUpReviews();
            }

            @Override
            public void onError(Object object) {

            }
        });
    }

    private void initializeViews() {
        setViewsLinkage();
        getExtraDataFromIntent();


    }

    private void setViewsLinkage() {
        passengerName=findViewById(R.id.txtPassengerName);
        bio=findViewById(R.id.txtPassengerBio);
        rating=findViewById(R.id.txtPassengerRating);
        noReviews=findViewById(R.id.txtNoReviewsYet);
        noBio=findViewById(R.id.txtNoBioYet);
        reviewRecyclerView=findViewById(R.id.viewReviews);
        passengerProfilePicture=findViewById(R.id.imgPassengerProfilePicture);
        btnCall=findViewById(R.id.btn_callPassengerSelected);
        reviews=new ArrayList<>();

    }
}
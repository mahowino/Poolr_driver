package com.example.poolrdriver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class choose_trip_to_display extends AppCompatActivity {

    private CardView male,female;
    private ConstraintLayout maleConstraint,femaleConstraint;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_choice);

        //declarations
        male=findViewById(R.id.card_male_choice);
        female=findViewById(R.id.card_female_choice);


        maleConstraint=findViewById(R.id.card_male_constraint);
        femaleConstraint=findViewById(R.id.card_female_constraint);

        male.setOnClickListener(view -> {

            maleConstraint.setBackground(getResources().getDrawable(R.drawable.background_white_blue_boarders));
            male.setCardElevation(10);

            femaleConstraint.setBackground(getResources().getDrawable(R.drawable.bckground_edittext_white));
            female.setCardElevation(2);

             Intent HomeAndWork=new Intent(getApplicationContext(),MyNetworks.class);
             startActivity(HomeAndWork);
        });

        female.setOnClickListener(view -> {
            femaleConstraint.setBackground(getResources().getDrawable(R.drawable.background_white_blue_boarders));
            female.setCardElevation(10);

            maleConstraint.setBackground(getResources().getDrawable(R.drawable.bckground_edittext_white));
            male.setCardElevation(2);

            Intent HomeAndWork=new Intent(getApplicationContext(),My_trips.class);
            startActivity(HomeAndWork);


        });


    }
}
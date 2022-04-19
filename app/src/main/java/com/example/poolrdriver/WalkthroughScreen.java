package com.example.poolrdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.poolrdriver.adapters.IntroViewPagerAdapter;
import com.example.poolrdriver.classes.screenItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class WalkthroughScreen extends AppCompatActivity {

    private int position=0;
    private ViewPager viewPager2;
    private Button next,getStarted;
    private TabLayout indicator;
    private Animation btn_animation;
    private TextView skip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough_screen);

    //checkIfActivityWasOpenedBefore
        if(restorePreviousPref()){
        Intent login=new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(login);
        finish();
    }

    //set up list
    List<screenItem> mList=new ArrayList<>();

    //add to list
        mList.add(new screenItem("Welcome to poolr Driver","car pooling on a whole new level",R.drawable.undraw_fast_car_));
        mList.add(new screenItem("What is poolr Driver?","poolr is a rideHailing app which lets you connect with friends to ofset costs without loosing comfort",R.drawable.undraw_social));
        mList.add(new screenItem("Our goal?","To offer a platform where passengers get a cheaper, comfortable and effective way to travel",R.drawable.undraw_shared_goals));
        mList.add(new screenItem("Welcome to poolr","car pooling on a whole new level",R.drawable.undraw_fast_car_));
        mList.add(new screenItem("What is poolr?","poolr is a rideHailing app which lets you connect with friends to ofset costs without loosing comfort",R.drawable.undraw_social));
        mList.add(new screenItem("Our goal?","To offer a platform where passengers get a cheaper, comfortable and effective way to travel",R.drawable.undraw_shared_goals));

        //set up viewpager
        //set up viewpager
    //set up viewpager
    viewPager2=findViewById(R.id.viewPager);

    //set up button
    next=(Button)findViewById(R.id.btn_next_viewPager);
    getStarted=(Button)findViewById(R.id.btn_getStarted);

    //setUpTextView
    skip=(TextView)findViewById(R.id.textViewbtn_skipToSignUp);

    //set up animations
    btn_animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.btn_getstarted);

    //add adapter
    IntroViewPagerAdapter adapter=new IntroViewPagerAdapter(this,mList);
        viewPager2.setAdapter(adapter);

    //set up tabLayout
    indicator=findViewById(R.id.tabIndicator);

    //set up with pager
        indicator.setupWithViewPager(viewPager2);

    //onClickListeners
        next.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            position=viewPager2.getCurrentItem();

            if (position<mList.size()){
                position++;
                viewPager2.setCurrentItem(position);
            }

            if(position==mList.size()){
                //TODO: create the Signup button from bottom.
                loadLastScreen();
            }

        }
    });

        getStarted.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //sharedPreferences
            setUpSharedPreferences();

            //setupOfIntent
            Intent startScreen=new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(startScreen);
            finish();


        }
    });


    //skip screens
        skip.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //sharedPreferences
            setUpSharedPreferences();

            //setupOfIntent
            Intent startScreen=new Intent(getApplicationContext(), SignUpScreen.class);
            startActivity(startScreen);
            finish();

        }
    });
    //otherListeners

        indicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if(tab.getPosition()==mList.size()-1){
                loadLastScreen();
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    });
}

    private boolean restorePreviousPref() {
        SharedPreferences preferences=getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isWalkThroughOpenedBefore=preferences.getBoolean("isIntroOpened",false);
        return isWalkThroughOpenedBefore;
    }

    private void setUpSharedPreferences() {

        SharedPreferences preferences=getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        editor.putBoolean("isIntroOpened",true);
        editor.commit();

    }

    private void loadLastScreen() {

        //views visibility
        getStarted.setVisibility(View.VISIBLE);
        next.setVisibility(View.INVISIBLE);
        indicator.setVisibility(View.INVISIBLE);

        //animation
        getStarted.setAnimation(btn_animation);

    }
}
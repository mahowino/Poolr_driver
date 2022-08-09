package com.example.poolrdriver;

import static com.example.poolrdriver.util.AppSystem.displayError;
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import static java.lang.Thread.sleep;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.userRegistrationJourney.verifyPhoneNumberScreen;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    //variable initializations
    private CharSequence NAME ;
    private String DESCRIPTION ;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeVariables();

        createNotificationChannel();

        runLoadingScreenThread((new User().getUser()));

    }

    private void initializeVariables() {
        NAME = getString(R.string.channel_name);
        DESCRIPTION = getString(R.string.channel_description);
        mContext=getApplicationContext();
    }

    private void runLoadingScreenThread(FirebaseUser user){

        //splashscreen loading thread
        Thread splashScreenDisplay=new Thread(){
            @Override
            public void run() {
                load_splashscreen(user);
                super.run();
            }
        };

        splashScreenDisplay.start();
    }
    private void load_splashscreen(FirebaseUser user){

        //running of thread
        try {
            sleep(2000);
            if(user!=null) {
                if (user.getPhoneNumber()==null){redirectActivity(MainActivity.this, verifyPhoneNumberScreen.class); finish();}
                else redirectActivity(MainActivity.this, MapsActivity.class); finish(); finish();}
            else{redirectActivity(MainActivity.this, WalkthroughScreen.class); finish();}
        }
        catch (Exception exception){
            displayError(MainActivity.this,mContext,exception);
        }

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel=setNotificationInitials(NAME,DESCRIPTION);
            if (channel!=null) registerChannelWithSystem(channel);

        }
    }

    private NotificationChannel setNotificationInitials(CharSequence name, String description){

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "NotificationChannel";
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
        }
        return channel;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void registerChannelWithSystem(NotificationChannel channel){
        // Register the channel with the system
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) notificationManager.createNotificationChannel(channel);

    }


}

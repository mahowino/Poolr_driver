package com.example.poolrdriver.ui.activities.other;

import static com.example.poolrdriver.util.AppSystem.displayError;
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import static java.lang.Thread.sleep;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.R;
import com.example.poolrdriver.ui.activities.userRegistrationJourney.verifyPhoneNumberScreen;
import com.example.poolrdriver.util.AppSystem;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    //variable initializations
    private CharSequence NAME ;
    private String DESCRIPTION ;
    private Context mContext;


    //Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                    Toast.makeText(getApplicationContext(), "The app will not show notifications", Toast.LENGTH_SHORT).show();
                }
            });

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeVariables();

        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            askNotificationPermission();
        }
        AppSystem.createNotificationChannel(this);

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

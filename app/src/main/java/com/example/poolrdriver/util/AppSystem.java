package com.example.poolrdriver.util;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.provider.MediaStore;
import android.telecom.Call;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.poolrdriver.R;
import com.example.poolrdriver.onLocationPressedActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;

public abstract class AppSystem {
    private static final int PICK_IMAGE = 100;
    private static final int REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationProviderClient;
    public static  void redirectActivity(Activity activity, Class nextActivity){Intent intent=new Intent(activity, nextActivity);activity.startActivity(intent);}

    public static void displayError(Activity activity,Context mContext, Exception e){
        activity.runOnUiThread(() -> {Toast.makeText(mContext,"Error loading splashscreen",Toast.LENGTH_LONG).show();e.printStackTrace();});}
    public static void loadFragment(int FragmentContainerView, Fragment fragment, FragmentManager manager) {manager.beginTransaction().replace(FragmentContainerView,fragment).commit();}
    public static boolean restorePreviousPref(String name, String Key, Context context) {SharedPreferences preferences=context.getSharedPreferences(name,MODE_PRIVATE);return  preferences.getBoolean(Key,false);}
    public static void setUpSharedPreferences(String name, String Key, Context context) {

        SharedPreferences preferences = context.getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Key, true);
        editor.commit();

    }
    public static void setAdapter(RecyclerView.Adapter adapter, RecyclerView view, Context context) {view.setAdapter(adapter);view.setLayoutManager(new LinearLayoutManager(context));}

    public  static void requestLocationPermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(activity, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    2);
        }

    }

    public static Dialog createDialog(Context context,int contentView){
        final Dialog dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(contentView);


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations= R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        return dialog;

    }
    public static void openGallery(Activity activity) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        activity.startActivityForResult(gallery, PICK_IMAGE);
    }
    public static void requestGps(Activity activity) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices.getSettingsClient(activity)
                .checkLocationSettings(builder.build())
                .addOnCompleteListener(task -> promptUserForGps(task,activity));
    }
    public static void getMyDefaultLocation(Activity activity,com.example.poolrdriver.Firebase.Callback callback) {
       FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        if (ActivityCompat.checkSelfPermission(
                activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {if (location != null) callback.onSuccess(location);});
    }


    public static void promptUserForGps(Task<LocationSettingsResponse> task,Activity activity) {
        try {task.getResult(ApiException.class);}
        catch (ApiException exception) {
            switch (exception.getStatusCode()) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                    try {
                        // Cast to a resolvable exception.
                        ResolvableApiException resolvable = (ResolvableApiException) exception;
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        resolvable.startResolutionForResult(
                                activity,
                                LocationRequest.PRIORITY_HIGH_ACCURACY);
                    } catch (IntentSender.SendIntentException | ClassCastException ignored) {}
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: break;
            }
        }
    }

}



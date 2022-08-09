package com.example.poolrdriver.util;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
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

public abstract class AppSystem {
    private static final int PICK_IMAGE = 100;
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


}



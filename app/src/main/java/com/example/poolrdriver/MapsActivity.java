package com.example.poolrdriver;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.poolrdriver.databinding.ActivityMapsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MapsActivity extends FragmentActivity{

    private BottomNavigationView bottomNavigationView;


    Fragment fragment = null;
    FragmentTransaction fragmentTransaction;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        fragment = new fragment_where_to();
                        switchFragment(fragment);
                        return true;
                    case R.id.nav_schedule:
                        fragment = new more_items();
                        switchFragment(fragment);
                        return true;

                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.poolrdriver.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigationView= findViewById(R.id.main_bottomNavigation_switcher);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);


    }
    private void switchFragment(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.where_to, fragment);
        fragmentTransaction.commit();
    }
}

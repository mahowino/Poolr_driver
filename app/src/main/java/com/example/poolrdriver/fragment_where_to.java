package com.example.poolrdriver;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;


public class fragment_where_to extends Fragment  {
    private EditText whereTo;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            try {
                boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
            } catch (Exception e) {

            }
            LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_where_to, container, false);
        whereTo=(EditText) view.findViewById(R.id.location_to_editText);

        whereTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent onClickSearchPage=new Intent(getContext(), onLocationPressedActivity.class);
               // chosenDate=new dateFormat(chosenTempDate.getYear(), chosenTempDate.getMonth(),chosenTempDate.getDay(),chosenTempTime.getHour(),chosenTempTime.getMinute());
               // onClickSearchPage.putExtra("chosenDate", (Parcelable) chosenDate);
                startActivity(onClickSearchPage);
                getActivity().overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
            }
        });


        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);

        }
    }


}
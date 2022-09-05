package com.example.poolrdriver.userRegistrationJourney;

import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.HomePageActivity;
import com.example.poolrdriver.MapsActivity;
import com.example.poolrdriver.R;
import com.example.poolrdriver.adapters.AutoSuggestionsAdapter;
import com.example.poolrdriver.util.AppSystem;
import com.example.poolrdriver.util.LoadingDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class HomeAndWorkAdress extends AppCompatActivity {
    AutoCompleteTextView homeAdress, workAdress;
    TextView skip;
    Button finish;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_and_work_adress);
        initializeVariables();
        setListeners();
        setAdapters();

    }

    private void setAdapters() {
        //todo:check for errors in input
        homeAdress.setAdapter(new AutoSuggestionsAdapter(getApplicationContext(), android.R.layout.simple_list_item_1));
        workAdress.setAdapter(new AutoSuggestionsAdapter(getApplicationContext(), android.R.layout.simple_list_item_1));
        homeAdress.requestFocus();
    }

    private void setListeners() {

        homeAdress.setOnItemClickListener((parent, view, position, id) -> {

        });
        workAdress.setOnItemClickListener((parent, view, position, id) -> {

        });
        skip.setOnClickListener(v -> {redirectActivity(HomeAndWorkAdress.this, MapsActivity.class);});
        finish.setOnClickListener(v -> {loadingDialog.startLoadingAlertDialog();updateDataInDatabase();});
    }

    private void initializeVariables() {
        //initializations
        homeAdress =  findViewById(R.id.home_adress);
        workAdress =  findViewById(R.id.work_adress);
        skip = (TextView) findViewById(R.id.skip_setting_home_and_work_adress);
        finish = (Button) findViewById(R.id.advance_save_home_and_work_adress);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        loadingDialog=new LoadingDialog(this);
    }

    private Map getDetails() {
        String homeAdressString = homeAdress.getText().toString();
        String workAdressString = workAdress.getText().toString();
        Map<String, Object> map = new HashMap<>();
        map.put(FirebaseFields.HOME_ADRESS, homeAdressString);
        map.put(FirebaseFields.WORK_ADRESS, workAdressString);
        return map;
    }

    private void updateDataInDatabase() {
        FirebaseRepository.setDocument(getDetails(),
                FirebaseRepository.createDocumentReference(FirebaseConstants.PASSENGERS,
                        new User().getUID()), SetOptions.merge(), new Callback() {
                    @Override
                    public void onSuccess(Object object) {
                        redirectActivity(HomeAndWorkAdress.this, HomePageActivity.class);
                        loadingDialog.dismissDialog();
                    }

                    @Override
                    public void onError(Object object) {
                        AppSystem.displayError(HomeAndWorkAdress.this, getApplicationContext(), (Exception) object);
                    }
                });

    }



}

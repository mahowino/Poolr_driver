package com.example.poolrdriver;

import static com.example.poolrdriver.Firebase.FirebaseRepository.createDocumentReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.setDocument;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.Constants.FirebaseConstants;
import com.example.poolrdriver.Firebase.Constants.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.userRegistrationJourney.HomeAndWorkAdress;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class AddBio extends AppCompatActivity {
    EditText myBio;
    Button addBio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bio);
        initializeViews();
        setListeners();
    }

    private void setListeners() {
        addBio.setOnClickListener(v -> updateBio());
    }

    private void updateBio() {
        String path= FirebaseConstants.PASSENGERS+"/"+new User().getUID()+"/";
        String bio=myBio.getText().toString();
        setDocument(createBio(bio),createDocumentReference(path), SetOptions.merge(), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(AddBio.this, "Successfully updated bio", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(), HomeAndWorkAdress.class);
                startActivity(intent);
            }

            @Override
            public void onError(Object object) {

            }
        });
    }

    private Map createBio(String bio) {
        Map<String,Object> map=new HashMap<>();
        map.put(FirebaseFields.BIO,bio);
        return map;
    }

    private void initializeViews() {
        addBio=findViewById(R.id.btnBio);
        myBio=findViewById(R.id.bio_txt);
    }
}
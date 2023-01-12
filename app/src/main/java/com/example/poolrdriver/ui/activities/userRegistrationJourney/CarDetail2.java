package com.example.poolrdriver.ui.activities.userRegistrationJourney;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Abstract.Callback;
import com.example.poolrdriver.Abstract.FirebaseConstants;
import com.example.poolrdriver.Abstract.Constants.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.ui.activities.other.MapsActivity;
import com.example.poolrdriver.R;
import com.example.poolrdriver.classes.models.CarTypes;
import com.example.poolrdriver.util.LoadingDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CarDetail2 extends AppCompatActivity {
    CarTypes carType;
    TextView carMake;
    AutoCompleteTextView carNumberPlate,carModel;
    Spinner carColors;
    Button btnPostCar;
    ImageView carTypeLogo;
    private static final String CAR_DETAILS = "car_details";
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail2);
        initializeViews();
        setListeners();
    }

    private void setListeners() {
        btnPostCar.setOnClickListener(v->validateInput());
    }

    private void validateInput() {
        loadingDialog.startLoadingAlertDialog();
        String model=carModel.getText().toString().trim();
        String numberplate=carNumberPlate.getText().toString().trim();
        String carColor=carColors.getSelectedItem().toString();
        if (model.isEmpty()){carModel.setError("fill in the details"); loadingDialog.dismissDialog();return;};
        if (numberplate.isEmpty()){carNumberPlate.setError("fill in the details"); loadingDialog.dismissDialog();return;}
        if (numberplate.length()!=7){carNumberPlate.setError("please write a valid number plate"); loadingDialog.dismissDialog();return;}
        carType.setColor(carColor);
        carType.setModel(model);
        carType.setNumberplate(numberplate);
        updateDatabaseWithCars();


    }

    private void updateDatabaseWithCars() {
        loadingDialog.dismissDialog();
        String path= FirebaseConstants.DRIVERS+"/"+new User().getUID()+"/"+FirebaseConstants.CARS;
        FirebaseRepository.setDocument(createCarInDatabase(), FirebaseRepository.createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(CarDetail2.this, "Successfully added car", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(Object object) {

                Toast.makeText(CarDetail2.this, "Error occurred", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private Map<String,Object> createCarInDatabase() {
        Map<String,Object> map=new HashMap<>();
        map.put(FirebaseFields.MAKE,carType.getCarType());
        map.put(FirebaseFields.MODEL,carType.getModel());
        map.put(FirebaseFields.CAR_YEAR,carType.getYear());
        map.put(FirebaseFields.NUMBER_PLATE,carType.getNumberplate());
        map.put(FirebaseFields.COLOR,carType.getColor());
        return map;
    }

    private void initializeViews() {
        loadingDialog=new LoadingDialog(this);
        carMake=findViewById(R.id.txtCarMake);
        carNumberPlate=findViewById(R.id.txtCarNumberPlate);
        carModel=findViewById(R.id.editTextCarModel);
        carColors=findViewById(R.id.spinner_CarColor);
        btnPostCar=findViewById(R.id.btnPostCar);
        carTypeLogo=findViewById(R.id.imgCarLogoImage);
        getIntentData();

    }

    private void getIntentData() {
        carType=getIntent().getParcelableExtra(CAR_DETAILS);
        carMake.setText(carType.getCarType());

        StorageReference reference= FirebaseStorage.getInstance().getReference()
                .child("cars")
                .child(carType.getCarType()+".png");
        reference.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Log.d(TAG, "onSuccess: "+uri);
                    Picasso.with(getApplicationContext())
                            .load(uri)
                            .error(R.drawable.car_logo_placeholder)
                            .into(carTypeLogo);
                })
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: ",e.getCause()));
    }
}
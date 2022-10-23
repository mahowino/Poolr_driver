package com.example.poolrdriver;

import static android.content.ContentValues.TAG;
import static com.example.poolrdriver.Firebase.FirebaseRepository.*;
import static com.example.poolrdriver.util.AppSystem.createDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.Interfaces.ItemClickListener;
import com.example.poolrdriver.adapters.CarTypeAdapter;
import com.example.poolrdriver.models.CarTypes;
import com.example.poolrdriver.util.LoadingDialog;
import com.example.poolrdriver.util.MonthYearPickerDialog;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Car_details extends AppCompatActivity {
    private static final String CAR_DETAILS = "car_details";
    Calendar today;
    AutoCompleteTextView btnCarYear,btnCarType;
    MonthPickerDialog.Builder builder;
    List<CarTypes> carTypes;
    RecyclerView carTypeRecyclerViews;
    Button btnCarDetailsNext;
    int selectedMakePosition;
    LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        today=Calendar.getInstance();
        setContentView(R.layout.activity_car_details);
        initializeData();
        setListeners();
    }

    private void setListeners() {
        btnCarYear.setOnClickListener(v-> builder.build().show());
        btnCarType.setOnClickListener(v -> {loadingDialog.startLoadingAlertDialog();getCarTypesFromDatabase();});
        btnCarDetailsNext.setOnClickListener(v->validateInput());
    }

    private void validateInput() {
        loadingDialog.startLoadingAlertDialog();
        String year=btnCarYear.getText().toString().trim();
        String make=btnCarType.getText().toString().trim();
        if (year.isEmpty()){btnCarYear.setError("fill in the details");loadingDialog.dismissDialog();return;};
        if (make.isEmpty()){btnCarType.setError("fill in the details");loadingDialog.dismissDialog();return;}

        carTypes.get(selectedMakePosition).setYear(year);
        Intent intent=new Intent(getApplicationContext(),CarDetail2.class);
        intent.putExtra(CAR_DETAILS,carTypes.get(selectedMakePosition));
        startActivity(intent);
        loadingDialog.dismissDialog();

    }

    private void initializeData() {
        loadingDialog=new LoadingDialog(this);
        carTypes=new ArrayList<>();
        btnCarType=findViewById(R.id.car_picker_edit_text);
        btnCarYear=findViewById(R.id.time_picker_edit_text_car_year);
        btnCarDetailsNext=findViewById(R.id.btnCarDetailsNext);
        builder= new MonthPickerDialog.Builder(Car_details.this,
                (selectedMonth, selectedYear) -> {  }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
        initializeDialog();

    }

    private void getCarTypesFromDatabase() {
        String path= FirebaseConstants.CARS+"/";
        getDocumentsInCollection(createCollectionReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<QuerySnapshot> task=(Task<QuerySnapshot>)object;

                for (DocumentSnapshot snapshot:task.getResult()){
                    String carType=snapshot.getString(FirebaseFields.CAR_TYPE);
                    carTypes.add(createCarTypeObject(carType));
                }
                showDialogLayout(R.layout.car_type_selector_ui);
            }

            @Override
            public void onError(Object object) {

            }
        });
    }

    private CarTypes createCarTypeObject( String carType) {
        return new CarTypes(carType);
    }
    private void showDialogLayout(int layout) {
        loadingDialog.dismissDialog();
        Dialog dialog=createDialog(Car_details.this,layout);
        initializeVariables(dialog);
        setDialogOnClickListeners(dialog,layout);

    }

    private void setDialogOnClickListeners(Dialog dialog, int layout) {
        CarTypeAdapter carTypeAdapter=new CarTypeAdapter(this, carTypes, position->setTextOnUI(position,dialog));
        carTypeRecyclerViews.setAdapter(carTypeAdapter);
        carTypeRecyclerViews.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));

    }

    private void setTextOnUI(int position,Dialog dialog) {
        selectedMakePosition=position;
        Toast.makeText(getApplicationContext(),carTypes.get(position).getCarType(),Toast.LENGTH_LONG).show();
        btnCarType.setText(carTypes.get(position).getCarType());
        dialog.dismiss();
    }

    private void initializeVariables(Dialog dialog) {
        carTypeRecyclerViews=dialog.findViewById(R.id.car_type_Selector_recyclerView);

    }


    private void initializeDialog() {
        builder.setActivatedMonth(Calendar.JULY)
                .setMinYear(1990)
                .setActivatedYear(2017)
                .setMaxYear(2030)
                .setMinMonth(Calendar.FEBRUARY)
                .setTitle("Select trading month")
                .setYearRange(1990, 2022)
                .showYearOnly()
                .setOnMonthChangedListener(selectedMonth -> {  })
                .setOnYearChangedListener(selectedYear -> { btnCarYear.setText(selectedYear+""); });
    }


}
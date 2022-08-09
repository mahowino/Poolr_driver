package com.example.poolrdriver.userRegistrationJourney;

import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.R;
import com.example.poolrdriver.classes.SignedUpDriver;
import com.example.poolrdriver.util.LoadingDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class verifyOTP extends AppCompatActivity  {


    private EditText input1,input2,input3,input4,input5,input6;
    private Button Verify;
    private TextView resendOTP;
    private ImageView back_text;
    private String backEndOTP,phoneNumber;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        initializeVariables();
        //setupInputChangeListeners();
        setListneners();

    }
    private void initializeVariables() {
        input1=findViewById(R.id.editTextNumber1);
        input2=findViewById(R.id.editTextNumber2);
        input3=(EditText)findViewById(R.id.editTextNumber3);
        input4=(EditText)findViewById(R.id.editTextNumber4);
        input5=(EditText)findViewById(R.id.editTextNumber5);
        input6=(EditText)findViewById(R.id.editTextNumber6);
        Verify=(Button)findViewById(R.id.verifyBtn);
        resendOTP=(TextView)findViewById(R.id.resendOTP);
        back_text=(ImageView) findViewById(R.id.back_verify);
        loadingDialog=new LoadingDialog(this);


        backEndOTP = getIntent().getExtras().getString("backEndOTP");
        phoneNumber=getIntent().getExtras().getString("phoneNumber");
    }

    //listeners
    private void setListneners() {
        setupInputChangeListeners();

        //todo:check if timeout has arrived
        //todo:resend OTP ISSUES WHEN TIMEOUT
        resendOTP.setOnClickListener(v -> {/*verifyPhoneNumberScreen.verifyPhoneNumberOtp(phoneNumber);Toast.makeText(getApplicationContext(),"Code succesfully resent ",Toast.LENGTH_LONG).show();*/});
        back_text.setOnClickListener(v -> displayDialog());
        Verify.setOnClickListener(v -> {

            loadingDialog.startLoadingAlertDialog();
            if(getVerificationCode()!=null && backEndOTP!=null)
                signInWithPhoneCredential(PhoneAuthProvider.getCredential(backEndOTP, getVerificationCode()));
        });

    }

    public  void signInWithPhoneCredential(PhoneAuthCredential credential) {

        FirebaseAuth.getInstance().getCurrentUser().updatePhoneNumber(credential)
                .addOnSuccessListener(unused ->  redirectHomeAndWorkAdressPage())
                .addOnFailureListener(e ->{redirectPhoneNumberPage();
                    Log.d("otp", "signInWithPhoneCredential: "+credential);});


    }
    private void  setupInputChangeListeners() {
        input1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {input2.requestFocus();}

            @Override
            public void afterTextChanged(Editable s) {}
        });
        input2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {input3.requestFocus();}

            @Override
            public void afterTextChanged(Editable s) {}
        });
        input3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {input4.requestFocus();}

            @Override
            public void afterTextChanged(Editable s) {}
        });
        input4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {input5.requestFocus();}

            @Override
            public void afterTextChanged(Editable s) {}
        });
        input5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {input6.requestFocus();}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void displayDialog() {
        new AlertDialog.Builder(verifyOTP.this)
                .setTitle("go back")
                .setMessage("Are you sure you want to go back?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {redirectActivity(verifyOTP.this,SignUpScreen.class);})
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private String getVerificationCode() {
        if(input1.getText().toString().isEmpty()
                ||input2.getText().toString().isEmpty()
                ||input3.getText().toString().isEmpty()
                ||input4.getText().toString().isEmpty()
                ||input5.getText().toString().isEmpty()
                ||input6.getText().toString().isEmpty()
        ){
            Toast.makeText(getApplicationContext(),"Fill in the code well",Toast.LENGTH_LONG).show();
            return null;
        }
        return input1.getText().toString().trim()
                +input2.getText().toString().trim()
                +input3.getText().toString().trim()
                +input4.getText().toString().trim()
                +input5.getText().toString().trim()
                +input6.getText().toString().trim();
    }

    private void redirectHomeAndWorkAdressPage(){
        Toast.makeText(getApplicationContext(),"Code successfully verified  ",Toast.LENGTH_LONG).show();
        Intent intent =new Intent(verifyOTP.this,  HomeAndWorkAdress.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        loadingDialog.dismissDialog();
    }

    private void redirectPhoneNumberPage() {
        Toast.makeText(getApplicationContext(),"Phone number belongs to a different user, try a different number ",Toast.LENGTH_LONG).show();
        Intent intent =new Intent(verifyOTP.this,  verifyPhoneNumberScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        loadingDialog.dismissDialog();
    }


}

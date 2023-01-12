package com.example.poolrdriver.ui.activities.userRegistrationJourney;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.R;
import com.example.poolrdriver.util.LoadingDialog;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

public class verifyPhoneNumberScreen extends AppCompatActivity {
    Button verifyPhoneNumber;
    EditText phoneNumber;
    Spinner spinner;
    TextView btnBack;
    User user;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number_screen);
        initializeVariables();
        setOnclickListeners();
    }

    private void initializeVariables() {

        verifyPhoneNumber=(Button) findViewById(R.id.verifyPhoneNumberBtn);
        phoneNumber=(EditText)findViewById(R.id.editTextNumber);
        spinner=(Spinner)findViewById(R.id.spinner_phoneNumber);
        btnBack=(TextView)findViewById(R.id.back_image_btn);
        loadingDialog=new LoadingDialog(this);
        user=new User();

    }

    private void setOnclickListeners() {
        btnBack.setOnClickListener(v -> finish());
        verifyPhoneNumber.setOnClickListener(view ->{loadingDialog.startLoadingAlertDialog(); verifyPhoneNumberOtp(getInputData());});
    }

    private String getInputData() {
        String phoneNumberString=phoneNumber.getText().toString().trim();
        String countryCode=spinner.getSelectedItem().toString().trim();
        return countryCode+phoneNumberString;
    }

    public void verifyPhoneNumberOtp(String phoneNumber) { PhoneAuthProvider.verifyPhoneNumber(getPhoneAuthOptions(phoneNumber));}

    private PhoneAuthOptions getPhoneAuthOptions(String phoneNumber) {

        return user.verifyNumberOTP(phoneNumber,
                verifyPhoneNumberScreen.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String backEndOTP, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(backEndOTP, forceResendingToken);verifyOTP_Redirect(backEndOTP,phoneNumber);}
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {Toast.makeText(getApplicationContext(),"OTP failed "+e.getMessage(),Toast.LENGTH_LONG).show();}
                });
    }

    private void verifyOTP_Redirect(String OTP, String phoneNumber) {
        Intent verify=new Intent(getApplicationContext(), verifyOTP.class);
        verify.putExtra("backEndOTP",OTP);
        verify.putExtra("phoneNumber",phoneNumber);
        startActivity(verify);
        loadingDialog.dismissDialog();
    }

}
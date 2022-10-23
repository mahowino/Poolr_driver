package com.example.poolrdriver.userRegistrationJourney;

import static com.example.poolrdriver.Firebase.FirebaseRepository.*;
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseRepository;
import com.example.poolrdriver.MapsActivity;
import com.example.poolrdriver.R;
import com.example.poolrdriver.util.LoadingDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogInScreen extends AppCompatActivity {
    EditText memail,mpassword;
    Button mlogin;
    TextView register,forgotPassword;
    FirebaseAuth fAuth;
    LoadingDialog loadingDialog;
    ImageView passwordVisibility;
    ImageButton googleBtn,facebookBtn;
    private static boolean isPasswordVisible=false;
    String userID,email,password;
    FirebaseFirestore fstore;
    String web_client_id="939963134204-dmpgb5ovriof6kfjnuhgugquklc5pe3a.apps.googleusercontent.com";
    private int RC_SIGN_IN=1001;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_screen);

        initializeVariables();
        setUpGoogleSignIn();
        setListeners();

    }

    private void setListeners() {
        googleBtn.setOnClickListener(v -> startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN));

        forgotPassword.setOnClickListener(v -> redirectActivity(LogInScreen.this,ForgotPassword.class));

        register.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SignUpScreen.class)));

        //onclick listeners

        passwordVisibility.setOnClickListener(v -> {
            if (!isPasswordVisible) setPasswordTransformation(null,getResources().getDrawable(R.drawable.password_visible),true);
            else setPasswordTransformation(PasswordTransformationMethod.getInstance(),getResources().getDrawable(R.drawable.password_invisible),false);
        });
        mlogin.setOnClickListener(v -> validateInput());
    }

    private void signInWithEmailAndPassword() {

        Toast.makeText(LogInScreen.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        loadingDialog.dismissDialog();
        finish();
    }

    private void validateInput() {
        loadingDialog.startLoadingAlertDialog();
        getInputData();
        validate();

    }

    private void validate() {
        if (TextUtils.isEmpty(email)) {memail.setError("Email is required");loadingDialog.dismissDialog();return;}
        if (TextUtils.isEmpty(password)) {mpassword.setError("Password is required");loadingDialog.dismissDialog();return;}
        if (password.length() < 8) {mpassword.setError("Password must be more than 8 characters");loadingDialog.dismissDialog();return;}
        //user authentication
        signInUser(email, password, new Callback() {
            @Override
            public void onSuccess(Object object) {
                signInWithEmailAndPassword();
            }

            @Override
            public void onError(Object object) {
                mpassword.setError("wrong password input");
                loadingDialog.dismissDialog();
            }
        });

    }

    private void getInputData() {
        email = memail.getText().toString().trim();
        password = mpassword.getText().toString().trim();
    }

    private void setPasswordTransformation(PasswordTransformationMethod method, Drawable drawable, boolean isPasswordVisible){
        mpassword.setTransformationMethod(null);
        passwordVisibility.setImageDrawable(getResources().getDrawable(R.drawable.password_visible));
        this.isPasswordVisible=isPasswordVisible;
    }

    private void setUpGoogleSignIn() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(web_client_id)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initializeVariables() {
        loadingDialog=new LoadingDialog(this);

        register = findViewById(R.id.signup_link_on_login_page);
        memail = findViewById(R.id.email_login_edittext);
        mpassword = findViewById(R.id.password_edittext_login);
        mlogin = findViewById(R.id.login_button);
        forgotPassword=findViewById(R.id.forgot_password);
        passwordVisibility=findViewById(R.id.imageView_password_login);
        googleBtn=findViewById(R.id.imageButtonLoginGoogle);
        facebookBtn=findViewById(R.id.imageButton2LoginFacebook);
        fstore=FirebaseFirestore.getInstance();

        fAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("firebaseAuth", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken(),data);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("firebaseAuth", "Google sign in failed", e);
            }
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken,Intent data) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("sign in", "signInWithCredential:success");

                            //check if user exists


                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));


                            //put information into database



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("sign in", "signInWithCredential:failure", task.getException());
                            //updateUI(null);
                        }
                    }
                });
    }
}


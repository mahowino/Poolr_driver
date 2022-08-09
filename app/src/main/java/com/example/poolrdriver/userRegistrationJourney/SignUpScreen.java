package com.example.poolrdriver.userRegistrationJourney;

import static com.example.poolrdriver.Firebase.FirebaseRepository.addUserToFirebaseDatabase;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createCollectionReference;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createNewUser;
import static com.example.poolrdriver.Firebase.FirebaseRepository.createQuery;
import static com.example.poolrdriver.Firebase.FirebaseRepository.getDocumentsFromQueryInCollection;
import static com.example.poolrdriver.Firebase.GoogleSignIn.RC_SIGN_IN;
import static com.example.poolrdriver.Firebase.GoogleSignIn.SignUserUpWithGoogle;
import static com.example.poolrdriver.Firebase.GoogleSignIn.getGoogleSignInAccount;
import static com.example.poolrdriver.Firebase.GoogleSignIn.requestGoogleSignInPrompt;
import static com.example.poolrdriver.Firebase.GoogleSignIn.setGoogleSignIn;
import static com.example.poolrdriver.classes.userLogInAttempt.*;
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.Callback;
import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.Firebase.User;
import com.example.poolrdriver.MapsActivity;
import com.example.poolrdriver.R;
import com.example.poolrdriver.classes.userLogInAttempt;
import com.example.poolrdriver.util.LoadingDialog;
import com.example.poolrdriver.classes.SignedUpDriver;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignUpScreen extends AppCompatActivity {

    private  boolean isPasswordVisible;
    public  final String FACEBOOK_WARNING="This feature will be included in new releases";
    private String  emailString,nameString,passwordString;
    EditText name,email,password;
    Button signUp;
    ImageView viewPassword;
    User user;
    ImageButton google,facebook;
    Context mContext;
    ConstraintLayout full_name_ConstraintLayout,email_cConstraintLayout,password_constraintLayout;
    ProgressBar loading;
    TextView loginButton;
    Drawable boarderBackground,noBoarderBackground,passwordVisible,passwordInvisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);


        initializeVariables();
        redirectLoggedInUsers();
        //requestLoginWithGoogle();
        setListeners();

    }
    @SuppressLint("CutPasteId")
    private void initializeVariables() {
        //view setup
        initializeEditTexts();
        initializeButtons();
        initializeViews();
        initializeTextViews();
        initializeImageViews();
        initializeImageViews();

        variableUpdating();

    }
    private void initializeImageViews() { viewPassword=(ImageView) findViewById(R.id.imageView_password);}
    private void initializeTextViews() {  loginButton=(TextView)findViewById(R.id.login_text_signup_screen) ;}
    private void initializeViews() {
        full_name_ConstraintLayout = (ConstraintLayout)findViewById(R.id.fullnames_constraint_layout);
        email_cConstraintLayout = (ConstraintLayout)findViewById(R.id.email_signup_constraintlayout);
        password_constraintLayout = (ConstraintLayout)findViewById(R.id.password_constraintLayout);
        loading=(ProgressBar)findViewById(R.id.progressBar);
    }
    private void initializeButtons() {
        signUp=(Button)findViewById(R.id.signup_button);
        google=findViewById(R.id.imageButtonGoogleSignIn);
        facebook=findViewById(R.id.imageButtonFacebookSignIn);
    }

    private void initializeEditTexts() {
        name=findViewById(R.id.full_names_edit_text);
        email=(EditText)findViewById(R.id.email_signup_edittext);
        password=(EditText)findViewById(R.id.password_edittext);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void variableUpdating() {

        mContext=getApplicationContext();
        isPasswordVisible=false;

        //set backgrounds
        boarderBackground=getResources().getDrawable(R.drawable.background_textview);
        noBoarderBackground=getResources().getDrawable(R.drawable.background_textview_no_boarders);
        passwordVisible=getResources().getDrawable(R.drawable.password_visible);
        passwordInvisible=getResources().getDrawable(R.drawable.password_invisible);
        user=new User();


    }

    //listener set up
    private void setListeners() {setOnFocusChangeListeners(); setOnClickListeners();}
    private void setOnClickListeners() {
        signUp.setOnClickListener(v -> {loading.setVisibility(View.VISIBLE); validateInformation(); });
        google.setOnClickListener(v -> {loading.setVisibility(View.VISIBLE);requestLoginWithGoogle();});
        facebook.setOnClickListener(v -> Toast.makeText(getApplicationContext(),FACEBOOK_WARNING,Toast.LENGTH_SHORT).show());
        loginButton.setOnClickListener(v -> redirectActivity(SignUpScreen.this,LogInScreen.class));
        viewPassword.setOnClickListener(view -> { if (!isPasswordVisible) setPasswordView(null,passwordVisible,true); else setPasswordView(PasswordTransformationMethod.getInstance(),passwordInvisible,false);});
    }

    private void setPasswordView(PasswordTransformationMethod instance, Drawable visibility, boolean status){
        password.setTransformationMethod(instance);
        viewPassword.setImageDrawable(visibility);
        isPasswordVisible=status;
    }
    private void setOnFocusChangeListeners() {

        name.setOnFocusChangeListener((view, b) -> {
            if (name.hasFocus())full_name_ConstraintLayout.setBackground(boarderBackground);
            else full_name_ConstraintLayout.setBackground(noBoarderBackground);
        });

        email.setOnFocusChangeListener((view, b) -> {
            if (email.hasFocus()) email_cConstraintLayout.setBackground(boarderBackground);
            else email_cConstraintLayout.setBackground(noBoarderBackground);
            full_name_ConstraintLayout.setBackground(noBoarderBackground);
        });

        password.setOnFocusChangeListener((view, b) -> {
            if (password.hasFocus())password_constraintLayout.setBackground(boarderBackground);
            else password_constraintLayout.setBackground(noBoarderBackground);
        });

    }

    private userLogInAttempt getInputData() {

        //get Input Data
        emailString=email.getText().toString().trim();
        nameString=name.getText().toString().trim();
        passwordString=password.getText().toString().trim();

        return new userLogInAttempt(nameString,emailString,passwordString);
    }
    private boolean validateUserInput() {

        //validation
        if(TextUtils.isEmpty(userLogInAttempt.name)){ email.setError("Name is required"); return false; }
        if(TextUtils.isEmpty(userLogInAttempt.email)){ email.setError("Email is required"); return false; }
        if(TextUtils.isEmpty(userLogInAttempt.password)){ password.setError("Password is required"); return false;}
        if (password.length()<8){ password.setError("Password must be more than 8 characters"); return false; }

        return true;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void validateInformation() {
        //data filled correctly
        getInputData();
        if (!validateUserInput())return;

        CollectionReference reference= createCollectionReference(FirebaseConstants.PASSENGERS);
        signUserUpWithCredentials(createQuery(reference, FirebaseFields.EMAIL, userLogInAttempt.email));

    }

    private void signUserUpWithCredentials(Query query) {
        getDocumentsFromQueryInCollection(query, new Callback() {
            @Override
            public void onSuccess(Object object) {
                Task<QuerySnapshot> task = (Task<QuerySnapshot>) object;
                if (task.getResult().size()!=0){
                    Toast.makeText(SignUpScreen.this, "Username exists", Toast.LENGTH_SHORT).show();
                    redirectActivity(SignUpScreen.this,LogInScreen.class);
                }
                else signUserUp();

                loading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Object object) {Toast.makeText(getApplicationContext(),((Exception)object).getMessage(),Toast.LENGTH_SHORT).show();}});
    }

    private void signUserUp() {
        createNewUser(userLogInAttempt.email, userLogInAttempt.password, new Callback() {
            @Override
            public void onSuccess(Object object) {
                user=new User(); user.setEmail(userLogInAttempt.email, new Callback() {
                    @Override
                    public void onSuccess(Object object) {}

                    @Override
                    public void onError(Object object) {}});

                user.setName(userLogInAttempt.name);
                addUserToFirebaseDatabase(user,SignUpScreen.this);
                redirectActivity(SignUpScreen.this,verifyPhoneNumberScreen.class);}

            @Override
            public void onError(Object object) {Toast.makeText(SignUpScreen.this, "error logging in", Toast.LENGTH_SHORT).show();}
        });


    }

    private void requestLoginWithGoogle(){ requestGoogleSignInPrompt(SignUpScreen.this,setGoogleSignIn(mContext));}
    private void redirectLoggedInUsers() {if (User.isUserSignedIn()){ redirectActivity(SignUpScreen.this,MapsActivity.class);finish(); }}


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent
        if (requestCode == RC_SIGN_IN)

            try {
                SignUserUpWithGoogle( getGoogleSignInAccount(data).getIdToken(),
                        data,
                        SignUpScreen.this,
                        verifyPhoneNumberScreen.class);
            }catch (NullPointerException e){}

        loading.setVisibility(View.INVISIBLE);
    }

}

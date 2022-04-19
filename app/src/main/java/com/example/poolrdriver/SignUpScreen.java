package com.example.poolrdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poolrdriver.Firebase.FirebaseConstants;
import com.example.poolrdriver.Firebase.FirebaseFields;
import com.example.poolrdriver.classes.LoadingDialog;
import com.example.poolrdriver.classes.SignedUpDriver;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignUpScreen extends AppCompatActivity {

    private static boolean isPasswordVisible=false;
    public static final String TAG = "TAG";
    EditText memail,mpassword,mconfirmpassword,mname,mschool;
    Button mnext;
    ImageView mpic;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String userID;
    LoadingDialog dialog=new LoadingDialog(this);
    ImageButton google,facebook;
    private static final int RC_SIGN_IN = 9001;
    String web_client_id="939963134204-dmpgb5ovriof6kfjnuhgugquklc5pe3a.apps.googleusercontent.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        mname=findViewById(R.id.full_names_edit_text);
        //mpic=findViewById(R.id.profilepic);
        // mschool=findViewById(R.id.school);
        memail=findViewById(R.id.email_signup_edittext);
        mpassword=findViewById(R.id.password_edittext);
        google=(ImageButton)findViewById(R.id.imageButtonGoogleSignIn);
        facebook=(ImageButton)findViewById(R.id.imageButtonFacebookSignIn);
        // Configure Google Sign In


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(web_client_id)
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // mconfirmpassword=findViewById(R.id.confirmpassword);
        //mnext=findViewById(R.id.next);
        fAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        //already logged in
        // Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //startActivityForResult(signInIntent, RC_SIGN_IN);

        if (fAuth.getCurrentUser() !=null){
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            finish();
        }





        //views
        ConstraintLayout full_name_ConstraintLayout = (ConstraintLayout)findViewById(R.id.fullnames_constraint_layout);
        ConstraintLayout email_cConstraintLayout = (ConstraintLayout)findViewById(R.id.email_signup_constraintlayout);
        ConstraintLayout password_constraintLayout = (ConstraintLayout)findViewById(R.id.password_constraintLayout);

        //edit texts
        EditText fullnames = (EditText)findViewById(R.id.full_names_edit_text);
        EditText email=(EditText)findViewById(R.id.email_signup_edittext);
        EditText password=(EditText)findViewById(R.id.password_edittext);

        //progressBar
        ProgressBar loading=(ProgressBar)findViewById(R.id.progressBar);

        //image views
        ImageView viewPassword=(ImageView) findViewById(R.id.imageView_password);

        //button
        Button signUp=(Button)findViewById(R.id.signup_button);

        //Textviews
        TextView loginButton=(TextView)findViewById(R.id.login_text_signup_screen) ;

        //OnClick listeners
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loading.setVisibility(View.VISIBLE);
                //TODO:PUT IN VALIDATION INFORMATION ON DATA INPUT AND PROCEED IF TRUE
                validateInformation();

            }

            private void validateInformation() {
                String email=memail.getText().toString().trim();
                String name=mname.getText().toString().trim();
                // String school=mschool.getText().toString().trim();
                String password=mpassword.getText().toString().trim();


                // if(TextUtils.isEmpty(school)){
                //mschool.setError("School is required");
                //  return;

                //  }
                if(TextUtils.isEmpty(name)){
                    memail.setError("Name is required");
                    return;

                }
                if(TextUtils.isEmpty(email)){
                    memail.setError("Email is required");
                    return;

                }
                if(TextUtils.isEmpty(password)){
                    mpassword.setError("Password is required");
                    return;

                }
                if (password.length()<8){
                    mpassword.setError("Password must be more than 8 characters");
                    return;
                }

                CollectionReference reference=fstore.collection(FirebaseConstants.DRIVERS);
                Query query = reference.whereEqualTo(FirebaseFields.EMAIL, email);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot : task.getResult()){
                                String user = documentSnapshot.getString("username");

                                if(user.equals(email)){
                                    Log.d(TAG, "User Exists");
                                    Toast.makeText(SignUpScreen.this, "Username exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        if(task.getResult().size() == 0 ){
                            Log.d(TAG, "User not Exists");
                            //You can store new user information here

                            //firebase registration
                            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(SignUpScreen.this,"User created", Toast.LENGTH_SHORT).show();

                                        //fbase db
                                        userID=fAuth.getCurrentUser().getUid();
                                        DocumentReference documentReference=fstore.collection("passengers").document(userID);

                                        //document creation
                                        Map<String,Object> user=new HashMap<>();
                                        user.put("name",name);
                                        user.put("email",email);
                                        //  user.put("school",school);
                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG,"onSuccess:user profile created successfully for"+userID);
                                                //startActivity(new Intent(getApplicationContext(), verifyPhoneNumberScreen.class));
                                                loading.setVisibility(View.INVISIBLE);
                                                new SignedUpDriver(userID,name,email);

                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG,"onSuccess:user profile failed for"+userID);
                                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    }else {
                                        Toast.makeText(SignUpScreen.this,"Error ! "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        }
                    }
                });


            }
});
    }
}

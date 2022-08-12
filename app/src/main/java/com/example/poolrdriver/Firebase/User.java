package com.example.poolrdriver.Firebase;


import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;


import com.example.poolrdriver.MainActivity;
import com.example.poolrdriver.classes.SignedUpDriver;
import com.example.poolrdriver.classes.userLogInAttempt;
import com.example.poolrdriver.verifyPhoneNumber;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class User {
    private static final String TAG ="User" ;

    FirebaseUser user;
    String UID,name,email,HomeAdress,WorkAdress,phoneNumber,password;
    Uri profilePic;
    Callback callback;
    Intent intent;
    Activity activity;
    private String bio;

    public User(){
        user=FirebaseRepository.getSignedUpUser();

        if (user!=null)
            getHomeAndWorkAddressFromFirebase();

    }



    private void getHomeAndWorkAddressFromFirebase() {
        String path=FirebaseConstants.PASSENGERS+"/"+getUID();
        FirebaseRepository.getDocument(FirebaseRepository.createDocumentReference(path), new Callback() {
            @Override
            public void onSuccess(Object object) {

                Task<DocumentSnapshot> task = (Task<DocumentSnapshot>) object;


                DocumentSnapshot snapshot=task.getResult();
                if (snapshot.exists()){
                    Log.d("error","document is "+task.getResult().getString(FirebaseFields.HOME_ADRESS));
                    setHomeAdress(task.getResult().getString(FirebaseFields.HOME_ADRESS));
                    setWorkAdress(task.getResult().getString(FirebaseFields.WORK_ADRESS));

                }

            }

            @Override
            public void onError(Object object) {Toast.makeText(activity.getApplicationContext(),((Exception)object).getMessage(),Toast.LENGTH_SHORT).show();}
        });
    }

    public FirebaseUser getUser(){return user;}
    private void setIntent(Intent intent){this.intent=intent;}

    public boolean getUserDataFromGoogle(Intent intent){
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
        assert result != null;
        GoogleSignInAccount acct = result.getSignInAccount();
        if (acct.getDisplayName()!=null && acct.getEmail()!=null && acct.getPhotoUrl()!=null){
            setName(acct.getDisplayName());setEmail(acct.getEmail() , new Callback() {
                @Override
                public void onSuccess(Object object) {}

                @Override
                public void onError(Object object) {}
            });setProfilePic(acct.getPhotoUrl());return true;}
        return false;
    }

    public String getUID() {
        return user.getUid();
    }


    public String getName() {
        return user.getDisplayName();
    }

    public void setName(String name) {
        UserProfileChangeRequest.Builder request=new UserProfileChangeRequest.Builder();
        request.setDisplayName(name);
        updateUserProfile(request, new Callback() {
            @Override
            public void onSuccess(Object object) {
            }

            @Override
            public void onError(Object object) {
            }
        });
    }
    public static boolean isUserSignedIn(){if(FirebaseRepository.getSignedUpUser()!=null) return true; else return false;}

    public String getEmail() {return user.getEmail();}

    public void setEmail(String email,Callback callback) {
        user.updateEmail(email)
                .addOnCompleteListener(task -> callback.onSuccess(task))
                .addOnFailureListener(e -> callback.onError(e));
    }

    //todo: get and set home adresses code
    public String getHomeAdress() {return HomeAdress;}

    public void setHomeAdress(String HomeAdress) {this.HomeAdress = HomeAdress;}

    public String getWorkAdress() {return WorkAdress;}

    public void setWorkAdress(String WorkAdress) {this.WorkAdress = WorkAdress;}

    public String getPhoneNumber() { return user.getPhoneNumber();}

    public void setPhoneNumber(PhoneAuthCredential credential) {
        user.updatePhoneNumber(credential);
        setName(userLogInAttempt.name);
        setEmail(userLogInAttempt.email, new Callback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(Object object) {

            }
        });
    }

    public Uri getProfilePic() {return user.getPhotoUrl();}

    public void setProfilePic(Uri profilePic) {
        UserProfileChangeRequest.Builder request=new UserProfileChangeRequest.Builder();
        request.setPhotoUri(profilePic);
    }

    private void updateUserProfile(UserProfileChangeRequest.Builder request, Callback callback) {
        user.updateProfile(request.build())
                .addOnCompleteListener(task -> callback.onSuccess(task))
                .addOnFailureListener(e -> callback.onError(e));
    }

    public void sendEmailVerification(Callback callback){
        user.sendEmailVerification()
                .addOnCompleteListener(task -> callback.onSuccess(task))
                .addOnFailureListener(e -> callback.onSuccess(e));
    }

    public Map<String,Object> getMapDetails() {
        //document creation
        Map<String, Object> map = new HashMap<>();
        map.put(FirebaseFields.FULL_NAMES, getName());
        map.put(FirebaseFields.EMAIL, getEmail());
        map.put(FirebaseFields.PHONE_NUMBER,getPhoneNumber());

        return map;
    }
    public void createUserInFirebase(Map<String,Object> map,DocumentReference reference, Activity activity) {
        this.activity=activity;
        createNewUserProfileInFirestore(map,reference);


    }

    private void createNewUserProfileInFirestore(Map<String,Object> map, DocumentReference reference) {
        FirebaseRepository.setDocument(map, reference, new Callback() {
            @Override
            public void onSuccess(Object object) {
                Log.d(TAG,"onSuccess:user profile created successfully");}
            @Override
            public void onError(Object object) { Toast.makeText(activity.getApplicationContext(),((Exception)object).getMessage(),Toast.LENGTH_SHORT).show();}});
    }

    public PhoneAuthOptions verifyNumberOTP(String phoneNumber, Activity activity, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks){
        return PhoneAuthOptions.newBuilder(FirebaseInitVariables.mAuth)
                .setPhoneNumber("+"+phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build();// Activity (for callback binding
    }


    public void signOut() {
        FirebaseInitVariables.mAuth.signOut();
    }

    public String getBio(){ return bio;}
}

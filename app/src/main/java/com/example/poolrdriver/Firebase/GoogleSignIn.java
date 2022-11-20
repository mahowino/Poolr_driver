package com.example.poolrdriver.Firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


import com.example.poolrdriver.Firebase.Constants.FirebaseConstants;
import com.example.poolrdriver.Firebase.Constants.FirebaseInitVariables;
import com.example.poolrdriver.util.AppSystem;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;

public abstract class GoogleSignIn {
    private static final String TAG ="Firebase Sign In" ;
    private static final String web_client_id="939963134204-dmpgb5ovriof6kfjnuhgugquklc5pe3a.apps.googleusercontent.com";
    public static final int RC_SIGN_IN = 9001;

    public static GoogleSignInClient setGoogleSignIn(Context mContext) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(web_client_id)
                .requestEmail()
                .build();


        return  com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(mContext, gso);
    }
    public static void requestGoogleSignInPrompt(Activity activity, GoogleSignInClient mGoogleSignInClient) {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    public static GoogleSignInAccount getGoogleSignInAccount(Intent intent){
        Task<GoogleSignInAccount> task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(intent);
        GoogleSignInAccount accountSign=null;
        try {
            // Google Sign In was successful, authenticate with Firebase
            accountSign = task.getResult(ApiException.class);
            Log.d(TAG, "firebaseAuthWithGoogle:" + accountSign.getId());

        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e);
        }
        return accountSign;
    }

    private static void authenticateGoogleAccountWithFirebase(String idToken, Callback callback){

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseInitVariables.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(callback::onSuccess)
                .addOnFailureListener(e -> callback.onError(e.getMessage()));

    }
    public static void SignUserUpWithGoogle(String idToken, Intent data, Activity activity, Class nextClass) {

        GoogleSignIn.authenticateGoogleAccountWithFirebase(idToken, new Callback() {
            @Override
            public void onSuccess(Object object) {if (((Task<AuthResult>) object).isSuccessful())runSuccesfullSignIn(data,activity,nextClass);}
            @Override
            public void onError(Object object) {
                AppSystem.displayError(activity,activity.getApplicationContext(),(Exception) object);}});
    }
    private static void runSuccesfullSignIn(Intent intent, Activity activity, Class nextClass) { Log.d(TAG, "signInWithCredential:success");

        User user=new User();
        DocumentReference reference=FirebaseRepository.createDocumentReference(FirebaseConstants.PASSENGERS,user.getUID());

        if(user.getUserDataFromGoogle(intent)) FirebaseRepository.updateGoogleSignInUserData(reference,activity, nextClass);
        else Toast.makeText(activity.getApplicationContext(),"Error in database",Toast.LENGTH_SHORT).show();
    }

}

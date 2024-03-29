package com.example.poolrdriver.Firebase;

import static android.content.ContentValues.TAG;
import static com.example.poolrdriver.Abstract.Constants.FirebaseInitVariables.db;
import static com.example.poolrdriver.util.AppSystem.redirectActivity;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.poolrdriver.Abstract.Callback;
import com.example.poolrdriver.Abstract.FirebaseConstants;
import com.example.poolrdriver.Abstract.Constants.FirebaseFields;
import com.example.poolrdriver.Abstract.Constants.FirebaseInitVariables;
import com.example.poolrdriver.util.AppSystem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseRepository {


    static boolean isSuccessful;


    public static FirebaseUser getSignedUpUser() {
        return FirebaseInitVariables.mAuth.getCurrentUser();
    }

    public static void getDocument(@NonNull DocumentReference reference, Callback callback) {
        reference.get().addOnCompleteListener(task -> runTaskValidation(task, callback));
    }

    public static void setDocument(Map map, @NonNull DocumentReference reference, final Callback callback) {
        reference.set(map).addOnCompleteListener(task -> runTaskValidation(task, callback));
    }
    public static void setDocument(Map map, @NonNull CollectionReference reference, final Callback callback) {
        reference.document().set(map).addOnCompleteListener(task -> runTaskValidation(task, callback));
    }
    public static void deleteDocument(@NonNull DocumentReference reference, Callback callback){
        reference.delete().addOnCompleteListener(task -> runTaskValidation(task,callback));
    }


    public static void getDocumentsInCollection(@NonNull CollectionReference collectionReference, final Callback callback) {
        collectionReference.get().addOnCompleteListener(task -> runTaskValidation(task, callback));
    }
    public static void getTripsInCollection(@NonNull CollectionReference collectionReference, final Callback callback) {
        collectionReference.orderBy(FirebaseFields.TRiP_DATE, Query.Direction.DESCENDING).limit(10).get().addOnCompleteListener(task -> runTaskValidation(task, callback));
    }


    public static void getDocumentsFromQueryInCollection(@NonNull Query query, final Callback callback) {
        query.get().addOnCompleteListener(task -> runTaskValidation(task, callback));
    }

    private static void runTaskValidation(@NonNull Task task, Callback callback) {
        if (task.isSuccessful()) callback.onSuccess(task);
        else callback.onError(FirebaseConstants.FAIL);
    }

    public static void createNewUser(String email, String password, Callback callback) {
        FirebaseInitVariables.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> runTaskValidation(task, callback));
    }
    public static void signInUser(String email, String password, Callback callback) {
        FirebaseInitVariables.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> runTaskValidation(task, callback));
    }

    public static DocumentReference createDocumentReference(String path, String documentRef) {
        String documentReference = path + "/" + documentRef;
        return FirebaseInitVariables.db.document(documentReference);
    }
    public static DocumentReference createDocumentReference(String path) {
        return FirebaseInitVariables.db.document(path);
    }

    public static CollectionReference createCollectionReference(String path) {
        return FirebaseInitVariables.db.collection(path);
    }

    public static Query createQuery(CollectionReference reference, String key, String value) {
        return reference.whereEqualTo(key, value);
    }
    public static Query createArrayQuery(CollectionReference reference, String key, String value) {
        return reference.whereArrayContains(key,value);
    }

    public static void updateGoogleSignInUserData(DocumentReference reference, Activity activity, Class nextClass) {
        if (checkIfDocumentExists(reference, activity))
            redirectActivity(activity, nextClass);
        else updateSignedUpUserDetails(reference, activity);
        redirectActivity(activity, nextClass);

    }
    public static void getPictureFromFirebaseStorage(StorageReference reference, Callback callback){
        reference.getDownloadUrl()
                .addOnSuccessListener(uri -> {callback.onSuccess(uri);})
                .addOnFailureListener(e ->{ Log.e(TAG, "onFailure: ",e.getCause());callback.onError(e);});
    }
    public static void deleteBatch(List<DocumentReference> references,Callback callback) {
        // Get a new write batch
        WriteBatch batch = db.batch();
        for (DocumentReference reference: references ) {
            batch.delete(reference);
        }

        batch.commit().addOnCompleteListener(task -> {
            runTaskValidation(task,callback);
        });
    }

    public static boolean checkIfDocumentExists(DocumentReference reference, Activity activity) {
        isSuccessful = false;

        getDocument(reference, new Callback() {
            @Override
            public void onSuccess(Object object) {
                if (((Task<DocumentSnapshot>) object).getResult().exists()) isSuccessful = true; //;
                else isSuccessful = false;
            }

            @Override
            public void onError(Object object) {
                AppSystem.displayError(activity, activity.getApplicationContext(), (Exception) object);
            }
        });
        return isSuccessful;
    }


    public static void addUserToFirebaseDatabase(User user, Activity activity) {
        DocumentReference documentReference = createDocumentReference(FirebaseConstants.PASSENGERS, user.getUID());
        DocumentReference passWalletReference=createDocumentReference(FirebaseConstants.PASSENGERS+"/"+user.getUID()+"/"+FirebaseConstants.PASSENGER_WALLET+"/"+user.getUID());
        DocumentReference driverWalletReference=createDocumentReference(FirebaseConstants.PASSENGERS+"/"+user.getUID()+"/"+FirebaseConstants.DRIVER_WALLET+"/"+user.getUID());

        user.createUserInFirebase(user.getMapDetails(), documentReference, activity);
        user.createUserInFirebase(updateNewWallet(),driverWalletReference,activity);
        user.createUserInFirebase(updateNewWallet(),passWalletReference,activity);
    }
    private static Map updateNewWallet() {
        Map<String,Object> map=new HashMap<>();
        map.put(FirebaseFields.CASH,0);
        return map;
    }
    private static void updateSignedUpUserDetails(DocumentReference reference, Activity activity) {
        setDocument(new User().getMapDetails(), reference, new Callback() {
            @Override
            public void onSuccess(Object object) {
                return;
            }

            @Override
            public void onError(Object object) {
                AppSystem.displayError(activity, activity.getApplicationContext(), (Exception) object);
            }
        });
    }

    public static boolean signInWithPhoneCredential(PhoneAuthCredential credential) {
        isSuccessful=false;
        FirebaseInitVariables.mAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {isSuccessful=true;});
        return isSuccessful;

    }

    public static WriteBatch createSetBatch(DocumentReference reference, WriteBatch batch, Map<String,Object> map){
        return batch.set(reference,map);

    }
    public static WriteBatch createDeleteBatch(DocumentReference reference, WriteBatch batch,Map<String,Object> map){
        return batch.delete(reference);

    }
    public static void submitBatch(WriteBatch batch, Callback callback){
        batch.commit()
                .addOnSuccessListener(command -> callback.onSuccess(true))
                .addOnFailureListener(callback::onError);
    }

    public static void setDocument(Map details, DocumentReference documentReference, SetOptions merge, Callback callback) {
        documentReference.set(details,merge).addOnCompleteListener(task -> runTaskValidation(task, callback));
    }


}

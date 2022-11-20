package com.example.poolrdriver.Firebase.Constants;

import static com.example.poolrdriver.Firebase.Constants.FirebaseInitVariables.*;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

public class FirebaseCollections {
    //collectionReferences
    public static final CollectionReference NETWORK_REFERENCE= db.collection(FirebasePaths.NetworkPath);
    public static final CollectionReference PASSENGER_REFERENCE=db.collection(FirebasePaths.PassengerPath);

}

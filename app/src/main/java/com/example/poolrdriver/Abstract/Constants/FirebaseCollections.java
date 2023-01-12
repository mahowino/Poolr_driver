package com.example.poolrdriver.Abstract.Constants;

import static com.example.poolrdriver.Abstract.Constants.FirebaseInitVariables.*;

import com.google.firebase.firestore.CollectionReference;

public class FirebaseCollections {
    //collectionReferences
    public static final CollectionReference NETWORK_REFERENCE= db.collection(FirebasePaths.NetworkPath);
    public static final CollectionReference PASSENGER_REFERENCE=db.collection(FirebasePaths.PassengerPath);

}

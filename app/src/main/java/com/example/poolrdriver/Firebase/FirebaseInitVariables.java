package com.example.poolrdriver.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseInitVariables {
    public static FirebaseFirestore db=FirebaseFirestore.getInstance();
    public static FirebaseAuth mAuth=FirebaseAuth.getInstance();

}

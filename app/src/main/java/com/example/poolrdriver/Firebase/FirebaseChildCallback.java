package com.example.poolrdriver.Firebase;

public abstract class FirebaseChildCallback {

    public abstract void onChildAdded(Object object);

    public abstract void onChildChanged(Object object);

    public abstract void onChildRemoved(Object object);

    public abstract void onCancelled(Object object);

}

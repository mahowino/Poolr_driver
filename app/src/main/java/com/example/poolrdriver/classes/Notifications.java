package com.example.poolrdriver.classes;

import android.net.Uri;

import java.net.URL;

public class Notifications {

    /**
     *  type 1: request to pool with you
     *  type 2: successfully posted a ride
     *  type 3: notification user is going the same way
     */

    private String Message;
    private int type;
    private Uri imageUri;

    public Notifications(String message, int type) {
        Message = message;
        this.type = type;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

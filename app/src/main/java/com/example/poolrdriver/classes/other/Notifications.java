package com.example.poolrdriver.classes.other;

import android.net.Uri;

import java.net.URL;

public class Notifications {

    /**
     *  type 1: request to pool with you
     *  type 2: successfully posted a ride
     *  type 3: notification user is going the same way
     */

    private String message,Title;
    private int type;
    private Uri imageUri;

    public Notifications(String message, int type,String Title) {
        this.message = message;
        this.Title=Title;
        this.type = type;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

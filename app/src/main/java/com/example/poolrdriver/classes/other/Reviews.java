package com.example.poolrdriver.classes.other;

import android.net.Uri;

public class Reviews {
    String reviewerName,review;

    public Uri getReviewerProfilePicture() {
        return reviewerProfilePicture;
    }

    public void setReviewerProfilePicture(Uri reviewerProfilePicture) {
        this.reviewerProfilePicture = reviewerProfilePicture;
    }

    Uri reviewerProfilePicture;
    float rating;

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}

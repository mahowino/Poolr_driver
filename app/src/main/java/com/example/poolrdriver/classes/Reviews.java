package com.example.poolrdriver.classes;

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
    int rating;

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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}

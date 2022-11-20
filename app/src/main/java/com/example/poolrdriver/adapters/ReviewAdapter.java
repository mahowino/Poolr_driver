package com.example.poolrdriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.poolrdriver.R;
import com.example.poolrdriver.classes.Reviews;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    Context mContext;
    List<Reviews> reviews;

    public ReviewAdapter(Context mContext, List<Reviews> reviews) {
        this.mContext = mContext;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.review_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        Reviews review=reviews.get(position);
        setDataOnReview(review,holder);
    }

    private void setDataOnReview(Reviews review,ViewHolder holder) {
        holder.reviewerName.setText(review.getReviewerName());
        holder.review.setText(review.getReview());
        holder.reviewRating.setText(review.getRating()+" stars");
        Glide.with(holder.itemView).load(review.getReviewerProfilePicture()).into(holder.reviewerProfilePicture);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView reviewerName,review,reviewRating;
        ImageView reviewerProfilePicture;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeVariables(itemView);

        }

        private void initializeVariables(View itemView) {
            reviewerName=itemView.findViewById(R.id.reviewer_passenger_name);
            review=itemView.findViewById(R.id.reviewer_passenger_review);
            reviewRating=itemView.findViewById(R.id.reviewer_passenger_rating);
            reviewerProfilePicture=itemView.findViewById(R.id.reviewer_passenger_DP);
        }
    }
}

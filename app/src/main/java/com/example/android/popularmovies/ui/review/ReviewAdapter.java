package com.example.android.popularmovies.ui.review;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.databinding.ReviewListItemBinding;
import com.example.android.popularmovies.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> mReviews;
    private final ReviewAdapterOnClickHandler mOnClickHandler;

    public interface ReviewAdapterOnClickHandler {
        void onItemClick(String url);
    }

    public ReviewAdapter(List<Review> reviews, ReviewAdapterOnClickHandler onClickHandler) {
        mReviews = reviews;
        mOnClickHandler = onClickHandler;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        ReviewListItemBinding reviewItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.review_list_item, viewGroup, false);
        return new ReviewViewHolder(reviewItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        if (null == mReviews) return 0;
        return mReviews.size();
    }

    public void addAll(List<Review> reviews) {
        mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ReviewListItemBinding mReviewItemBinding;

        ReviewViewHolder(ReviewListItemBinding reviewItemBinding) {
            super(reviewItemBinding.getRoot());
            mReviewItemBinding = reviewItemBinding;

            // Call setOnClickListener on  the View passed into the constructor
            itemView.setOnClickListener(this);
        }

        void bind(Review review) {
            // Set author and content of review to the TextView
            mReviewItemBinding.setReview(review);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Review review = mReviews.get(adapterPosition);
            mOnClickHandler.onItemClick(review.getUrl());
        }
    }
}

package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewResponse {

    @SerializedName("id")
    private int mId;

    @SerializedName("page")
    private int mPage;

    @SerializedName("results")
    private List<Review> mReviewResults = null;

    public ReviewResponse() {
    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setPage(int page) {
        mPage = page;
    }

    public int getPage() {
        return mPage;
    }

    public void setReviewResults(List<Review> reviewResults) {
        mReviewResults = reviewResults;
    }

    public List<Review> getReviewResults() {
        return mReviewResults;
    }
}

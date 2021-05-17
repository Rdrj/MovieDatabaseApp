package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoResponse {

    @SerializedName("id")
    private int mId;

    @SerializedName("results")
    private List<Video> mVideoResults = null;

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setVideoResults(List<Video> videoResults) {
        mVideoResults = videoResults;
    }

    public List<Video> getVideoResults() {
        return mVideoResults;
    }
}

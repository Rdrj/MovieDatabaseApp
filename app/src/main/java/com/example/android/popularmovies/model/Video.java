package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("id")
    private String mVideoId;

    @SerializedName("key")
    private String mKey;

    @SerializedName("name")
    private String mName;

    @SerializedName("site")
    private String mSite;

    @SerializedName("size")
    private int mSize;

    @SerializedName("type")
    private String mType;

    public Video(){

    }

    public void setVideoId(String videoId) {
        mVideoId = videoId;
    }

    public String getVideoId() {
        return mVideoId;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getKey() {
        return mKey;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setSite(String site) {
        mSite = site;
    }

    public String getSite() {
        return mSite;
    }

    public void setSize(int size) {
        mSize = size;
    }

    public int getSize() {
        return mSize;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }
}

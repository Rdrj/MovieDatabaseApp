package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("author")
    private String mAuthor;

    @SerializedName("content")
    private String mContent;

    @SerializedName("id")
    private String mId;

    @SerializedName("url")
    private String mUrl;

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getContent() {
        return mContent;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }
}

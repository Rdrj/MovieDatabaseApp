package com.example.android.popularmovies.data;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.example.android.popularmovies.model.Movie;

public class MovieDataSourceFactory extends DataSource.Factory<Integer, Movie> {

    private MutableLiveData<MovieDataSource> mPostLiveData;
    private MovieDataSource mMovieDataSource;
    private String mSortBy;

    public MovieDataSourceFactory(String sortBy) {
        mPostLiveData = new MutableLiveData<>();
        mSortBy = sortBy;
    }

    @Override
    public DataSource<Integer, Movie> create() {
        mMovieDataSource = new MovieDataSource(mSortBy);
        mPostLiveData = new MutableLiveData<>();
        mPostLiveData.postValue(mMovieDataSource);

        return mMovieDataSource;
    }
}

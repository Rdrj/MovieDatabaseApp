package com.example.android.popularmovies.data;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.example.android.popularmovies.model.Movie;

public class SearchDataSourceFactory extends DataSource.Factory<Integer, Movie>{
    private MutableLiveData<SearchDataSource> mPostLiveData;
    private SearchDataSource searchDataSource;
    private String query;

    public SearchDataSourceFactory(String query) {
        mPostLiveData = new MutableLiveData<>();
        this.query = query;
    }

    @Override
    public DataSource<Integer, Movie> create() {
        searchDataSource = new SearchDataSource(query);
        mPostLiveData = new MutableLiveData<>();
        mPostLiveData.postValue(searchDataSource);

        return searchDataSource;
    }
}

package com.example.android.popularmovies.ui.trailer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.popularmovies.data.MovieRepository;
import com.example.android.popularmovies.model.VideoResponse;

public class TrailerViewModel extends ViewModel {

    private final MovieRepository mRepository;
    private final LiveData<VideoResponse> mVideoResponse;

    public TrailerViewModel (MovieRepository repository, int movieId) {
        mRepository = repository;
        mVideoResponse = mRepository.getVideoResponse(movieId);
    }

    public LiveData<VideoResponse> getVideoResponse() {
        return mVideoResponse;
    }
}

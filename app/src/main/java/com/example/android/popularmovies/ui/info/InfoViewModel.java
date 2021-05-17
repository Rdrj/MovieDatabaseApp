package com.example.android.popularmovies.ui.info;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.popularmovies.data.MovieRepository;
import com.example.android.popularmovies.model.MovieDetails;

public class InfoViewModel extends ViewModel {

    private final MovieRepository mRepository;
    private final LiveData<MovieDetails> mMovieDetails;

    public InfoViewModel (MovieRepository repository, int movieId) {
        mRepository = repository;
        mMovieDetails = mRepository.getMovieDetails(movieId);
    }

    public LiveData<MovieDetails> getMovieDetails() {
        return mMovieDetails;
    }
}

package com.example.android.popularmovies.ui.review;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.popularmovies.data.MovieRepository;

public class ReviewViewModelFactory extends ViewModelProvider.NewInstanceFactory  {

    private final MovieRepository mRepository;
    private final int mMovieId;

    public ReviewViewModelFactory(MovieRepository repository, int movieId) {
        this.mRepository = repository;
        this.mMovieId = movieId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ReviewViewModel(mRepository, mMovieId);
    }
}

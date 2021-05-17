package com.example.android.popularmovies.ui.main;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.popularmovies.data.MovieRepository;

public class FavViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MovieRepository mRepository;
    private final int mMovieId;

    public FavViewModelFactory(MovieRepository repository, int movieId) {
        mRepository = repository;
        mMovieId = movieId;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new FavViewModel(mRepository, mMovieId);
    }
}

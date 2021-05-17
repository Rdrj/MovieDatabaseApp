package com.example.android.popularmovies.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.android.popularmovies.data.MovieDataSourceFactory;
import com.example.android.popularmovies.data.MovieEntry;
import com.example.android.popularmovies.data.MovieRepository;
import com.example.android.popularmovies.data.SearchDataSourceFactory;
import com.example.android.popularmovies.model.Movie;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.Module;

import static com.example.android.popularmovies.utilities.Constant.INITIAL_LOAD_SIZE_HINT;
import static com.example.android.popularmovies.utilities.Constant.NUMBER_OF_FIXED_THREADS_FIVE;
import static com.example.android.popularmovies.utilities.Constant.PAGE_SIZE;
import static com.example.android.popularmovies.utilities.Constant.PREFETCH_DISTANCE;

public class MainActivityViewModel extends ViewModel {

    private final MovieRepository mRepository;

    private LiveData<PagedList<Movie>> mMoviePagedList;
    private LiveData<PagedList<Movie>> mSearchPagedList;
    private LiveData<List<MovieEntry>> mFavoriteMovies;
    private LiveData<Movie> mMovie;

    public MainActivityViewModel(MovieRepository repository, String sortCriteria) {
        mRepository = repository;
        init(sortCriteria);
    }

    private void init(String sortCriteria) {
        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_FIXED_THREADS_FIVE);
        MovieDataSourceFactory movieDataFactory = new MovieDataSourceFactory(sortCriteria);

        PagedList.Config config = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                // Size hint for initial load of PagedList
                .setInitialLoadSizeHint(INITIAL_LOAD_SIZE_HINT)
                // Size of each page loaded by the PagedList
                .setPageSize(PAGE_SIZE)
                // Prefetch distance which defines how far ahead to load
                .setPrefetchDistance(PREFETCH_DISTANCE)
                .build();

        // The LivePagedListBuilder class is used to get a LiveData object of type PagedList
        mMoviePagedList = new LivePagedListBuilder<>(movieDataFactory, config)
                .setFetchExecutor(executor)
                .build();
    }

    public void initSearch(String query){
        initSearchQuery(query);
    }

    private void initSearchQuery(String query) {
        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_FIXED_THREADS_FIVE);

        SearchDataSourceFactory searchDataFactory = new SearchDataSourceFactory(query);
        PagedList.Config config = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(INITIAL_LOAD_SIZE_HINT)
                .setPageSize(PAGE_SIZE)
                .setPrefetchDistance(PREFETCH_DISTANCE)
                .build();

        mSearchPagedList = new LivePagedListBuilder<>(searchDataFactory, config)
                .setFetchExecutor(executor)
                .build();
    }

    public LiveData<PagedList<Movie>> getMoviePagedList() {
        return mMoviePagedList;
    }

    public LiveData<PagedList<Movie>> getSearchPagedList() {
        return mSearchPagedList;
    }

    public LiveData<Movie> findMovie(Integer movieId) {
        mMovie = mRepository.findMovie(movieId);
        return mMovie;
    }

    public LiveData<List<MovieEntry>> getFavoriteMovies() {
        return mFavoriteMovies;
    }

    public void setFavoriteMovies() {
        mFavoriteMovies = mRepository.getFavoriteMovies();
    }


}

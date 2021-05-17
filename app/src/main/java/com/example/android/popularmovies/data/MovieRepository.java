package com.example.android.popularmovies.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import com.example.android.popularmovies.AppExecutors;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieDetails;
import com.example.android.popularmovies.model.MovieResponse;
import com.example.android.popularmovies.model.ReviewResponse;
import com.example.android.popularmovies.model.VideoResponse;
import com.example.android.popularmovies.utilities.Constant;
import com.example.android.popularmovies.utilities.TheMovieApi;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.popularmovies.utilities.Constant.API_KEY;
import static com.example.android.popularmovies.utilities.Constant.CREDITS;
import static com.example.android.popularmovies.utilities.Constant.LANGUAGE;
import static com.example.android.popularmovies.utilities.Constant.NEXT_PAGE_KEY_TWO;
import static com.example.android.popularmovies.utilities.Constant.PAGE;
import static com.example.android.popularmovies.utilities.Constant.PREVIOUS_PAGE_KEY_ONE;
import static com.example.android.popularmovies.utilities.Constant.RESPONSE_CODE_API_STATUS;

public class MovieRepository {

    private static final String TAG = MovieRepository.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static MovieRepository sInstance;
    private final MovieDao mMovieDao;
    private final TheMovieApi mTheMovieApi;
    private String mSortCriteria;

    public MovieRepository(MovieDao movieDao,
                            TheMovieApi theMovieApi,
                            AppExecutors executors) {
        mMovieDao = movieDao;
        mTheMovieApi = theMovieApi;
    }

    public synchronized static MovieRepository getInstance(
            MovieDao movieDao, TheMovieApi theMovieApi, AppExecutors executors) {
        Log.d(TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Making new repository");
                sInstance = new MovieRepository(movieDao, theMovieApi, executors);
            }
        }
        return sInstance;
    }

    public LiveData<Movie> findMovie(int movieId) {
        final MutableLiveData<Movie> movie = new MutableLiveData<>();

        mTheMovieApi.findMovie(movieId, API_KEY)
                .enqueue(new Callback<Movie>() {
                    @Override
                    public void onResponse(Call<Movie> call, Response<Movie> response) {
                        if (response.isSuccessful()) {
                            Movie movieDetails = response.body();
                            movie.setValue(movieDetails);
                        }
                    }

                    @Override
                    public void onFailure(Call<Movie> call, Throwable t) {
                        movie.setValue(null);
                        Log.e(TAG, "Failed getting MovieDetails: " + t.getMessage());
                    }
                });
        return movie;
    }

    public LiveData<MovieDetails> getMovieDetails(int movieId) {
        final MutableLiveData<MovieDetails> movieDetailsData = new MutableLiveData<>();

        mTheMovieApi.getDetails(movieId, API_KEY, LANGUAGE, CREDITS)
                .enqueue(new Callback<MovieDetails>() {
                    @Override
                    public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                        if (response.isSuccessful()) {
                            MovieDetails movieDetails = response.body();
                            movieDetailsData.setValue(movieDetails);
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieDetails> call, Throwable t) {
                        movieDetailsData.setValue(null);
                        Log.e(TAG, "Failed getting MovieDetails: " + t.getMessage());
                    }
                });
        return movieDetailsData;
    }

    public LiveData<ReviewResponse> getReviewResponse(int movieId) {
        final MutableLiveData<ReviewResponse> reviewResponseData = new MutableLiveData<>();
        mTheMovieApi.getReviews(movieId, API_KEY, LANGUAGE, PAGE)
                .enqueue(new Callback<ReviewResponse>() {
                    /**
                     * Invoked for a received HTTP response.
                     */
                    @Override
                    public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                        if (response.isSuccessful()) {
                            ReviewResponse reviewResponse = response.body();
                            reviewResponseData.setValue(reviewResponse);
                        }
                    }

                    /**
                     * Invoked when a network exception occurred talking to the server or when an unexpected exception
                     * occurred creating the request or processing the response.
                     */
                    @Override
                    public void onFailure(Call<ReviewResponse> call, Throwable t) {
                        reviewResponseData.setValue(null);
                        Log.e(TAG, "Failed getting ReviewResponse: " + t.getMessage());
                    }
                });
        return reviewResponseData;
    }

    /**
     * Make a network request by calling enqueue and provide a LiveData object of VideoResponse for ViewModel
     *
     * @param movieId The ID of the movie
     */
    public LiveData<VideoResponse> getVideoResponse(int movieId) {
        final MutableLiveData<VideoResponse> videoResponseData = new MutableLiveData<>();
        mTheMovieApi.getVideos(movieId, API_KEY, LANGUAGE)
                .enqueue(new Callback<VideoResponse>() {
                    @Override
                    public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                        if (response.isSuccessful()) {
                            VideoResponse videoResponse = response.body();
                            if (videoResponse != null) {
                                videoResponseData.setValue(videoResponse);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<VideoResponse> call, Throwable t) {
                        videoResponseData.setValue(null);
                        Log.e(TAG, "Failed getting VideoResponse: " + t.getMessage());
                    }
                });
        return videoResponseData;
    }

    /**
     * Return a LiveData of the list of MovieEntries directly from the database
     */
    public LiveData<List<MovieEntry>> getFavoriteMovies() {
        return mMovieDao.loadAllMovies();
    }

    /**
     * Returns a LiveData of MovieEntry directly from the database
     *
     * @param movieId The movie ID
     */
    public LiveData<MovieEntry> getFavoriteMovieByMovieId(int movieId) {
        return mMovieDao.loadMovieByMovieId(movieId);
    }

}
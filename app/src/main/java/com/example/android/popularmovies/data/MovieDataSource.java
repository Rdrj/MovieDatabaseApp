package com.example.android.popularmovies.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieResponse;
import com.example.android.popularmovies.utilities.Constant;
import com.example.android.popularmovies.utilities.Controller;
import com.example.android.popularmovies.utilities.TheMovieApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.popularmovies.utilities.Constant.NEXT_PAGE_KEY_TWO;
import static com.example.android.popularmovies.utilities.Constant.PREVIOUS_PAGE_KEY_ONE;
import static com.example.android.popularmovies.utilities.Constant.RESPONSE_CODE_API_STATUS;
public class MovieDataSource extends PageKeyedDataSource<Integer, Movie> {

    private static final String TAG = MovieDataSource.class.getSimpleName();

    private TheMovieApi mTheMovieApi;
    private String mSortCriteria;

    public MovieDataSource(String sortCriteria) {
        mTheMovieApi = Controller.getClient().create(TheMovieApi.class);
        mSortCriteria = sortCriteria;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull final LoadInitialCallback<Integer, Movie> callback) {
        mTheMovieApi.getMovies(mSortCriteria, Constant.API_KEY, Constant.LANGUAGE, Constant.PAGE_ONE)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful()) {
                            callback.onResult(response.body().getMovieResults(),
                                    PREVIOUS_PAGE_KEY_ONE, NEXT_PAGE_KEY_TWO);

                        } else if (response.code() == RESPONSE_CODE_API_STATUS) {
                            Log.e(TAG, "Invalid Api key. Response code: " + response.code());
                        } else {
                            Log.e(TAG, "Response Code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Log.e(TAG, "Failed initializing a PageList: " + t.getMessage());
                    }
                });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params,
                           @NonNull LoadCallback<Integer, Movie> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params,
                          @NonNull final LoadCallback<Integer, Movie> callback) {

        final int currentPage = params.key;

        mTheMovieApi.getMovies(mSortCriteria, Constant.API_KEY, Constant.LANGUAGE, currentPage)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful()) {
                            int nextKey = currentPage + 1;
                            callback.onResult(response.body().getMovieResults(), nextKey);
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Log.e(TAG, "Failed appending page: " + t.getMessage());
                    }
                });

    }
}

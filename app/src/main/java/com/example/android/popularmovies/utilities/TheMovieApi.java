package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieDetails;
import com.example.android.popularmovies.model.MovieResponse;
import com.example.android.popularmovies.model.ReviewResponse;
import com.example.android.popularmovies.model.VideoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieApi {

    @GET("movie/{sort_criteria}")
    Call<MovieResponse> getMovies(
            @Path("sort_criteria") String sortCriteria,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("search/movie")
    Call<MovieResponse> searchMovie(
            @Query("query") String query,
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET("movie/{id}")
    Call<Movie> findMovie(
            @Path("id") int id,
            @Query("api_key") String apiKey
    );

    @GET("movie/{id}")
    Call<MovieDetails> getDetails(
            @Path("id") int id,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("append_to_response") String credits
    );

    @GET("movie/{id}/reviews")
    Call<ReviewResponse> getReviews(
            @Path("id") int id,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("movie/{id}/videos")
    Call<VideoResponse> getVideos(
            @Path("id") int id,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

}

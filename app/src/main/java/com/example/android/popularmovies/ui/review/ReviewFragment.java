package com.example.android.popularmovies.ui.review;

import androidx.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.databinding.FragmentReviewBinding;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.ReviewResponse;
import com.example.android.popularmovies.utilities.InjectorUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.utilities.Constant.EXTRA_MOVIE;

public class ReviewFragment extends Fragment implements ReviewAdapter.ReviewAdapterOnClickHandler {

    private static final String TAG = ReviewFragment.class.getSimpleName();
    private List<Review> mReviews;
    private FragmentReviewBinding mReviewBinding;
    private ReviewAdapter mReviewAdapter;
    private Movie mMovie;
    private ReviewViewModel mReviewViewModel;
    public ReviewFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Store the Intent
        Intent intent = getActivity().getIntent();
        // Check if the Intent is not null, and has the extra we passed from MainActivity
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                // Receive the Movie object which contains information, such as ID, original title,
                // poster path, overview, vote average, release date, backdrop path.
                Bundle b = intent.getBundleExtra(EXTRA_MOVIE);
                mMovie = b.getParcelable(EXTRA_MOVIE);
            }
        }

        // Observe the data and update the UI
        setupViewModel(this.getActivity());
    }

    private void setupViewModel(Context context) {
        ReviewViewModelFactory factory = InjectorUtils.provideReviewViewModelFactory(context, mMovie.getId());
        mReviewViewModel = new ViewModelProvider(this, factory).get(ReviewViewModel.class);

        mReviewViewModel.getReviewResponse().observe(getViewLifecycleOwner(), new Observer<ReviewResponse>() {
            @Override
            public void onChanged(@Nullable ReviewResponse reviewResponse) {
                if (reviewResponse != null) {
                    // Get the list of reviews
                    mReviews = reviewResponse.getReviewResults();
                    reviewResponse.setReviewResults(mReviews);
                    if (!mReviews.isEmpty()) {
                        mReviewAdapter.addAll(mReviews);
                    } else {
                        // If there are no reviews, show a message that says no reviews found
                        showNoReviewsMessage();
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Instantiate mReviewBinding using DataBindingUtil
        mReviewBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_review, container, false);
        View rootView = mReviewBinding.getRoot();

        // A LinearLayoutManager is responsible for measuring and positioning item views within a
        // RecyclerView into a linear list.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mReviewBinding.rvReview.setLayoutManager(layoutManager);
        mReviewBinding.rvReview.setHasFixedSize(true);

        // Create an empty ArrayList
        mReviews = new ArrayList<>();

        // The ReviewAdapter is responsible for displaying each item in the list.
        mReviewAdapter = new ReviewAdapter(mReviews, this);
        // Set ReviewAdapter on RecyclerView
        mReviewBinding.rvReview.setAdapter(mReviewAdapter);

        // Show a message when offline
        showOfflineMessage(isOnline());

        return rootView;
    }

    @Override
    public void onItemClick(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void showNoReviewsMessage() {
        // First, hide the currently visible data
        mReviewBinding.rvReview.setVisibility(View.INVISIBLE);
        // Then, show a message that says no reviews found
        mReviewBinding.tvNoReviews.setVisibility(View.VISIBLE);
    }

    private void showOfflineMessage(boolean isOnline) {
        if (isOnline) {
            // First, hide the offline message
            mReviewBinding.tvOffline.setVisibility(View.INVISIBLE);
            // Then, make sure the review data is visible
            mReviewBinding.rvReview.setVisibility(View.VISIBLE);
        } else {
            // First, hide the currently visible data
            mReviewBinding.rvReview.setVisibility(View.INVISIBLE);
            // Then, show an offline message
            mReviewBinding.tvOffline.setVisibility(View.VISIBLE);
        }
    }

    private boolean isOnline() {
        // Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}

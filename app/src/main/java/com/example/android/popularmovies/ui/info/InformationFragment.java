/*
 *  Copyright 2018 Soojeong Shin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.android.popularmovies.ui.info;

import android.app.Activity;
import androidx.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.databinding.FragmentInfoBinding;
import com.example.android.popularmovies.model.Cast;
import com.example.android.popularmovies.model.Credits;
import com.example.android.popularmovies.model.Crew;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieDetails;
import com.example.android.popularmovies.utilities.FormatUtils;
import com.example.android.popularmovies.utilities.InjectorUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.utilities.Constant.EXTRA_MOVIE;

public class InformationFragment extends Fragment {

    private FragmentInfoBinding mInfoBinding;
    OnInfoSelectedListener mCallback;

    public interface OnInfoSelectedListener {
        void onInformationSelected(MovieDetails movieDetails);
    }
    OnViewAllSelectedListener mViewAllCallback;

    public interface OnViewAllSelectedListener {
        void onViewAllSelected();
    }
    private Movie mMovie;
    private InfoViewModel mInfoViewModel;

    public InformationFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMovie = getMovieData();
        setupViewModel(this.getActivity(), mMovie.getId());
        loadDetails();
    }

    private void setupViewModel(Context context, int movieId) {
        InfoViewModelFactory factory = InjectorUtils.provideInfoViewModelFactory(context, movieId);
        mInfoViewModel = new ViewModelProvider(this, factory).get(InfoViewModel.class);
        mInfoViewModel.getMovieDetails().observe(getViewLifecycleOwner(), new Observer<MovieDetails>() {
            @Override
            public void onChanged(@Nullable MovieDetails movieDetails) {
                if (movieDetails != null) {
                    mCallback.onInformationSelected(movieDetails);
                    loadMovieDetailInfo(movieDetails);
                    loadCastCrew(movieDetails);
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInfoBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_info, container, false);
        View rootView = mInfoBinding.getRoot();
        mInfoBinding.tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trigger the callback onViewAllSelected
                mViewAllCallback.onViewAllSelected();
            }
        });

        return rootView;
    }

    private Movie getMovieData() {
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                Bundle b = intent.getBundleExtra(EXTRA_MOVIE);
                mMovie = b.getParcelable(EXTRA_MOVIE);
            }
        }
        return mMovie;
    }

    private void loadCastCrew(MovieDetails movieDetails) {
        Credits credits = movieDetails.getCredits();
        List<Cast> castList = credits.getCast();
        List<String> castStrList = new ArrayList<>();
        for (int i = 0; i < castList.size(); i++) {
            Cast cast = castList.get(i);
            String castName = cast.getName();
            castStrList.add(castName);
        }

        Activity activity = getActivity();
        if (activity != null) {
            // Join a string using a delimiter
            String castStr = TextUtils.join(getString(R.string.delimiter_comma), castStrList);
            // Display the list of cast name
            mInfoBinding.tvCast.setText(castStr);

            // Display director of the movie
            List<Crew> crewList = credits.getCrew();
            for (int i = 0; i < crewList.size(); i++) {
                Crew crew = crewList.get(i);
                // if job is "director", set the director's name to the TextView
                if (crew.getJob().equals(getString(R.string.director))) {
                    mInfoBinding.tvDirector.setText(crew.getName());
                    break;
                }
            }
        }
    }

    private void loadMovieDetailInfo(MovieDetails movieDetails) {
        // Get the  vote count, budget, revenue, status
        int voteCount = movieDetails.getVoteCount();
        long budget = movieDetails.getBudget();
        long revenue = movieDetails.getRevenue();
        String status = movieDetails.getStatus();

        // Display vote count, budget, revenue, status of the movie. Use FormatUtils class
        // to format the integer number
        mInfoBinding.tvVoteCount.setText(FormatUtils.formatNumber(voteCount));
        mInfoBinding.tvBudget.setText(FormatUtils.formatCurrency(budget));
        mInfoBinding.tvRevenue.setText(FormatUtils.formatCurrency(revenue));
        mInfoBinding.tvStatus.setText(status);
    }

    private void loadDetails() {
        // Display the overview of the movie
        mInfoBinding.tvOverview.setText(mMovie.getOverview());
        // Display the vote average of the movie
        mInfoBinding.tvVoteAverage.setText(String.valueOf(mMovie.getVoteAverage()));
        // Display the original title of the movie
        mInfoBinding.tvOriginalTitle.setText(mMovie.getOriginalTitle());
        // Display the release date of the movie
        mInfoBinding.tvReleaseDate.setText(FormatUtils.formatDate(mMovie.getReleaseDate()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnInfoSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnInfoSelectedListener");
        }

        try {
            mViewAllCallback = (OnViewAllSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnViewAllSelectedListener");
        }
    }
}

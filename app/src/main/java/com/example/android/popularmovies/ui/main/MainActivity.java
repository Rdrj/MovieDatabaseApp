
package com.example.android.popularmovies.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieEntry;
import com.example.android.popularmovies.databinding.ActivityMainBinding;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.ui.detail.DetailActivity;
import com.example.android.popularmovies.utilities.InjectorUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import static com.example.android.popularmovies.utilities.Constant.EXTRA_MOVIE;
import static com.example.android.popularmovies.utilities.Constant.GRID_SPAN_COUNT;
import static com.example.android.popularmovies.utilities.Constant.LAYOUT_MANAGER_STATE;
import static com.example.android.popularmovies.utilities.Constant.REQUEST_CODE_DIALOG;

public class MainActivity extends AppCompatActivity implements
        FavoriteAdapter.FavoriteAdapterOnClickHandler,
        MoviePagedListAdapter.MoviePagedListAdapterOnClickHandler {

    private MoviePagedListAdapter mMoviePagedListAdapter;
    private FavoriteAdapter mFavoriteAdapter;
    private String mSortCriteria = "popular" ;
    private Parcelable mSavedLayoutState;

    public MainActivityViewModel mMainViewModel;
    private ActivityMainBinding mMainBinding;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private boolean savedOpen = false;
    private EditText editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initAdapter();

        setupViewModel(mSortCriteria);

        if (savedInstanceState == null) {
            showNetworkDialog(isOnline());
        }

        Intent intent =getIntent();
        if(intent!=null){
            handleDeeplink(intent);
        }

        updateUI(savedOpen);
        setSwipeRefreshLayout();

        if (savedInstanceState != null) {
            mSavedLayoutState = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE);
            mMainBinding.rvMovie.getLayoutManager().onRestoreInstanceState(mSavedLayoutState);
        }

    }

    private void handleDeeplink(Intent intent) {
        Uri uri = intent.getData();
        if(uri!=null){
            String lastPathSegment = uri.getLastPathSegment();
            Integer movieId = Integer.valueOf(lastPathSegment);
            mMainViewModel.findMovie(movieId).observe(this, new Observer<Movie>() {
                @Override
                public void onChanged(Movie movie) {
                    Bundle b = new Bundle();
                    b.putParcelable(EXTRA_MOVIE, movie);
                    Intent dlIntent = new Intent(MainActivity.this, DetailActivity.class);
                    dlIntent.putExtra(EXTRA_MOVIE, b);
                    startActivity(dlIntent);
                }
            });
        }

    }

    private void initAdapter() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_SPAN_COUNT);
        mMainBinding.rvMovie.setLayoutManager(layoutManager);
        mMainBinding.rvMovie.setHasFixedSize(true);
        mMoviePagedListAdapter = new MoviePagedListAdapter(this);
        mFavoriteAdapter = new FavoriteAdapter(this, this);
    }

    private void setupViewModel(String sortCriteria) {
        MainViewModelFactory factory = InjectorUtils.provideMainActivityViewModelFactory(
                MainActivity.this, sortCriteria);
        mMainViewModel = new ViewModelProvider(this, factory).get(MainActivityViewModel.class);
    }

    private void updateUI(Boolean isSavedOpen) {
        mMainViewModel.setFavoriteMovies();

        if (isSavedOpen) {
            mMainBinding.rvMovie.setAdapter(mFavoriteAdapter);
            observeFavoriteMovies();
        } else {
            mMainBinding.rvMovie.setAdapter(mMoviePagedListAdapter);
            observeMoviePagedList();
        }
    }

    private void observeMoviePagedList() {
        mMainViewModel.getMoviePagedList().observe(this, new Observer<PagedList<Movie>>() {
            @Override
            public void onChanged(@Nullable PagedList<Movie> pagedList) {
                mMainBinding.rvMovie.setVisibility(View.VISIBLE);
                if (pagedList != null) {
                    mMoviePagedListAdapter.submitList(pagedList);
                    mMainBinding.rvMovie.getLayoutManager().onRestoreInstanceState(mSavedLayoutState);
                }
                if (!isOnline()) {
                    mMainBinding.rvMovie.setVisibility(View.VISIBLE);
                    showSnackbarOffline();
                }
            }
        });
    }

    private void observeSearchPagedList() {
        mMainViewModel.getSearchPagedList().observe(this, new Observer<PagedList<Movie>>() {
            @Override
            public void onChanged(@Nullable PagedList<Movie> pagedList) {
                mMainBinding.rvMovie.setVisibility(View.VISIBLE);
                if (pagedList != null) {
                    mMoviePagedListAdapter.submitList(pagedList);
                    mMainBinding.rvMovie.getLayoutManager().onRestoreInstanceState(mSavedLayoutState);
                }
                if (!isOnline()) {
                    mMainBinding.rvMovie.setVisibility(View.VISIBLE);
                    showSnackbarOffline();
                }
            }
        });
    }

    private void observeFavoriteMovies() {
        mMainViewModel.getFavoriteMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                mFavoriteAdapter.setMovies(movieEntries);
                mMainBinding.rvMovie.getLayoutManager().onRestoreInstanceState(mSavedLayoutState);
            }
        });
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if(savedOpen) {
            savedOpen =false;
            updateUI(false);
            return;
        }
        if (isSearchOpened) {
            handleMenuSearch();
            return;
        }
        super.onBackPressed();
    }

    private void handleMenuSearch() {
        ActionBar action = getSupportActionBar();
        if (action == null) {
            Log.e("MainActivity.java", "getSupportActionBar returned null");
        }
        final boolean liveSearch = true;
        if (isSearchOpened) {
            if (editSearch.getText().toString().equals("")) {
                action.setDisplayShowCustomEnabled(true);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
                }

                mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search));
                isSearchOpened = false;
                action.setCustomView(null);
                action.setDisplayShowTitleEnabled(true);
                observeMoviePagedList();
            } else {
                editSearch.setText("");
            }
        }else {
            if (action != null) {
                action.setDisplayShowCustomEnabled(true);
                action.setCustomView(R.layout.search_bar);
                action.setDisplayShowTitleEnabled(false);
            }

            editSearch = (EditText) action.getCustomView().findViewById(R.id.editSearch);

            editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        mMainViewModel.initSearch(editSearch.getText().toString());
                        observeSearchPagedList();
                        return true;
                    }
                    return false;
                }
            });

            editSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (liveSearch) {
                        mMainViewModel.initSearch(editSearch.getText().toString());
                        observeSearchPagedList();
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            editSearch.requestFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(editSearch, InputMethodManager.SHOW_IMPLICIT);
            }

            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close));

            isSearchOpened = true;
        }
    }

    @Override
    public void onItemClick(Movie movie) {
        Bundle b = new Bundle();
        b.putParcelable(EXTRA_MOVIE, movie);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, b);
        startActivity(intent);
    }

    @Override
    public void onFavItemClick(MovieEntry movieEntry) {
        int movieId = movieEntry.getMovieId();
        String originalTitle = movieEntry.getOriginalTitle();
        String title = movieEntry.getTitle();
        String posterPath = movieEntry.getPosterPath();
        String overview = movieEntry.getOverview();
        double voteAverage = movieEntry.getVoteAverage();
        String releaseDate = movieEntry.getReleaseDate();
        String backdropPath = movieEntry.getBackdropPath();

        Movie movie = new Movie(movieId, originalTitle, title, posterPath, overview,
                voteAverage, releaseDate, backdropPath);
        Bundle b = new Bundle();
        b.putParcelable(EXTRA_MOVIE, movie);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, b);
        startActivity(intent);
    }

    private void setSwipeRefreshLayout() {
        mMainBinding.swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mMainBinding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mMainBinding.rvMovie.setVisibility(View.VISIBLE);
                updateUI(savedOpen);
                mMainBinding.swipeRefresh.setRefreshing(false);
                showSnackbarRefresh(isOnline());
            }
        });
    }

    private void showSnackbarRefresh(boolean isOnline) {
        if (isOnline) {
            Snackbar.make(mMainBinding.rvMovie, getString(R.string.snackbar_updated)
                    , Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showNetworkDialog(final boolean isOnline) {
        if (!isOnline) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
            builder.setIcon(R.drawable.ic_warning);
            builder.setTitle(getString(R.string.no_network_title));
            builder.setMessage(getString(R.string.no_network_message));
            builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), REQUEST_CODE_DIALOG);
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    savedOpen = true;
                    updateUI(true);
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void showSnackbarOffline() {
        Snackbar snackbar = Snackbar.make(
                mMainBinding.frameMain, R.string.snackbar_offline, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.WHITE);
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_saved:
                savedOpen = true;
                updateUI(true);
                return true;

            case R.id.action_search:
                handleMenuSearch();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LAYOUT_MANAGER_STATE,
                mMainBinding.rvMovie.getLayoutManager().onSaveInstanceState());
    }
}



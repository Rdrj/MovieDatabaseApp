package com.example.android.popularmovies;

import com.example.android.popularmovies.ui.main.MainActivity;
import com.example.android.popularmovies.utilities.InjectorUtils;

import dagger.Component;

public interface DIComponent {

    void injectMain(MainActivity mainActivity);
}

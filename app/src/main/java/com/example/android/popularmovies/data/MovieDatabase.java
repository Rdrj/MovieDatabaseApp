package com.example.android.popularmovies.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import static com.example.android.popularmovies.utilities.Constant.DATABASE_NAME;

@Database(entities = {MovieEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class MovieDatabase extends RoomDatabase {

    private static final String TAG = MovieDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static MovieDatabase sInstance;

    public static MovieDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MovieDatabase.class, DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract MovieDao movieDao();
}

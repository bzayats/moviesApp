package com.magiclabyrinth.popularmovies.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.magiclabyrinth.popularmovies.AppExecutors;
import com.magiclabyrinth.popularmovies.sync.MovieDataRepository;

import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {MovieEntry.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{

    private static final String TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "allMovies";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME).fallbackToDestructiveMigration().build();
            }
        }
        Log.d(TAG, "Getting database instance");

        return sInstance;
    }

    public abstract MovieEntryDao movieDao();
}

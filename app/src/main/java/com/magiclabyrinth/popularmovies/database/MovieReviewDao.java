package com.magiclabyrinth.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

//TODO: change this to accomodate reviews
@Dao
public interface MovieReviewDao {
    @Query("SELECT * FROM reviews")
    LiveData<List<MovieReviewEntry>> loadAllMovies();

    @Query("SELECT * FROM reviews WHERE id = :id")
    LiveData<MovieReviewEntry> loadMovieById(int id);

    @Insert
    void insertMovie(MovieReviewEntry favoriteMovieEntry);

    @Update
    void updateMovie(MovieReviewEntry favoriteMovieEntry);

    @Delete
    void deleteMovie(MovieReviewEntry favoriteMovieEntry);
}

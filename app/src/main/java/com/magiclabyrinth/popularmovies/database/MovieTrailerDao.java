package com.magiclabyrinth.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

//TODO: change this to accomodate trailers
@Dao
public interface MovieTrailerDao {
    @Query("SELECT * FROM trailers")
    LiveData<List<MovieTrailerDao>> loadAllMovies();

    @Query("SELECT * FROM trailers WHERE id = :id")
    LiveData<MovieTrailerDao> loadMovieById(int id);

    @Insert
    void insertMovie(MovieTrailerDao favoriteMovieEntry);

    @Update
    void updateMovie(MovieTrailerDao favoriteMovieEntry);

    @Delete
    void deleteMovie(MovieTrailerDao favoriteMovieEntry);
}

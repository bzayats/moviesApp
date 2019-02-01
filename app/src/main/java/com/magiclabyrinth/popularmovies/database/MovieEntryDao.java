package com.magiclabyrinth.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.magiclabyrinth.popularmovies.database.MovieEntry;

import java.util.List;

@Dao
public interface MovieEntryDao {
    @Query("SELECT * FROM movies")
    List<MovieEntry> loadAllMovies();

    @Query("SELECT * FROM movies WHERE movie_type = :type")
    List<MovieEntry> loadAllMoviesByType(String type);

    @Query("SELECT * FROM movies WHERE movie_id = :id")
    MovieEntry loadMovieById(String id);

    @Query("SELECT * FROM movies WHERE movie_favorite = 1")
    List<MovieEntry> loadAllFavorites();

    @Query("DELETE FROM movies")
    void deleteMoviesTable();

    @Query("UPDATE movies SET movie_favorite = :movieFav WHERE movie_id = :id")
    void addRemoveFavorites(String id, int movieFav);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(MovieEntry movieEntry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long[] insertMovies(List<MovieEntry> moviesEntries);

    @Update
    void updateMovie(MovieEntry movieEntry);

    @Delete
    void deleteMovie(MovieEntry movieEntry);
}

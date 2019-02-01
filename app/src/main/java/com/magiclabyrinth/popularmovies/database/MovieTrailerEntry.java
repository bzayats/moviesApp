package com.magiclabyrinth.popularmovies.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "trailers")
public class MovieTrailerEntry {

    @PrimaryKey (autoGenerate = true)
    private int id;

    private String movieId;

    @ColumnInfo(name = "trailer_title")
    private String trailerTitle;

    @ColumnInfo(name = "trailer_url")
    private String trailerUrl;

    @Ignore
    public MovieTrailerEntry(){}

    public MovieTrailerEntry(int id, String movieId, String title, String url){
        this.id = id;
        this.movieId = movieId;
        this.trailerTitle = title;
        this.trailerUrl = url;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTrailerTitle() {
        return trailerTitle;
    }

    public void setTrailerTitle(String title) {
        this.trailerTitle = title;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }
}

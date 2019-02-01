package com.magiclabyrinth.popularmovies.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "reviews")
public class MovieReviewEntry {

    @PrimaryKey (autoGenerate = true)
    private int id;

    private String movieId;

    @ColumnInfo(name = "review_author")
    private String author;

    @ColumnInfo(name = "review_content")
    private String content;

    @Ignore
    public MovieReviewEntry(){}

    public MovieReviewEntry(int id, String movieId, String author, String content){
        this.id = id;
        this.movieId = movieId;
        this.author = author;
        this.content = content;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

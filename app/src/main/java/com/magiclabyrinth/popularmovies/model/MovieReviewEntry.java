package com.magiclabyrinth.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieReviewEntry implements Parcelable{
    private String movieId;
    private String movieReviewAuthor;
    private String movieReviewContent;
    private String movieReviewId;
    private String movieReviewUrl;

    public MovieReviewEntry(){
    }

    public MovieReviewEntry(Parcel parcel){
        movieId = parcel.readString();
        movieReviewAuthor = parcel.readString();
        movieReviewContent = parcel.readString();
        movieReviewId = parcel.readString();
        movieReviewUrl = parcel.readString();
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId){
        this.movieId = movieId;
    }

    public String getMovieReviewContent() {
        return movieReviewContent;
    }

    public void setMovieReviewContent(String movieReviewContent) {
        this.movieReviewContent = movieReviewContent;
    }

    public String getMovieReviewId() {
        return movieReviewId;
    }

    public void setMovieReviewId(String movieReviewId) {
        this.movieReviewId = movieReviewId;
    }

    public String getMovieReviewUrl() {
        return movieReviewUrl;
    }

    public void setMovieReviewUrl(String movieReviewUrl) {
        this.movieReviewUrl = movieReviewUrl;
    }

    public String getMovieReviewAuthor() {
        return movieReviewAuthor;
    }

    public void setMovieReviewAuthor(String movieReviewAuthor) {
        this.movieReviewAuthor = movieReviewAuthor;
    }

    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(movieId);
        dest.writeString(movieReviewAuthor);
        dest.writeString(movieReviewContent);
        dest.writeString(movieReviewId);
        dest.writeString(movieReviewUrl);
    }

    public static final Creator<MovieReviewEntry> CREATOR = new Creator<MovieReviewEntry>(){

        @Override
        public MovieReviewEntry createFromParcel(Parcel parcel) {
            return new MovieReviewEntry(parcel);
        }

        @Override
        public MovieReviewEntry[] newArray(int i) {
            return new MovieReviewEntry[0];
        }
    };

    public int describeContents(){
        return hashCode();
    }
}

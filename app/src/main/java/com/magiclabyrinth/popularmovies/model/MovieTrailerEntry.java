package com.magiclabyrinth.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieTrailerEntry implements Parcelable{
    private String movieId;
    private String movieTrailerId;
    private String movieTrailerUrl;
    private String movieTrailerName;

    public MovieTrailerEntry(){
    }

    public MovieTrailerEntry(Parcel parcel){
        movieId = parcel.readString();
        movieTrailerId = parcel.readString();
        movieTrailerUrl = parcel.readString();
        movieTrailerName = parcel.readString();
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId){
        this.movieId = movieId;
    }

    public String getMovieTrailerUrl() {
        return movieTrailerUrl;
    }

    public void setMovieTrailerUrl(String movieTrailerUrl) {
        this.movieTrailerUrl = movieTrailerUrl;
    }

    public String getMovieTrailerName() {
        return movieTrailerName;
    }

    public void setMovieTrailerName(String movieTrailerName) {
        this.movieTrailerName = movieTrailerName;
    }

    public String getMovieTrailerId() {
        return movieTrailerId;
    }

    public void setMovieTrailerId(String movieTrailerId) {
        this.movieTrailerId = movieTrailerId;
    }

    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(movieId);
        dest.writeString(movieTrailerId);
        dest.writeString(movieTrailerUrl);
        dest.writeString(movieTrailerName);
    }

    public static final Creator<MovieTrailerEntry> CREATOR = new Creator<MovieTrailerEntry>(){

        @Override
        public MovieTrailerEntry createFromParcel(Parcel parcel) {
            return new MovieTrailerEntry(parcel);
        }

        @Override
        public MovieTrailerEntry[] newArray(int i) {
            return new MovieTrailerEntry[0];
        }
    };

    public int describeContents(){
        return hashCode();
    }
}

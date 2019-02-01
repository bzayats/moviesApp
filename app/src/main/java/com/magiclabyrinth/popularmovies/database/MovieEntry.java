package com.magiclabyrinth.popularmovies.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "movies")
public class MovieEntry implements Parcelable{

    @PrimaryKey (autoGenerate = true)
    private int id;
    @ColumnInfo(name = "movie_id")
    private String movieId;
    @ColumnInfo(name = "movie_poster")
    private String moviePosterImage;
    @ColumnInfo(name = "movie_title")
    private String movieTitle;
    @ColumnInfo(name = "movie_thumbnail")
    private String movieThumbnailImage;
    @ColumnInfo(name = "movie_release_date")
    private String movieReleaseDate;
    @ColumnInfo(name = "movie_rating")
    private String movieRating;
    @ColumnInfo(name = "movie_synopsis")
    private String moviePlotSynopsis;
    @ColumnInfo(name = "movie_type")
    private String movieType;
    @ColumnInfo(name = "movie_favorite")
    private int favorite;

    @Ignore
    public MovieEntry(){}

    @Ignore
    public MovieEntry(Parcel parcel){
        id = parcel.readInt();
        movieId = parcel.readString();
        moviePosterImage = parcel.readString();
        movieTitle = parcel.readString();
        movieThumbnailImage = parcel.readString();
        movieReleaseDate = parcel.readString();
        movieRating = parcel.readString();
        moviePlotSynopsis = parcel.readString();
        movieType = parcel.readString();
        favorite = parcel.readInt();
    }

    public MovieEntry(int id, String movieId, String moviePosterImage, String movieTitle, String movieThumbnailImage,
                      String movieReleaseDate, String movieRating, String moviePlotSynopsis, String movieType, int favorite){
        this.id = id;
        this.movieId = movieId;
        this.moviePosterImage = moviePosterImage;
        this.movieTitle = movieTitle;
        this.movieThumbnailImage = movieThumbnailImage;
        this.movieReleaseDate = movieReleaseDate;
        this.movieRating = movieRating;
        this.moviePlotSynopsis = moviePlotSynopsis;
        this.movieType = movieType;
        this.favorite = favorite;
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

    public String getMoviePosterImage() {
        return moviePosterImage;
    }

    public void setMoviePosterImage(String moviePosterImage) {
        this.moviePosterImage = moviePosterImage;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieThumbnailImage() {
        return movieThumbnailImage;
    }

    public void setMovieThumbnailImage(String movieThumbnailImage) {
        this.movieThumbnailImage = movieThumbnailImage;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public void setMovieReleaseDate(String movieReleaseDate) {
        this.movieReleaseDate = movieReleaseDate;
    }

    public String getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(String movieRating) {
        this.movieRating = movieRating;
    }

    public String getMovieType() {
        return movieType;
    }

    public void setMovieType(String movieType) {
        this.movieType = movieType;
    }

    public String getMoviePlotSynopsis() {
        return moviePlotSynopsis;
    }

    public void setMoviePlotSynopsis(String moviePlotSynopsis) {
        this.moviePlotSynopsis = moviePlotSynopsis;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public void writeToParcel(Parcel dest, int flags){
        dest.writeInt(id);
        dest.writeString(movieId);
        dest.writeString(moviePosterImage);
        dest.writeString(movieTitle);
        dest.writeString(movieThumbnailImage);
        dest.writeString(movieReleaseDate);
        dest.writeString(movieRating);
        dest.writeString(moviePlotSynopsis);
        dest.writeString(movieType);
        dest.writeInt(favorite);
    }

    public static final Parcelable.Creator<MovieEntry> CREATOR = new Parcelable.Creator<MovieEntry>(){

        @Override
        public MovieEntry createFromParcel(Parcel parcel) {
            return new MovieEntry(parcel);
        }

        @Override
        public MovieEntry[] newArray(int i) {
            return new MovieEntry[0];
        }
    };

    public int describeContents(){
        return hashCode();
    }
}

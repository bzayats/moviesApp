package com.magiclabyrinth.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.magiclabyrinth.popularmovies.database.MovieEntry;
import com.magiclabyrinth.popularmovies.database.MovieReviewEntry;
import com.magiclabyrinth.popularmovies.database.MovieTrailerEntry;
import com.magiclabyrinth.popularmovies.sync.MovieDataRepository;

import java.util.List;

public class DetailsViewModel extends AndroidViewModel {
    private MovieDataRepository repository;

    // Constant for logging
    private static final String TAG = DetailsViewModel.class.getSimpleName();

    private LiveData<List<MovieTrailerEntry>> mTrailers;
    private LiveData<List<MovieReviewEntry>> mReviews;
    private LiveData<MovieEntry> addRemoveFavorite;
    private LiveData<MovieEntry> isFavorite;

    public DetailsViewModel(Application application, MovieDataRepository repo) {
        super(application);
        repository = repo;
    }

    public void init(boolean add, String type){
        switch (type) {
            case "data":
                if (mTrailers != null && mReviews != null){
                    return;
                }
                mTrailers = repository.getTrailers();
                mReviews = repository.getReviews();
                break;

            case "favorite":
                if (!add) {
                    addRemoveFavorite = repository.removeFavoriteMovieById();
                } else {
                    addRemoveFavorite = repository.addFavoriteMovieById();
                }
                break;

            case "isFavorite":
                isFavorite = repository.getMovieById();
                break;

            default:
                throw new RuntimeException("Please provide proper type..");
        }
    }

    public LiveData<List<MovieTrailerEntry>> getMovieTrailers() {
        return mTrailers;
    }
    public LiveData<List<MovieReviewEntry>> getMovieReviews() {
        return mReviews;
    }
    public LiveData<MovieEntry> getMovieById() {
        return isFavorite;
    }
    public LiveData<MovieEntry> addRemoveFavoriteMovie(){
        return addRemoveFavorite;
    }
}

package com.magiclabyrinth.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.magiclabyrinth.popularmovies.database.MovieEntry;
import com.magiclabyrinth.popularmovies.sync.MovieDataRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private MovieDataRepository repository;

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();
    private String movieType;

    private LiveData<List<MovieEntry>> mPopularMovies;
    private LiveData<List<MovieEntry>> mVotedMovies;
    private LiveData<List<MovieEntry>> mFavoriteMovies;

    public MainViewModel(Application application, MovieDataRepository repo) {
        super(application);
        repository = repo;
    }

    public void init(String movieType){

        switch(movieType) {
            case "popularity":
                if (mPopularMovies != null){
                    return;
                }
                mPopularMovies = repository.getMovies(movieType);
                break;

            case "vote":
                if (mVotedMovies != null){
                    return;
                }
                mVotedMovies = repository.getMovies(movieType);
                break;

            case "favorite":
                mFavoriteMovies = repository.getFavoriteMovies();
                break;

            default:
                throw new RuntimeException("Please specify proper movie type to obtain..");
        }
    }

    public LiveData<List<MovieEntry>> getPopularMovies() {
        return mPopularMovies;
    }

    public LiveData<List<MovieEntry>> getVotedMovies() {
        return mVotedMovies;
    }

    public LiveData<List<MovieEntry>> getFavoriteMovies() {
        return mFavoriteMovies;
    }

    public void initDBInsert(){
        repository.getMoviesFromAsync();
    }
}

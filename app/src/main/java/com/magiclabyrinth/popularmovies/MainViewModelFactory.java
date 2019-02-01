package com.magiclabyrinth.popularmovies;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.magiclabyrinth.popularmovies.sync.MovieDataRepository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory{
    private Application application;
    private MovieDataRepository repo;

    public MainViewModelFactory(Application application, MovieDataRepository repository){
        this.application = application;
        repo = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(application, repo);
    }
}

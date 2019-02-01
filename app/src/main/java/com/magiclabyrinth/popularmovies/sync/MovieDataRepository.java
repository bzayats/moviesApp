package com.magiclabyrinth.popularmovies.sync;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.magiclabyrinth.popularmovies.AppExecutors;
import com.magiclabyrinth.popularmovies.database.AppDatabase;
import com.magiclabyrinth.popularmovies.database.MovieEntry;
import com.magiclabyrinth.popularmovies.database.MovieReviewEntry;
import com.magiclabyrinth.popularmovies.database.MovieTrailerEntry;
import com.magiclabyrinth.popularmovies.utils.CheckNetwork;
import com.magiclabyrinth.popularmovies.utils.NetworkUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MovieDataRepository {
    private static String TAG = MovieDataRepository.class.getSimpleName();
    private String movieId;
    private String movieType;
    private static Application app;
    private static Map<String, MutableLiveData<List<MovieEntry>>> allMoviesFromAsyncForDBMutable = new LinkedHashMap();
    private Map<String, MutableLiveData<List<MovieEntry>>> moviesPopularCache = new LinkedHashMap();
    private Map<String, MutableLiveData<List<MovieEntry>>> moviesVotedCache = new LinkedHashMap();
    private Map<String, MutableLiveData<List<MovieEntry>>> moviesPopularDBCache = new LinkedHashMap();
    private Map<String, MutableLiveData<List<MovieEntry>>> moviesVotedDBCache = new LinkedHashMap();
    private Map<String, MutableLiveData<List<MovieTrailerEntry>>> trailersCache = new LinkedHashMap();
    private Map<String, MutableLiveData<List<MovieReviewEntry>>> reviewsCache = new LinkedHashMap();
    private Map<String, MutableLiveData<List<MovieEntry>>> favoritesCache = new LinkedHashMap();
    private Map<String, MutableLiveData<MovieEntry>> favoriteMovieCache = new LinkedHashMap();
    private Map<String, MutableLiveData<MovieEntry>> movieCache = new LinkedHashMap();

    public MovieDataRepository(Application application, String movieId){
        this.movieId = movieId;
        app = application;
    }

    public MovieDataRepository(Application application, String movieId, String movieType){
        this.movieType = movieType;
        app = application;
    }

    public LiveData<MovieEntry> addFavoriteMovieById(){
        boolean add = true;

        final MutableLiveData<MovieEntry> data = new MutableLiveData<>();
        favoriteMovieCache.put(movieId, data);

        return addRemoveFavoriteMovieFromDB(data, movieId, app, add);
    }

    public LiveData<MovieEntry> getMovieById(){
        if (!movieCache.isEmpty()){
            return movieCache.get("cached");
        }

        final MutableLiveData<MovieEntry> data = new MutableLiveData<>();
        movieCache.put("cached", data);
        getMovieFromDB(data, movieId, app);

        while(movieCache.get(movieId) == null) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }

        return movieCache.get(movieId);
    }

    public LiveData<MovieEntry> removeFavoriteMovieById(){
        boolean add = false;

        final MutableLiveData<MovieEntry> data = new MutableLiveData<>();
        favoriteMovieCache.put(movieId, data);

        return addRemoveFavoriteMovieFromDB(data, movieId, app, add);
    }

    public LiveData<List<MovieEntry>> getFavoriteMovies(){
        final MutableLiveData<List<MovieEntry>> data = new MutableLiveData<>();
        fetchFavoriteMoviesFromDB(data, app);

        if (favoritesCache.get("data") == null){
            return null;
        }

        return favoritesCache.get("data");
    }

    public LiveData<List<MovieEntry>> getMoviesFromServer(String type){
        final MutableLiveData<List<MovieEntry>> data = new MutableLiveData<>();

        switch(type){
            case "popularity":
                if (!moviesPopularCache.isEmpty()){
                    return moviesPopularCache.get("cached");
                }
                moviesPopularCache.put("cached", data);
                break;
            case "vote":
                if (!moviesVotedCache.isEmpty()){
                    return moviesVotedCache.get("cached");
                }
                moviesVotedCache.put("cached", data);
                break;
        }

        return fetchMoviesFromServer(data, type);
    }

    public LiveData<List<MovieEntry>> getMoviesFromAsync(){
        final MutableLiveData<List<MovieEntry>> moviesList = new MutableLiveData<>();

        if (!allMoviesFromAsyncForDBMutable.isEmpty()){
            return allMoviesFromAsyncForDBMutable.get("cached");
        }

        allMoviesFromAsyncForDBMutable.put("cached", moviesList);
        MoviesFromAsyncData asyncData = new MoviesFromAsyncData();
        asyncData.execute("popularity");

        while(allMoviesFromAsyncForDBMutable.get("data") == null) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }

        Log.i(TAG,"Total size: " + allMoviesFromAsyncForDBMutable.get("data"));

        return allMoviesFromAsyncForDBMutable.get("data");
    }

    public LiveData<List<MovieEntry>> getMovies(String type){
        final MutableLiveData<List<MovieEntry>> moviesList = new MutableLiveData<>();

        switch(type){
            case "popularity":
                if (!moviesPopularDBCache.isEmpty()){
                    return moviesPopularDBCache.get("cached");
                }
                moviesPopularDBCache.put("cached", moviesList);
                break;
            case "vote":
                if (!moviesVotedDBCache.isEmpty()){
                    return moviesVotedDBCache.get("cached");
                }
                moviesVotedDBCache.put("cached", moviesList);
                break;
        }

        return fetchMoviesFromDB(moviesList, app, type);
    }

    public LiveData<List<MovieTrailerEntry>> getTrailers(){
        if (!trailersCache.isEmpty()){
            return trailersCache.get(movieId);
        }

        final MutableLiveData<List<MovieTrailerEntry>> data = new MutableLiveData<>();
        trailersCache.put(movieId, data);

        return fetchMovieTrailersFromServer(data, movieId);
    }

    public LiveData<List<MovieReviewEntry>> getReviews(){
        if (!reviewsCache.isEmpty()){
            return reviewsCache.get(movieId);
        }

        final MutableLiveData<List<MovieReviewEntry>> data = new MutableLiveData<>();
        reviewsCache.put(movieId, data);

        return fetchMovieReviewsFromServer(data, movieId);
    }

    private MutableLiveData<List<MovieEntry>> fetchMoviesFromDB(final MutableLiveData<List<MovieEntry>> data,
                                                                final Application app, final String type){
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<MovieEntry> movies = null;

                AppDatabase database = AppDatabase.getInstance(app.getApplicationContext());
                Log.d(TAG, "Actively retrieving movies from the DataBase");
                movies = database.movieDao().loadAllMoviesByType(type);

                if (movies != null && !movies.isEmpty()) {
                    Log.d("In onPostExecute: ", movies.get(0).getMovieId());
                    data.postValue(movies);
                }
            }
        });

        return data;
    }

    private MutableLiveData<MovieEntry> addRemoveFavoriteMovieFromDB(final MutableLiveData<MovieEntry> data,
                                                                     final String movieId, final Application app, final boolean add){
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                MovieEntry movie = null;
                int favorite = 0;

                if (add) {
                    favorite = 1;
                }

                AppDatabase database = AppDatabase.getInstance(app.getApplicationContext());
                if (add) {
                    Log.d(TAG, "Actively adding favorite status to a movie in the DataBase");
                } else {
                    Log.d(TAG, "Actively removing favorite status from a movie in the DataBase");
                }
                database.movieDao().addRemoveFavorites(movieId, favorite);

                if (movie != null) {
                    Log.d(TAG, "movie id: " + movie.getMovieId() + " movie favorite status: " + movie.getFavorite());
                    data.postValue(movie);
                }

            }
        });

        return data;
    }

    private MutableLiveData<MovieEntry> getMovieFromDB(final MutableLiveData<MovieEntry> data,
                                                                     final String movieId, final Application app){
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                MovieEntry movie = null;
                AppDatabase database = AppDatabase.getInstance(app.getApplicationContext());
                Log.d(TAG, "getMovieFromDB, with ID: " + movieId);
                movie = database.movieDao().loadMovieById(movieId);

                if (movie != null) {
                    Log.d(TAG, "getMovieFromDB after call to DB: " + movie.getMovieId());
                    data.postValue(movie);
                } else {
                    Log.d(TAG, "getMovieFromDB after call resulted in movie being null, movieID: " + movieId);
                }
                movieCache.put(movieId, data);
            }
        });

        return data;
    }

    private MutableLiveData<List<MovieEntry>> fetchFavoriteMoviesFromDB(final MutableLiveData<List<MovieEntry>> data,
                                                                        final Application app){
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<MovieEntry> favoriteMovies = null;

                AppDatabase database = AppDatabase.getInstance(app.getApplicationContext());
                Log.d(TAG, "Actively retrieving favorite movies from the DataBase");
                favoriteMovies = database.movieDao().loadAllFavorites();

                if (favoriteMovies != null && !favoriteMovies.isEmpty()) {
                    Log.d("Favorite 0 ID: ", favoriteMovies.get(0).getMovieId());
                    data.postValue(favoriteMovies);
                }
            }
        });
        favoritesCache.put("data", data);

        return data;
    }

    //executing trailer network call
    private MutableLiveData<List<MovieTrailerEntry>> fetchMovieTrailersFromServer(final MutableLiveData<List<MovieTrailerEntry>> data, final String movieId){

        AppExecutors.getInstance().getNetworkIO().execute(new Runnable() {
            @Override
            public void run() {
                List<MovieTrailerEntry> entries = null;

                if (new CheckNetwork(app.getApplicationContext()).isNetworkAvailable()) {
                    entries = NetworkUtils.getAllMovieTrailers(movieId);
                }

                if (entries != null && !entries.isEmpty()) {
                    Log.d("In onPostExecute: ", entries.get(0).getMovieId());
                    data.postValue(entries);
                }

            }
        });

        return data;
    }

    private MutableLiveData<List<MovieEntry>> fetchMoviesFromServer(final MutableLiveData<List<MovieEntry>> data, final String type){
        AppExecutors.getInstance().getNetworkIO().execute(new Runnable() {
            @Override
            public void run() {
                List<MovieEntry> entries = null;

                if (new CheckNetwork(app.getApplicationContext()).isNetworkAvailable()) {
                    entries = NetworkUtils.getAllMovieDetails(type);
                }

                if (entries != null && !entries.isEmpty()) {
                    Log.d("In onPostExecute: ", entries.get(0).getMovieId());
                    data.postValue(entries);
                }
            }
        });

        return data;
    }

    //executing reviews network call
    private MutableLiveData<List<MovieReviewEntry>> fetchMovieReviewsFromServer(final MutableLiveData<List<MovieReviewEntry>> data, final String movieId){

        AppExecutors.getInstance().getNetworkIO().execute(new Runnable() {
            @Override
            public void run() {
                List<MovieReviewEntry> entries = null;

                if (new CheckNetwork(app.getApplicationContext()).isNetworkAvailable()) {
                    entries = NetworkUtils.getAllMovieReviews(movieId);
                }

                if (entries != null && !entries.isEmpty()) {
                    data.postValue(entries);
                }
            }
        });

        return data;
    }

    private static class MoviesFromAsyncData extends AsyncTask<String, String, List<MovieEntry>> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<MovieEntry> doInBackground(String... params) {
                AppDatabase database = AppDatabase.getInstance(app.getApplicationContext());
                Log.i(TAG," checking for movies in DB..");
                List<MovieEntry> checkList = database.movieDao().loadAllMovies();
                MutableLiveData<List<MovieEntry>> data = new MutableLiveData<>();

                if (checkList.isEmpty()) {
                    Log.i(TAG," movies size from DB before insert is " + checkList.size());
                    if (new CheckNetwork(app.getApplicationContext()).isNetworkAvailable()) {
                        checkList = NetworkUtils.getAllMovieDetails("popularity");
                        checkList.addAll(NetworkUtils.getAllMovieDetails("vote"));
                    }

                    Log.i(TAG, " movies size from server is " + checkList.size());
                    database.movieDao().insertMovies(checkList);


                    data.postValue(checkList);
                }
                Log.i(TAG," movies size from DB is after insert " + checkList.size());
                allMoviesFromAsyncForDBMutable.put("data", data);

                return checkList;
        }

        @Override
        protected void onPostExecute(final List<MovieEntry> popularMoviesList) {
            Log.i(TAG, " in onPostExecute: " + popularMoviesList.size());
            MutableLiveData<List<MovieEntry>> data = new MutableLiveData<>();
            data.postValue(popularMoviesList);
            allMoviesFromAsyncForDBMutable.put("data", data);

            Log.i(TAG, " final list size synced from DB for isDBFilled(): " + popularMoviesList.size());
        }
    }
}

package com.magiclabyrinth.popularmovies.sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.magiclabyrinth.popularmovies.database.MovieReviewEntry;
import com.magiclabyrinth.popularmovies.utils.CheckNetwork;
import com.magiclabyrinth.popularmovies.utils.NetworkUtils;

import java.util.LinkedList;
import java.util.List;

public class SyncMovieReviews implements
        LoaderManager.LoaderCallbacks<List<MovieReviewEntry>> {

    private Context context;
    private Bundle bundle;
    private List<MovieReviewEntry> movieReviews = new LinkedList<>();
    private ProgressDialog dialog;
    private static final String BUNDLE_KEY = "movieId";

    public SyncMovieReviews(Context context, Bundle bundle, ProgressDialog dialog){
        this.context = context;
        this.bundle = bundle;
        this.dialog = dialog;
    }

    @Override
    public Loader<List<MovieReviewEntry>> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<List<MovieReviewEntry>>(context) {

            /* This List of MovieEntry will hold and help cache our weather data */
            List<MovieReviewEntry> mMovies = null;

            // COMPLETED (3) Cache the weather data in a member variable and deliver it in onStartLoading.

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                if (mMovies != null) {
                    deliverResult(mMovies);
                } else {
                    dialog.setMessage("Please wait");
                    dialog.show();
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from OpenWeatherMap in the background.
             *
             * @return Weather data from OpenWeatherMap as an array of Strings.
             * null if an error occurs
             */
            @Override
            public List<MovieReviewEntry> loadInBackground() {

                if (new CheckNetwork(context).isNetworkAvailable()) {
                    return NetworkUtils.getAllMovieReviews(bundle.get(BUNDLE_KEY).toString());
                } else {
                    dialog.closeOptionsMenu();
                    dialog.setMessage("Network error occurred! Please check your network connection");
                    dialog.show();
                }
                return null;
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(List<MovieReviewEntry> data) {
                mMovies = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<MovieReviewEntry>> loader, List<MovieReviewEntry> data) {
        if (data != null && !data.isEmpty()) {
            System.out.println("In onPostExecute: " + data.get(0).getMovieId());
            movieReviews.clear();
            movieReviews.addAll(data);
        }

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onLoaderReset (Loader<List<MovieReviewEntry>> loader) {
        /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }

    public List<MovieReviewEntry> getMovieReviews(){
        return movieReviews;
    }
}

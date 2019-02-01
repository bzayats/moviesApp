package com.magiclabyrinth.popularmovies;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.magiclabyrinth.popularmovies.adapters.PopularMoviesAdapter;
import com.magiclabyrinth.popularmovies.database.AppDatabase;
import com.magiclabyrinth.popularmovies.database.MovieEntry;
import com.magiclabyrinth.popularmovies.sync.MovieDataRepository;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SORT_BY_POPULARITY = "popularity";
    private static final String SORT_BY_VOTE = "vote";
    private static final String SORT_BY_FAVORITE = "favorite";
    private static final String BUNDLE_KEY = "sortType";
    private static List<MovieEntry> mPopularMovies = new LinkedList<>();
    private RecyclerView rvPopularMovies;
    private PopularMoviesAdapter mMoviesAdapter;
    private Bundle mBundleForViewModelStateOnLaunch;
    private ProgressDialog mDialog;
    private AppDatabase mDb;
    private MainViewModel mViewModel;
    Observer<List<MovieEntry>> mPopularMoviesObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = AppDatabase.getInstance(getApplicationContext());

        rvPopularMovies = (RecyclerView) findViewById(R.id.rvMovies);
        mMoviesAdapter = new PopularMoviesAdapter(MainActivity.this, mPopularMovies);
        rvPopularMovies.setAdapter(mMoviesAdapter);
        rvPopularMovies.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        mBundleForViewModelStateOnLaunch = new Bundle();
        mBundleForViewModelStateOnLaunch = savedInstanceState;

        mDialog = new ProgressDialog(MainActivity.this);
        mPopularMoviesObserver = new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable final List<MovieEntry> movieEntries) {
                // Update the UI
                Log.d(TAG, "Updating list of movies from LiveData in ViewModel");
                mMoviesAdapter.setMovies(movieEntries);
            }
        };

        MovieDataRepository repository = new MovieDataRepository(getApplication(), null, "all");
        MainViewModelFactory factory = new MainViewModelFactory(getApplication(), repository);
        mViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
        mViewModel.initDBInsert();
        launchViews();
    }

    private void launchViews(){
        if (mBundleForViewModelStateOnLaunch != null) {
            Log.d("In SavedInstanceState", "sortType: " + mBundleForViewModelStateOnLaunch.getString(BUNDLE_KEY));
            initSortTypeView();
        } else {
            mViewModel.init(SORT_BY_POPULARITY);

            if (mBundleForViewModelStateOnLaunch == null){
                mBundleForViewModelStateOnLaunch = new Bundle();
                mBundleForViewModelStateOnLaunch.putString(BUNDLE_KEY, SORT_BY_POPULARITY);
            }

            mViewModel.getPopularMovies().observe(this, mPopularMoviesObserver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mBundleForViewModelStateOnLaunch.getString(BUNDLE_KEY).equalsIgnoreCase(SORT_BY_FAVORITE)){
            initSortTypeView();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mBundleForViewModelStateOnLaunch.putString(BUNDLE_KEY, savedInstanceState.getString(BUNDLE_KEY));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(BUNDLE_KEY, mBundleForViewModelStateOnLaunch.getString(BUNDLE_KEY));
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_by_popularity){
            if (mViewModel.getPopularMovies() != null){
                mViewModel.getPopularMovies().removeObserver(mPopularMoviesObserver);
            }
            mViewModel.init(SORT_BY_POPULARITY);
            mViewModel.getPopularMovies().observe(this, mPopularMoviesObserver);
            mBundleForViewModelStateOnLaunch.clear();
            mBundleForViewModelStateOnLaunch.putString(BUNDLE_KEY, SORT_BY_POPULARITY);
        }

        if (id == R.id.action_sort_by_vote){
            if (mViewModel.getVotedMovies() != null){
                mViewModel.getVotedMovies().removeObserver(mPopularMoviesObserver);
            }
            mViewModel.init(SORT_BY_VOTE);
            mViewModel.getVotedMovies().observe(this, mPopularMoviesObserver);
            mBundleForViewModelStateOnLaunch.clear();
            mBundleForViewModelStateOnLaunch.putString(BUNDLE_KEY, SORT_BY_VOTE);

        }

        if (id == R.id.action_sort_by_favorites){
            if (mViewModel.getFavoriteMovies() != null){
                mViewModel.getFavoriteMovies().removeObserver(mPopularMoviesObserver);
            }
            mViewModel.init(SORT_BY_FAVORITE);
            if (mViewModel.getFavoriteMovies() == null){
                displayAlertDialog(getString(R.string.no_favorites_found_error)).show();
            } else {
                mViewModel.getFavoriteMovies().observe(this, mPopularMoviesObserver);
                mBundleForViewModelStateOnLaunch.clear();
                mBundleForViewModelStateOnLaunch.putString(BUNDLE_KEY, SORT_BY_FAVORITE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog displayAlertDialog(final String dialogTitle){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(dialogTitle);
        builder.setPositiveButton(R.string.no_favorites_found_dialog_OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               //
            }
        });

        AlertDialog dialog = builder.create();

        return dialog;
    }

    private void initSortTypeView(){
        String currentType = mBundleForViewModelStateOnLaunch.getString(BUNDLE_KEY);

        switch (currentType){
            case SORT_BY_POPULARITY:
                mViewModel.init(SORT_BY_POPULARITY);
                mViewModel.getPopularMovies().observe(this, mPopularMoviesObserver);
                break;
            case SORT_BY_VOTE:
                mViewModel.init(SORT_BY_VOTE);
                mViewModel.getVotedMovies().observe(this, mPopularMoviesObserver);
                break;
            case SORT_BY_FAVORITE:
                if (mViewModel.getFavoriteMovies() == null){
                    displayAlertDialog(getString(R.string.no_favorites_found_error)).show();
                } else {
                    mViewModel.init(SORT_BY_FAVORITE);
                    mViewModel.getFavoriteMovies().observe(this, mPopularMoviesObserver);
                }
                break;
        }
    }
}

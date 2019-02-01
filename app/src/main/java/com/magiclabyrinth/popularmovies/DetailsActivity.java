package com.magiclabyrinth.popularmovies;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.magiclabyrinth.popularmovies.adapters.MovieReviewsAdapter;
import com.magiclabyrinth.popularmovies.adapters.MovieTrailersAdapter;
import com.magiclabyrinth.popularmovies.database.MovieEntry;
import com.magiclabyrinth.popularmovies.database.MovieReviewEntry;
import com.magiclabyrinth.popularmovies.database.MovieTrailerEntry;
import com.magiclabyrinth.popularmovies.sync.MovieDataRepository;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class DetailsActivity extends AppCompatActivity implements MovieTrailersAdapter.ItemClickListener{
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private TextView mTitle;
    private ImageView mThumbnail;
    private TextView mReleaseDate;
    private TextView mRating;
    private TextView mPlotSynopsis;
    private TextView mTrailersTitle;
    private TextView mReviewsTitle;
    private ImageView mRatingStar;

    private RecyclerView rvMovieTrailers;
    private RecyclerView rvMovieReviews;
    private List<MovieTrailerEntry> movieTrailerEntries = new LinkedList<>();
    private List<MovieReviewEntry> movieReviewsEntries = new LinkedList<>();
    private MovieTrailersAdapter moviesTrailersAdapter;
    private MovieReviewsAdapter moviesReviewsAdapter;
    private Bundle bundleForLoader;
    private ProgressDialog dialog;
    private static final String BUNDLE_KEY = "movieId";
    private DetailsViewModel viewModel;
    Observer<MovieEntry> mRatingBtnObserver;
    private boolean mRatingSelected = false;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mTitle = (TextView) findViewById(R.id.tv_title);
        mThumbnail = (ImageView) findViewById(R.id.im_thumbnail);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mRating = (TextView) findViewById(R.id.tv_rating);
        mPlotSynopsis = (TextView) findViewById(R.id.tv_plot_synopsis);
        mTrailersTitle = (TextView) findViewById(R.id.tv_trailer_title);
        mReviewsTitle = (TextView) findViewById(R.id.tv_reviews_title);
        mRatingStar = (ImageView) findViewById(R.id.iv_rating_star);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DetailsActivity.this);

        Intent intent = getIntent();
        MovieEntry popularMovie = intent.getParcelableExtra("PopularMovie");

        mTitle.setText(popularMovie.getMovieTitle());
        Picasso.with(this)
                .load(popularMovie.getMovieThumbnailImage())
                .error(R.mipmap.ic_launcher)
                .into(mThumbnail);
        mReleaseDate.setText(popularMovie.getMovieReleaseDate());
        mRating.setText(popularMovie.getMovieRating().concat("/10"));
        mPlotSynopsis.setText(popularMovie.getMoviePlotSynopsis());
        dialog = new ProgressDialog(DetailsActivity.this);
        bundleForLoader = new Bundle();
        bundleForLoader.putString(BUNDLE_KEY, popularMovie.getMovieId());
        setupViewModel();

        //trailers
        rvMovieTrailers = (RecyclerView) findViewById(R.id.rv_trailers);
        moviesTrailersAdapter = new MovieTrailersAdapter(DetailsActivity.this, this);
        rvMovieTrailers.setAdapter(moviesTrailersAdapter);
        rvMovieTrailers.setLayoutManager(new LinearLayoutManager(DetailsActivity.this));

        DividerItemDecoration trailersDecoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        rvMovieTrailers.addItemDecoration(trailersDecoration);

        //reviews
        rvMovieReviews = (RecyclerView) findViewById(R.id.rv_reviews);
        moviesReviewsAdapter = new MovieReviewsAdapter(DetailsActivity.this);
        rvMovieReviews.setAdapter(moviesReviewsAdapter);
        rvMovieReviews.setLayoutManager(new LinearLayoutManager(DetailsActivity.this));

        DividerItemDecoration reviewsDecoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        rvMovieReviews.addItemDecoration(reviewsDecoration);

        String movieId = bundleForLoader.get(BUNDLE_KEY).toString();

        mRatingBtnObserver = new Observer<MovieEntry>() {
            @Override
            public void onChanged(@Nullable MovieEntry movieEntry) {
                Log.d(TAG, "Setting rating button based on favorite status in viewmodel");
                if (movieEntry.getFavorite() == 1){
                    mRatingStar.setImageResource(R.drawable.star_icon_selected);
                    Log.d("onCreate", "movie fav state: " + movieEntry.getFavorite());
                    mRatingSelected = true;
                } else {
                    mRatingStar.setImageResource(R.drawable.star_icon_not_selected);
                    Log.d("onCreate", "movie fav state: " + movieEntry.getFavorite());
                    mRatingSelected = false;
                }
            }
        };

        MovieDataRepository repository = new MovieDataRepository(getApplication(), movieId);
        DetailsViewModelFactory factory = new DetailsViewModelFactory(getApplication(), repository);
        viewModel = ViewModelProviders.of(this, factory).get(DetailsViewModel.class);
        viewModel.init(false, "isFavorite");
        viewModel.getMovieById().observe(this, mRatingBtnObserver);
        initRatingStarView();
    }

    private void initRatingStarView() {
        mRatingStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRatingSelected) {
                    view.setSelected(view.isSelected());
                } else {
                    view.setSelected(!view.isSelected());
                }

                if (view.isSelected()) {
                    mRatingStar.setImageResource(R.drawable.star_icon_selected);
                    Log.d(TAG,"rating Start selected");
                    onAddFavoriteButtonClicked(true);
                } else {
                    mRatingStar.setImageResource(R.drawable.star_icon_not_selected);
                    Log.d(TAG,"rating Start NOT selected");
                    onAddFavoriteButtonClicked(false);
                }
            }
        });
    }

    public void onAddFavoriteButtonClicked(boolean add) {
        if (!add){
            viewModel.init(false, "favorite");
        } else {
            viewModel.init(true, "favorite");
        }

        viewModel.getMovieById().observe(this, mRatingBtnObserver);
    }

    @Override
    public void onItemClickListener(int itemId) {

    }

    private void setupViewModel() {
        String movieId = bundleForLoader.get(BUNDLE_KEY).toString();
        MovieDataRepository repository = new MovieDataRepository(getApplication(), movieId);
        DetailsViewModelFactory factory = new DetailsViewModelFactory(getApplication(), repository);
        viewModel = ViewModelProviders.of(this, factory).get(DetailsViewModel.class);
        viewModel.init(false, "data");
        viewModel.getMovieTrailers().observe(this, new Observer<List<MovieTrailerEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieTrailerEntry> movieTrailerEntries) {
                Log.d(TAG, "Updating list of trailers from LiveData in ViewModel");
                moviesTrailersAdapter.setMovieTrailers(movieTrailerEntries);
            }
        });
        viewModel.getMovieReviews().observe(this, new Observer<List<MovieReviewEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieReviewEntry> movieReviewEntries) {
                Log.d(TAG, "Updating list of reviews from LiveData in ViewModel");
                moviesReviewsAdapter.setMovieReviews(movieReviewEntries);
            }
        });
    }
}

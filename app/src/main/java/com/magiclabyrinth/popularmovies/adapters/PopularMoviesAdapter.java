package com.magiclabyrinth.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.magiclabyrinth.popularmovies.AppExecutors;
import com.magiclabyrinth.popularmovies.DetailsActivity;
import com.magiclabyrinth.popularmovies.R;
import com.magiclabyrinth.popularmovies.database.MovieEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesViewHolder> {
    private List<MovieEntry> mMovieEntryEntries;
    private Context mContext;

    public PopularMoviesAdapter(Context context, List<MovieEntry> popularMovies) {
        mContext = context;
        mMovieEntryEntries = popularMovies;
    }

    public class PopularMoviesViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout row;
        public ImageView mMoviePoster;

        public PopularMoviesViewHolder(View itemView){
            super(itemView);
            row = (ConstraintLayout) itemView.findViewById(R.id.a_row);
            mMoviePoster = (ImageView) itemView.findViewById(R.id.im_movie_poster);
        }
    }

    @NonNull
    @Override
    public PopularMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View popularMoviesView = inflater.inflate(R.layout.movies_row_list_item, parent, false);
        PopularMoviesViewHolder viewHolder = new PopularMoviesViewHolder(popularMoviesView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PopularMoviesAdapter.PopularMoviesViewHolder holder, int position) {
//        MovieEntry popularMovies = mPopularMovies.get(position);
        MovieEntry movieEntry = mMovieEntryEntries.get(position);
        ImageView imageView = holder.mMoviePoster;
        Picasso.with(mContext)
                .load(movieEntry.getMoviePosterImage())
                .error(R.mipmap.ic_launcher)
                .into(imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Context context = view.getContext();
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("PopularMovie", mMovieEntryEntries.get(position));
                context.startActivity(intent);
            }
        });
    }

//    private void populateUI(final List<MovieEntry> movieEntries){
//        final MovieEntry movieEntry;
//        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                mDb.movieDao().insertMovies(movieEntries);
//                finish();
//            }
//        });
//    }

    @Override
    public int getItemCount() {
        if (mMovieEntryEntries == null) {
            return 0;
        }
        return mMovieEntryEntries.size();
    }

    public void setMovies(List<MovieEntry> movieEntries) {
        mMovieEntryEntries = movieEntries;
        notifyDataSetChanged();
    }
}

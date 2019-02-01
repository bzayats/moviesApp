package com.magiclabyrinth.popularmovies.adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.magiclabyrinth.popularmovies.R;
import com.magiclabyrinth.popularmovies.database.MovieReviewEntry;
import com.magiclabyrinth.popularmovies.database.MovieTrailerEntry;

import java.util.List;

/**
 * This TaskAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ReviewViewHolder> {

    // Class variables for the List that holds task data and the Context
    private List<MovieReviewEntry> movieReviewEntries;
    private Context mContext;

    /**
     * Constructor for the MovieReviewsAdapter that initializes the Context.
     *
     * @param context  the current Context
     */
    public MovieReviewsAdapter(Context context) {
        mContext = context;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TrailerViewHolder that holds the view for each task
     */
    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(context)
                .inflate(R.layout.reviews_row_list_item, parent, false);

        return new ReviewViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        // Determine the values of the wanted data
        MovieReviewEntry reviewEntry = movieReviewEntries.get(position);
        System.out.println("In onBindViewHolder, current review author is: " + reviewEntry.getAuthor());
        String reviewAuthor = reviewEntry.getAuthor();
        String reviewContent = reviewEntry.getContent();

        //Set values
        holder.reviewAuthor.setText(reviewAuthor);
        holder.reviewContent.setText(reviewContent);
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (movieReviewEntries == null) {
            return 0;
        }
        return movieReviewEntries.size();
    }

    // Inner class for creating ViewHolders
    class ReviewViewHolder extends RecyclerView.ViewHolder {

        // Class variables for the task description and priority TextViews
        public ConstraintLayout row;
        TextView reviewAuthor;
        TextView reviewContent;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public ReviewViewHolder(View itemView) {
            super(itemView);
            row = (ConstraintLayout) itemView.findViewById(R.id.a_review_row);
            reviewAuthor = (TextView) itemView.findViewById(R.id.tv_author);
            reviewContent = (TextView) itemView.findViewById(R.id.tv_review_content);
        }
    }

    public void setMovieReviews(List<MovieReviewEntry> movieReviews) {
        System.out.println("In ReviewAdapter, total size of param movieReviews" + movieReviews.size());
        movieReviewEntries = movieReviews;
        System.out.println("In ReviewAdapter, total size of param movieReviews" + movieReviewEntries.size());
        notifyDataSetChanged();
    }
}
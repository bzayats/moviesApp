package com.magiclabyrinth.popularmovies.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.magiclabyrinth.popularmovies.R;
import com.magiclabyrinth.popularmovies.database.MovieTrailerEntry;

import java.util.List;

/**
 * This TaskAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.TrailerViewHolder> {

    // Member variable to handle item clicks
    //TODO: just added null to it, test it out and see if that makes a difference, original didnt have a value assigned
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds task data and the Context
    private List<MovieTrailerEntry> mTrailerEntries;
    private String mTrailerUrl;
    private Context mContext;

    /**
     * Constructor for the TaskAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public MovieTrailersAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
//        Log.d(MovieTrailersAdapter.class.getSimpleName(), "List of trailer entries size: " + mTrailerEntries.size());
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TrailerViewHolder that holds the view for each trailer
     */
    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the trailer layout to a view
        Context context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.trailers_row_list_item, parent, false);

        return new TrailerViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        // Determine the values of the wanted data
        MovieTrailerEntry trailerEntry = mTrailerEntries.get(position);
        String trailerTitle = trailerEntry.getTrailerTitle();
        System.out.println("In onBindViewHolder, current movieTrailer title is: " + trailerTitle);
        String trailerUrl = trailerEntry.getTrailerUrl();
        mTrailerUrl = trailerUrl;

        //Set values
        holder.trailerImage.setImageResource(R.drawable.play_icon_black);
        holder.trailerTitle.setText(trailerTitle);
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mTrailerEntries == null) {
            return 0;
        }
        return mTrailerEntries.size();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        public ConstraintLayout row;
        ImageView trailerImage;
        TextView trailerTitle;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public TrailerViewHolder(View itemView) {
            super(itemView);

            row = (ConstraintLayout) itemView.findViewById(R.id.a_trailer_row);
            trailerImage = (ImageView) itemView.findViewById(R.id.iv_play_icon);
            trailerTitle = (TextView) itemView.findViewById(R.id.tv_trailer_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mTrailerEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
            Context context = view.getContext();

            try {
                //TODO: get trailer ID to to use as 'id'
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailerUrl));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            } catch (ActivityNotFoundException e) {

                // youtube is not installed. Will be opened in other available apps
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailerUrl));
                context.startActivity(i);
            }
        }
    }

    public void setMovieTrailers(List<MovieTrailerEntry> movieTrailers) {
        System.out.println("In TrailerAdapter, total size of param movieTrailers" + movieTrailers.size());
        mTrailerEntries = movieTrailers;
        System.out.println("In TrailerAdapter, total size of local movieTrailers" + mTrailerEntries.size());
        notifyDataSetChanged();
    }
}
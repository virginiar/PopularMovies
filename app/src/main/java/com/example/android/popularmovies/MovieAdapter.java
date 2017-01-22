package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    /* Tag for log messages */
    private static final String LOG_TAG = MovieAdapter.class.getName();
    /* Array for the data obtained in the database */
    private List<Movie> mMovieData;

    //private int mNumberItems;

    /* Default constructor */
    public MovieAdapter() {
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new MovieViewHolder(view);
     }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Log.d(LOG_TAG, "#" + position);
        String movieForThisPosition = mMovieData.get(position).getTitle();
        holder.listItemView.setText(movieForThisPosition);
    }

    @Override
    public int getItemCount() {
        /* Early return if not valid data */
        if (mMovieData == null) {
            return 0;
        }
        return mMovieData.size();
    }

    public void setMovieData(List<Movie> movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views for a list item.
     */
    class MovieViewHolder extends RecyclerView.ViewHolder {

        /* Display the position in the list */
        TextView listItemView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            listItemView = (TextView) itemView.findViewById(R.id.list_item_view);
        }
    }
}

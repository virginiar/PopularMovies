package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    /* Tag for log messages */
    private static final String LOG_TAG = MovieAdapter.class.getName();
    /* List for the data obtained in the database */
    private List<Movie> mMovieData;
    /* The base for constructing a query for the poster of a movie */
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    /* The size for the poster image */
    private static final String SIZE_IMAGE_URL = "w185";

    /* Default constructor */
    public MovieAdapter() {}

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
        String movieForThisPosition = BASE_IMAGE_URL + SIZE_IMAGE_URL + mMovieData.get(position).getPosterPath();
        Picasso.with(holder.imageItemView.getContext())
                .load(movieForThisPosition)
                .into(holder.imageItemView);
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

        /* Display the poster of the movie */
        ImageView imageItemView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            imageItemView = (ImageView) itemView.findViewById(R.id.image_item_view);
        }
    }
}

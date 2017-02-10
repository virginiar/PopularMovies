package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.popularmovies.QueryUtils.buildPosterStringUrl;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    /* Tag for log messages */
    private static final String LOG_TAG = MovieAdapter.class.getName();
    /* On-click handler */
    final MovieAdapterOnClickHandler mClickHandler;
    /* List for the data obtained in the database */
    private List<Movie> mMovieData;

    /**
     * Creates a MovieAdapter
     *
     * @param clickHandler The on-click handler for this adapter
     */
    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        String posterPath = mMovieData.get(position).getPosterPath();
        String movieForThisPosition = buildPosterStringUrl(posterPath);
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

    public List<Movie> getMovies() {
        return mMovieData;
    }

    /* The interface that receives the onClick messages */
    interface MovieAdapterOnClickHandler {
        void onClick(Movie movieItem);
    }

    /**
     * Cache of the children views for a movie list item.
     */
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /* Display the poster of the movie */
        ImageView imageItemView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            imageItemView = (ImageView) itemView.findViewById(R.id.image_item_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movieItem = mMovieData.get(adapterPosition);
            mClickHandler.onClick(movieItem);
        }
    }
}

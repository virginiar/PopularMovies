package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.utils.QueryUtils;
import com.example.android.popularmovies.utils.SingletonRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import static com.example.android.popularmovies.utils.QueryUtils.buildReviewStringUrl;
import static com.example.android.popularmovies.utils.QueryUtils.buildYouTubeStringUrl;
import static com.example.android.popularmovies.utils.QueryUtils.checkConnection;

public class DetailActivity extends AppCompatActivity implements
        TrailerAdapter.TrailerAdapterOnClickHandler,
        ReviewAdapter.ReviewAdapterOnClickHandler {

    /* Tag for intent extra data */
    static final String EXTRA_MOVIE = "EXTRA_MOVIE";

    /* Tag for log messages */
    private static final String LOG_TAG = DetailActivity.class.getName();
    /* Movie to show its details */
    private Movie mMovie;
    /* If the movie has marked as favorite */
    private boolean mIsFavorite;
    /* Toast to show changes in favorite's state */
    private Toast mToast;
    /* Menu element to show the correct icon if favorite*/
    private MenuItem mFavoriteMenu;
    /* The first trailer */
    private Trailer mFirstTrailer = null;

    /* TextViews to show error messages */
    private TextView mEmptyReview;
    private TextView mEmptyTrailer;
    /* Adapters for RecyclerViews */
    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;
    /* RecyclerView to inject the trailers and reviews */
    private RecyclerView mReviewRecyclerView;
    private RecyclerView mTrailerRecyclerView;
    /* ProgressBar to show that a request is running */
    private ProgressBar mLoadingReview;
    private ProgressBar mLoadingTrailer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView titleTextView = (TextView) findViewById(R.id.movie_title);
        TextView releaseDateTextView = (TextView) findViewById(R.id.movie_release_date);
        TextView synopsisTextView = (TextView) findViewById(R.id.movie_synopsis);
        TextView userRatingTextView = (TextView) findViewById(R.id.movie_user_rating);
        ImageView imageView = (ImageView) findViewById(R.id.movie_image);

        mEmptyReview = (TextView) findViewById(R.id.empty_view_review);
        mReviewRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_review);
        mLoadingReview = (ProgressBar) findViewById(R.id.loading_indicator_review);

        mEmptyTrailer = (TextView) findViewById(R.id.empty_view_trailer);
        mTrailerRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_trailer);
        mLoadingTrailer = (ProgressBar) findViewById(R.id.loading_indicator_trailer);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(DetailActivity.EXTRA_MOVIE)) {
            mMovie = intent.getParcelableExtra(DetailActivity.EXTRA_MOVIE);
            titleTextView.setText(mMovie.getTitle());
            releaseDateTextView.setText(mMovie.getReleaseDate());
            synopsisTextView.setText(mMovie.getSynopsis());
            String userRating = mMovie.getUserRating() + " / 10";
            userRatingTextView.setText(userRating);
            String imagePath = QueryUtils.buildPosterStringUrl(mMovie.getPosterPath());
            Picasso.with(this).load(imagePath)
                    .into(imageView);

            LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
            mReviewRecyclerView.setLayoutManager(reviewsLayoutManager);
            DividerItemDecoration mDividerReview = new DividerItemDecoration(
                    mReviewRecyclerView.getContext(),
                    reviewsLayoutManager.getOrientation());
            mReviewRecyclerView.addItemDecoration(mDividerReview);
            mReviewAdapter = new ReviewAdapter(this);
            mReviewRecyclerView.setAdapter(mReviewAdapter);
            mReviewRecyclerView.setNestedScrollingEnabled(false);

            LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(this);
            mTrailerRecyclerView.setLayoutManager(trailersLayoutManager);
            DividerItemDecoration mDividerTrailer = new DividerItemDecoration(
                    mTrailerRecyclerView.getContext(),
                    trailersLayoutManager.getOrientation());
            mTrailerRecyclerView.addItemDecoration(mDividerTrailer);
            mTrailerAdapter = new TrailerAdapter(this);
            mTrailerRecyclerView.setAdapter(mTrailerAdapter);
            mTrailerRecyclerView.setNestedScrollingEnabled(false);

            if (checkConnection(this)) {
                loadData();
            } else {
                notConnection();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        /* Inflate the correct favorite icon */
        mFavoriteMenu = menu.findItem(R.id.favorite_icon);
        new IsFavoriteTask().execute();
        /* Inflate the share menu */

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.favorite_icon:
                if (mToast != null) {
                    mToast.cancel();
                }
                if (mIsFavorite) {
                    new DeleteFavoriteTask().execute();
                    item.setIcon(R.drawable.ic_favorite_white);
                    mToast = Toast.makeText(this, R.string.deleted_favorite, Toast.LENGTH_SHORT);
                    mToast.show();
                } else {
                    new AddFavoriteTask().execute();
                    item.setIcon(R.drawable.ic_favorite_accent);
                    mToast = Toast.makeText(this, R.string.added_favorite, Toast.LENGTH_SHORT);
                    mToast.show();
                }
                mIsFavorite = !mIsFavorite;
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.share_icon:
                createShareTrailerIntent(mFirstTrailer);
                break;
        }
        return true;
    }

    @Override
    public void onClick(Trailer trailerItem) {
        String url = buildYouTubeStringUrl(trailerItem.getKey());
        Intent youtubeActivity = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(youtubeActivity);
    }

    @Override
    public void onClick(Review reviewItem) {
        String url = reviewItem.getUrl();
        Intent browserActivity = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserActivity);
    }

    /**
     * Gets the identifier of the movie and makes
     * the request in a background thread.
     */
    private void loadData() {
        showData();
        JsonObjectRequest reviewRequest = getReviewsRequest(mMovie.getId());
        SingletonRequest.getInstance(this).addToRequestQueue(reviewRequest);
        JsonObjectRequest trailerRequest = getTrailersRequest(mMovie.getId());
        SingletonRequest.getInstance(this).addToRequestQueue(trailerRequest);
    }

    /**
     * Make the View for the review data visible and
     * hide the error message View.
     */
    private void showData() {
        mEmptyReview.setVisibility(View.GONE);
        mTrailerRecyclerView.setVisibility(View.GONE);
        mReviewRecyclerView.setVisibility(View.VISIBLE);
        mTrailerRecyclerView.setVisibility(View.VISIBLE);
        mLoadingReview.setVisibility(View.VISIBLE);
        mLoadingReview.setVisibility(View.VISIBLE);
    }

    /**
     * Make the View the error message visible and
     * hide for the data View.
     */
    private void showReviewErrorMessage() {
        mReviewRecyclerView.setVisibility(View.GONE);
        mEmptyReview.setVisibility(View.VISIBLE);
        mEmptyReview.setText(R.string.not_available);
    }

    /**
     * Make the View the error message visible and
     * hide for the data View.
     */
    private void showTrailerErrorMessage() {
        mTrailerRecyclerView.setVisibility(View.GONE);
        mEmptyTrailer.setVisibility(View.VISIBLE);
        mEmptyTrailer.setText(R.string.not_available);
    }

    /**
     * Handles the not connection error, showing the error
     * and hiding other views
     */
    private void notConnection() {
        mLoadingReview.setVisibility(View.GONE);
        mReviewRecyclerView.setVisibility(View.GONE);
        mLoadingTrailer.setVisibility(View.GONE);
        mTrailerRecyclerView.setVisibility(View.GONE);
        mEmptyReview.setVisibility(View.VISIBLE);
        mEmptyReview.setText(R.string.no_connection);
    }

    /**
     * Makes a request for reviews for this movie
     *
     * @param id the identifier of the movie
     * @return a JsonObjectRequest to add to the queue
     */
    private JsonObjectRequest getReviewsRequest(int id) {
        String reviewStringUrl = buildReviewStringUrl(id);

        JsonObjectRequest reviewRequest = new JsonObjectRequest(
                Request.Method.GET, reviewStringUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mLoadingReview.setVisibility(View.GONE);
                        mEmptyReview.setVisibility(View.GONE);
                        List<Review> reviews = QueryUtils.getReviewsFromJson(response);
                        if (reviews != null && !reviews.isEmpty()) {
                            mReviewAdapter.setReviewData(reviews);
                        } else {
                            showReviewErrorMessage();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, "Error Response", error);
                        showReviewErrorMessage();
                    }
                }
        );
        return reviewRequest;
    }

    /**
     * Makes a request for trailers for this movie
     *
     * @param id the identifier of the movie
     * @return a JsonObjectRequest to add to the queue
     */
    private JsonObjectRequest getTrailersRequest(int id) {
        String trailerStringUrl = QueryUtils.buildTrailerStringUrl(id);

        JsonObjectRequest trailerRequest = new JsonObjectRequest(
                Request.Method.GET, trailerStringUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mLoadingTrailer.setVisibility(View.GONE);
                        List<Trailer> trailers = QueryUtils.getTrailersFromJson(response);
                        if (trailers != null && !trailers.isEmpty()) {
                            mTrailerAdapter.setTrailerData(trailers);
                            mFirstTrailer = trailers.get(0);
                        } else {
                            showTrailerErrorMessage();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, "Error Response", error);
                        showTrailerErrorMessage();
                    }
                }
        );
        return trailerRequest;
    }

    /**
     * Creates a new intent to share the first trailer
     */
    private void createShareTrailerIntent(Trailer trailer) {
        String sharedText = getString(R.string.share_text);
        if (trailer != null) {
            sharedText += "\n\n" + QueryUtils.buildYouTubeStringUrl(trailer.getKey());
        }
        ShareCompat.IntentBuilder
                .from(this)
                .setType("text/plain")
                .setSubject(getString(R.string.share_subject) + mMovie.getTitle())
                .setText(sharedText)
                .startChooser();
    }

    /**
     * Inner class to query if the movie has been marked as favorite
     */
    private class IsFavoriteTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            Cursor cursor = getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    MovieContract.MovieEntry.COLUMN_ID + "=?",
                    new String[]{String.valueOf(mMovie.getId())},
                    null
            );
            boolean isFavorite = false;
            if (cursor != null) {
                isFavorite = cursor.getCount() > 0;
                cursor.close();
            }
            Log.d(LOG_TAG, "isFavorited = " + isFavorite);
            return isFavorite;
        }

        @Override
        protected void onPostExecute(Boolean isFavorite) {
            mIsFavorite = isFavorite;
            if (mIsFavorite) {
                mFavoriteMenu.setIcon(R.drawable.ic_favorite_accent);
            } else {
                mFavoriteMenu.setIcon(R.drawable.ic_favorite_white);
            }
        }
    }

    /**
     * Inner class to add movie to the Content Provider
     */
    private class AddFavoriteTask extends AsyncTask<Void, Void, Uri> {

        @Override
        protected Uri doInBackground(Void... params) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_ID, mMovie.getId());
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, mMovie.getPosterPath());
            contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, mMovie.getSynopsis());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, mMovie.getUserRating());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());

            Uri uri = getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI,
                    contentValues
            );
            Log.d(LOG_TAG, "new uri " + uri.toString());
            return uri;
        }
    }

    /**
     * Inner class to delete a movie from the Content Provider
     */
    private class DeleteFavoriteTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int rowsDeleted = getContentResolver().delete(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MovieContract.MovieEntry.COLUMN_ID + "=?",
                    new String[]{String.valueOf(mMovie.getId())}
            );
            Log.d(LOG_TAG, "rows deleted " + rowsDeleted);
            return rowsDeleted;
        }
    }
}

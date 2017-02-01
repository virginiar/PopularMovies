package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    /* Tag for intent extra data */
    static final String EXTRA_MOVIE = "EXTRA_MOVIE";
    /* Path for detail_reviews */
    static final String REVIEWS_PATH = "detail_reviews";
    /* Tag for log messages */
    private static final String LOG_TAG = DetailActivity.class.getName();
    RequestQueue queue;
    /* Movie to show its details */
    private Movie mMovie;
    private TextView mEmptyReview;
    private ReviewAdapter mReviewAdapter;
    private RecyclerView mReviewRecyclerView;
    private ProgressBar mLoadingReview;


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

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(DetailActivity.EXTRA_MOVIE)) {
            mMovie = intent.getParcelableExtra(DetailActivity.EXTRA_MOVIE);
            titleTextView.setText(mMovie.getTitle());
            releaseDateTextView.setText(mMovie.getReleaseDate());
            synopsisTextView.setText(mMovie.getSynopsis());
            String userRating = mMovie.getUserRating() + " / 10";
            userRatingTextView.setText(userRating);
            String imagePath = MovieAdapter.BASE_IMAGE_URL +
                    MovieAdapter.SIZE_IMAGE_URL +
                    mMovie.getPosterPath();
            Picasso.with(this).load(imagePath)
                    .into(imageView);

            int movieId = mMovie.getId();
            queue = Volley.newRequestQueue(this);

            LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
            mReviewRecyclerView.setLayoutManager(reviewsLayoutManager);
            mReviewAdapter = new ReviewAdapter();
            mReviewRecyclerView.setAdapter(mReviewAdapter);

            // Check the network connectivity
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                loadData();
            } else {
                notConnection();
            }

        }
    }

    /**
     * Gets the identifier of the movie and makes
     * the request in a background thread.
     */
    private void loadData() {
        showData();
        JsonObjectRequest reviewRequest = getReviewsRequest(mMovie.getId());
        queue.add(reviewRequest);
    }

    /**
     * Make the View for the movies data visible and
     * hide the error message View.
     */
    private void showData() {
        mEmptyReview.setVisibility(View.GONE);
        mReviewRecyclerView.setVisibility(View.VISIBLE);
        mLoadingReview.setVisibility(View.VISIBLE);
    }

    /**
     * Make the View the error message visible and
     * hide for the data View.
     */
    private void showErrorMessage() {
        mReviewRecyclerView.setVisibility(View.GONE);
        mEmptyReview.setVisibility(View.VISIBLE);
        mEmptyReview.setText(R.string.not_available);
    }

    /**
     * Handles the not connection error, showing the error
     * and hiding other views
     */
    private void notConnection() {
        mLoadingReview.setVisibility(View.GONE);
        mReviewRecyclerView.setVisibility(View.GONE);
        mEmptyReview.setVisibility(View.VISIBLE);
        mEmptyReview.setText(R.string.no_connection);
    }

    /**
     * Makes a request for detail_reviews for this movie
     *
     * @param id the identifier of the movie
     * @return a JsonObjectRequest to add to the queue
     */
    private JsonObjectRequest getReviewsRequest(int id) {
        String reviewStringUrl = QueryUtils.buildReviewStringUrl(mMovie.getId());

        JsonObjectRequest reviewRequest = new JsonObjectRequest(
                Request.Method.GET, reviewStringUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mLoadingReview.setVisibility(View.GONE);
                        List<Review> reviews = QueryUtils.getReviewsFromJson(response);
                        if (reviews != null && !reviews.isEmpty()) {
                            mReviewAdapter.setReviewData(reviews);
                        } else {
                            showErrorMessage();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, "Error Response", error);
                        showErrorMessage();
                    }
                }
        );
        return reviewRequest;
    }
}

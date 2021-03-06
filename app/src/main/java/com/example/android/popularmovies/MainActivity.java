package com.example.android.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utils.QueryUtils;
import com.example.android.popularmovies.utils.SingletonRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.utils.QueryUtils.buildMovieStringUrl;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler {

    /* Tag for log messages */
    private static final String LOG_TAG = MainActivity.class.getName();
    /* Query parameter to get the most popular movies */
    private static final String POPULAR_QUERY = "popular";
    /* Query parameter to get the top rated movies */
    private static final String TOP_RATED_QUERY = "top_rated";
    /* Query parameter to get the favorite movies */
    private static final String FAVORITES_QUERY = "favorites";
    /* Tag for saved state for movie list */
    private static final String BUNDLE_MOVIES = "BUNDLE_MOVIES";
    /* Tag for saved state for sort parameter */
    private static final String BUNDLE_SORT = "BUNDLE_SORT";
    /* Query parameter to sort the movies, set POPULAR_QUERY as initial state */
    private String mSortBy = POPULAR_QUERY;

    /* TextView to show error messages */
    private TextView mEmptyTextView;
    /* ProgressBar to show that a request is running */
    private ProgressBar mLoadingIndicator;
    /* Adapter for the RecyclerView */
    private MovieAdapter mAdapter;
    /* RecyclerView to inject the movies */
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmptyTextView = (TextView) findViewById(R.id.empty_view);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        /* Select the number of columns based on orientation and size */
        int columnNumber = getGridColumnsNumber();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager =
                new GridLayoutManager(this, columnNumber);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mSortBy = savedInstanceState.getString(BUNDLE_SORT);
            if (savedInstanceState.containsKey(BUNDLE_MOVIES)) {
                List<Movie> movies = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIES);
                mAdapter.setMovieData(movies);
                showMoviesData();
            }
        } else if (mSortBy.equals(FAVORITES_QUERY)) {
            showLoading();
            new MoviesFromFavorites().execute();
        } else if (QueryUtils.checkConnection(this)) {
            loadMoviesData();
        } else {
            showErrorMessage(R.string.no_connection);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ArrayList<Movie> movies = (ArrayList) mAdapter.getMovies();
        if (movies != null && !movies.isEmpty()) {
            outState.putParcelableArrayList(BUNDLE_MOVIES, movies);
        }
        outState.putString(BUNDLE_SORT, mSortBy);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        /* Set checked the selected option */
        switch (mSortBy) {
            case POPULAR_QUERY:
                menu.findItem(R.id.sort_by_popular).setChecked(true);
                break;

            case TOP_RATED_QUERY:
                menu.findItem(R.id.sort_by_top_rated).setChecked(true);
                break;

            case FAVORITES_QUERY:
                menu.findItem(R.id.sort_by_favorites).setChecked(true);
                break;
            default:
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /* Change the selected option, the query parameter
           and perform a new request */
        switch (id) {
            case R.id.sort_by_popular:
                mSortBy = POPULAR_QUERY;
                item.setChecked(true);
                break;

            case R.id.sort_by_top_rated:
                mSortBy = TOP_RATED_QUERY;
                item.setChecked(true);
                break;

            case R.id.sort_by_favorites:
                mSortBy = FAVORITES_QUERY;
                item.setChecked(true);
                break;
        }
        showLoading();
        if (mSortBy.equals(FAVORITES_QUERY)) {
            new MoviesFromFavorites().execute();
        } else {
            JsonObjectRequest movieRequest = getMoviesRequest(mSortBy);
            SingletonRequest.getInstance(this).addToRequestQueue(movieRequest);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movieItem) {
        Intent detailActivity = new Intent(this, DetailActivity.class);
        detailActivity.putExtra(DetailActivity.EXTRA_MOVIE, movieItem);
        startActivity(detailActivity);
    }

    /**
     * Gets the query parameter for sort the movies and makes
     * an request in a background thread.
     */
    private void loadMoviesData() {
        showLoading();
        JsonObjectRequest movieRequest = getMoviesRequest(mSortBy);
        SingletonRequest.getInstance(this).addToRequestQueue(movieRequest);
    }

    /**
     * Show the loading indicator and hide the
     * error message View and the movies View.
     */
    private void showLoading() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mEmptyTextView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
    }

    /**
     * Make the View for the movies data visible and
     * hide the error message View.
     */
    private void showMoviesData() {
        mLoadingIndicator.setVisibility(View.GONE);
        mEmptyTextView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Make the View the error message visible and
     * hide for the movies data View.
     *
     * @param message the id for the string message
     */
    private void showErrorMessage(int message) {
        mEmptyTextView.setText(message);
        mLoadingIndicator.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mEmptyTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Makes a request for reviews for this movie
     *
     * @param sortBy is the query parameter to sort the movies
     * @return a JsonObjectRequest to add to the queue
     */
    private JsonObjectRequest getMoviesRequest(String sortBy) {
        String movieStringUrl = buildMovieStringUrl(sortBy);

        JsonObjectRequest moviesRequest = new JsonObjectRequest(
                Request.Method.GET, movieStringUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        List<Movie> movies = QueryUtils.getMoviesFromJson(response);
                        if (movies != null && !movies.isEmpty()) {
                            mLoadingIndicator.setVisibility(View.GONE);
                            showMoviesData();
                            mAdapter.setMovieData(movies);
                        } else {
                            showErrorMessage(R.string.error_message);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, "Error Response", error);
                        showErrorMessage(R.string.error_message);
                    }
                }
        );
        return moviesRequest;
    }

    /**
     * @return the number of columns in the gridview as
     * function of orientation and size of the screen
     */
    private int getGridColumnsNumber() {
        Boolean landscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        Boolean greatSize = getResources().getConfiguration()
                .isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE);
        int columnNumber = 2;
        if (landscape) {
            columnNumber += 2;
        }
        if (greatSize) {
            columnNumber += 1;
        }
        return columnNumber;
    }

    /* AsyncTask to get the movies in the content provider */
    private class MoviesFromFavorites extends AsyncTask<Void, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(Void... params) {
            Cursor cursor = getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            List<Movie> moviesResult = new ArrayList<>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Movie movie = new Movie(cursor);
                    moviesResult.add(movie);
                }
                cursor.close();
            }
            Log.d(LOG_TAG, "movies " + moviesResult.size());
            return moviesResult;
        }

        @Override
        protected void onPostExecute(List<Movie> moviesResult) {

            if (moviesResult == null) {
                showErrorMessage(R.string.error_message);
            } else if (moviesResult.isEmpty()) {
                showErrorMessage(R.string.no_favorites);
            } else {
                Log.d(LOG_TAG, "movie " + moviesResult.get(0).getTitle());
                mAdapter.setMovieData(moviesResult);
                showMoviesData();

            }
        }
    }
}

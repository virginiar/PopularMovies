package com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{

    /* Tag for log messages */
    private static final String LOG_TAG = MainActivity.class.getName();
    /* Query parameter to get the most popular movies */
    private static final String POPULAR_QUERY = "popular";
    /* Query parameter to get the top rated movies */
    private static final String TOP_RATED_QUERY = "top_rated";
    /* Query parameter to sort the movies, set POPULAR:QUERY as initial state */
    private String mSortBy = POPULAR_QUERY;
    /* TextView that is displayed when the list is empty */
    private TextView mEmptyTextView;
    /* ProgressBar to show and hide the progress */
    private ProgressBar mLoadingIndicator;
    /* TextView for debug purposes*/
    //private TextView mTextView;

    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mTextView = (TextView) findViewById(R.id.text_view);
        mEmptyTextView = (TextView) findViewById(R.id.empty_view);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager =
                new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // Check the network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            loadMoviesData();
        } else {
            mLoadingIndicator.setVisibility(View.GONE);
            mEmptyTextView.setText(R.string.no_connection);
            showErrorMessage();
        }
    }

    /**
     * Gets the query parameter for sort the movies and makes
     * an request in a background thread.
     */
    private void loadMoviesData() {
        showMoviesData();
        new MoviesQueryTask().execute(mSortBy);
    }

    /**
     * Make the View for the movies data visible and
     * hide the error message View.
     */
    private void showMoviesData() {
        mEmptyTextView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Make the View the error message visible and
     * hide for the movies data View.
     */
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.GONE);
        mEmptyTextView.setVisibility(View.VISIBLE);
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
        }
        return true;
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
                new MoviesQueryTask().execute(mSortBy);
                break;

            case R.id.sort_by_top_rated:
                mSortBy = TOP_RATED_QUERY;
                item.setChecked(true);
                new MoviesQueryTask().execute(mSortBy);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movieItem) {
        Toast.makeText(this, movieItem.getTitle(), Toast.LENGTH_SHORT).show();
    }

    /* AsyncTask class to perform the request in a new thread */
    public class MoviesQueryTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... strings) {
            /* If strings is empty or null, return early */
            if (strings.length == 0) {
                return null;
            }

            String queryURL = strings[0];
            List<Movie> moviesResult = null;

            try {
                moviesResult = QueryUtils.fetchMovieData(queryURL);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return moviesResult;
        }

        @Override
        protected void onPostExecute(List<Movie> moviesResult) {
            mLoadingIndicator.setVisibility(View.GONE);

            if (moviesResult != null && !moviesResult.isEmpty()) {
                showMoviesData();
                mAdapter.setMovieData(moviesResult);

            } else {
                mEmptyTextView.setText(R.string.error_message);
                showErrorMessage();
            }
        }
    }
}

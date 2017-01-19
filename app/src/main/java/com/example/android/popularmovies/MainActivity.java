package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    /* Tag for log messages */
    private static final String LOG_TAG = MainActivity.class.getName();
    /* Query parameter to get the most popular movies */
    private static final String POPULAR_QUERY = "popular";
    /* Query parameter to get the top rated movies */
    private static final String TOP_RATED_QUERY = "top_rated";
    /* Query parameter to sort the movies, set POPULAR:QUERY as initial state */
    private String mSortBy = POPULAR_QUERY;
    /* TextView for testing purposes */
    private TextView mtextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Testing Picasso library */
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        Picasso.with(this).load("http://i.imgur.com/DvpvklR.png").into(imageView);

        /* Testing JSON parsing */
        mtextView = (TextView) findViewById(R.id.text_view);
        new MoviesQueryTask().execute(mSortBy);
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

    /* AsyncTask class to perform the request in a new thread */
    public class MoviesQueryTask extends AsyncTask<String, Void, List<Movie>> {

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
            if (moviesResult != null && !moviesResult.equals("")) {
                // Test the parsing result of JSON
                for (Movie movie : moviesResult) {
                    mtextView.append((movie.getTitle() + "\n"));
                }
            }
        }
    }
}

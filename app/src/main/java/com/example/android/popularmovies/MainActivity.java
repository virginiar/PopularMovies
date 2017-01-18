package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = MainActivity.class.getName();
    /* Query parameter to get the most popular movies */
    private static final String POPULAR_QUERY = "popular";
    /* Query parameter to get the most rated movies */
    private static final String TOP_RATED_QUERY = "top_rated";
    private TextView mtextView;
    private URL mQueryUrl;
    private String mSortBy = POPULAR_QUERY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        Picasso.with(this).load("http://i.imgur.com/DvpvklR.png").into(imageView);

        mtextView = (TextView) findViewById(R.id.text_view);
        mQueryUrl = QueryUtils.buildUrl(mSortBy);
        //mtextView.setText(queryUrl.toString());
        new MoviesQueryTask().execute(mQueryUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

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
        switch (id) {
            case R.id.sort_by_popular:
                mSortBy = POPULAR_QUERY;
                item.setChecked(true);
                mQueryUrl = QueryUtils.buildUrl(mSortBy);
                new MoviesQueryTask().execute(mQueryUrl);
                break;
            case R.id.sort_by_top_rated:
                mSortBy = TOP_RATED_QUERY;
                item.setChecked(true);
                mQueryUrl = QueryUtils.buildUrl(mSortBy);
                new MoviesQueryTask().execute(mQueryUrl);
                break;
        }
        Log.v(LOG_TAG, "Sort by " + mSortBy);
        return super.onOptionsItemSelected(item);
    }

    public class MoviesQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL queryURL = urls[0];
            String moviesResult = null;
            try {
                moviesResult = QueryUtils.getResponseFromHttpUrl(queryURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return moviesResult;
        }

        @Override
        protected void onPostExecute(String moviesResult) {
            if (moviesResult != null && !moviesResult.equals("")) {
                mtextView.setText(moviesResult);
            }
        }
    }
}

package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    /* Tag for intent extra data */
    static final String EXTRA_MOVIE = "EXTRA_MOVIE";
    /* Tag for log messages */
    private static final String LOG_TAG = MainActivity.class.getName();

    private TextView mMovieTextView;
    private Movie mMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mMovieTextView = (TextView) findViewById(R.id.movie_title);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(DetailActivity.EXTRA_MOVIE)) {
                mMovie = intent.getParcelableExtra(DetailActivity.EXTRA_MOVIE);
                mMovieTextView.setText(mMovie.getTitle());
            }
        }

    }
}

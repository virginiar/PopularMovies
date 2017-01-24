package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    /* Tag for intent extra data */
    static final String EXTRA_MOVIE = "EXTRA_MOVIE";
    /* Tag for log messages */
    private static final String LOG_TAG = MainActivity.class.getName();
    /* Movie to show its details */
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView titleTextView = (TextView) findViewById(R.id.movie_title);
        TextView releaseDateTextView = (TextView) findViewById(R.id.movie_release_date);
        TextView synopsisTextView = (TextView) findViewById(R.id.movie_synopsis);
        TextView userRatingTextView = (TextView) findViewById(R.id.movie_user_rating);
        ImageView imageView = (ImageView) findViewById(R.id.movie_image);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(DetailActivity.EXTRA_MOVIE)) {
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
            }
        }

    }
}

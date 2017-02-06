package com.example.android.popularmovies.data;


import android.provider.BaseColumns;

public class MovieContract {
    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_POSTER = "poster_path";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATING = "user_rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
    }
}

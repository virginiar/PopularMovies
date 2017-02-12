package com.example.android.popularmovies.data;


import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    /* Authority */
    static final String AUTHORITY = "com.example.android.popularmovies";
    /* Base content URI */
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    /* Path for the movies directory */
    static final String PATH_MOVIES = "movies";

    /* Class that defines the contents of the movie table */
    public static final class MovieEntry implements BaseColumns {

        /* Movie content URI = base content URI + path */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon()
                        .appendPath(PATH_MOVIES)
                        .build();

        /* Movie table and column names */
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster_path";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATING = "user_rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";

        /* Columns to show */
        public static final String[] MOVIES_PROJECTION = {
                MovieEntry.COLUMN_ID,
                MovieEntry.COLUMN_TITLE,
                MovieEntry.COLUMN_POSTER,
                MovieEntry.COLUMN_SYNOPSIS,
                MovieEntry.COLUMN_RATING,
                MovieEntry.COLUMN_RELEASE_DATE
        };

        /* Constants that represents each column name position */
        public static final int INDEX_ID = 0;
        public static final int INDEX_TITLE = 1;
        public static final int INDEX_POSTER = 2;
        public static final int INDEX_SYNOPSIS = 3;
        public static final int INDEX_RATING = 4;
        public static final int INDEX_RELEASE = 5;
    }
}

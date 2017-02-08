package com.example.android.popularmovies.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;


public class MovieProvider extends ContentProvider {

    /* Integer constants for the directory and single item */
    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mMovieDbHelper;

    /**
     * @return A UriMatcher that correctly matches the constants
     * for CODE_MOVIES and CODE_MOVIE_WITH_ID
     */
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        /* Add all paths */
        uriMatcher.addURI(MovieContract.AUTHORITY,
                MovieContract.PATH_MOVIES, CODE_MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY,
                MovieContract.PATH_MOVIES + "/#", CODE_MOVIE_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        /* Access to the database */
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        /* URI matching code */
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case CODE_MOVIES:
                /* Query for the movies directory */
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        /* Notify on the cursor */
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /* Access to the database */
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        /* URI matching code */
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case CODE_MOVIES:
                /* Insert the movie into the database */
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME,
                        null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        /* Notify if the uri has been changed */
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        /* Keep track of the deleted movies */
        int moviesDeleted = 0;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE_WITH_ID:
                // Delete the selected movie
                String id = uri.getPathSegments().get(1);
                moviesDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        "_id=?",
                        new String[]{id});
                break;
            case CODE_MOVIES:
                moviesDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        /* Notify the resolver if any movie have been deleted */
        if (moviesDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return moviesDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

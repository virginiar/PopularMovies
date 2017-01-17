package com.example.android.popularmovies;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public final class QueryUtils {

    /** Tag for log messages */
    private static final String LOG_TAG = QueryUtils.class.getName();

    /* The base for constructing a query for themoviedb.org */
    final static String BASE_QUERY_URL = "http://api.themoviedb.org/3/movie/";
    /* Query parameter to get the most popular movies */
    final static String POPULAR_QUERY = "popular";
    /* Query parameter to get the most rated movies */
    final static String TOP_RATED_QUERY = "top_rated";
    /* Parameter for the API key */
    final static String API_KEY_PARAM = "api_key";
    /* The required API key to perform the query */
    final static String MY_APY_KEY = "my_api_key";

    /*
    * Build the URL used to query themoviedb.org
    *
    * @param query the keyword that will be queried for
    * @return the URL to use to query the themoviedb.org
     */
    public static URL buildUrl(String query) {
        Uri builtUri = Uri.parse(BASE_QUERY_URL).buildUpon()
                .appendPath(query)
                .appendQueryParameter(API_KEY_PARAM, MY_APY_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, "Built URI " + url);
        return url;
    }
}

package com.example.android.popularmovies;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class QueryUtils {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getName();

    /* The base for constructing a query for themoviedb.org */
    private static final String BASE_QUERY_URL = "http://api.themoviedb.org/3/movie/";
    /* Parameter for the API key */
    private static final String API_KEY_PARAM = "api_key";
    /* The required API key to perform the query */
    private static final String MY_APY_KEY = "my_api_key";

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
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}

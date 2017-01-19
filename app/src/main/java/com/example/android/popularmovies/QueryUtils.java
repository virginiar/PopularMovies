package com.example.android.popularmovies;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Helper methods related to requesting and receiving earthquake data from themoviedb.org.
 */
public final class QueryUtils {

    /* Tag for log messages */
    private static final String LOG_TAG = QueryUtils.class.getName();
    /* The base for constructing a query for themoviedb.org */
    private static final String BASE_QUERY_URL = "http://api.themoviedb.org/3/movie/";
    /* Parameter for the API key */
    private static final String API_KEY_PARAM = "api_key";
    /* The required API key to perform the query */
    private static final String MY_APY_KEY = "[YOUR-API-KEY-HERE]";

    /**
     * Query the movies database and return a list of {@link Movie} objects.
     *
     * @param sortBy The sort by criteria to perform the query
     * @return A list of movies sorted by criteria
     */
    public static List<Movie> fetchMovieData(String sortBy) {
        URL url = buildUrl(sortBy);
        String jsonResponse = null;

        try {
            jsonResponse = getResponseFromHttpUrl(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request. ", e);
        }

        List<Movie> movies = getMoviesFromJson(jsonResponse);
        return movies;
    }

    /*
    * Build the URL used to query themoviedb.org
    *
    * @param query the keyword that will be queried for
    * @return the URL to use to query the themoviedb.org
     */
    private static URL buildUrl(String query) {
        Uri builtUri = Uri.parse(BASE_QUERY_URL).buildUpon()
                .appendPath(query)
                .appendQueryParameter(API_KEY_PARAM, MY_APY_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
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
    private static String getResponseFromHttpUrl(URL url) throws IOException {
        String jsonResponse = "";
        /*  If the URL is null, then return early */
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = url.openStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                jsonResponse = scanner.next();
            } else {
                jsonResponse = null;
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * This method parses JSON from a web response and returns
     * a list of {@link Movie} objects
     *
     * @param moviesJsonString The JSON String response from server
     * @return Array of Strings describing the movies data
     * @throws org.json.JSONException If JSON data cannot be properly parsed
     */
    private static List<Movie> getMoviesFromJson(String moviesJsonString) {
        /*  If the JSON String is empty or null, then return early */
        if (TextUtils.isEmpty(moviesJsonString)) {
            return null;
        }

        List<Movie> movies = new ArrayList<>();

        /*  Try to parse the JSON String */
        try {
            JSONObject baseJsonResponse = new JSONObject(moviesJsonString);
            JSONArray moviesArray = baseJsonResponse.getJSONArray("results");

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject currentMovie = moviesArray.getJSONObject(i);

                int id = currentMovie.getInt("id");
                String title = currentMovie.getString("title");
                String posterPath = currentMovie.getString("poster_path");
                String synopsis = currentMovie.getString("overview");
                String userRating = currentMovie.getString("vote_average");
                String releaseDate = currentMovie.getString("release_date");

                Movie movie = new Movie(id, title, posterPath, synopsis, userRating, releaseDate);

                movies.add(movie);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movies JSON response. ", e);
        }
        return movies;
    }
}

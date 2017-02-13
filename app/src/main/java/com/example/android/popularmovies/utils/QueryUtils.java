package com.example.android.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from themoviedb.org.
 */
public class QueryUtils {

    /* The base for constructing a query for themoviedb.org */
    static final String BASE_QUERY_URL = "http://api.themoviedb.org/3/movie/";
    /* Parameter for the API key */
    static final String API_KEY_PARAM = "?api_key=";
    /* Path for reviews */
    static final String REVIEWS_PATH = "/reviews";
    /* Path for trailers */
    static final String TRAILERS_PATH = "/videos";
    /* The base for constructing a query for the poster of a movie */
    static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    /* The size for the poster image */
    static final String SIZE_IMAGE_URL = "w185";
    /* Path for youtube videos */
    static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    /* The required API key to perform the query */
    static final String MY_API_KEY = "[YOUR-API-KEY-HERE]";

    /* Tag for log messages */
    private static final String LOG_TAG = QueryUtils.class.getName();

    /**
     * Check the network connectivity
     * @return if the device is connected to Internet
     */
    public static boolean checkConnection(Context context) {
        // Check the network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /*
     * Build the URL used to query themoviedb.org for the poster of a movie
     * /movie/{id}/reviews
     *
     * @param posterPath the path to get the poster of the movie that will be queried for
     * @return the URL to use to query the themoviedb.org for reviews
     */
    public static String buildMovieStringUrl(String sortBy) {
        return BASE_QUERY_URL + sortBy + API_KEY_PARAM + MY_API_KEY;
    }

    /*
     * Build the URL used to query themoviedb.org for the poster of a movie
     * /movie/{id}/reviews
     *
     * @param posterPath the path to get the poster of the movie that will be queried for
     * @return the URL to use to query the themoviedb.org for reviews
     */
    public static String buildPosterStringUrl(String posterPath) {
        return BASE_IMAGE_URL + SIZE_IMAGE_URL + posterPath;
    }

    /*
     * Build the URL used to query themoviedb.org for the poster of a movie
     * /movie/{id}/reviews
     *
     * @param posterPath the path to get the poster of the movie that will be queried for
     * @return the URL to use to query the themoviedb.org for reviews
     */
    public static String buildYouTubeStringUrl(String key) {
        return BASE_YOUTUBE_URL + key;
    }

    /*
     * Build the URL used to query themoviedb.org for reviews
     * /movie/{id}/reviews
     *
     * @param query the id of the movie that will be queried for
     * @return the URL to use to query the themoviedb.org for reviews
     */
    public static String buildReviewStringUrl(int movieId) {
        return BASE_QUERY_URL + String.valueOf(movieId) + REVIEWS_PATH + API_KEY_PARAM + MY_API_KEY;
    }

    /*
     * Build the URL used to query themoviedb.org for trailers
     * /movie/{id}/videos
     *
     * @param query the id of the movie that will be queried for
     * @return the URL to use to query the themoviedb.org for trailers
     */
    public static String buildTrailerStringUrl(int movieId) {
        return BASE_QUERY_URL + String.valueOf(movieId) + TRAILERS_PATH + API_KEY_PARAM + MY_API_KEY;
    }

    /**
     * This method parses JSON from a web response and returns
     * a list of {@link Movie} objects
     *
     * @param moviesJson The JSON response from server
     * @return List of Movie describing the movies data
     */
    public static List<Movie> getMoviesFromJson(JSONObject moviesJson) {
        /*  If the JSON String is empty or null, then return early */
        if (moviesJson.length() == 0) {
            return null;
        }

        List<Movie> movies = new ArrayList<>();

        /*  Try to parse the JSON String */
        try {
            JSONArray moviesArray = moviesJson.getJSONArray("results");

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

    /**
     * This method parses JSON from a web response for reviews and returns
     * a list of {@link Review} objects
     *
     * @param reviewsJson The JSON  response from server
     * @return List of Review describing the reviews data
     */
    public static List<Review> getReviewsFromJson(JSONObject reviewsJson) {
        /*  If the JSON is empty or null, then return early */
        if (reviewsJson.length() == 0) {
            return null;
        }

        List<Review> reviews = new ArrayList<>();

        /*  Try to parse the JSON String */
        try {

            JSONArray reviewsArray = reviewsJson.getJSONArray("results");

            for (int i = 0; i < reviewsArray.length(); i++) {
                JSONObject currentReview = reviewsArray.getJSONObject(i);

                String id = currentReview.getString("id");
                String author = currentReview.getString("author");
                String content = currentReview.getString("content");
                String url = currentReview.getString("url");

                Review review = new Review(id, author, content, url);

                reviews.add(review);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movies JSON response for reviews. ", e);
            return null;
        }
        return reviews;
    }

    /**
     * This method parses JSON from a web response for trailers and returns
     * a list of {@link Trailer} objects
     *
     * @param trailersJson The JSON  response from server
     * @return List of Trailer describing the trailers data
     */
    public static List<Trailer> getTrailersFromJson(JSONObject trailersJson) {
        /*  If the JSON is empty or null, then return early */
        if (trailersJson.length() == 0) {
            return null;
        }

        List<Trailer> trailers = new ArrayList<>();

        /*  Try to parse the JSON String */
        try {

            JSONArray trailersArray = trailersJson.getJSONArray("results");

            for (int i = 0; i < trailersArray.length(); i++) {
                JSONObject currentTrailer = trailersArray.getJSONObject(i);

                String id = currentTrailer.getString("id");
                String name = currentTrailer.getString("name");
                String site = currentTrailer.getString("site");
                String key = currentTrailer.getString("key");

                // Only add Youtube videos
                if (site.equalsIgnoreCase("Youtube")) {
                    Trailer trailer = new Trailer(id, name, site, key);
                    trailers.add(trailer);
                }

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movies JSON response for trailers. ", e);
            return null;
        }
        return trailers;
    }
}

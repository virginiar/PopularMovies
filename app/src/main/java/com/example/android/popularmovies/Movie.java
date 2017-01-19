package com.example.android.popularmovies;

import static android.R.attr.id;

/**
 * An {@link Movie} object contains information related to a single movie.
 */

public class Movie {

    /* Identifier of the movie in the database */
    private int mId;

    /* Title of the movie */
    private String mTitle;

    /* Relative path for the movie poster image */
    private String mPosterPath;

    /* Plot synopsis of the movie */
    private String mSynopsis;

    /* User rating of the movie */
    private String mUserRating;

    /* Release date of the movie */
    private String mReleaseDate;

    /**
     * Construct a new {@link Movie} object.
     *
     * @param ìd          is the identifier of the movie in the database
     * @param title       is the title of the movie
     * @param posterPath  is the relative path for the movie poster image
     * @param synopsis    is the plot synopsis of the movie
     * @param userRating  is the user rating of the movie
     * @param releaseDate is the release date of the movie
     */
    public Movie(int ìd, String title, String posterPath,
                 String synopsis, String userRating, String releaseDate) {
        this.mId = id;
        this.mPosterPath = posterPath;
        this.mTitle = title;
        this.mSynopsis = synopsis;
        this.mUserRating = userRating;
        this.mReleaseDate = releaseDate;
    }

    /**
     * @return the id of the movie in the database
     */
    public int getId() {
        return mId;
    }

    /**
     * @return the title of the movie
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @return the relative path for the movie poster image
     */
    public String getPosterPath() {
        return mPosterPath;
    }

    /**
     * @return the plot synopsis of the movie
     */
    public String getSynopsis() {
        return mSynopsis;
    }

    /**
     * @return the user rating of the movie
     */
    public String getUserRating() {
        return mUserRating;
    }

    /**
     * @return the release date of the movie
     */
    public String getReleaseDate() {
        return mReleaseDate;
    }
}

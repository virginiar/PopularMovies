package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * An {@link Movie} object contains information related to a single movie.
 */

public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
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
     * @param id          is the identifier of the movie in the database
     * @param title       is the title of the movie
     * @param posterPath  is the relative path for the movie poster image
     * @param synopsis    is the plot synopsis of the movie
     * @param userRating  is the user rating of the movie
     * @param releaseDate is the release date of the movie
     */
    public Movie(int id, String title, String posterPath,
                 String synopsis, String userRating, String releaseDate) {
        this.mId = id;
        this.mPosterPath = posterPath;
        this.mTitle = title;
        this.mSynopsis = synopsis;
        this.mUserRating = userRating;
        this.mReleaseDate = releaseDate;
    }

    protected Movie(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mPosterPath = in.readString();
        mSynopsis = in.readString();
        mUserRating = in.readString();
        mReleaseDate = in.readString();
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mTitle);
        parcel.writeString(mPosterPath);
        parcel.writeString(mSynopsis);
        parcel.writeString(mUserRating);
        parcel.writeString(mReleaseDate);
    }
}

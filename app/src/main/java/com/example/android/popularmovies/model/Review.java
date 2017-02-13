package com.example.android.popularmovies.model;


public class Review {

    /* Identifier of the review in the database */
    private String mId;
    /* Title of the author */
    private String mAuthor;
    /* Content of the review*/
    private String mContent;
    /* Url of the content */
    private String mUrl;

    /**
     * Construct a new {@link Review} object.
     *
     * @param id      is the identifier of the review
     * @param author  is the author of the review
     * @param content is the content of the review
     * @param url     is the url of the review
     */
    public Review(String id, String author, String content, String url) {
        this.mId = id;
        this.mAuthor = author;
        this.mContent = content;
        this.mUrl = url;
    }

    /**
     * @return the id of the review
     */
    public String getId() {
        return mId;
    }

    /**
     * @return the author of the review
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * @return the content of the review
     */
    public String getContent() {
        return mContent;
    }

    /**
     * @return the url of the review
     */
    public String getUrl() {
        return mUrl;
    }
}

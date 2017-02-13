package com.example.android.popularmovies.model;

public class Trailer {
    /* Identifier for the trailer */
    private String mId;
    /* Name of the trailer */
    private String mName;
    /* Site of the trailer */
    private String mSite;
    /* Key for the trailer */
    private String mKey;

    /**
     * Construct a new {@link Trailer} object.
     *
     * @param id   is the identifier for the trailer
     * @param name is the name of the trailer
     * @param site is the site of the trailer
     * @param key  is the key of the trailer
     */
    public Trailer(String id, String name, String site, String key) {
        this.mId = id;
        this.mName = name;
        this.mSite = site;
        this.mKey = key;
    }

    /**
     * @return the identifier of the trailer
     */
    public String getId() {
        return mId;
    }

    /**
     * @return the name of the trailer
     */
    public String getName() {
        return mName;
    }

    /**
     * @return the site of the trailer
     */
    public String getSite() {
        return mSite;
    }

    /**
     * @return the key of the trailer
     */
    public String getKey() {
        return mKey;
    }
}

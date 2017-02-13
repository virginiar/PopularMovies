package com.example.android.popularmovies.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SingletonRequest {
    /* The instance of the SingletonRequest */
    private static SingletonRequest mInstance;
    /* The queue to perform the request */
    private RequestQueue mRequestQueue;
    /* The application context */
    private Context mContext;

    /**
     * Creates a new {@link SingletonRequest}
     *
     * @param context The context to create the instance
     */
    private SingletonRequest(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    /**
     * @param context The context of the request
     * @return the instance of the SingletonRequest
     */
    public static synchronized SingletonRequest getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SingletonRequest(context);
        }
        return mInstance;
    }

    /**
     * Creates a new queue if not exists
     * @return a queue to perform the request
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Add request to the queue
     * @param request The request to add to the queue
     * @param <T> The type of the request
     */
    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }
}

package com.example.android.popularmovies;


import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieDbHelper;
import com.example.android.popularmovies.data.MovieProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class ContentProviderTest {

    //================================================================================
    // Test UriMatcher
    //================================================================================
    private static final Uri TEST_MOVIES = MovieContract.MovieEntry.CONTENT_URI;
    // Content URI for a single task with id = 1
    private static final Uri TEST_MOVIE_WITH_ID =
            TEST_MOVIES.buildUpon().appendPath("1").build();


    //================================================================================
    // Test ContentProvider Registration
    //================================================================================
    /* Context used to access various parts of the system */
    private final Context mContext = InstrumentationRegistry.getTargetContext();

    /**
     * Delete all entries in the movies directory.
     */
    @Before
    public void setUp() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        database.close();
    }

    @Test
    public void testProviderRegistry() {

        String packageName = mContext.getPackageName();
        String taskProviderClassName = MovieProvider.class.getName();
        ComponentName componentName = new ComponentName(packageName, taskProviderClassName);

        try {

            PackageManager pm = mContext.getPackageManager();

            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            String actualAuthority = providerInfo.authority;
            String expectedAuthority = packageName;

            /* Make sure that the registered authority matches the authority from the Contract */
            String incorrectAuthority =
                    "Error: TaskContentProvider registered with authority: " + actualAuthority +
                            " instead of expected authority: " + expectedAuthority;
            assertEquals(incorrectAuthority,
                    actualAuthority,
                    expectedAuthority);

        } catch (PackageManager.NameNotFoundException e) {
            String providerNotRegisteredAtAll =
                    "Error: TaskContentProvider not registered at " + mContext.getPackageName();
            /*
             * This exception is thrown if the ContentProvider hasn't been registered with the
             * manifest at all. If this is the case, you need to double check your
             * AndroidManifest file
             */
            fail(providerNotRegisteredAtAll);
        }
    }

    @Test
    public void testUriMatcher() {

        /* Create a URI matcher that the TaskContentProvider uses */
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        /* Test that the code returned from our matcher matches the expected MOVIES int */
        String tasksUriDoesNotMatch = "Error: The MOVIES URI was matched incorrectly.";
        int actualTasksMatchCode = testMatcher.match(TEST_MOVIES);
        int expectedTasksMatchCode = MovieProvider.CODE_MOVIES;
        assertEquals(tasksUriDoesNotMatch,
                actualTasksMatchCode,
                expectedTasksMatchCode);

        /* Test that the code returned from our matcher matches the expected MOVIE_WITH_ID */
        String taskWithIdDoesNotMatch =
                "Error: The MOVIE_WITH_ID URI was matched incorrectly.";
        int actualTaskWithIdCode = testMatcher.match(TEST_MOVIE_WITH_ID);
        int expectedTaskWithIdCode = MovieProvider.CODE_MOVIE_WITH_ID;
        assertEquals(taskWithIdDoesNotMatch,
                actualTaskWithIdCode,
                expectedTaskWithIdCode);
    }

    //================================================================================
    // Test Insert
    //================================================================================
    @Test
    public void testInsert() {

        /* Create values to insert */
        ContentValues testMovieValues = new ContentValues();
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_ID, 124);
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Test");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_POSTER, "poster.jpg");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, "The best movie");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_RATING, "5.4");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2017-02-06");

        /* TestContentObserver allows us to test if notifyChange was called appropriately */
        TestUtilities.TestContentObserver taskObserver = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (tasks) */
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                MovieContract.MovieEntry.CONTENT_URI,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                taskObserver);


        Uri uri = contentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, testMovieValues);


        Uri expectedUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, 1);

        String insertProviderFailed = "Unable to insert item through Provider";
        assertEquals(insertProviderFailed, uri, expectedUri);

        /*
         * If this fails, it's likely you didn't call notifyChange in your insert method from
         * your ContentProvider.
         */
        taskObserver.waitForNotificationOrFail();

        /*
         * waitForNotificationOrFail is synchronous, so after that call, we are done observing
         * changes to content and should therefore unregister this observer.
         */
        contentResolver.unregisterContentObserver(taskObserver);
    }

    //================================================================================
    // Test Query (for movies directory)
    //================================================================================


    /**
     * Inserts data, then tests if a query for the tasks directory returns that data as a Cursor
     */
    @Test
    public void testQuery() {

        /* Get access to a writable database */
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        /* Create values to insert */
        ContentValues testMovieValues = new ContentValues();
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_ID, 124);
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Test");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_POSTER, "poster.jpg");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, "The best movie");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_RATING, "5.4");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2017-02-06");

        /* Insert ContentValues into database and get a row ID back */
        long movieRowId = database.insert(
                /* Table to insert values into */
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                /* Values to insert into table */
                testMovieValues);

        String insertFailed = "Unable to insert directly into the database";
        assertTrue(insertFailed, movieRowId != -1);

        /* We are done with the database, close it now. */
        database.close();

        /* Perform the ContentProvider query */
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);


        String queryFailed = "Query failed to return a valid Cursor";
        assertTrue(queryFailed, movieCursor != null);

        /* We are done with the cursor, close it now. */
        movieCursor.close();
    }

    //================================================================================
    // Test Delete (for a single item)
    //================================================================================
    @Test
    public void testDelete() {
        /* Access writable database */
        MovieDbHelper helper = new MovieDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase database = helper.getWritableDatabase();

        /* Create a new row of task data */
        ContentValues testMovieValues = new ContentValues();
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_ID, 124);
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Test");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_POSTER, "poster.jpg");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, "The best movie");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_RATING, "5.4");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2017-02-06");

        /* Insert ContentValues into database and get a row ID back */
        long taskRowId = database.insert(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                testMovieValues);

        /* Always close the database when you're through with it */
        database.close();

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, taskRowId != -1);

        /* TestContentObserver allows us to test if notifyChange was called appropriately */
        TestUtilities.TestContentObserver taskObserver = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (tasks) */
        contentResolver.registerContentObserver(
                MovieContract.MovieEntry.CONTENT_URI,
                true,
                taskObserver);

        /* The delete method deletes the previously inserted row with id = 1 */
        Uri uriToDelete = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath("1").build();
        int tasksDeleted = contentResolver.delete(uriToDelete, null, null);

        String deleteFailed = "Unable to delete item in the database";
        assertTrue(deleteFailed, tasksDeleted != 0);

        /*
         * If this fails, it's likely you didn't call notifyChange in your delete method from
         * your ContentProvider.
         */
        taskObserver.waitForNotificationOrFail();

        /*
         * waitForNotificationOrFail is synchronous, so after that call, we are done observing
         * changes to content and should therefore unregister this observer.
         */
        contentResolver.unregisterContentObserver(taskObserver);
    }
}

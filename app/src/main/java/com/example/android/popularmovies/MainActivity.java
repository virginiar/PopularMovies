package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView mtextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        Picasso.with(this).load("http://i.imgur.com/DvpvklR.png").into(imageView);

        mtextView = (TextView) findViewById(R.id.text_view);
        URL queryUrl = QueryUtils.buildUrl(QueryUtils.POPULAR_QUERY);
        //mtextView.setText(queryUrl.toString());
        new MoviesQueryTask().execute(queryUrl);
    }

    public class MoviesQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL queryURL = urls[0];
            String moviesResult = null;
            try {
                moviesResult = QueryUtils.getResponseFromHttpUrl(queryURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return moviesResult;
        }

        @Override
        protected void onPostExecute(String moviesResult) {
            if (moviesResult != null && !moviesResult.equals("")) {
                mtextView.setText(moviesResult);
            }
        }
    }
}

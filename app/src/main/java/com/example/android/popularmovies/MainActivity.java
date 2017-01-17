package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        Picasso.with(this).load("http://i.imgur.com/DvpvklR.png").into(imageView);

        TextView textView = (TextView) findViewById(R.id.text_view);
        URL queryUrl = QueryUtils.buildUrl(QueryUtils.POPULAR_QUERY);
        textView.setText(queryUrl.toString());
    }
}

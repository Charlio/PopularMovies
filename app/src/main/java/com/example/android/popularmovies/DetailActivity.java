package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by chali on 1/14/2018.
 */

public class DetailActivity extends AppCompatActivity {

    private TextView mMovieTitle;
    private TextView mMovieReleaseDate;
    private ImageView mMoviePoster;
    private TextView mMovieVoteAverage;
    private TextView mMoviePlotSynopsis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}

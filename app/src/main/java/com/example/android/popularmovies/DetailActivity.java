package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.SingleMovieDetailStringUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by chali on 1/14/2018.
 */

public class DetailActivity extends AppCompatActivity {

    private String mSingleMovieData;

    private TextView mMovieTitle;
    private TextView mMovieReleaseDate;
    private ImageView mMoviePoster;
    private TextView mMovieVoteAverage;
    private TextView mMoviePlotSynopsis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mMovieTitle = findViewById(R.id.movie_title);
        mMovieReleaseDate = findViewById(R.id.movie_release_date);
        mMoviePoster = findViewById(R.id.movie_poster);
        mMovieVoteAverage = findViewById(R.id.movie_vote_average);
        mMoviePlotSynopsis = findViewById(R.id.movie_plot_synopsis);

        Intent intentStartedThisActivity = getIntent();
        if (intentStartedThisActivity != null
                && intentStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            mSingleMovieData = intentStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
        }

        String[] movieData = SingleMovieDetailStringUtils.getMovieData(mSingleMovieData);

        Uri posterUri = NetworkUtils.buildMoviePosterUrl(movieData[0]);
        Picasso.with(this).load(posterUri).into(mMoviePoster);
        mMovieTitle.setText(movieData[1]);
        mMoviePlotSynopsis.setText(movieData[2]);
        mMovieVoteAverage.setText(movieData[3]);
        mMovieReleaseDate.setText(movieData[4]);

    }
}

package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by chali on 1/14/2018.
 */

public class DetailActivity extends AppCompatActivity {

    private TextView mMovieTitle;
    private TextView mMovieReleaseDate;
    private ImageView mMoviePoster;
    private TextView mMovieVoteAverage;
    private TextView mMoviePlotSynopsis;

    // TODO add favorite button, trailer link, reviews

    // TODO onclick favorite button, trigger database insert and deselect to delete

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mMovieTitle = findViewById(R.id.movie_title);
        mMovieReleaseDate = findViewById(R.id.movie_release_date);
        mMoviePoster = findViewById(R.id.movie_poster);
        mMovieVoteAverage = findViewById(R.id.movie_vote_average);
        mMoviePlotSynopsis = findViewById(R.id.movie_plot_synopsis);

        Intent intentStartedActivity = getIntent();

        if (intentStartedActivity.hasExtra("parcel_data")) {
            Movie mSingleMovieData = intentStartedActivity.getParcelableExtra("parcel_data");

            Uri posterUri = NetworkUtils.buildMoviePosterUrl(mSingleMovieData.getPosterRelativePath());
            Picasso.with(this).load(posterUri).into(mMoviePoster);
            mMovieTitle.setText(mSingleMovieData.getOriginalTitle());
            mMoviePlotSynopsis.setText(mSingleMovieData.getOverview());
            mMovieVoteAverage.setText(Double.toString(mSingleMovieData.getVoteAverage()));
            mMovieReleaseDate.setText(mSingleMovieData.getReleaseDate());

        }
    }
}

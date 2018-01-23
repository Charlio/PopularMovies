package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by chali on 1/14/2018.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private Movie mMovie;
    private TextView mMovieTitle;
    private TextView mMovieReleaseDate;
    private ImageView mMoviePoster;
    private TextView mMovieVoteAverage;
    private TextView mMoviePlotSynopsis;
    private ToggleButton mAddToFavorite;
    private TextView mTrailerLink;
    private TextView mReviews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mMovieTitle = findViewById(R.id.movie_title);
        mMovieReleaseDate = findViewById(R.id.movie_release_date);
        mMoviePoster = findViewById(R.id.movie_poster);
        mMovieVoteAverage = findViewById(R.id.movie_vote_average);
        mMoviePlotSynopsis = findViewById(R.id.movie_plot_synopsis);
        mAddToFavorite = findViewById(R.id.favorite_toggle_button);
        mTrailerLink = findViewById(R.id.trailer_link);
        mReviews = findViewById(R.id.reviews);

        Intent intentStartedActivity = getIntent();

        if (intentStartedActivity.hasExtra("parcel_data")) {
            mMovie = intentStartedActivity.getParcelableExtra("parcel_data");

            Uri posterUri = NetworkUtils.buildMoviePosterUrl(mMovie.getPosterRelativePath());
            Picasso.with(this).load(posterUri).into(mMoviePoster);
            mMovieTitle.setText(mMovie.getOriginalTitle());
            mMoviePlotSynopsis.setText(mMovie.getOverview());
            mMovieVoteAverage.setText(Double.toString(mMovie.getVoteAverage()));
            mMovieReleaseDate.setText(mMovie.getReleaseDate());

        }

        mAddToFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    addToFavorite();
                    Toast.makeText(getBaseContext(), "added to favorites", Toast.LENGTH_LONG).show();
                } else {
                    deleteFromFavorite();
                    Toast.makeText(getBaseContext(), "deleted from favorites", Toast.LENGTH_LONG).show();
                }
                compoundButton.setChecked(!isChecked);
            }
        });
    }

    private void addToFavorite() {
        // TODO add mMovie to database
        // create contentValue and Uri
        // get content resolver and call insert
        // update returned id into mMovie
    }

    private void deleteFromFavorite() {
        // TODO delete mMovie from database
        // get id from mMovie and create uri
        // get content resolver and call delete
        // update returned id into mMovie
    }

}

package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by chali on 1/14/2018.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private static final boolean CHECKED = true;

    private Movie mMovie;
    private TextView mMovieTitle;
    private TextView mMovieReleaseDate;
    private ImageView mMoviePoster;
    private TextView mMovieVoteAverage;
    private TextView mMoviePlotSynopsis;
    private CheckBox mAddToFavorite;
    // TODO add trailers and reviews
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
        mAddToFavorite = findViewById(R.id.favorite_checkbox);
        mTrailerLink = findViewById(R.id.trailer_link);
        mReviews = findViewById(R.id.reviews);

        Intent intentStartedActivity = getIntent();

        if (intentStartedActivity.hasExtra("parcel_data")) {
            mMovie = intentStartedActivity.getParcelableExtra("parcel_data");

            displayMovieInfo();

            if (existsInDatabase(mMovie.getId())) {
                mAddToFavorite.setChecked(CHECKED);
            } else {
                mAddToFavorite.setChecked(!CHECKED);
            }

        }

        mAddToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAddToFavorite.isChecked()) {
                    addToFavorite();
                } else {
                    deleteFromFavorite();
                }
            }
        });
    }

    private void displayMovieInfo() {
        Uri posterUri = NetworkUtils.buildMoviePosterUrl(mMovie.getPosterRelativePath());
        Picasso.with(this).load(posterUri).into(mMoviePoster);
        mMovieTitle.setText(mMovie.getOriginalTitle());
        mMoviePlotSynopsis.setText(mMovie.getOverview());
        mMovieVoteAverage.setText(Double.toString(mMovie.getVoteAverage()));
        mMovieReleaseDate.setText(mMovie.getReleaseDate());

        int movieId = mMovie.getId();
        // TODO fetech videos and revies from urls built with movieId

    }

    private boolean existsInDatabase(int id) {
        String[] projection = {MovieEntry.COLUMN_ID};
        String selection = MovieEntry.COLUMN_ID + "=?";
        String[] selectionArgs = {Integer.toString(id)};
        Cursor cursor = getContentResolver().query(MovieEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            return false;
        }
    }

    private void addToFavorite() {
        int id = mMovie.getId();
        String posterRelativePath = mMovie.getPosterRelativePath();
        String originalTitle = mMovie.getOriginalTitle();
        String overview = mMovie.getOverview();
        double voteAverage = mMovie.getVoteAverage();
        String releaseDate = mMovie.getReleaseDate();

        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_ID, id);
        values.put(MovieEntry.COLUMN_POSTER_RELATIVE_PATH, posterRelativePath);
        values.put(MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
        values.put(MovieEntry.COLUMN_OVERVIEW, overview);
        values.put(MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        values.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);

        Uri uri = getContentResolver().insert(MovieEntry.CONTENT_URI, values);
        if (uri != null) {
            Toast.makeText(this, "Movie added to favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteFromFavorite() {
        String stringId = Integer.toString(mMovie.getId());
        Uri uri = MovieEntry.CONTENT_URI.buildUpon().appendPath(stringId).build();
        getContentResolver().delete(uri, null, null);
        Toast.makeText(this, "Movie deleted from favorites", Toast.LENGTH_SHORT).show();
    }

    // TODO set up AsyncTaskLoaders for loading videos and reviews
    // TODO set up RecycleViews and layouts in activity_detail.xml

}

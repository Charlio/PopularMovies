package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.adapters.ReviewAdapter;
import com.example.android.popularmovies.adapters.VideoAdapter;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * Created by chali on 1/14/2018.
 */

public class DetailActivity extends AppCompatActivity implements
        VideoAdapter.VideoAdapterOnClickHandler {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private static final boolean CHECKED = true;

    private Movie mMovie;
    private int mMovieId;

    private TextView mMovieTitle;
    private TextView mMovieReleaseDate;
    private ImageView mMoviePoster;
    private TextView mMovieVoteAverage;
    private TextView mMoviePlotSynopsis;
    private CheckBox mAddToFavorite;
    private RecyclerView mRecyclerviewVideos;
    private RecyclerView mREcyclerviewReviews;

    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;


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
        mRecyclerviewVideos = findViewById(R.id.recyclerview_videos);
        mREcyclerviewReviews = findViewById(R.id.recyclerview_reviews);

        Intent intentStartedActivity = getIntent();

        if (intentStartedActivity.hasExtra("parcel_data")) {
            mMovie = intentStartedActivity.getParcelableExtra("parcel_data");
            mMovieId = mMovie.getId();

            displayMovieInfo();

            if (existsInDatabase()) {
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

        LinearLayoutManager videoLayoutManager
                = new LinearLayoutManager(this);
        mRecyclerviewVideos.setLayoutManager(videoLayoutManager);
        mRecyclerviewVideos.setHasFixedSize(true);
        mVideoAdapter = new VideoAdapter(this, this);
        mRecyclerviewVideos.setAdapter(mVideoAdapter);

        fetchVideoData();

        LinearLayoutManager reviewLayoutManager
                = new LinearLayoutManager(this);
        mREcyclerviewReviews.setLayoutManager(reviewLayoutManager);
        mREcyclerviewReviews.setHasFixedSize(true);
        mReviewAdapter = new ReviewAdapter(this);
        mREcyclerviewReviews.setAdapter(mReviewAdapter);

        fetchReviewData();

    }

    private boolean existsInDatabase() {
        String[] projection = {MovieEntry.COLUMN_ID};
        String selection = MovieEntry.COLUMN_ID + "=?";
        String[] selectionArgs = {Integer.toString(mMovieId)};
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


    private void fetchVideoData() {
        // TODO fetch video links ArrayList<URL> from mMovie and call mVideoAdapter.setVideoData
    }

    private void fetchReviewData() {
        // TODO fetch reviews ArrayList<String> from mMovie and call mReviewAdapter.setReviewData
    }

    @Override
    public void onClick(URL singleVideoLink) {
        // TODO open intent to start trailers on youtube
    }

}

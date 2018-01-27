package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.adapters.ReviewAdapter;
import com.example.android.popularmovies.adapters.VideoAdapter;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Video;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.OpenJsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements
        VideoAdapter.VideoAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<String[]> {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private static final int VIDEO_AND_REVIEW_LOADER_ID = 1;

    private static final boolean CHECKED = true;
    private static final int FETCH_BY_DATABASE = 2;
    private static final String FETCH_METHOD = "fetch";
    private static final int API = 0;
    private static final int DATABASE = 1;
    private static final String SAVED_LAYOUT_MANAGER = "saved_layout_manager";
    private Movie mMovie;
    private int mMovieId;
    private TextView mMovieTitle;
    private TextView mMovieReleaseDate;
    private ImageView mMoviePoster;
    private TextView mMovieVoteAverage;
    private TextView mMoviePlotSynopsis;
    private CheckBox mAddToFavorite;
    private RecyclerView mRecyclerviewVideos;
    private RecyclerView mRecyclerviewReviews;
    private String mVideoJsonString = "";
    private String mReviewJsonString = "";
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;
    private int fetchFrom;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private Parcelable layoutManagerSavedState;

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
        mRecyclerviewReviews = findViewById(R.id.recyclerview_reviews);
        mErrorMessageDisplay = findViewById(R.id.detail_tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.detail_pb_loading_indicator);

        Intent intentStartedActivity = getIntent();

        if (intentStartedActivity.hasExtra("parcel_data")) {
            mMovie = intentStartedActivity.getParcelableExtra("parcel_data");
            int fetchMethod = intentStartedActivity.getIntExtra("fetch_method", -1);
            if (fetchMethod == FETCH_BY_DATABASE) {
                fetchFrom = DATABASE;
            } else {
                fetchFrom = API;
            }
            mMovieId = mMovie.getId();

            if (savedInstanceState != null) {
                if (savedInstanceState.containsKey(SAVED_LAYOUT_MANAGER)) {
                    layoutManagerSavedState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER);
                }
            }

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

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle queryBundle) {
        return new AsyncTaskLoader<String[]>(this) {

            String[] mParsedResults = new String[2];

            @Override
            protected void onStartLoading() {
                mLoadingIndicator.setVisibility(View.VISIBLE);
                if (mParsedResults[0] != null && mParsedResults[1] != null) {
                    deliverResult(mParsedResults);
                } else {
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {
                int fetchMethod;
                if (queryBundle == null) {
                    fetchMethod = API;
                } else {
                    fetchMethod = queryBundle.getInt(FETCH_METHOD);
                }

                if (fetchMethod == DATABASE) {
                    Cursor cursor;
                    try {
                        String[] projection =
                                {MovieEntry.COLUMN_VIDEO_JSON_STRING, MovieEntry.COLUMN_REVIEW_JSON_STRING};
                        String selection = MovieEntry.COLUMN_ID + "=?";
                        String[] selectionArgs = {Integer.toString(mMovieId)};
                        cursor = getContentResolver().query(MovieEntry.CONTENT_URI,
                                projection,
                                selection,
                                selectionArgs,
                                null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                    return fetchVideosAndReviewsFromDatabase(cursor);
                } else {
                    URL videosQueryUrl = NetworkUtils.buildMovieVideosUrl(mMovieId);
                    URL reviewsQueryUrl = NetworkUtils.buildMovieReviewsUrl(mMovieId);

                    Log.v(TAG, "videos query url: " + videosQueryUrl);
                    Log.v(TAG, "reviews query url: " + reviewsQueryUrl);

                    try {
                        String videoJsonString = NetworkUtils.getResponseFromHttpUrl(videosQueryUrl);
                        String reviewJsonString = NetworkUtils.getResponseFromHttpUrl(reviewsQueryUrl);
                        return new String[]{videoJsonString, reviewJsonString};
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }

            @Override
            public void deliverResult(String[] parsedResuls) {
                mParsedResults = parsedResuls;
                super.deliverResult(parsedResuls);
            }
        };
    }

    private String[] fetchVideosAndReviewsFromDatabase(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            Log.v(TAG, "null cursor from database");
            return null;
        }

        int videoJsonStringId = cursor.getColumnIndex(MovieEntry.COLUMN_VIDEO_JSON_STRING);
        int reviewJsonStringId = cursor.getColumnIndex(MovieEntry.COLUMN_REVIEW_JSON_STRING);

        cursor.moveToFirst();
        String videoJsonString = cursor.getString(videoJsonStringId);
        String reviewJsonString = cursor.getString(reviewJsonStringId);
        cursor.close();

        return new String[]{videoJsonString, reviewJsonString};

    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data == null || data[0] == null || data[1] == null) {
            showErrorMessage();
        } else {
            showMovieDataView();
            mVideoJsonString = data[0];
            mReviewJsonString = data[1];
            ArrayList<Video> videos = new ArrayList<>();
            ArrayList<Review> reviews = new ArrayList<>();
            try {
                videos = OpenJsonUtils.getSingleMovieVideosFromJsonString(mVideoJsonString);
                reviews = OpenJsonUtils.getSingleMovieReviewsFromJsonString(mReviewJsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mVideoAdapter.setVideoData(videos);
            mReviewAdapter.setReviewData(reviews);
            if (layoutManagerSavedState != null) {
                mRecyclerviewReviews.getLayoutManager()
                        .onRestoreInstanceState(layoutManagerSavedState);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }

    private void displayMovieInfo() {
        Uri posterUri = NetworkUtils.buildMoviePosterUrl(mMovie.getPosterRelativePath());
        Picasso.with(this).load(posterUri).into(mMoviePoster);
        mMovieTitle.setText(mMovie.getOriginalTitle());
        mMoviePlotSynopsis.setText(mMovie.getOverview());
        mMovieVoteAverage.setText(Double.toString(mMovie.getVoteAverage()));
        mMovieReleaseDate.setText(mMovie.getReleaseDate());

        int recyclerViewOrientation = LinearLayoutManager.VERTICAL;
        final boolean shouldReverseLayout = false;

        LinearLayoutManager videoLayoutManager
                = new LinearLayoutManager(this, recyclerViewOrientation, shouldReverseLayout);
        mRecyclerviewVideos.setLayoutManager(videoLayoutManager);
        mRecyclerviewVideos.setHasFixedSize(true);
        mVideoAdapter = new VideoAdapter(this, this);
        mRecyclerviewVideos.setAdapter(mVideoAdapter);

        LinearLayoutManager reviewLayoutManager
                = new LinearLayoutManager(this, recyclerViewOrientation, shouldReverseLayout);
        mRecyclerviewReviews.setLayoutManager(reviewLayoutManager);
        mRecyclerviewReviews.setHasFixedSize(true);
        mReviewAdapter = new ReviewAdapter(this);
        mRecyclerviewReviews.setAdapter(mReviewAdapter);

        fetchVideoAndReviewData();

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
        String videoJsonString = mVideoJsonString;
        String reviewJsonString = mReviewJsonString;

        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_ID, id);
        values.put(MovieEntry.COLUMN_POSTER_RELATIVE_PATH, posterRelativePath);
        values.put(MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
        values.put(MovieEntry.COLUMN_OVERVIEW, overview);
        values.put(MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        values.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
        values.put(MovieEntry.COLUMN_VIDEO_JSON_STRING, videoJsonString);
        values.put(MovieEntry.COLUMN_REVIEW_JSON_STRING, reviewJsonString);

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

    private void fetchVideoAndReviewData() {
        Bundle queryBundle = new Bundle();
        queryBundle.putInt(FETCH_METHOD, fetchFrom);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> videosAndReviewsLoader = loaderManager.getLoader(VIDEO_AND_REVIEW_LOADER_ID);
        if (videosAndReviewsLoader == null) {
            loaderManager.initLoader(VIDEO_AND_REVIEW_LOADER_ID, queryBundle, this);
        } else {
            loaderManager.restartLoader(VIDEO_AND_REVIEW_LOADER_ID, queryBundle, this);
        }
    }

    @Override
    public void onClick(Video singleVideo) {
        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(singleVideo.getUrl().toString()));
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerviewVideos.setVisibility(View.VISIBLE);
        mRecyclerviewReviews.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerviewVideos.setVisibility(View.INVISIBLE);
        mRecyclerviewReviews.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(SAVED_LAYOUT_MANAGER,
                mRecyclerviewReviews.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(SAVED_LAYOUT_MANAGER)) {
            layoutManagerSavedState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER);
        }
    }

}

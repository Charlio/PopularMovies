package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.OpenMovieJsonUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 0;
    private static final int SORT_BY_POPULARITY = 0;
    private static final int SORT_BY_RATING = 1;
    private static final String SEARCH_QUERY_SORT_METHOD_EXTRA = "sort";
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private int mSortById;

    // TODO create a variable to indicate whether to fetch data from api or database: mFetchFromAPI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mSortById = SORT_BY_POPULARITY;

        mRecyclerView = findViewById(R.id.recyclerview_movie);
        int numberOfColumns = getNumberOfColumns();
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, numberOfColumns);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mMovieAdapter);

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);

    }

    private int getNumberOfColumns() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        return (int) (dpWidth / scalingFactor);
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int i, final Bundle queryBundle) {

        return new AsyncTaskLoader<ArrayList<Movie>>(this) {

            ArrayList<Movie> mParsedMovieResults;

            @Override
            protected void onStartLoading() {
                mLoadingIndicator.setVisibility(View.VISIBLE);
                if (mParsedMovieResults != null) {
                    deliverResult(mParsedMovieResults);
                } else {
                    forceLoad();
                }
            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                int sortById;

                // TODO check mFetchFromAPI, add code for qeury favoriate list from database

                if (queryBundle == null) {
                    sortById = SORT_BY_POPULARITY;
                } else {
                    sortById = queryBundle.getInt(SEARCH_QUERY_SORT_METHOD_EXTRA);
                }
                URL searchQueryUrl = NetworkUtils.buildMovieDataUrl(sortById);
                try {
                    String movieResultJson = NetworkUtils.getResponseFromHttpUrl(searchQueryUrl);
                    ArrayList<Movie> parsedMovieResults
                            = OpenMovieJsonUtils.getSimpleMovieStringsFromJson(movieResultJson);
                    return parsedMovieResults;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(ArrayList<Movie> parsedMovieResuls) {
                mParsedMovieResults = parsedMovieResuls;
                super.deliverResult(parsedMovieResuls);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> parsedMovieResults) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (parsedMovieResults == null) {
            showErrorMessage();
        } else {
            showMovieDataView();
            mMovieAdapter.setMovieData(parsedMovieResults);
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

    @Override
    public void onClick(Movie singleMoviedata) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("parcel_data", singleMoviedata);
        startActivity(intentToStartDetailActivity);

    }

    // TODO add a method to create ArrayList<Movie> from Cursor, used in loadInBackground in onCreateLoader

    private void invalidateData() {
        mMovieAdapter.setMovieData(null);
    }

    private void fetchMovieData() {
        Bundle queryBundle = new Bundle();
        queryBundle.putInt(SEARCH_QUERY_SORT_METHOD_EXTRA, mSortById);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieSearchLoader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if (movieSearchLoader == null) {
            loaderManager.initLoader(MOVIE_LOADER_ID, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_LOADER_ID, queryBundle, this);
        }

    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_popular) {
            invalidateData();
            mSortById = SORT_BY_POPULARITY;
            fetchMovieData();
            return true;
        }

        if (id == R.id.action_sort_rating) {
            invalidateData();
            mSortById = SORT_BY_RATING;
            fetchMovieData();
            return true;
        }

        // TODO change to switch, add option on displaying favorite list

        return super.onOptionsItemSelected(item);
    }

}

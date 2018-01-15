package com.example.android.popularmovies;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.OpenMovieJsonUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<String[]> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView mMovieSearchUrl;
    private TextView mMovieResultsJsonDisplay;

    private RecyclerView mRecyclerView;

    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    // loader id as the input into LoaderManager to get the movie loader
    private static final int MOVIE_LOADER_ID = 0;

    private static final int SORT_BY_POPULARITY = 0;
    private static final int SORT_BY_RATING = 1;
    private int mSortById;

    private static final String SEARCH_QUERY_SORT_METHOD_EXTRA = "sort";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mSortById = SORT_BY_POPULARITY;

        mMovieSearchUrl = findViewById(R.id.url_display);
        mMovieResultsJsonDisplay = findViewById(R.id.movie_result_json_display);
        // TODO find views and assign to variables

        //TODO setup GridLayoutManager and MovieAdapter for mRecyclerView

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    public Loader<String[]> onCreateLoader(int i, final Bundle queryBundle) {
        //TODO
        return new AsyncTaskLoader<String[]>(this) {

            String[] mParsedMovieResults;

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
            public String[] loadInBackground() {
                int sortById;
                if (queryBundle == null) {
                    sortById = SORT_BY_POPULARITY;
                } else {
                    sortById = queryBundle.getInt(SEARCH_QUERY_SORT_METHOD_EXTRA);
                }
                URL searchQueryUrl = NetworkUtils.buildMovieDataUrl(sortById);
                try {
                    String movieResultJson = NetworkUtils.getResponseFromHttpUrl(searchQueryUrl);
                    String[] parsedMovieResults
                            = OpenMovieJsonUtils.getSimpleMovieStringsFromJson(movieResultJson);
                    return parsedMovieResults;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String[] parsedMovieResuls) {
                mParsedMovieResults = parsedMovieResuls;
                super.deliverResult(parsedMovieResuls);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] parsedMovieResults) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (parsedMovieResults == null) {
            showErrorMessage();
        } else {
            for (int i = 0; i < parsedMovieResults.length; i++) {
                mMovieResultsJsonDisplay.append(parsedMovieResults[i] + "\n\n\n");
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }

    @Override
    public void onClick() {
        //TODO
    }

    private void invalidateData() {
        mMovieResultsJsonDisplay.setText("");
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

        return super.onOptionsItemSelected(item);
    }

}

package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import com.example.android.popularmovies.adapters.MovieAdapter;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
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
    private static final int FETCH_BY_DATABASE = 2;
    private static final String SEARCH_QUERY_FETCH_METHOD_EXTRA = "fetch";
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private int mFetchMethod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        if (savedInstanceState != null &&
                savedInstanceState.containsKey(SEARCH_QUERY_FETCH_METHOD_EXTRA)) {
            mFetchMethod = savedInstanceState.getInt(SEARCH_QUERY_FETCH_METHOD_EXTRA);
        } else {
            mFetchMethod = SORT_BY_POPULARITY;
        }

        mRecyclerView = findViewById(R.id.recyclerview_movie);
        int numberOfColumns = getNumberOfColumns();
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, numberOfColumns);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mMovieAdapter);

        fetchMovieData();

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
                int fetchById;


                if (queryBundle == null) {
                    fetchById = SORT_BY_POPULARITY;
                } else {
                    fetchById = queryBundle.getInt(SEARCH_QUERY_FETCH_METHOD_EXTRA);
                }

                if (fetchById == FETCH_BY_DATABASE) {
                    Cursor cursor;
                    try {
                        cursor = getContentResolver().query(MovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                    return fetchAllMoviesFromDatabase(cursor);
                } else {
                    URL searchQueryUrl = NetworkUtils.buildMovieDataUrl(fetchById);
                    try {
                        String movieResultJson = NetworkUtils.getResponseFromHttpUrl(searchQueryUrl);
                        // TODO also fetch videos and reviews json and combine to create ArrayList<Movie>
                        ArrayList<Movie> parsedMovieResults
                                = OpenMovieJsonUtils.getMoviesArrayListFromJsonString(movieResultJson);
                        return parsedMovieResults;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }

            @Override
            public void deliverResult(ArrayList<Movie> parsedMovieResuls) {
                mParsedMovieResults = parsedMovieResuls;
                super.deliverResult(parsedMovieResuls);
            }
        };
    }

    private ArrayList<Movie> fetchAllMoviesFromDatabase(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        ArrayList<Movie> movies = new ArrayList<>();

        int idIndex = cursor.getColumnIndex(MovieEntry.COLUMN_ID);
        int posterRelativePathId = cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_RELATIVE_PATH);
        int originalTitleId = cursor.getColumnIndex(MovieEntry.COLUMN_ORIGINAL_TITLE);
        int overviewId = cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW);
        int voteAverageId = cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE);
        int releaseDateId = cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(idIndex);
            String posterRelativePath = cursor.getString(posterRelativePathId);
            String originalTitle = cursor.getString(originalTitleId);
            String overview = cursor.getString(overviewId);
            double voteAverage = cursor.getDouble(voteAverageId);
            String releaseDate = cursor.getString(releaseDateId);
            Movie movie = new Movie(id,
                    posterRelativePath,
                    originalTitle,
                    overview,
                    voteAverage,
                    releaseDate);
            movies.add(movie);

            cursor.moveToNext();
        }
        cursor.close();

        return movies;
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

    private void invalidateData() {
        mMovieAdapter.setMovieData(null);
    }

    private void fetchMovieData() {
        Bundle queryBundle = new Bundle();
        queryBundle.putInt(SEARCH_QUERY_FETCH_METHOD_EXTRA, mFetchMethod);

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

        switch (id) {
            case R.id.action_sort_popular:
                invalidateData();
                mFetchMethod = SORT_BY_POPULARITY;
                fetchMovieData();
                return true;
            case R.id.action_sort_rating:
                invalidateData();
                mFetchMethod = SORT_BY_RATING;
                fetchMovieData();
                return true;
            case R.id.action_favorite_list:
                invalidateData();
                mFetchMethod = FETCH_BY_DATABASE;
                fetchMovieData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SEARCH_QUERY_FETCH_METHOD_EXTRA, mFetchMethod);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mFetchMethod = savedInstanceState.getInt(SEARCH_QUERY_FETCH_METHOD_EXTRA);
    }
}

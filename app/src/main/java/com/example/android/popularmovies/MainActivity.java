package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<String[]> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    // loader id as the input into LoaderManager to get the movie loader
    private static final int MOVIE_LOADER_ID = 0;

    private static final int SORT_BY_POPULARITY = 0;
    private static final int SORT_BY_RATING = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO find views and assign to variables

        //TODO setup GridLayoutManager and MovieAdapter for mRecyclerView

        //TODO initialize loader manager
    }

    @Override
    public Loader<String[]> onCreateLoader(int i, Bundle bundle) {
        //TODO
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] strings) {
        //TODO
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {
        //TODO
    }

    @Override
    public void onClick() {
        //TODO
    }

    private void invalidateData() {
        //TODO
    }

    private void fetchMovieData(int sortby) {
        //TODO
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
            fetchMovieData(SORT_BY_POPULARITY);
            return true;
        }

        if (id == R.id.action_sort_rating) {
            invalidateData();
            fetchMovieData(SORT_BY_RATING);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by chali on 1/14/2018.
 */

public final class NetworkUtils {

    public static final int SORT_ID_POPULARITY = 0;

    public static final int SORT_ID_RATING = 1;
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_MOVIE_IMAGE_URL =
            "http://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w185/";
    private static final String BASE_MOVIE_DATA_URL =
            "http://api.themoviedb.org/3/movie/";
    private static final String SORT_BY_POPULARITY = "popular";
    private static final String SORT_BY_RATING = "top_rated";

    private static final String VIDEOS = "videos";
    private static final String REVIEWS = "reviews";

    private static final String API_KEY_QUERY = "api_key";

    private static final String API_KEY = BuildConfig.MY_API_KEY;


    /*
     * Example movie poster url: http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
     */
    @Nullable
    public static Uri buildMoviePosterUrl(String imageRelativePath) {
        Uri builtUri = null;
        if (imageRelativePath != null) {
            builtUri = Uri.parse(BASE_MOVIE_IMAGE_URL + POSTER_SIZE + imageRelativePath);
        }

        Log.v(TAG, "Built movie poster image URL " + builtUri);

        return builtUri;
    }

    /*
     * Example movie data url: http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
     */
    @Nullable
    public static URL buildMovieDataUrl(int sortId) {
        String sortBy;
        if (sortId == SORT_ID_POPULARITY) {
            sortBy = SORT_BY_POPULARITY;
        } else {
            sortBy = SORT_BY_RATING;
        }
        Uri builtUri = Uri.parse(BASE_MOVIE_DATA_URL + sortBy).buildUpon()
                .appendQueryParameter(API_KEY_QUERY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built movie data URL " + url);

        return url;
    }

    /*
     * Example movie videos url: http://api.themoviedb.org/3/movie/346364/videos?api_key=[YOUR_API_KEY]
     */
    @Nullable
    public static URL buildMovieVideosUrl(int movieId) {
        Uri builtUri = Uri.parse(BASE_MOVIE_DATA_URL + VIDEOS).buildUpon()
                .appendQueryParameter(API_KEY_QUERY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built single movie's videos URL " + url);

        return url;
    }

    /*
     * Example movie reviews url: http://api.themoviedb.org/3/movie/346364/reviews?api_key=[YOUR_API_KEY]
     */
    @Nullable
    public static URL buildMovieReviewsUrl(int movieId) {
        Uri builtUri = Uri.parse(BASE_MOVIE_DATA_URL + REVIEWS).buildUpon()
                .appendQueryParameter(API_KEY_QUERY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built single movie's reviews URL " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);

            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}

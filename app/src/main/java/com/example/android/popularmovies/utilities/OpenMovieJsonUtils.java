package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by chali on 1/14/2018.
 */

public final class OpenMovieJsonUtils {
    public static ArrayList<Movie> getMoviesArrayListFromJsonString(String movieJsonString)
            throws JSONException {
        final String MOVIE_LIST = "results";

        final String POSTER_PATH = "poster_path";

        final String ID = "id";
        final String ORIGINAL_TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";

        ArrayList<Movie> parsedMovieData;

        JSONObject movieJson = new JSONObject(movieJsonString);
        JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);
        parsedMovieData = new ArrayList<>();

        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieObject = movieArray.getJSONObject(i);

            int id = movieObject.getInt(ID);
            String posterPath = movieObject.getString(POSTER_PATH);
            String originalTitle = movieObject.getString(ORIGINAL_TITLE);
            String overview = movieObject.getString(OVERVIEW);
            double voteAverage = movieObject.getDouble(VOTE_AVERAGE);
            String releaseDate = movieObject.getString(RELEASE_DATE);

            Movie movie = new Movie(id, posterPath, originalTitle, overview, voteAverage, releaseDate);

            parsedMovieData.add(movie);
        }

        return parsedMovieData;
    }

    public static ArrayList<URL> getSingleMovieVideosFromJsonString(String videosJsonString)
            throws JSONException {
        // TODO generate array list of videos urls
        return null;
    }

    public static ArrayList<String> getSingleMovieReviewsFromJsonString(String reviewsJsonString)
            throws JSONException {
        // TODO generate array list of reviews strings
        return null;
    }
}

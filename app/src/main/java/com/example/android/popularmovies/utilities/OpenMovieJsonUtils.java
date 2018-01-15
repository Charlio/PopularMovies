package com.example.android.popularmovies.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chali on 1/14/2018.
 */

public final class OpenMovieJsonUtils {
    public static String[] getSimpleMovieStringsFromJson(String movieJsonString)
            throws JSONException {
        final String MOVIE_LIST = "results";

        final String POSTER_PATH = "poster_path";

        final String ORIGINAL_TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";

        String[] parsedMovieData;

        JSONObject movieJson = new JSONObject(movieJsonString);
        JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);
        parsedMovieData = new String[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movie = movieArray.getJSONObject(i);

            String posterPath = movie.getString(POSTER_PATH);
            String originalTitle = movie.getString(ORIGINAL_TITLE);
            String overview = movie.getString(OVERVIEW);
            String voteAverage = Double.toString(movie.getDouble(VOTE_AVERAGE));
            String releaseDate = movie.getString(RELEASE_DATE);

            parsedMovieData[i] = posterPath
                    + "-" + originalTitle
                    + "-" + overview
                    + "-" + voteAverage
                    + "-" + releaseDate;
        }

        return parsedMovieData;
    }
}

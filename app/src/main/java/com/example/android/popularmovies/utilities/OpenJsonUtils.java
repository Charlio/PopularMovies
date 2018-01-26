package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public final class OpenJsonUtils {
    public static ArrayList<Movie> getMovieArrayListFromJsonString(String movieJsonString)
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

    public static ArrayList<Video> getSingleMovieVideosFromJsonString(String videosJsonString)
            throws JSONException {
        final String YOUTUBE_WATCH_URL = "https://www.youtube.com/watch";
        final String VIDEO_QUERY = "v";
        final String VIDEO_LIST = "results";
        final String KEY = "key";
        final String NAME = "name";
        final String SITE = "site";
        final String YOUTUBE = "YouTube";

        Log.v(OpenJsonUtils.class.getSimpleName(), "vidoes json string: " + videosJsonString);
        /*
         * youtube link example: https://www.youtube.com/watch?v=xKJmEC5ieOk
         * where xKJmEC5ieOk is the key
         */
        ArrayList<Video> parsedVideoData;

        JSONObject videoJson = new JSONObject(videosJsonString);
        JSONArray videoArray = videoJson.getJSONArray(VIDEO_LIST);
        parsedVideoData = new ArrayList<>();

        for (int i = 0; i < videoArray.length(); i++) {
            JSONObject videoObject = videoArray.getJSONObject(i);

            String site = videoObject.getString(SITE);
            if (site.equals(YOUTUBE)) {
                String key = videoObject.getString(KEY);
                String name = videoObject.getString(NAME);

                Uri builtUri = Uri.parse(YOUTUBE_WATCH_URL).buildUpon()
                        .appendQueryParameter(VIDEO_QUERY, key)
                        .build();

                URL url = null;

                try {
                    url = new URL(builtUri.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                Video video = new Video(name, url);
                parsedVideoData.add(video);
            }
        }
        return parsedVideoData;
    }

    public static ArrayList<Review> getSingleMovieReviewsFromJsonString(String reviewsJsonString)
            throws JSONException {
        final String REVIEW_LIST = "results";
        final String AUTHOR = "author";
        final String CONTENT = "content";

        ArrayList<Review> parsedReviewData;
        JSONObject reviewJson = new JSONObject(reviewsJsonString);
        JSONArray reviewArray = reviewJson.getJSONArray(REVIEW_LIST);
        parsedReviewData = new ArrayList<>();

        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject reviewObject = reviewArray.getJSONObject(i);

            String author = reviewObject.getString(AUTHOR);
            String content = reviewObject.getString(CONTENT);

            Review review = new Review(author, content);
            parsedReviewData.add(review);
        }
        return parsedReviewData;
    }
}

package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String AUTHORITY = "com.example.android.popularmovies";
    public static final String PATH_MOVIES = "movies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_POSTER_RELATIVE_PATH = "poster_relative_path";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VIDEO_JSON_STRING = "video_json_string";
        public static final String COLUMN_REVIEW_JSON_STRING = "review_json_string";
    }

}

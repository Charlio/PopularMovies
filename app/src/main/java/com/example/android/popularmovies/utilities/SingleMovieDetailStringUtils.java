package com.example.android.popularmovies.utilities;

/**
 * Created by chali on 1/15/2018.
 */

public final class SingleMovieDetailStringUtils {
    public static String getPosterRelativePath(String singleMoviedata) {
        int pos = singleMoviedata.indexOf('-');
        String relativePath = singleMoviedata.substring(0, pos - 1);
        return relativePath;
    }

    public static String[] getMovieData(String singleMovieData) {
        return singleMovieData.split(" - ", 5);
    }
}

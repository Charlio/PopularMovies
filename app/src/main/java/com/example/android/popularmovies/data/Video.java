package com.example.android.popularmovies.data;

import java.net.URL;

/**
 * Created by chali on 1/23/2018.
 */

public class Video {
    private String mName;
    private URL mUrl;

    public Video(String name, URL url) {
        mName = name;
        mUrl = url;
    }

    public String getName() {
        return mName;
    }

    public URL getUrl() {
        return mUrl;
    }
}

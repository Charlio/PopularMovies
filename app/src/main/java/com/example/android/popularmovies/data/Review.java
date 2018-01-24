package com.example.android.popularmovies.data;

/**
 * Created by chali on 1/23/2018.
 */

public class Review {
    private String mAuthor;
    private String mContent;

    public Review(String author, String content) {
        mAuthor = author;
        mContent = content;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }
}

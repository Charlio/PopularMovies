package com.example.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chali on 1/21/2018.
 */

public class Movie implements Parcelable {
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private String mPosterRelativePath;
    private String mOriginalTitle;
    private String mOverview;
    private double mVoteAverage;
    private String mReleaseDate;

    public Movie(String posterRelativePath,
                 String originalTitle,
                 String overview,
                 double voteAverage,
                 String releaseDate) {
        mPosterRelativePath = posterRelativePath;
        mOriginalTitle = originalTitle;
        mOverview = overview;
        mVoteAverage = voteAverage;
        mReleaseDate = releaseDate;
    }

    private Movie(Parcel in) {
        this.mPosterRelativePath = in.readString();
        this.mOriginalTitle = in.readString();
        this.mOverview = in.readString();
        this.mVoteAverage = in.readDouble();
        this.mReleaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mPosterRelativePath);
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mOverview);
        parcel.writeDouble(mVoteAverage);
        parcel.writeString(mReleaseDate);
    }

    public String getPosterRelativePath() {
        return mPosterRelativePath;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }
}

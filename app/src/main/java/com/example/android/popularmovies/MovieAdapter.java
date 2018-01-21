package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by chali on 1/14/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private final Context context;
    private final MovieAdapterOnClickHandler mClickHandler;
    private ArrayList<Movie> mMovieData;

    public MovieAdapter(Context context, MovieAdapterOnClickHandler clickHandler) {
        this.context = context;
        mClickHandler = clickHandler;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie singleMovieData = mMovieData.get(position);
        String posterRelativePath = singleMovieData.getPosterRelativePath();
        Uri posterUri = NetworkUtils.buildMoviePosterUrl(posterRelativePath);
        Picasso.with(context).load(posterUri).into(holder.mPosterImageView);
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null) {
            return 0;
        } else {
            return mMovieData.size();
        }
    }

    public void setMovieData(ArrayList<Movie> movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie singleMovieData);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mPosterImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mPosterImageView = view.findViewById(R.id.iv_list_item);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie singleMovieData = mMovieData.get(adapterPosition);
            mClickHandler.onClick(singleMovieData);
        }
    }
}

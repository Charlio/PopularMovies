package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;

import java.util.ArrayList;

/**
 * Created by chali on 1/23/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private final Context context;
    private ArrayList<String> mReviewData;

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ReviewAdapter.ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewAdapterViewHolder holder, int position) {
        if (mReviewData == null || mReviewData.size() == 0) {
            return;
        }
        String singleReview = mReviewData.get(position);
        holder.mReviewTextView.setText(singleReview);
    }

    @Override
    public int getItemCount() {
        if (mReviewData == null) {
            return 0;
        } else {
            return mReviewData.size();
        }
    }

    public void setReviewData(ArrayList<String> reviewData) {
        mReviewData = reviewData;
        notifyDataSetChanged();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mReviewTextView;

        public ReviewAdapterViewHolder(View view) {
            super(view);
            mReviewTextView = view.findViewById(R.id.review_list_item);
        }
    }
}

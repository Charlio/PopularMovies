package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by chali on 1/23/2018.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {

    private final Context context;
    private final VideoAdapterOnClickHandler mClickHandler;
    private ArrayList<URL> mVideoData;

    public VideoAdapter(Context context, VideoAdapterOnClickHandler clickHandler) {
        this.context = context;
        mClickHandler = clickHandler;
    }

    @Override
    public VideoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.video_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new VideoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoAdapterViewHolder holder, int position) {
        if (mVideoData == null || mVideoData.size() == 0) {
            return;
        }
        URL singleVideoLink = mVideoData.get(position);
        holder.mURLTextView.setText(singleVideoLink.toString());
    }

    @Override
    public int getItemCount() {
        if (mVideoData == null) {
            return 0;
        } else {
            return mVideoData.size();
        }
    }

    public void setVideoData(ArrayList<URL> videoData) {
        mVideoData = videoData;
        notifyDataSetChanged();
    }

    public interface VideoAdapterOnClickHandler {
        void onClick(URL singleVideoLink);
    }

    public class VideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mURLTextView;

        public VideoAdapterViewHolder(View view) {
            super(view);
            mURLTextView = view.findViewById(R.id.video_list_item);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            URL singleVideoLink = mVideoData.get(adapterPosition);
            mClickHandler.onClick(singleVideoLink);
        }
    }
}

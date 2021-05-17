package com.example.android.popularmovies.ui.trailer;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.databinding.TrailerListItemBinding;
import com.example.android.popularmovies.model.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.popularmovies.utilities.Constant.YOUTUBE_BASE_URL;
import static com.example.android.popularmovies.utilities.Constant.YOUTUBE_THUMBNAIL_BASE_URL;
import static com.example.android.popularmovies.utilities.Constant.YOUTUBE_THUMBNAIL_URL_JPG;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private List<Video> mVideos;
    private final TrailerAdapterOnClickHandler mOnClickHandler;

    public interface TrailerAdapterOnClickHandler {
        void onItemClick(String videoUrl);
    }

    public TrailerAdapter(List<Video> videos, TrailerAdapterOnClickHandler onClickHandler) {
        mVideos = videos;
        mOnClickHandler = onClickHandler;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        TrailerListItemBinding trailerItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.trailer_list_item, viewGroup, false);
        return new TrailerViewHolder(trailerItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Video video = mVideos.get(position);
        holder.bind(video);
    }

    @Override
    public int getItemCount() {
        if (null == mVideos) return 0;
        return mVideos.size();
    }

    public void addAll(List<Video> videos) {
        mVideos.clear();
        mVideos.addAll(videos);
        notifyDataSetChanged();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TrailerListItemBinding mTrailerItemBinding;

        public TrailerViewHolder(TrailerListItemBinding trailerItemBinding) {
            super(trailerItemBinding.getRoot());
            mTrailerItemBinding = trailerItemBinding;

            // Call setOnClickListener on the trailer thumbnail ImageView
            mTrailerItemBinding.ivTrailerThumbnail.setOnClickListener(this);
        }

        void bind(Video video) {
            // Get the video ID
            String videoKey = video.getKey();
            // Get the complete the trailer thumbnail url
            String trailerThumbnailUrl = YOUTUBE_THUMBNAIL_BASE_URL + videoKey +
                    YOUTUBE_THUMBNAIL_URL_JPG;

            // Load trailer thumbnail with Picasso library
            Picasso.with(itemView.getContext())
                    .load(trailerThumbnailUrl)
                    .into(mTrailerItemBinding.ivTrailerThumbnail);

            // Get the video name and set name to the TextView to display the trailer name
            String videoName = video.getName();
            mTrailerItemBinding.tvTrailerName.setText(videoName);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Video video = mVideos.get(adapterPosition);
            // Get the video key
            String videoKey = video.getKey();
            // Get the complete YouTube video url to display a trailer video
            String videoUrl = YOUTUBE_BASE_URL + videoKey;
            mOnClickHandler.onItemClick(videoUrl);
        }
    }
}

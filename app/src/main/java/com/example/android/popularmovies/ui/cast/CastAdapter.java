package com.example.android.popularmovies.ui.cast;

import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.databinding.CastListItemBinding;
import com.example.android.popularmovies.model.Cast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.popularmovies.utilities.Constant.IMAGE_BASE_URL;
import static com.example.android.popularmovies.utilities.Constant.IMAGE_FILE_SIZE;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private List<Cast> mCasts;
    public CastAdapter(List<Cast> casts) {
        mCasts = casts;
    }
    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        CastListItemBinding castItemBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.cast_list_item, viewGroup, false);
        return new CastViewHolder(castItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        Cast cast = mCasts.get(position);
        holder.bind(cast);
    }

    @Override
    public int getItemCount() {
        if (null == mCasts) return 0;
        return mCasts.size();
    }

    public void addAll(List<Cast> casts) {
        mCasts.clear();
        mCasts.addAll(casts);
        notifyDataSetChanged();
    }

    public class CastViewHolder extends RecyclerView.ViewHolder {
        CastListItemBinding mCastItemBinding;

        CastViewHolder(CastListItemBinding castItemBinding) {
            super(castItemBinding.getRoot());
            mCastItemBinding = castItemBinding;
        }

         void bind(Cast cast) {
            String profile = IMAGE_BASE_URL + IMAGE_FILE_SIZE + cast.getProfilePath();

            Picasso.with(itemView.getContext())
                    .load(profile)
                    .into(mCastItemBinding.ivCast, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) mCastItemBinding.ivCast.getDrawable())
                                    .getBitmap();
                            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(
                                    itemView.getContext().getResources(), // to determine density
                                    imageBitmap); // image to round
                            drawable.setCircular(true);
                            mCastItemBinding.ivCast.setImageDrawable(drawable);
                        }

                        @Override
                        public void onError() {
                            mCastItemBinding.ivCast.setImageResource(R.drawable.account_circle);
                        }
                    });

            mCastItemBinding.setCast(cast);
        }
    }
}

package com.sumian.common.media.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sumian.common.R;
import com.sumian.common.media.base.BaseRecyclerAdapter;
import com.sumian.common.media.bean.Image;
import com.sumian.common.media.config.ImageLoaderListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 图片列表界面适配器
 */
@SuppressWarnings("ALL")
public class ImageAdapter extends BaseRecyclerAdapter<Image> {
    private ImageLoaderListener loader;
    private boolean isSingleSelect;

    public ImageAdapter(Context context, ImageLoaderListener loader) {
        super(context, NEITHER);
        this.loader = loader;
    }

    public void setSingleSelect(boolean singleSelect) {
        isSingleSelect = singleSelect;
    }

    @Override
    public int getItemViewType(int position) {
        Image image = getItem(position);
        if (image.getId() == 0)
            return 0;
        return 1;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof ImageViewHolder) {
            ImageViewHolder h = (ImageViewHolder) holder;
        }
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        if (type == 0)
            return new CamViewHolder(mInflater.inflate(R.layout.item_list_cam, parent, false));
        return new ImageViewHolder(mInflater.inflate(R.layout.item_list_image, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Image item, int position) {
        if (item.getId() != 0) {
            ImageViewHolder h = (ImageViewHolder) holder;
            h.mCheckView.setSelected(item.isSelect());
            h.mMaskView.setVisibility(item.isSelect() ? View.VISIBLE : View.GONE);

            // Show gif mask
            h.mGifMask.setVisibility(item.getPath().toLowerCase().endsWith("gif") ?
                    View.VISIBLE : View.GONE);

            loader.displayImage(h.mImageView, item.getPath());
            h.mCheckView.setVisibility(isSingleSelect ? View.GONE : View.VISIBLE);
        }
    }

    private static class CamViewHolder extends RecyclerView.ViewHolder {
        CamViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        ImageView mCheckView;
        ImageView mGifMask;
        View mMaskView;

        ImageViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_image);
            mCheckView = itemView.findViewById(R.id.cb_selected);
            mMaskView = itemView.findViewById(R.id.lay_mask);
            mGifMask = itemView.findViewById(R.id.iv_is_gif);
        }
    }
}

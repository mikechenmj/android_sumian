package com.sumian.common.media.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.sumian.common.media.SelectImageActivity;
import com.sumian.common.media.adapter.MediaSelectImageAdapter;
import com.sumian.common.media.config.SelectOptions;


/**
 * Created by dq
 * on 16/7/18.
 * <p>
 * 动弹发布界面, 图片预览器
 * <p>
 * 提供图片预览/图片操作 返回选中图片等功能
 */

public class PicturesPreviewer extends RecyclerView implements MediaSelectImageAdapter.Callback {

    private MediaSelectImageAdapter mImageAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private RequestManager mCurImageLoader;
    private Runnable mDeleteAction;

    private OnPreviewerCallback mOnPreviewerCallback;

    public PicturesPreviewer(Context context) {
        super(context);
        init();
    }

    public PicturesPreviewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PicturesPreviewer(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setOnPreviewerCallback(OnPreviewerCallback onPreviewerCallback) {
        mOnPreviewerCallback = onPreviewerCallback;
    }

    private void init() {
        mImageAdapter = new MediaSelectImageAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        this.setLayoutManager(layoutManager);
        this.setAdapter(mImageAdapter);
        this.setOverScrollMode(View.OVER_SCROLL_NEVER);

        ItemTouchHelper.Callback callback = new TweetPicturesPreviewerItemTouchCallback(mImageAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(this);
    }

    public void set(String[] paths) {
        mImageAdapter.clear();
        for (String path : paths) {
            mImageAdapter.add(path);
        }
        mImageAdapter.notifyDataSetChanged();
        setVisibility(VISIBLE);
    }

    @Override
    public void onLoadMoreClick() {

        if (mOnPreviewerCallback != null) {
            mOnPreviewerCallback.onLoadMore();
            return;
        }

        SelectImageActivity.show(getContext(), new SelectOptions.Builder()
                .setHasCam(false)
                .setSelectCount(9)
                .setSelectedImages(mImageAdapter.getPaths())
                .setCallback(this::set).build());
    }

    @Override
    public RequestManager getImgLoader() {
        if (mCurImageLoader == null) {
            mCurImageLoader = Glide.with(getContext());
        }
        return mCurImageLoader;
    }

    @Override
    public void onStartDrag(ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onDeleteCallback() {
        if (mDeleteAction != null) {
            showEmptyView(mDeleteAction);
        }
        if (mOnPreviewerCallback != null) {
            mOnPreviewerCallback.onClearPicture();
        }
    }


    public void showEmptyView(Runnable action) {
        mDeleteAction = action;
        action.run();
    }

    public String[] getPaths() {
        return mImageAdapter.getPaths();
    }

    public interface OnPreviewerCallback {

        void onLoadMore();

        void onClearPicture();
    }
}

package com.sumian.common.media.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.sumian.common.media.SelectImageActivity;
import com.sumian.common.media.adapter.TweetSelectImageAdapter;
import com.sumian.common.media.config.SelectOptions;


/**
 * Created by JuQiu
 * on 16/7/18.
 * <p>
 * 动弹发布界面, 图片预览器
 * <p>
 * 提供图片预览/图片操作 返回选中图片等功能
 */

public class TweetPicturesPreviewer extends RecyclerView implements TweetSelectImageAdapter.Callback {

    private TweetSelectImageAdapter mImageAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private RequestManager mCurImageLoader;
    private Runnable mDeleteAction;

    public TweetPicturesPreviewer(Context context) {
        super(context);
        init();
    }

    public TweetPicturesPreviewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TweetPicturesPreviewer(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mImageAdapter = new TweetSelectImageAdapter(this);

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
    }


    public void showEmptyView(Runnable action) {
        mDeleteAction = action;
        action.run();
    }

    public String[] getPaths() {
        return mImageAdapter.getPaths();
    }
}

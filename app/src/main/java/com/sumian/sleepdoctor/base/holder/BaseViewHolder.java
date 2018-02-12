package com.sumian.sleepdoctor.base.holder;

import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.Locale;

import butterknife.ButterKnife;

/**
 * Created by jzz
 * on 2018/1/21.
 * desc:
 */

public abstract class BaseViewHolder<Item> extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String TAG = BaseViewHolder.class.getSimpleName();

    protected final RequestManager mLoader;

    protected Item mItem;

    public BaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mLoader = Glide.with(itemView.getContext());
    }

    public void initView(Item item) {
        this.mItem = item;
        itemView.setOnClickListener(this::onItemClick);
        itemView.setOnLongClickListener(v -> {
            onItemLongClick(v);
            return true;
        });
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ---------->" + v.toString());
    }

    protected void load(String url, ImageView iv) {
        load(url, null, iv);
    }

    protected void load(String url, RequestOptions options, ImageView iv) {
        load(null, url, options, iv);
    }

    protected void load(String thumbnail, String url, RequestOptions options, ImageView iv) {
        if (options == null) {
            mLoader.load(url).into(iv);
        } else {
            mLoader.load(url).apply(options).thumbnail(mLoader.load(thumbnail).apply(options)).into(iv);
        }
    }

    protected String getText(@StringRes int strId) {
        return itemView.getContext().getString(strId);
    }

    protected void setText(TextView tv, String text) {
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);
    }

    protected String formatText(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }

    protected void gone(View v) {
        gone(true, v);
    }

    protected void gone(boolean isGone, View v) {
        v.setVisibility(isGone ? View.GONE : View.VISIBLE);
    }

    protected void visible(View v) {
        gone(false, v);
    }

    protected void invisible(View v) {
        v.setVisibility(View.INVISIBLE);
    }

    protected void onItemClick(View v) {
        Log.d(TAG, "onItemClick: -------->" + v.toString());
    }

    protected boolean onItemLongClick(View v) {
        Log.e(TAG, "onItemLongClick: ----------->" + v.toString());

        return false;
    }
}

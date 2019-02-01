package com.sumian.sd.base.holder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.Locale;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by jzz
 * on 2018/1/21.
 * desc:
 */

public abstract class SdBaseViewHolder<Item> extends RecyclerView.ViewHolder {

    //private static final String TAG = SdBaseViewHolder.class.getSimpleName();

    private final RequestManager mLoader;

    protected Item mItem;

    public SdBaseViewHolder(View itemView) {
        super(itemView);
        mLoader = Glide.with(itemView.getContext());
    }

    public void initView(Item item) {
        this.mItem = item;
        //itemView.setOnClickListener(this::onItemClick);
        itemView.setOnLongClickListener(v -> {
            onItemLongClick(v);
            return true;
        });
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

    protected void load(String url, @DrawableRes int defaultIconId, ImageView iv) {
        if (TextUtils.isEmpty(url)) {
            mLoader.load(defaultIconId).into(iv);
        } else {
            mLoader.load(url).into(iv);
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
        Gone(true, v);
    }

    protected void Gone(boolean isGone, View v) {
        v.setVisibility(isGone ? View.GONE : View.VISIBLE);
    }

    protected void show(View v) {
        Gone(false, v);
    }

    protected void hide(View v) {
        v.setVisibility(View.INVISIBLE);
    }

//    protected void onItemClick(View v) {
//        // PlayLog.d(TAG, "onItemClick: -------->" + v.toString());
//    }

    protected boolean onItemLongClick(View v) {
        //  PlayLog.e(TAG, "onItemLongClick: ----------->" + v.toString());
        return false;
    }

    @SuppressWarnings("unchecked")
    protected <T> T getView(@IdRes int resId) {
        return (T) itemView.findViewById(resId);
    }
}

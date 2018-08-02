package com.sumian.common.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Glide 图片加载辅助类
 * 适配圆形图片加载情况
 */

public class ImageLoader {
    private ImageLoader() {
    }

    public static void loadImage(Activity activity, ImageView view, String url, int placeholder) {
//        loadImage(Glide.with(activity), view, url, placeholder, placeholder);
    }

    public static void loadImage(Activity activity, ImageView view, String url, int placeholder, int error) {
//        loadImage(Glide.with(activity), view, url, placeholder, error);
    }

    public static void loadImage(Fragment fragment, ImageView view, String url, int placeholder) {
//        loadImage(Glide.with(fragment), view, url, placeholder, placeholder);
    }

    public static void loadImage(Fragment fragment, ImageView view, String url, int placeholder, int error) {
//        loadImage(Glide.with(fragment), view, url, placeholder, error);
    }

    public static void loadImage(RequestManager loader, ImageView view, String url) {
//        loadImage(loader, view, url, 0);
    }

    private static void loadImage(RequestManager loader, ImageView view, String url, int placeholder) {
//        loadImage(loader, view, url, placeholder, placeholder);
    }

    private static void loadImage(RequestManager loader, ImageView view, String url, int placeholder, int error) {
        boolean isCenterCrop = false;
        if (view instanceof CircleImageView) {
            isCenterCrop = true;
        }
        loadImage(loader, view, url, placeholder, error, isCenterCrop);
    }

    private static void loadImage(RequestManager loader, final ImageView view, String url, int placeholder, int error, boolean isCenterCrop) {
        if (TextUtils.isEmpty(url)) {
            view.setImageResource(placeholder);
        } else {
            RequestOptions options = RequestOptions.placeholderOf(placeholder).error(error).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
            if (isCenterCrop) {
                options = options.centerCrop();
            }

            if (view instanceof CircleImageView) {
                RequestBuilder<Drawable> builder = loader.load(url).apply(options);
                builder.into(new DrawableImageViewTarget(view));
            } else {
                RequestBuilder builder = loader.load(url).apply(options);
                builder.into(view);
            }
        }
    }
}

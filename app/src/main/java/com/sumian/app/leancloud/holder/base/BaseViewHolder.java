package com.sumian.app.leancloud.holder.base;

import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.app.R;
import com.sumian.app.common.util.TimeUtil;
import com.sumian.app.leancloud.LeanCloudHelper;
import com.sumian.app.widget.BubbleImageView;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2018/1/3.
 * desc:
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    private static final String TAG = BaseViewHolder.class.getSimpleName();

    protected T mCacheMsg;
    protected boolean mIsLeft;
    protected int mServiceType;
    protected String mMediaUrlPath;
    private String mNewUrl;

    public BaseViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        ButterKnife.bind(this, itemView);
    }

    public abstract void initView(int serviceType, T msg);

    protected void showTime(TextView tvTimeLine, AVIMMessage msg) {
        if (getAdapterPosition() % 6 == 0) {
            tvTimeLine.setText(TimeUtil.formatMsgTime(msg.getTimestamp()));
            tvTimeLine.setVisibility(View.VISIBLE);
        } else {
            tvTimeLine.setVisibility(View.GONE);
        }
    }

    protected void showText(TextView tvContent, AVIMTextMessage msg) {
        String content;
        content = msg.getText().trim();
        if (!TextUtils.isEmpty(content) && !"".equals(content)) {
            tvContent.setText(content);
        } else {
            tvContent.setText(null);
        }
    }

    protected void formatServiceType(CircleImageView civIcon, int serviceType) {
        @DrawableRes int mipmapId;
        if (serviceType == LeanCloudHelper.SERVICE_TYPE_ONLINE_DOCTOR) {
            mipmapId = mIsLeft ? R.mipmap.advisory_doctorhead : R.mipmap.ic_chat_right_default;
        } else {
            mipmapId = mIsLeft ? R.mipmap.ic_chat_left_default : R.mipmap.ic_chat_right_default;
        }

//        Glide.with(civIcon.getContext())
//            .load(mIsLeft ? mipmapId : HwAppManager.getAccountModel().getUserInfo().getAvatar())
//            .asBitmap()
//            .diskCacheStrategy(DiskCacheStrategy.RESULT)
//            .placeholder(mipmapId)
//            .error(mipmapId)
//            .into(civIcon);
    }

    protected void showSendState(ImageView ivMsgFailed, ProgressBar loading, AVIMMessage msg) {
        //Log.e(TAG, "showSendState: ------->" + msg.getMessageStatus() + "  " + getAdapterPosition());
        switch (msg.getMessageStatus()) {
            case AVIMMessageStatusSending://发送中
                show(loading);
                hide(ivMsgFailed);
                break;
            case AVIMMessageStatusFailed://发送失败
                show(ivMsgFailed);
                hide(loading);
                break;
            case AVIMMessageStatusNone://未知状态
            case AVIMMessageStatusRecalled://被拒收
            case AVIMMessageStatusSent://发送成功
            case AVIMMessageStatusReceipt://已被接收成功
            default:
                hide(loading, ivMsgFailed);
                break;
        }
    }

    protected void showImage(BubbleImageView bubbleImageView, TextView tvLoadingIndicator, AVIMImageMessage msg) {

        String localFilePath = msg.getLocalFilePath();
        if (TextUtils.isEmpty(localFilePath)) {
            localFilePath = msg.getFileUrl();
        }
        this.mMediaUrlPath = localFilePath;

        if (TextUtils.isEmpty(localFilePath)) {
            runOnUiThread(() -> itemView.setVisibility(View.GONE));
            return;
        }

        try {//由于 okHttp 拦截器当中,直接忽略了 url 大小写.但是 leancloud消息中的返回的host 却是大小写区分的,故要转一下
            URI uri = new URI(localFilePath);
            String oldHost = uri.getHost();
            if (!TextUtils.isEmpty(oldHost)) {
                String newHost = oldHost.toLowerCase();
                this.mNewUrl = localFilePath.replace(oldHost, newHost);
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

//        MyGlideModule.addProgressListener(mNewUrl, (url, progress) -> {
//            // Log.e(TAG, "showImage: ---------->progress=" + String.format("%03d", progress) + "  url=" + url);
//            runOnUiThread(() -> {
//                tvLoadingIndicator.setText(String.format(Locale.getDefault(), "%d%s", progress, "%"));
//                tvLoadingIndicator.setVisibility(progress >= 98 ? View.GONE : View.VISIBLE);
//            });
//        });
//
//        Glide.with(bubbleImageView.getContext())
//            .load(mMediaUrlPath)
//            .asBitmap()
//            .priority(Priority.HIGH)
//            .diskCacheStrategy(DiskCacheStrategy.ALL)
//            //.thumbnail(0.2f)
//            .placeholder(mIsLeft ? R.color.colorP : R.color.colorP)
//            .error(mIsLeft ? R.mipmap.ic_advisory_chatbubble_mask_l : R.mipmap.ic_advisory_chatbubble_mask_r)
//            .override((int) bubbleImageView.getContext().getResources().getDimension(R.dimen.space_100),
//                (int) bubbleImageView.getContext().getResources().getDimension(R.dimen.space_160))
//            //.crossFade()
//            .into(new GlideDrawableImageViewTarget(bubbleImageView) {
//
//                @Override
//                public void onLoadStarted(Drawable placeholder) {
//                    super.onLoadStarted(placeholder);
//                    runOnUiThread(() -> {
//                        tvLoadingIndicator.setText(R.string.percent_zero);
//                        tvLoadingIndicator.setVisibility(View.VISIBLE);
//                    });
//                    Log.e(TAG, "onLoadStarted: ----------->");
//                }
//
//                @Override
//                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
//                    super.onResourceReady(resource, glideAnimation);
//                    Log.e(TAG, "onResourceReady: --------->");
//                    runOnUiThread(() -> bubbleImageView.setImageDrawable(resource));
//                    removeProgressListener(tvLoadingIndicator);
//                }
//
//                @Override
//                public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                    super.onLoadFailed(e, errorDrawable);
//                    Log.e(TAG, "onLoadFailed: -------->");
//                    removeProgressListener(tvLoadingIndicator);
//                    //bubbleImageView.setImageDrawable(errorDrawable);
//                    showImage(bubbleImageView, tvLoadingIndicator, msg);
//                }
//
//            }.getView());
    }

    protected void showVoiceAndDuration(TextView tvVoiceDuration, AVIMAudioMessage msg) {
        String localFilePath = msg.getLocalFilePath();
        if (TextUtils.isEmpty(localFilePath)) {
            localFilePath = msg.getFileUrl();
        }
        this.mMediaUrlPath = localFilePath;
        tvVoiceDuration.setText(String.format(Locale.getDefault(), "%02d%s%s", Math.round(msg.getDuration() + 0.5), " ", "''"));
    }

    private void removeProgressListener(TextView tvLoadingIndicator) {
        runOnUiThread(() -> {
            tvLoadingIndicator.setText(R.string.percent_zero);
            tvLoadingIndicator.setVisibility(View.GONE);
        });
        // MyGlideModule.removeProgressListener(mNewUrl);
    }

    private void hide(View... view) {
        for (View v : view) {
            v.setVisibility(View.INVISIBLE);
        }
    }

    private void show(View... view) {
        for (View v : view) {
            v.setVisibility(View.VISIBLE);
        }
    }

    protected void runOnUiThread(Runnable run) {
        itemView.post(run);
    }
}

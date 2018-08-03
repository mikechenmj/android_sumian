package com.sumian.hw.leancloud.holder.base;

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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.bumptech.glide.Glide;
import com.sumian.common.image.ImageLoader;
import com.sumian.hw.common.util.TimeUtil;
import com.sumian.hw.leancloud.HwLeanCloudHelper;
import com.sumian.hw.widget.BubbleImageView;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;

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
        if (serviceType == HwLeanCloudHelper.SERVICE_TYPE_ONLINE_DOCTOR) {
            mipmapId = mIsLeft ? R.mipmap.advisory_doctorhead : R.mipmap.ic_chat_right_default;
        } else {
            mipmapId = mIsLeft ? R.mipmap.ic_chat_left_default : R.mipmap.ic_chat_right_default;
        }

        Glide.with(civIcon.getContext())
                .load(mIsLeft ? mipmapId : AppManager.getAccountViewModel().getUserInfo().getAvatar())
                .into(civIcon);
        //ImageLoader.loadImage(mIsLeft ? mipmapId : AppManager.getAccountViewModel().getUserInfo().getAvatar(), civIcon,mipmapId,mipmapId);
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

        tvLoadingIndicator.setText(R.string.percent_zero);
        tvLoadingIndicator.setVisibility(View.VISIBLE);

        bubbleImageView.setVisibility(View.VISIBLE);

        msg.getAVFile().getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, AVException e) {

            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer integer) {
                runOnUiThread(() -> {
                    tvLoadingIndicator.setText(String.format(Locale.getDefault(), "%d%s", integer, "%"));
                    tvLoadingIndicator.setVisibility(integer >= 98 ? View.GONE : View.VISIBLE);
                });
            }
        });

        ImageLoader.loadImage(mMediaUrlPath, bubbleImageView, mIsLeft ? R.mipmap.ic_advisory_chatbubble_mask_l : R.mipmap.ic_advisory_chatbubble_mask_r, mIsLeft ? R.mipmap.ic_advisory_chatbubble_mask_l : R.mipmap.ic_advisory_chatbubble_mask_r);
    }

    protected void showVoiceAndDuration(TextView tvVoiceDuration, AVIMAudioMessage msg) {
        String localFilePath = msg.getLocalFilePath();
        if (TextUtils.isEmpty(localFilePath)) {
            localFilePath = msg.getFileUrl();
        }
        this.mMediaUrlPath = localFilePath;
        tvVoiceDuration.setText(String.format(Locale.getDefault(), "%02d%s%s", Math.round(msg.getDuration() + 0.5), " ", "''"));
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

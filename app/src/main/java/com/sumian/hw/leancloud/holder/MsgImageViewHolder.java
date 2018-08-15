package com.sumian.hw.leancloud.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.sumian.hw.leancloud.HwLeanCloudHelper;
import com.sumian.hw.leancloud.holder.base.BaseViewHolder;
import com.sumian.hw.widget.BubbleImageView;
import com.sumian.sd.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2018/1/4.
 * desc:
 */

public class MsgImageViewHolder extends BaseViewHolder<AVIMImageMessage> implements View.OnClickListener {

    private static final String TAG = MsgImageViewHolder.class.getSimpleName();

    TextView mTvTimeLine;
    CircleImageView mIvIcon;
    BubbleImageView mBivImage;
    TextView mTvLoadingIndicator;
    ImageView mIvMsgFailed;
    ProgressBar mLoading;

    public MsgImageViewHolder(ViewGroup parent, boolean isLeft) {
        super(parent, isLeft ? R.layout.hw_lay_item_left_image_chat : R.layout.hw_lay_item_right_image_chat);
        this.mIsLeft = isLeft;

        mTvTimeLine = itemView.findViewById(R.id.tv_time_line);
        mIvIcon = itemView.findViewById(R.id.iv_icon);
        mBivImage = itemView.findViewById(R.id.biv_image);
        mTvLoadingIndicator = itemView.findViewById(R.id.tv_loading_indicator);
        mIvMsgFailed = itemView.findViewById(R.id.iv_msg_failed);
        mLoading = itemView.findViewById(R.id.loading);
    }

    @Override
    public void initView(int serviceType, AVIMImageMessage msg) {
        this.mCacheMsg = msg;
        mIvMsgFailed.setOnClickListener(this);
        showTime(mTvTimeLine, msg);
        formatServiceType(mIvIcon, serviceType);
        showImage(mBivImage, mTvLoadingIndicator, msg);
        showSendState(mIvMsgFailed, mLoading, msg);
        itemView.setOnClickListener(v -> {
            //ImageGalleryActivity.show(v.getContext(), mMediaUrlPath);
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.biv_image) {
            showImage(mBivImage, mTvLoadingIndicator, mCacheMsg);
        } else if (i == R.id.iv_msg_failed) {
            HwLeanCloudHelper.sendImageMsg(mServiceType, mMediaUrlPath);
        }
    }
}

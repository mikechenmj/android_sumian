package com.sumian.app.leancloud.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.sumian.app.R;
import com.sumian.app.leancloud.LeanCloudHelper;
import com.sumian.app.leancloud.holder.base.BaseViewHolder;
import com.sumian.app.widget.BubbleImageView;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2018/1/4.
 * desc:
 */

public class MsgImageViewHolder extends BaseViewHolder<AVIMImageMessage> implements View.OnClickListener {

    private static final String TAG = MsgImageViewHolder.class.getSimpleName();

    @BindView(R.id.tv_time_line)
    TextView mTvTimeLine;
    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;
    @BindView(R.id.biv_image)
    BubbleImageView mBivImage;
    @BindView(R.id.tv_loading_indicator)
    TextView mTvLoadingIndicator;
    @BindView(R.id.iv_msg_failed)
    ImageView mIvMsgFailed;
    @BindView(R.id.loading)
    ProgressBar mLoading;

    public MsgImageViewHolder(ViewGroup parent, boolean isLeft) {
        super(parent, isLeft ? R.layout.hw_lay_item_left_image_chat : R.layout.hw_lay_item_right_image_chat);
        this.mIsLeft = isLeft;
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
        switch (v.getId()) {
            case R.id.biv_image://点击再次加载
                showImage(mBivImage, mTvLoadingIndicator, mCacheMsg);
                break;
            case R.id.iv_msg_failed://自己发送失败,再次发送
                LeanCloudHelper.sendImageMsg(mServiceType, mMediaUrlPath);
                break;
            default:
                break;
        }
    }

}

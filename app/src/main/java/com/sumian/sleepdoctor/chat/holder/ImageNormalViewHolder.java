package com.sumian.sleepdoctor.chat.holder;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.bumptech.glide.request.RequestOptions;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.chat.base.BaseChatViewHolder;
import com.sumian.sleepdoctor.chat.widget.BubbleImageView;
import com.sumian.sleepdoctor.chat.widget.MsgSendErrorView;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class ImageNormalViewHolder extends BaseChatViewHolder<AVIMImageMessage> {

    private static final String TAG = ImageNormalViewHolder.class.getSimpleName();

    @BindView(R.id.tv_time_line)
    TextView mTvTimeLine;

    @BindView(R.id.tv_label)
    TextView mTvLabel;
    @BindView(R.id.tv_nickname)
    TextView mTvNickname;

    @BindView(R.id.msg_send_error_view)
    MsgSendErrorView mMsgSendErrorView;

    @BindView(R.id.biv_image)
    BubbleImageView mBivImage;

    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;

    private String mMediaUrlPath;

    public ImageNormalViewHolder(ViewGroup parent, boolean isLeft, int leftLayoutId, int rightLayoutId) {
        super(parent, isLeft, leftLayoutId, rightLayoutId);
    }

    @Override
    public void initView(AVIMImageMessage avimImageMessage) {
        super.initView(avimImageMessage);

        updateUserProfile(avimImageMessage.getFrom(), mGroupId, mTvLabel, mTvNickname, mIvIcon);

        String thumbnailUrl = avimImageMessage.getAVFile().getThumbnailUrl(true, 100, 100, 50, "png");

        String localFilePath = avimImageMessage.getLocalFilePath();

        if (TextUtils.isEmpty(localFilePath)) {
            localFilePath = avimImageMessage.getFileUrl();
        }

        RequestOptions options = new RequestOptions();
        options.error(mIsLeft ? R.mipmap.group_photobubble_shadow : R.mipmap.group_photobubble_shadow).getOptions();

        this.mMediaUrlPath = localFilePath;

        mLoader.load(localFilePath).apply(options).thumbnail(mLoader.load(thumbnailUrl).apply(options)).into(mBivImage);
    }

    @OnClick({R.id.biv_image})
    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}

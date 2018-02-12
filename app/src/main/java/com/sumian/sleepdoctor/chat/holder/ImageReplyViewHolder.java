package com.sumian.sleepdoctor.chat.holder;

import android.support.text.emoji.widget.EmojiAppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.chat.base.BaseChatViewHolder;
import com.sumian.sleepdoctor.chat.widget.MsgSendErrorView;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class ImageReplyViewHolder extends BaseChatViewHolder<AVIMImageMessage> {

    @BindView(R.id.tv_time_line)
    TextView mTvTimeLine;
    @BindView(R.id.tv_label)
    TextView mTvLabel;
    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.msg_send_error_view)
    MsgSendErrorView mMsgSendErrorView;
    @BindView(R.id.tv_reply)
    EmojiAppCompatTextView mTvReply;
    @BindView(R.id.biv_image)
    QMUIRadiusImageView mBivImage;
    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;

    public ImageReplyViewHolder(ViewGroup parent, boolean isLeft, int leftLayoutId, int rightLayoutId) {
        super(parent, isLeft, leftLayoutId, rightLayoutId);
    }

    @Override
    public void initView(AVIMImageMessage avimImageMessage) {
        super.initView(avimImageMessage);
        updateUserProfile(avimImageMessage.getFrom(), mGroupId, mTvLabel, mTvNickname, mIvIcon);
        updateReplyContent(avimImageMessage, mTvReply);
        findMediaUrlAndUpdate(avimImageMessage, mBivImage);
    }

    @OnClick({R.id.biv_image, R.id.iv_icon})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_icon:
                showOtherUserProfile(v);
                break;
            default:
                break;
        }
    }
}

package com.sumian.sleepdoctor.chat.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.chat.base.BaseChatViewHolder;
import com.sumian.sleepdoctor.chat.widget.MsgSendErrorView;
import com.sumian.sleepdoctor.widget.shapeImageView.GlideImageView;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class ImageQuestionViewHolder extends BaseChatViewHolder<AVIMImageMessage> {

    @BindView(R.id.tv_time_line)
    TextView mTvTimeLine;

    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.tv_label)
    TextView mTvLabel;
    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;

    @BindView(R.id.biv_image)
    GlideImageView mBivImage;

    @BindView(R.id.msg_send_error_view)
    MsgSendErrorView mMsgSendErrorView;

    public ImageQuestionViewHolder(ViewGroup parent, boolean isLeft, int leftLayoutId, int rightLayoutId) {
        super(parent, isLeft, leftLayoutId, rightLayoutId);
    }


    @Override
    public void initView(AVIMImageMessage avimImageMessage) {
        super.initView(avimImageMessage);

        updateUserProfile(avimImageMessage.getFrom(), mGroupId, mTvLabel, mTvNickname, mIvIcon);
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

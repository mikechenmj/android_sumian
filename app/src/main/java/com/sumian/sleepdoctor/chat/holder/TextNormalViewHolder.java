package com.sumian.sleepdoctor.chat.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.chat.base.BaseChatViewHolder;
import com.sumian.sleepdoctor.chat.widget.MsgSendErrorView;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2018/1/4.
 * desc:
 */

public class TextNormalViewHolder extends BaseChatViewHolder<AVIMTextMessage> {

    private static final String TAG = TextNormalViewHolder.class.getSimpleName();

    @BindView(R.id.tv_time_line)
    TextView mTvTimeLine;

    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.tv_label)
    TextView mTvLabel;

    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;

    @BindView(R.id.tv_content)
    TextView mTvContent;

    @BindView(R.id.msg_send_error_view)
    MsgSendErrorView mMsgSendErrorView;

    public TextNormalViewHolder(ViewGroup parent, boolean isLeft, int leftLayoutId, int rightLayoutId) {
        super(parent, isLeft, leftLayoutId, rightLayoutId);
    }

    @Override
    public void initView(AVIMTextMessage avimTextMessage) {
        super.initView(avimTextMessage);

        updateUserProfile(avimTextMessage.getFrom(), mGroupId, mTvLabel, mTvNickname, mIvIcon);
        updateText(avimTextMessage, mTvContent);
    }

    @OnClick({R.id.iv_msg_failed})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_msg_failed://发送失败,再次发送
                break;
            default:
                break;
        }
    }

}

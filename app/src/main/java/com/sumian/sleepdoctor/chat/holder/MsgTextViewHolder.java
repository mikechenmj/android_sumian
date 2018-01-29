package com.sumian.sleepdoctor.chat.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;
import com.sumian.sleepdoctor.chat.widget.MsgSendErrorView;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2018/1/4.
 * desc:
 */

public class MsgTextViewHolder extends BaseViewHolder<AVIMTextMessage> {

    @BindView(R.id.tv_time_line)
    TextView mTvTimeLine;

    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.tv_label)
    TextView mTvLabel;

    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;

    @BindView(R.id.tv_msg)
    TextView mTvMsg;

    @BindView(R.id.msg_send_error_view)
    MsgSendErrorView mMsgSendErrorView;

    private boolean mIsLeft;

    public MsgTextViewHolder(ViewGroup parent, boolean isLeft) {
        super(View.inflate(parent.getContext(), isLeft ? R.layout.lay_item_left_text_chat : R.layout.lay_item_right_text_chat, parent));
        this.mIsLeft = isLeft;
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

    @Override
    protected void onItemClick(View v) {
        super.onItemClick(v);
    }
}

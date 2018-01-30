package com.sumian.sleepdoctor.chat.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;
import com.sumian.sleepdoctor.chat.widget.MsgSendErrorView;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2018/1/4.
 * desc:
 */

public class TextNormalViewHolder extends BaseViewHolder<AVIMTextMessage> {

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

    public TextNormalViewHolder(ViewGroup parent, boolean isLeft) {
        super(LayoutInflater.from(parent.getContext()).inflate(isLeft ? R.layout.lay_item_left_text_nomal_chat : R.layout.lay_item_right_text_chat, parent, false));
        this.mIsLeft = isLeft;
    }

    @Override
    public void initView(AVIMTextMessage avimTextMessage) {
        super.initView(avimTextMessage);

        String text = avimTextMessage.getText();

        mTvMsg.setText(text);


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

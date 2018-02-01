package com.sumian.sleepdoctor.chat.holder;

import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;
import com.sumian.sleepdoctor.chat.widget.MsgSendErrorView;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class VoiceNormalViewHolder extends BaseViewHolder<AVIMAudioMessage> {

    @BindView(R.id.tv_time_line)
    TextView mTvTimeLine;

    @BindView(R.id.tv_label)
    TextView mTvLabel;
    @BindView(R.id.tv_nickname)
    TextView mTvNickname;

    @BindView(R.id.msg_send_error_view)
    MsgSendErrorView mMsgSendErrorView;

    @BindView(R.id.voice_dot)
    View mVoiceDot;
    @BindView(R.id.tv_voice_duration)
    TextView mTvVoiceDuration;

    @BindView(R.id.biv_image)
    ImageView mBivImage;

    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;

    private boolean mIsLeft;

    public VoiceNormalViewHolder(ViewGroup parent, boolean isLeft) {
        super(LayoutInflater.from(parent.getContext()).inflate(isLeft ? R.layout.lay_item_left_voice_normal_chat : R.layout.lay_item_right_voice_normal_chat, parent, false));
        this.mIsLeft = isLeft;
    }

    @Override
    public void initView(AVIMAudioMessage avimAudioMessage) {
        super.initView(avimAudioMessage);


    }

    public void bindGroupId(int groupId) {

    }

    @OnClick({R.id.biv_image})
    @Override
    public void onClick(View v) {
        super.onClick(v);

        AnimationDrawable drawable = (AnimationDrawable) mBivImage.getDrawable();
        drawable.start();
    }
}

package com.sumian.sleepdoctor.chat.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.chat.base.BaseChatViewHolder;
import com.sumian.sleepdoctor.chat.widget.MsgSendErrorView;
import com.sumian.sleepdoctor.chat.widget.VoiceProgress;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class VoiceQuestionViewHolder extends BaseChatViewHolder<AVIMAudioMessage> {

    @BindView(R.id.tv_time_line)
    TextView mTvTimeLine;
    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.tv_label)
    TextView mTvLabel;
    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;
    @BindView(R.id.biv_image)
    VoiceProgress mBivImage;
    @BindView(R.id.voice_dot)
    View mVoiceDot;
    @BindView(R.id.tv_voice_duration)
    TextView mTvVoiceDuration;
    @BindView(R.id.msg_send_error_view)
    MsgSendErrorView mMsgSendErrorView;

    public VoiceQuestionViewHolder(ViewGroup parent, boolean isLeft, int leftLayoutId, int rightLayoutId) {
        super(parent, isLeft, leftLayoutId, rightLayoutId);
    }

    @Override
    public void initView(AVIMAudioMessage avimAudioMessage) {
        super.initView(avimAudioMessage);
        updateUserProfile(avimAudioMessage.getFrom(), mGroupId, mTvLabel, mTvNickname, mIvIcon);
    }

}

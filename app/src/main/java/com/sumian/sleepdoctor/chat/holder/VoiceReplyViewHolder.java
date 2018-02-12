package com.sumian.sleepdoctor.chat.holder;

import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.chat.base.BaseChatViewHolder;
import com.sumian.sleepdoctor.chat.player.VoicePlayer;
import com.sumian.sleepdoctor.chat.widget.MsgSendErrorView;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class VoiceReplyViewHolder extends BaseChatViewHolder<AVIMAudioMessage> implements VoicePlayer.onPlayStatusListener {

    @BindView(R.id.tv_time_line)
    TextView mTvTimeLine;

    @BindView(R.id.tv_label)
    TextView mTvLabel;
    @BindView(R.id.tv_nickname)
    TextView mTvNickname;

    @BindView(R.id.msg_send_error_view)
    MsgSendErrorView mMsgSendErrorView;

    @BindView(R.id.tv_reply)
    TextView mTvReplyMsg;

    @BindView(R.id.voice_dot)
    View mVoiceDot;
    @BindView(R.id.tv_voice_duration)
    TextView mTvVoiceDuration;

    @BindView(R.id.biv_image)
    ImageView mBivImage;

    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;

    public VoiceReplyViewHolder(ViewGroup parent, boolean isLeft, int leftLayoutId, int rightLayoutId) {
        super(parent, isLeft, leftLayoutId, rightLayoutId);
    }

    @Override
    public void initView(AVIMAudioMessage avimAudioMessage) {
        super.initView(avimAudioMessage);
        updateUserProfile(avimAudioMessage.getFrom(), mGroupId, mTvLabel, mTvNickname, mIvIcon);
        updateDuration(avimAudioMessage, mTvVoiceDuration, mVoiceDot);
        updateReplyContent(avimAudioMessage, mTvReplyMsg);
    }

    @OnClick({R.id.biv_image, R.id.iv_icon})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_icon:
                showOtherUserProfile(v);
                break;
            case R.id.biv_image:

                mVoiceDot.setVisibility(View.INVISIBLE);
                String conversationId = mItem.getConversationId();
                AppManager.getChatEngine().getAVIMConversation(conversationId).read();

                String localFilePath = mItem.getLocalFilePath();
                if (TextUtils.isEmpty(localFilePath)) {
                    localFilePath = mItem.getFileUrl();
                }
                AppManager.getVoicePlayer().play(localFilePath, getAdapterPosition()).setStatusListener(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void play() {
        mVoiceDot.setVisibility(View.INVISIBLE);
        AnimationDrawable drawable = (AnimationDrawable) mBivImage.getDrawable();
        drawable.setVisible(true, true);
        drawable.start();
    }

    @Override
    public void stop() {
        mVoiceDot.setVisibility(View.INVISIBLE);
        AnimationDrawable drawable = (AnimationDrawable) mBivImage.getDrawable();
        drawable.stop();
        drawable.setVisible(true, true);
    }
}

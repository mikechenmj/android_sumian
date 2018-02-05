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

public class VoiceNormalViewHolder extends BaseChatViewHolder<AVIMAudioMessage> implements VoicePlayer.onPlayStatusListener {

    private static final String TAG = VoiceNormalViewHolder.class.getSimpleName();

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

    public VoiceNormalViewHolder(ViewGroup parent, boolean isLeft, int leftLayoutId, int rightLayoutId) {
        super(parent, isLeft, leftLayoutId, rightLayoutId);
    }

    @Override
    public void initView(AVIMAudioMessage avimAudioMessage) {
        super.initView(avimAudioMessage);
        updateUserProfile(avimAudioMessage.getFrom(), mGroupId, mTvLabel, mTvNickname, mIvIcon);

        double duration = avimAudioMessage.getDuration();

        mTvVoiceDuration.setText(String.format("%s''", String.valueOf(duration)));

        mVoiceDot.setVisibility(mIsLeft ? View.VISIBLE : View.INVISIBLE);
    }

    @OnClick({R.id.biv_image})
    @Override
    public void onClick(View v) {
        super.onClick(v);

        mVoiceDot.setVisibility(View.INVISIBLE);
        String conversationId = mItem.getConversationId();
        AppManager.getChatEngine().getAVIMConversation(conversationId).read();

        String localFilePath = mItem.getLocalFilePath();
        if (TextUtils.isEmpty(localFilePath)) {
            localFilePath = mItem.getFileUrl();
        }

        AppManager.getVoicePlayer().play(localFilePath, getAdapterPosition()).setStatusListener(this);
    }

    @Override
    public void play() {
        mVoiceDot.setVisibility(View.INVISIBLE);
        AnimationDrawable drawable = (AnimationDrawable) mBivImage.getDrawable();
        drawable.start();
    }

    @Override
    public void stop() {
        mVoiceDot.setVisibility(View.INVISIBLE);
        AnimationDrawable drawable = (AnimationDrawable) mBivImage.getDrawable();
        drawable.stop();
    }
}

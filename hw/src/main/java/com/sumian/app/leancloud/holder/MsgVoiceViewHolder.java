package com.sumian.app.leancloud.holder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.sumian.app.R;
import com.sumian.app.app.AppManager;
import com.sumian.app.leancloud.LeanCloudHelper;
import com.sumian.app.leancloud.holder.base.BaseViewHolder;
import com.sumian.app.leancloud.player.VoicePlayer;
import com.sumian.app.widget.VoiceProgress;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2018/1/4.
 * desc:
 */

public class MsgVoiceViewHolder extends BaseViewHolder<AVIMAudioMessage> implements VoicePlayer.onPlayStatusListener {

    private static final String TAG = MsgVoiceViewHolder.class.getSimpleName();

    private TextView mTvTimeLine;
    private CircleImageView mIvIcon;
    private VoiceProgress mVpProgress;
    private TextView mTvVoiceDuration;
    private ImageView mIvMsgFailed;
    private ProgressBar mLoading;

    public MsgVoiceViewHolder(ViewGroup parent, boolean isLeft) {
        super(parent, isLeft ? R.layout.hw_lay_item_left_voice_chat : R.layout.hw_lay_item_right_voice_chat);
        this.mIsLeft = isLeft;

        mTvTimeLine = itemView.findViewById(R.id.tv_time_line);
        mIvIcon = itemView.findViewById(R.id.iv_icon);
        mVpProgress = itemView.findViewById(R.id.vp_progress);
        mTvVoiceDuration = itemView.findViewById(R.id.tv_voice_duration);
        mIvMsgFailed = itemView.findViewById(R.id.iv_msg_failed);
        mLoading = itemView.findViewById(R.id.loading);

    }

    @Override
    public void initView(int serviceType, AVIMAudioMessage msg) {
        this.mCacheMsg = msg;
        itemView.setOnClickListener(v -> AppManager.getVoicePlayer().play(mMediaUrlPath, getAdapterPosition()).setStatusListener(this));
        mIvMsgFailed.setOnClickListener(v -> LeanCloudHelper.sendVoiceMsg(serviceType, mMediaUrlPath));
        showTime(mTvTimeLine, msg);
        formatServiceType(mIvIcon, serviceType);
        showVoiceAndDuration(mTvVoiceDuration, msg);
        showSendState(mIvMsgFailed, mLoading, msg);
    }


    @Override
    public void play() {//开始播放声音动画
        mVpProgress.play();
    }

    @Override
    public void stop() {//暂停声音动画
        mVpProgress.stop();
    }

}

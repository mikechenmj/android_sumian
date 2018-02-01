package com.sumian.sleepdoctor.chat.holder;

import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;
import com.sumian.sleepdoctor.chat.player.VoicePlayer;
import com.sumian.sleepdoctor.chat.widget.MsgSendErrorView;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class VoiceNormalViewHolder extends BaseViewHolder<AVIMAudioMessage> implements VoicePlayer.onPlayStatusListener {

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

    private boolean mIsLeft;

    public VoiceNormalViewHolder(ViewGroup parent, boolean isLeft) {
        super(LayoutInflater.from(parent.getContext()).inflate(isLeft ? R.layout.lay_item_left_voice_normal_chat : R.layout.lay_item_right_voice_normal_chat, parent, false));
    }

    @Override
    public void initView(AVIMAudioMessage avimAudioMessage) {
        super.initView(avimAudioMessage);

        double duration = avimAudioMessage.getDuration();

        mTvVoiceDuration.setText(String.valueOf(duration) + "''");

        mVoiceDot.setVisibility(mIsLeft ? View.VISIBLE : View.INVISIBLE);

    }

    public void bindGroupId(int groupId) {

        AppManager
                .getHttpService()
                .getLeancloudGroupUsers(mItem.getFrom(), groupId)
                .enqueue(new BaseResponseCallback<String>() {

                    @Override
                    protected void onSuccess(String response) {

                        Log.e(TAG, "onSuccess: -------2----->" + mItem.getFrom() + "   " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String json = jsonObject.getString(mItem.getFrom());

                            UserProfile tempUserProfile = JSON.parseObject(json, UserProfile.class);

                            if (tempUserProfile != null) {
                                mTvNickname.setText(tempUserProfile.nickname);
                                formatRoleLabel(tempUserProfile.role, mTvLabel);
                                formatRoleAvatar(tempUserProfile.role, tempUserProfile.avatar, mIvIcon);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    protected void onFailure(String error) {
                        formatRoleLabel(0, mTvLabel);
                        formatRoleAvatar(0, null, mIvIcon);
                    }
                });

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

package com.sumian.sleepdoctor.chat.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageInterval;
import com.avos.avoscloud.im.v2.AVIMMessageQueryDirection;
import com.avos.avoscloud.im.v2.AVIMMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.qmuiteam.qmui.span.QMUIAlignMiddleImageSpan;
import com.qmuiteam.qmui.span.QMUIMarginImageSpan;
import com.sumian.common.media.Image;
import com.sumian.common.media.ImageGalleryActivity;
import com.sumian.common.media.MediaUtil;
import com.sumian.common.media.SelectOptions;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;
import com.sumian.sleepdoctor.chat.engine.ChatEngine;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.pager.activity.OtherUserProfileActivity;
import com.sumian.sleepdoctor.widget.shapeImageView.GlideImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/31.
 * desc:
 */

public abstract class BaseChatViewHolder<Item> extends BaseViewHolder<Item> {

    private static final String TAG = BaseChatViewHolder.class.getSimpleName();

    protected int mRole;
    protected int mGroupId;
    protected boolean mIsLeft;

    protected UserProfile mUserProfile;

    protected OnReplayListener<Item> mOnReplayListener;

    protected String mMediaUrlPath;


    public BaseChatViewHolder(ViewGroup parent, boolean isLeft, @LayoutRes int leftLayoutId, @LayoutRes int rightLayoutId) {
        super(LayoutInflater.from(parent.getContext()).inflate(isLeft ? leftLayoutId : rightLayoutId, parent, false));
        this.mIsLeft = isLeft;
    }

    public BaseChatViewHolder<Item> bindGroupRole(int role) {
        this.mRole = role;
        return this;
    }

    public BaseChatViewHolder<Item> bindGroupId(int groupId) {
        this.mGroupId = groupId;
        return this;
    }

    public void setOnReplayListener(OnReplayListener<Item> onReplayListener) {
        mOnReplayListener = onReplayListener;
    }

    protected void updateImageText(AVIMTextMessage msg, TextView tvContent) {
        String text = msg.getText();

        Drawable drawable = itemView.getResources().getDrawable(R.mipmap.ic_group_chatbubble_icon_label);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth() + 10, drawable.getIntrinsicHeight() + 10);

        QMUIMarginImageSpan imgSpan = new QMUIMarginImageSpan(drawable, QMUIAlignMiddleImageSpan.ALIGN_MIDDLE, 0, 0);
        SpannableString spannableString = new SpannableString("[icon]  " + text);
        spannableString.setSpan(imgSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvContent.setText(spannableString);
    }


    protected void findMediaUrlAndUpdate(AVIMTypedMessage msg, ImageView qriv) {
        String thumbnailUrl = null;
        String localFilePath = null;
        switch (msg.getMessageType()) {
            case AVIMMessageType.IMAGE_MESSAGE_TYPE:
                thumbnailUrl = ((AVIMImageMessage) msg).getAVFile().getThumbnailUrl(true, 100, 100, 50, "png");
                localFilePath = ((AVIMImageMessage) msg).getLocalFilePath();

                if (TextUtils.isEmpty(localFilePath)) {
                    localFilePath = ((AVIMImageMessage) msg).getFileUrl();
                }

                break;
            case AVIMMessageType.AUDIO_MESSAGE_TYPE:
                thumbnailUrl = ((AVIMAudioMessage) msg).getAVFile().getThumbnailUrl(true, 100, 100, 50, "png");
                localFilePath = ((AVIMAudioMessage) msg).getLocalFilePath();

                if (TextUtils.isEmpty(localFilePath)) {
                    localFilePath = ((AVIMAudioMessage) msg).getFileUrl();
                }

                break;
            default:
                break;
        }

        RequestOptions options = new RequestOptions();
        options.error(mIsLeft ? R.mipmap.ic_group_photobubble_shadow : R.mipmap.ic_group_photobubble_shadow).getOptions();

        this.mMediaUrlPath = localFilePath;

        mLoader.load(localFilePath).apply(options).thumbnail(mLoader.load(thumbnailUrl).apply(options)).into(qriv);
    }

    protected void findMediaUrlAndLoadShape(AVIMTypedMessage msg, GlideImageView glideImageView) {
        String thumbnailUrl = null;
        String localFilePath = null;
        switch (msg.getMessageType()) {
            case AVIMMessageType.IMAGE_MESSAGE_TYPE:
                thumbnailUrl = ((AVIMImageMessage) msg).getAVFile().getThumbnailUrl(true, 100, 100, 50, "png");
                localFilePath = ((AVIMImageMessage) msg).getLocalFilePath();

                if (TextUtils.isEmpty(localFilePath)) {
                    localFilePath = ((AVIMImageMessage) msg).getFileUrl();
                }

                break;
            case AVIMMessageType.AUDIO_MESSAGE_TYPE:
                thumbnailUrl = ((AVIMAudioMessage) msg).getAVFile().getThumbnailUrl(true, 100, 100, 50, "png");
                localFilePath = ((AVIMAudioMessage) msg).getLocalFilePath();

                if (TextUtils.isEmpty(localFilePath)) {
                    localFilePath = ((AVIMAudioMessage) msg).getFileUrl();
                }

                break;
            default:
                break;
        }

        this.mMediaUrlPath = localFilePath;

        RequestOptions options = glideImageView.requestOptions(R.color.trans)
                .fitCenter()
                .skipMemoryCache(false) // 不跳过内存缓存
                .diskCacheStrategy(DiskCacheStrategy.ALL); // 缓存到SDCard中

        glideImageView.getImageLoader().requestBuilder(localFilePath, options)
                .thumbnail(mLoader.load(thumbnailUrl).apply(options))//加载缩略图
                //.transition(DrawableTransitionOptions.withCrossFade()) // 动画渐变加载
                .into(glideImageView);

        glideImageView.setOnClickListener(v -> {
            List<Image> mSelectedImage = new ArrayList<>();
            Image image = new Image();
            image.setPath(mMediaUrlPath);
            mSelectedImage.add(image);
            ImageGalleryActivity.show(v.getContext(), new SelectOptions.Builder().setSelectedImages(MediaUtil.toArray(mSelectedImage)).build(), 0);
        });
    }

    protected void updateDuration(AVIMAudioMessage msg, TextView tvVoiceDuration, View voiceDot) {
        double duration = msg.getDuration();
        tvVoiceDuration.setText(String.format(Locale.getDefault(), "%d%s%s", (int) (duration + 0.5), " ", "''"));

        voiceDot.setVisibility(mIsLeft ? View.INVISIBLE : View.INVISIBLE);
    }

    protected void updateText(AVIMTextMessage msg, TextView tvContent) {
        String text = msg.getText();
        tvContent.setText(text);
    }

    public void updateUserProfile(String from, int groupId, TextView tvLabel, TextView tvNickName, CircleImageView civAvatar) {
        AppManager
                .getHttpService()
                .getLeancloudGroupUsers(from, groupId)
                .enqueue(new BaseResponseCallback<String>() {

                    @Override
                    protected void onSuccess(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String json = jsonObject.getString(from);

                            UserProfile tempUserProfile = JSON.parseObject(json, UserProfile.class);

                            if (tempUserProfile != null) {
                                tvNickName.setText(tempUserProfile.nickname);
                                // formatRoleLabel(tempUserProfile.role, tvLabel);
                                // formatRoleAvatar(tempUserProfile.role, tempUserProfile.avatar, civAvatar);
                            }

                            mUserProfile = tempUserProfile;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onFailure(ErrorResponse errorResponse) {
                        formatRoleLabel(0, tvLabel);
                        formatRoleAvatar(0, null, civAvatar);
                    }
                });
    }

    protected void updateReplyContent(AVIMTypedMessage msg, TextView tvReply) {
        Map<String, Object> attrs;
        switch (msg.getMessageType()) {
            case AVIMMessageType.TEXT_MESSAGE_TYPE:
                attrs = ((AVIMTextMessage) msg).getAttrs();
                break;
            case AVIMMessageType.IMAGE_MESSAGE_TYPE:
                attrs = ((AVIMImageMessage) msg).getAttrs();
                break;
            case AVIMMessageType.AUDIO_MESSAGE_TYPE:
                attrs = ((AVIMAudioMessage) msg).getAttrs();
                break;
            default:
                attrs = ((AVIMTextMessage) msg).getAttrs();
                break;
        }

        String questionMsgId = (String) attrs.get(ChatEngine.MSG_QUESTION_MSG_ID);
        long sendTimestamp = (long) attrs.get(ChatEngine.MSG_SEND_TIMESTAMP);


        AVIMConversation avimConversation = AppManager.getChatEngine().getAVIMConversation(msg.getConversationId());

        AVIMMessageInterval.AVIMMessageIntervalBound start = AVIMMessageInterval.createBound(questionMsgId, sendTimestamp, true);
        AVIMMessageInterval.AVIMMessageIntervalBound end = AVIMMessageInterval.createBound(questionMsgId, sendTimestamp, true);
        AVIMMessageInterval interval = new AVIMMessageInterval(start, end);

        avimConversation.queryMessages(interval, AVIMMessageQueryDirection.AVIMMessageQueryDirectionFromNewToOld, 1, new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVIMException e) {

                for (AVIMMessage message : list) {
                    tvReply.setText(((AVIMTextMessage) message).getText());
                }
            }
        });
    }

    protected void showOtherUserProfile(View v) {
        if (mRole != 0) {
            Bundle extras = new Bundle();
            extras.putParcelable(OtherUserProfileActivity.ARGS_USER_PROFILE, mUserProfile);
            OtherUserProfileActivity.show(v.getContext(), OtherUserProfileActivity.class, extras);
        }
    }

    private void formatRoleAvatar(int role, String url, CircleImageView cIv) {

        @DrawableRes int drawableId;
        if (role == 0) {
            drawableId = R.mipmap.ic_info_avatar_patient;
        } else {
            drawableId = R.mipmap.ic_info_avatar_doctor;
        }

        RequestOptions options = new RequestOptions();

        options.placeholder(drawableId).error(drawableId).getOptions();

        mLoader.load(url).apply(options).into(cIv);

    }

    private void formatRoleLabel(int role, TextView tvRoleLabel) {
        String roleLabel = null;
        switch (role) {
            case 0://患者
                roleLabel = itemView.getResources().getString(R.string.patient);
                break;
            case 1://运营
                // roleLabel = itemView.getResources().getString(R.string.dbo);
            case 2://助理
                roleLabel = itemView.getResources().getString(R.string.assistant);
                break;
            case 3://医生
                roleLabel = itemView.getResources().getString(R.string.doctor);
                break;
            default:
                break;
        }
        tvRoleLabel.setText(roleLabel);
        tvRoleLabel.setBackground(role != 3 ? tvRoleLabel.getResources().getDrawable(R.drawable.bg_chat_assistant_label) : tvRoleLabel.getResources().getDrawable(R.drawable.bg_chat_doctor_label));
        tvRoleLabel.setVisibility(role == 0 || TextUtils.isEmpty(roleLabel) ? View.INVISIBLE : View.VISIBLE);
    }

    public interface OnReplayListener<Message> {

        void onReplyMsg(Message msg);
    }
}

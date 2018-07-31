package com.sumian.app.leancloud.holder.delegate;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.app.app.HwAppManager;
import com.sumian.app.leancloud.LeanCloudHelper;
import com.sumian.app.leancloud.holder.MsgImageViewHolder;
import com.sumian.app.leancloud.holder.MsgTextViewHolder;
import com.sumian.app.leancloud.holder.MsgVoiceViewHolder;
import com.sumian.app.leancloud.holder.base.BaseViewHolder;

/**
 * Created by jzz
 * on 2018/1/4.
 * desc:
 */

public class AdapterDelegate {

    private static final int LEFT_TEXT_TYPE = 0x11;
    private static final int LEFT_IMAGE_TYPE = 0x12;
    private static final int LEFT_VOICE_TYPE = 0x13;

    private static final int RIGHT_TEXT_TYPE = 0x21;
    private static final int RIGHT_IMAGE_TYPE = 0x22;
    private static final int RIGHT_VOICE_TYPE = 0x23;

    private static final int UNKNOWN_TYPE = 0x00;

    private int mServiceType;

    public AdapterDelegate(int serviceType) {
        mServiceType = serviceType;
    }

    public BaseViewHolder findViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder baseViewHolder;
        boolean isLeft = false;
        switch (viewType) {
            case LEFT_TEXT_TYPE:
                isLeft = true;
            case RIGHT_TEXT_TYPE:
                baseViewHolder = new MsgTextViewHolder(parent, isLeft);
                break;
            case LEFT_IMAGE_TYPE:
                isLeft = true;
            case RIGHT_IMAGE_TYPE:
                baseViewHolder = new MsgImageViewHolder(parent, isLeft);
                break;
            case LEFT_VOICE_TYPE:
                isLeft = true;
            case RIGHT_VOICE_TYPE:
                baseViewHolder = new MsgVoiceViewHolder(parent, isLeft);
                break;
            default:
                throw new NullPointerException("unknown msg type");
        }
        baseViewHolder.itemView.setTag(baseViewHolder);
        return baseViewHolder;
    }

    @SuppressWarnings("unchecked")
    public void onBindViewHolder(int viewType, BaseViewHolder holder, AVIMMessage msg) {
        switch (viewType) {
            case LEFT_TEXT_TYPE:
            case RIGHT_TEXT_TYPE:
                MsgTextViewHolder msgTextViewHolder = (MsgTextViewHolder) holder;
                msgTextViewHolder.initView(mServiceType, (AVIMTextMessage) msg);
                break;
            case LEFT_IMAGE_TYPE:
            case RIGHT_IMAGE_TYPE:
                MsgImageViewHolder msgImageViewHolder = (MsgImageViewHolder) holder;
                msgImageViewHolder.initView(mServiceType, (AVIMImageMessage) msg);
                break;
            case LEFT_VOICE_TYPE:
            case RIGHT_VOICE_TYPE:
                MsgVoiceViewHolder voiceViewHolder = (MsgVoiceViewHolder) holder;
                voiceViewHolder.initView(mServiceType, (AVIMAudioMessage) msg);
                break;
            default:
                break;
        }
    }

    public int getItemViewType(AVIMMessage msg) {
        if (mServiceType == LeanCloudHelper.SERVICE_TYPE_MAIL) {
            return getChatType(msg, LEFT_TEXT_TYPE, LEFT_IMAGE_TYPE, LEFT_VOICE_TYPE);
        } else {
            String from = msg.getFrom();
            if (TextUtils.isEmpty(from)) return UNKNOWN_TYPE;

            if (from.equals(HwAppManager.getAccountModel().getLeanCloudId())) {
                return getChatType(msg, RIGHT_TEXT_TYPE, RIGHT_IMAGE_TYPE, RIGHT_VOICE_TYPE);
            } else {
                return getChatType(msg, LEFT_TEXT_TYPE, LEFT_IMAGE_TYPE, LEFT_VOICE_TYPE);
            }
        }
    }

    private int getChatType(AVIMMessage msg, int textType, int imageType, int voiceType) {
        switch (((AVIMTypedMessage) msg).getMessageType()) {
            case AVIMMessageType.TEXT_MESSAGE_TYPE:
                return textType;
            case AVIMMessageType.IMAGE_MESSAGE_TYPE:
                return imageType;
            case AVIMMessageType.AUDIO_MESSAGE_TYPE:
                return voiceType;
            default:
                return UNKNOWN_TYPE;
        }
    }

}

package com.sumian.chat.holder.delegate;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.app.app.AppManager;
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

    private int mServiceType = LeanCloudHelper.SERVICE_TYPE_MAIL;

    public BaseViewHolder findViewHolder(ViewGroup parent, int viewType, int serviceType) {
        this.mServiceType = serviceType;
        boolean isLeft = false;
        switch (viewType) {
            case LEFT_TEXT_TYPE:
                isLeft = true;
            case RIGHT_TEXT_TYPE:
                MsgTextViewHolder msgTextViewHolder = new MsgTextViewHolder(parent, isLeft);
                msgTextViewHolder.itemView.setTag(msgTextViewHolder);
                return msgTextViewHolder;
            case LEFT_IMAGE_TYPE:
                isLeft = true;
            case RIGHT_IMAGE_TYPE:
                MsgImageViewHolder msgImageViewHolder = new MsgImageViewHolder(parent, isLeft);
                msgImageViewHolder.itemView.setTag(msgImageViewHolder);
                return msgImageViewHolder;
            case LEFT_VOICE_TYPE:
                isLeft = true;
            case RIGHT_VOICE_TYPE:
                MsgVoiceViewHolder msgVoiceViewHolder = new MsgVoiceViewHolder(parent, isLeft);
                msgVoiceViewHolder.itemView.setTag(msgVoiceViewHolder);
                return msgVoiceViewHolder;
            default:
                return null;
        }
    }

    public void onBindViewHolder(int serviceType, int viewType, BaseViewHolder holder, AVIMMessage msg) {
        boolean isLeft = false;
        switch (viewType) {
            case LEFT_TEXT_TYPE:
                isLeft = true;
            case RIGHT_TEXT_TYPE:
                MsgTextViewHolder rightTextViewHolder = (MsgTextViewHolder) holder;
                rightTextViewHolder.initView(isLeft, serviceType, msg);
                break;
            case LEFT_IMAGE_TYPE:
                isLeft = true;
            case RIGHT_IMAGE_TYPE:
                MsgImageViewHolder msgImageViewHolder = (MsgImageViewHolder) holder;
                msgImageViewHolder.initView(isLeft, serviceType, (AVIMImageMessage) msg);
                break;
            case LEFT_VOICE_TYPE:
                isLeft = true;
            case RIGHT_VOICE_TYPE:
                MsgVoiceViewHolder voiceViewHolder = (MsgVoiceViewHolder) holder;
                voiceViewHolder.initView(isLeft, serviceType, (AVIMAudioMessage) msg);
                break;
            default:
                break;
        }
    }

    public int getItemViewType(AVIMMessage msg) {

        if (mServiceType == LeanCloudHelper.SERVICE_TYPE_MAIL) {
            return getLeftViewType(msg);
        } else {
            String from = msg.getFrom();
            if (TextUtils.isEmpty(from)) return UNKNOWN_TYPE;

            if (from.equals(AppManager.getAccountModel().getLeanCloudId())) {
                if (msg instanceof AVIMTextMessage) {
                    return RIGHT_TEXT_TYPE;
                } else if (msg instanceof AVIMImageMessage) {
                    return RIGHT_IMAGE_TYPE;
                } else if (msg instanceof AVIMAudioMessage) {
                    return RIGHT_VOICE_TYPE;
                }
            } else {
                if (msg instanceof AVIMTextMessage) {
                    return LEFT_TEXT_TYPE;
                } else if (msg instanceof AVIMImageMessage) {
                    return LEFT_IMAGE_TYPE;
                } else if (msg instanceof AVIMAudioMessage) {
                    return LEFT_VOICE_TYPE;
                }
            }
        }
        return UNKNOWN_TYPE;
    }

    private int getLeftViewType(AVIMMessage msg) {
        if (msg instanceof AVIMTextMessage) {
            return LEFT_TEXT_TYPE;
        } else if (msg instanceof AVIMImageMessage) {
            return LEFT_IMAGE_TYPE;
        } else if (msg instanceof AVIMAudioMessage) {
            return LEFT_VOICE_TYPE;
        } else {
            return LEFT_TEXT_TYPE;
        }
    }
}

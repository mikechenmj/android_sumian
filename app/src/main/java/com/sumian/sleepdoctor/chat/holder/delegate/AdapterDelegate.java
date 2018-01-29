package com.sumian.sleepdoctor.chat.holder.delegate;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;
import com.sumian.sleepdoctor.chat.holder.MsgTextViewHolder;

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


    public BaseViewHolder findViewHolder(ViewGroup parent, int viewType) {
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
                //MsgImageViewHolder msgImageViewHolder = new MsgImageViewHolder(parent, isLeft);
                //  msgImageViewHolder.itemView.setTag(msgImageViewHolder);
                //  return msgImageViewHolder;
            case LEFT_VOICE_TYPE:
                isLeft = true;
            case RIGHT_VOICE_TYPE:
                // MsgVoiceViewHolder msgVoiceViewHolder = new MsgVoiceViewHolder(parent, isLeft);
                //msgVoiceViewHolder.itemView.setTag(msgVoiceViewHolder);
                //return msgVoiceViewHolder;
            default:
                return null;
        }
    }

    public void onBindViewHolder(int viewType, BaseViewHolder holder, AVIMMessage msg) {
        switch (viewType) {
            case LEFT_TEXT_TYPE:
            case RIGHT_TEXT_TYPE:
                MsgTextViewHolder rightTextViewHolder = (MsgTextViewHolder) holder;
                rightTextViewHolder.initView((AVIMTextMessage) msg);
                break;
            case LEFT_IMAGE_TYPE:
            case RIGHT_IMAGE_TYPE:
                break;
            case LEFT_VOICE_TYPE:
            case RIGHT_VOICE_TYPE:
                break;
            default:
                break;
        }
    }

    public int getItemViewType(AVIMMessage msg) {
        String from = msg.getFrom();
        if (TextUtils.isEmpty(from)) return UNKNOWN_TYPE;

        if (from.equals(AppManager.getAccountViewModel().getToken().user.leancloud_id)) {
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
        return UNKNOWN_TYPE;
    }
}

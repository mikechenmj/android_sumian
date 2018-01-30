package com.sumian.sleepdoctor.chat.holder.delegate;

import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;
import com.sumian.sleepdoctor.chat.holder.ImageNormalViewHolder;
import com.sumian.sleepdoctor.chat.holder.ImageQuestionViewHolder;
import com.sumian.sleepdoctor.chat.holder.ImageReplyViewHolder;
import com.sumian.sleepdoctor.chat.holder.TextNormalViewHolder;
import com.sumian.sleepdoctor.chat.holder.TextQuestionViewHolder;
import com.sumian.sleepdoctor.chat.holder.TextReplyViewHolder;
import com.sumian.sleepdoctor.chat.holder.VoiceNormalViewHolder;
import com.sumian.sleepdoctor.chat.holder.VoiceQuestionViewHolder;
import com.sumian.sleepdoctor.chat.holder.VoiceReplyViewHolder;

import java.util.Map;

/**
 * Created by jzz
 * on 2018/1/4.
 * desc:
 */

public class AdapterDelegate {

    private static final String TAG = AdapterDelegate.class.getSimpleName();

    private static final int LEFT_TEXT_NORMAL_TYPE = 0x11;
    private static final int LEFT_TEXT_REPLAY_TYPE = 0x12;
    private static final int LEFT_TEXT_QUESTION_TYPE = 0x13;

    private static final int LEFT_IMAGE_NORMAL_TYPE = 0x21;
    private static final int LEFT_IMAGE_REPLAY_TYPE = 0x22;
    private static final int LEFT_IMAGE_QUESTION_TYPE = 0x23;

    private static final int LEFT_VOICE_NORMAL_TYPE = 0x31;
    private static final int LEFT_VOICE_REPLAY_TYPE = 0x32;
    private static final int LEFT_VOICE_QUESTION_TYPE = 0x33;

    private static final int RIGHT_TEXT_NORMAL_TYPE = 0x51;
    private static final int RIGHT_TEXT_REPLAY_TYPE = 0x52;
    private static final int RIGHT_TEXT_QUESTION_TYPE = 0x53;

    private static final int RIGHT_IMAGE_NORMAL_YPE = 0x61;
    private static final int RIGHT_IMAGE_REPLAY_TYPE = 0x62;
    private static final int RIGHT_IMAGE_QUESTION_TYPE = 0x63;

    private static final int RIGHT_VOICE_NORMAL_TYPE = 0x71;
    private static final int RIGHT_VOICE_REPLAY_TYPE = 0x72;
    private static final int RIGHT_VOICE_QUESTION_TYPE = 0x73;

    private static final int UNKNOWN_TYPE = 0x00;


    public BaseViewHolder findViewHolder(ViewGroup parent, int viewType) {
        boolean isLeft = false;

        BaseViewHolder baseViewHolder = null;

        switch (viewType) {
            case LEFT_TEXT_NORMAL_TYPE://text
                isLeft = true;
            case RIGHT_TEXT_NORMAL_TYPE:
                baseViewHolder = new TextNormalViewHolder(parent, isLeft);
                break;
            case LEFT_TEXT_REPLAY_TYPE:
                isLeft = true;
            case RIGHT_TEXT_REPLAY_TYPE:
                baseViewHolder = new TextReplyViewHolder(parent, isLeft);
                break;
            case LEFT_TEXT_QUESTION_TYPE:
                isLeft = true;
            case RIGHT_TEXT_QUESTION_TYPE:
                baseViewHolder = new TextQuestionViewHolder(parent, isLeft);
                break;
            case LEFT_IMAGE_NORMAL_TYPE://image
                isLeft = true;
            case RIGHT_IMAGE_NORMAL_YPE:
                baseViewHolder = new ImageNormalViewHolder(parent, isLeft);
                break;
            case LEFT_IMAGE_REPLAY_TYPE:
                isLeft = true;
            case RIGHT_IMAGE_REPLAY_TYPE:
                baseViewHolder = new ImageReplyViewHolder(parent, isLeft);
                break;
            case LEFT_IMAGE_QUESTION_TYPE:
                isLeft = true;
            case RIGHT_IMAGE_QUESTION_TYPE:
                baseViewHolder = new ImageQuestionViewHolder(parent, isLeft);
                break;
            case LEFT_VOICE_NORMAL_TYPE://voice
                isLeft = true;
            case RIGHT_VOICE_NORMAL_TYPE:
                baseViewHolder = new VoiceNormalViewHolder(parent, isLeft);
                break;
            case LEFT_VOICE_REPLAY_TYPE:
                isLeft = true;
            case RIGHT_VOICE_REPLAY_TYPE:
                baseViewHolder = new VoiceReplyViewHolder(parent, isLeft);
                break;
            case LEFT_VOICE_QUESTION_TYPE:
                isLeft = true;
            case RIGHT_VOICE_QUESTION_TYPE:
                baseViewHolder = new VoiceQuestionViewHolder(parent, isLeft);
                break;
            default:
                return null;
        }

        baseViewHolder.itemView.setTag(baseViewHolder);
        return baseViewHolder;
    }

    public void onBindViewHolder(int viewType, BaseViewHolder holder, AVIMMessage msg) {
        switch (viewType) {
            case LEFT_TEXT_NORMAL_TYPE://text
                //isLeft = true;
            case RIGHT_TEXT_NORMAL_TYPE:
                ((TextNormalViewHolder) holder).initView((AVIMTextMessage) msg);
                break;
            case LEFT_TEXT_REPLAY_TYPE:
                //isLeft = true;
            case RIGHT_TEXT_REPLAY_TYPE:
                ((TextReplyViewHolder) holder).initView((AVIMTextMessage) msg);
                break;
            case LEFT_TEXT_QUESTION_TYPE:
                // isLeft = true;
            case RIGHT_TEXT_QUESTION_TYPE:
                ((TextQuestionViewHolder) holder).initView((AVIMTextMessage) msg);
                break;
            case LEFT_IMAGE_NORMAL_TYPE://image
                //isLeft = true;
            case RIGHT_IMAGE_NORMAL_YPE:
                ((ImageNormalViewHolder) holder).initView((AVIMImageMessage) msg);
                break;
            case LEFT_IMAGE_REPLAY_TYPE:
                //isLeft = true;
            case RIGHT_IMAGE_REPLAY_TYPE:
                ((ImageReplyViewHolder) holder).initView((AVIMImageMessage) msg);
                break;
            case LEFT_IMAGE_QUESTION_TYPE:
                //isLeft = true;
            case RIGHT_IMAGE_QUESTION_TYPE:
                ((ImageQuestionViewHolder) holder).initView((AVIMImageMessage) msg);

                break;
            case LEFT_VOICE_NORMAL_TYPE://voice
                //isLeft = true;
            case RIGHT_VOICE_NORMAL_TYPE:
                ((VoiceNormalViewHolder) holder).initView((AVIMAudioMessage) msg);

                break;
            case LEFT_VOICE_REPLAY_TYPE:
                //  isLeft = true;
            case RIGHT_VOICE_REPLAY_TYPE:
                ((VoiceReplyViewHolder) holder).initView((AVIMAudioMessage) msg);

                break;
            case LEFT_VOICE_QUESTION_TYPE:
                // isLeft = true;
            case RIGHT_VOICE_QUESTION_TYPE:
                ((VoiceQuestionViewHolder) holder).initView((AVIMAudioMessage) msg);

                break;
            default:
                break;
        }
    }

    public int getItemViewType(AVIMTypedMessage msg) {
        String from = msg.getFrom();
        Log.e(TAG, "getItemViewType: ---------->" + from);
        if (TextUtils.isEmpty(from)) return UNKNOWN_TYPE;

        AVIMMessage.AVIMMessageIOType ioType = msg.getMessageIOType();
        if (ioType == AVIMMessage.AVIMMessageIOType.AVIMMessageIOTypeOut) {//右
            int messageType = msg.getMessageType();


        } else {//左

        }

        if (msg instanceof AVIMTextMessage) {
            ((AVIMTextMessage) msg).getMessageType()
            Map<String, Object> attrs = ((AVIMTextMessage) msg).getAttrs();
            if (attrs == null || attrs.isEmpty()) return RIGHT_TEXT_NORMAL_TYPE;

            String type = (String) attrs.get("type");
            switch (type) {
                case "reply":

                    return RIGHT_TEXT_REPLAY_TYPE;
                case "question":

                    return RIGHT_TEXT_QUESTION_TYPE;
                default:
                    return RIGHT_TEXT_NORMAL_TYPE;
            }

        } else if (msg instanceof AVIMImageMessage) {
            return RIGHT_IMAGE_TYPE;
        } else if (msg instanceof AVIMAudioMessage) {
            return RIGHT_VOICE_TYPE;
        }

        return UNKNOWN_TYPE;
    }
}

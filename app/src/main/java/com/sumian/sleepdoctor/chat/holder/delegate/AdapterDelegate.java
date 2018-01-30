package com.sumian.sleepdoctor.chat.holder.delegate;

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

import static com.avos.avoscloud.im.v2.AVIMMessageType.AUDIO_MESSAGE_TYPE;
import static com.avos.avoscloud.im.v2.AVIMMessageType.IMAGE_MESSAGE_TYPE;
import static com.avos.avoscloud.im.v2.AVIMMessageType.TEXT_MESSAGE_TYPE;

/**
 * Created by jzz
 * on 2018/1/4.
 * desc:
 */

public class AdapterDelegate implements BaseViewHolder.OnReplayListener<AVIMTypedMessage> {

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


    private OnReplyCallback mOnReplyCallback;

    public void setOnReplyCallback(OnReplyCallback onReplyCallback) {
        mOnReplyCallback = onReplyCallback;
    }

    @SuppressWarnings("unchecked")
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

        baseViewHolder.setOnReplayListener(this);
        baseViewHolder.itemView.setTag(baseViewHolder);
        return baseViewHolder;
    }

    public void onBindViewHolder(int viewType, BaseViewHolder holder, AVIMTypedMessage msg) {
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
        AVIMMessage.AVIMMessageIOType ioType = msg.getMessageIOType();
        int messageType = msg.getMessageType();
        Map<String, Object> attrs;
        String type;
        if (ioType == AVIMMessage.AVIMMessageIOType.AVIMMessageIOTypeOut) {//右
            switch (messageType) {
                case TEXT_MESSAGE_TYPE:
                    attrs = ((AVIMTextMessage) msg).getAttrs();
                    if (attrs == null || attrs.isEmpty()) return RIGHT_TEXT_NORMAL_TYPE;
                    type = (String) attrs.get("type");
                    switch (type) {
                        case "reply":
                            return RIGHT_TEXT_REPLAY_TYPE;
                        case "question":
                            return RIGHT_TEXT_QUESTION_TYPE;
                        default:
                            return RIGHT_TEXT_NORMAL_TYPE;
                    }
                    // break;
                case IMAGE_MESSAGE_TYPE:
                    attrs = ((AVIMImageMessage) msg).getAttrs();
                    if (attrs == null || attrs.isEmpty()) return RIGHT_IMAGE_NORMAL_YPE;
                    type = (String) attrs.get("type");
                    switch (type) {
                        case "reply":
                            return RIGHT_IMAGE_REPLAY_TYPE;
                        case "question":
                            return RIGHT_IMAGE_QUESTION_TYPE;
                        default:
                            return RIGHT_IMAGE_NORMAL_YPE;
                    }
                    // break;
                case AUDIO_MESSAGE_TYPE:
                    attrs = ((AVIMAudioMessage) msg).getAttrs();
                    if (attrs == null || attrs.isEmpty()) return RIGHT_VOICE_NORMAL_TYPE;
                    type = (String) attrs.get("type");
                    switch (type) {
                        case "reply":
                            return RIGHT_VOICE_REPLAY_TYPE;
                        case "question":
                            return RIGHT_VOICE_QUESTION_TYPE;
                        default:
                            return RIGHT_VOICE_NORMAL_TYPE;
                    }
                    // break;
            }
        } else {//左
            switch (messageType) {
                case TEXT_MESSAGE_TYPE:
                    attrs = ((AVIMTextMessage) msg).getAttrs();
                    if (attrs == null || attrs.isEmpty()) return LEFT_TEXT_NORMAL_TYPE;
                    type = (String) attrs.get("type");
                    switch (type) {
                        case "reply":
                            return LEFT_TEXT_REPLAY_TYPE;
                        case "question":
                            return LEFT_TEXT_QUESTION_TYPE;
                        default:
                            return LEFT_TEXT_NORMAL_TYPE;
                    }
                    // break;
                case IMAGE_MESSAGE_TYPE:
                    attrs = ((AVIMImageMessage) msg).getAttrs();
                    if (attrs == null || attrs.isEmpty()) return LEFT_IMAGE_NORMAL_TYPE;
                    type = (String) attrs.get("type");
                    switch (type) {
                        case "reply":
                            return LEFT_IMAGE_REPLAY_TYPE;
                        case "question":
                            return LEFT_IMAGE_QUESTION_TYPE;
                        default:
                            return LEFT_IMAGE_NORMAL_TYPE;
                    }
                    // break;
                case AUDIO_MESSAGE_TYPE:
                    attrs = ((AVIMAudioMessage) msg).getAttrs();
                    if (attrs == null || attrs.isEmpty()) return LEFT_VOICE_NORMAL_TYPE;
                    type = (String) attrs.get("type");
                    switch (type) {
                        case "reply":
                            return LEFT_VOICE_REPLAY_TYPE;
                        case "question":
                            return LEFT_VOICE_QUESTION_TYPE;
                        default:
                            return LEFT_VOICE_NORMAL_TYPE;
                    }
                    // break;
            }
        }
        return UNKNOWN_TYPE;
    }

    @Override
    public void onReplyMsg(AVIMTypedMessage msg) {

        if (mOnReplyCallback != null) {
            mOnReplyCallback.onReply(msg);
        }
    }


    public interface OnReplyCallback {

        void onReply(AVIMTypedMessage msg);
    }
}

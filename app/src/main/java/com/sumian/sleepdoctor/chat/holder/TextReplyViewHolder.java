package com.sumian.sleepdoctor.chat.holder;

import android.support.text.emoji.widget.EmojiAppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;
import com.sumian.sleepdoctor.chat.widget.MsgSendErrorView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class TextReplyViewHolder extends BaseViewHolder<AVIMTextMessage> {

    private static final String TAG = TextReplyViewHolder.class.getSimpleName();

    @BindView(R.id.tv_time_line)
    TextView mTvTimeLine;

    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.tv_label)
    TextView mTvLabel;

    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;

    @BindView(R.id.tv_reply)
    EmojiAppCompatTextView mTvReply;

    @BindView(R.id.tv_msg)
    EmojiAppCompatTextView mTvMsg;

    @BindView(R.id.msg_send_error_view)
    MsgSendErrorView mMsgSendErrorView;

    private boolean mIsLeft;

    public TextReplyViewHolder(ViewGroup parent, boolean isLeft) {
        super(LayoutInflater.from(parent.getContext()).inflate(isLeft ? R.layout.lay_item_left_text_reply_chat : R.layout.lay_item_left_text_reply_chat, parent, false));
        this.mIsLeft = isLeft;
    }

    @Override
    public void initView(AVIMTextMessage avimTextMessage) {
        super.initView(avimTextMessage);

        Map<String, Object> attrs = avimTextMessage.getAttrs();

        long sendTimestamp = (long) attrs.get("send_timestamp");
        String questionMsgId = (String) attrs.get("question_msg_id");

        AppManager.getChatEngine().getAVIMConversation(avimTextMessage.getConversationId()).queryMessages(questionMsgId, sendTimestamp, 2, new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVIMException e) {

                for (AVIMMessage message : list) {
                    Log.e(TAG, "done: ----------->" + ((AVIMTextMessage) message).getText());
                    mTvReply.setText(((AVIMTextMessage) message).getText());
                }
            }
        });

        String text = avimTextMessage.getText();
        mTvMsg.setText(text);
    }
}

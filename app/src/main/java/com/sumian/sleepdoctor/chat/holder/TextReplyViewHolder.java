package com.sumian.sleepdoctor.chat.holder;

import android.support.text.emoji.widget.EmojiAppCompatTextView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.chat.base.BaseChatViewHolder;
import com.sumian.sleepdoctor.chat.widget.MsgSendErrorView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.sumian.sleepdoctor.chat.engine.ChatEngine.MSG_QUESTION_MSG_ID;
import static com.sumian.sleepdoctor.chat.engine.ChatEngine.MSG_SEND_TIMESTAMP;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class TextReplyViewHolder extends BaseChatViewHolder<AVIMTextMessage> {

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

    @BindView(R.id.tv_content)
    EmojiAppCompatTextView mTvContent;

    @BindView(R.id.msg_send_error_view)
    MsgSendErrorView mMsgSendErrorView;

    public TextReplyViewHolder(ViewGroup parent, boolean isLeft, int leftLayoutId, int rightLayoutId) {
        super(parent, isLeft, leftLayoutId, rightLayoutId);
    }

    @Override
    public void initView(AVIMTextMessage avimTextMessage) {
        super.initView(avimTextMessage);
        updateUserProfile(avimTextMessage.getFrom(), mGroupId, mTvLabel, mTvNickname, mIvIcon);
        updateReplyContent(avimTextMessage);
        updateText(avimTextMessage, mTvContent);
    }

    private void updateReplyContent(AVIMTextMessage avimTextMessage) {
        Map<String, Object> attrs = avimTextMessage.getAttrs();

        long sendTimestamp = (long) attrs.get(MSG_SEND_TIMESTAMP);
        String questionMsgId = (String) attrs.get(MSG_QUESTION_MSG_ID);

        AppManager.getChatEngine().getAVIMConversation(avimTextMessage.getConversationId()).queryMessages(questionMsgId, sendTimestamp, 2, new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVIMException e) {

                for (AVIMMessage message : list) {
                    Log.e(TAG, "done: ----------->" + ((AVIMTextMessage) message).getText());
                    mTvReply.setText(((AVIMTextMessage) message).getText());
                }
            }
        });
    }
}

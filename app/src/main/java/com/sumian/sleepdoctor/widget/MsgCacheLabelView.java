package com.sumian.sleepdoctor.widget;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.chat.utils.TimeUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/2/24.
 * desc:
 */

public class MsgCacheLabelView extends LinearLayout {

    @BindView(R.id.tv_history)
    TextView mTvChatHistory;

    @BindView(R.id.tv_time)
    TextView mTvChatHistoryTime;

    public MsgCacheLabelView(Context context) {
        this(context, null);
    }

    public MsgCacheLabelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MsgCacheLabelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        ButterKnife.bind(inflate(context, R.layout.lay_cache_msg, this));
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setVisibility(GONE);
    }

    public void showWelcomeText(String groupName) {
        mTvChatHistory.setText(String.format(Locale.getDefault(), getResources().getString(R.string.welcome_join_title), groupName));
        mTvChatHistory.setVisibility(VISIBLE);
        mTvChatHistoryTime.setVisibility(GONE);
    }

    public void updateMsg(AVIMTypedMessage msg) {
        if (msg == null) {
            hide();
            return;
        }

        String text = null;
        String formatMsgTime = TimeUtils.formatMsgTime(msg.getTimestamp());
        switch (msg.getMessageType()) {
            case AVIMMessageType.TEXT_MESSAGE_TYPE:
                text = ((AVIMTextMessage) msg).getText();
                break;
            case AVIMMessageType.AUDIO_MESSAGE_TYPE:
                text = getContext().getString(R.string.pic_label);
                break;
            case AVIMMessageType.IMAGE_MESSAGE_TYPE:
                text = getContext().getString(R.string.voice_label);
                break;
            default:
                break;
        }
        mTvChatHistory.setText(text);
        mTvChatHistory.setVisibility(VISIBLE);
        mTvChatHistoryTime.setText(formatMsgTime);
        mTvChatHistoryTime.setVisibility(VISIBLE);
        show();
    }

    public void setTextColor(@ColorRes int color) {
        mTvChatHistory.setTextColor(getResources().getColor(color));
        mTvChatHistoryTime.setTextColor(getResources().getColor(color));
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }
}

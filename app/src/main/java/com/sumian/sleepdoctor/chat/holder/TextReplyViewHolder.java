package com.sumian.sleepdoctor.chat.holder;

import android.support.text.emoji.widget.EmojiAppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;
import com.sumian.sleepdoctor.chat.widget.MsgSendErrorView;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

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

    private int mGroupId;

    public TextReplyViewHolder(ViewGroup parent, boolean isLeft) {
        super(LayoutInflater.from(parent.getContext()).inflate(isLeft ? R.layout.lay_item_left_text_reply_chat : R.layout.lay_item_right_text_reply_chat, parent, false));
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

    public void bindGroupId(int groupId) {
        this.mGroupId = groupId;

        AppManager
                .getHttpService()
                .getLeancloudGroupUsers(mItem.getFrom(), mGroupId)
                .enqueue(new BaseResponseCallback<String>() {

                    @Override
                    protected void onSuccess(String response) {

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
}

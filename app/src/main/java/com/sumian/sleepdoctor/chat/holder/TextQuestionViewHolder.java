package com.sumian.sleepdoctor.chat.holder;

import android.support.text.emoji.widget.EmojiAppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;
import com.sumian.sleepdoctor.chat.widget.MsgSendErrorView;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class TextQuestionViewHolder extends BaseViewHolder<AVIMTextMessage> {

    @BindView(R.id.tv_time_line)
    TextView mTvTimeLine;

    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.tv_label)
    TextView mTvLabel;

    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;

    @BindView(R.id.tv_msg)
    EmojiAppCompatTextView mTvMsg;

    @BindView(R.id.msg_send_error_view)
    MsgSendErrorView mMsgSendErrorView;

    private boolean mIsLeft;
    private int mGroupId;

    public TextQuestionViewHolder(ViewGroup parent, boolean isLeft) {
        super(LayoutInflater.from(parent.getContext()).inflate(isLeft ? R.layout.lay_item_left_text_question_chat : R.layout.lay_item_right_text_question_chat, parent, false));
        this.mIsLeft = isLeft;
    }

    @Override
    public void initView(AVIMTextMessage avimTextMessage) {
        super.initView(avimTextMessage);

        String text = avimTextMessage.getText();

        ImageSpan imgSpan = new ImageSpan(itemView.getContext(), R.mipmap.group_chatbubble_icon_label);
        SpannableString spannableString = new SpannableString("[icon]  " + text);
        spannableString.setSpan(imgSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTvMsg.setText(spannableString);

    }

    @OnClick({R.id.msg_send_error_view})
    @Override
    public void onClick(View v) {
        super.onClick(v);
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

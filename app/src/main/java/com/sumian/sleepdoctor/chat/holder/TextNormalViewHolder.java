package com.sumian.sleepdoctor.chat.holder;

import android.util.Log;
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
 * Created by jzz
 * on 2018/1/4.
 * desc:
 */

public class TextNormalViewHolder extends BaseViewHolder<AVIMTextMessage> {

    private static final String TAG = TextNormalViewHolder.class.getSimpleName();

    @BindView(R.id.tv_time_line)
    TextView mTvTimeLine;

    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.tv_label)
    TextView mTvLabel;

    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;

    @BindView(R.id.tv_msg)
    TextView mTvMsg;

    @BindView(R.id.msg_send_error_view)
    MsgSendErrorView mMsgSendErrorView;

    private int mGroupId;

    public TextNormalViewHolder(ViewGroup parent, boolean isLeft) {
        super(LayoutInflater.from(parent.getContext()).inflate(isLeft ? R.layout.lay_item_left_text_nomal_chat : R.layout.lay_item_right_text_normal_chat, parent, false));
    }

    @Override
    public void initView(AVIMTextMessage avimTextMessage) {
        super.initView(avimTextMessage);

        String text = avimTextMessage.getText();

        mTvMsg.setText(text);

    }

    public void bindGroupId(int groupId) {
        this.mGroupId = groupId;

        Log.e(TAG, "bindGroupId: ------1--->" + mItem.getMessageIOType() + "   " + mItem.getFrom());

        AppManager
                .getHttpService()
                .getLeancloudGroupUsers(mItem.getFrom(), groupId)
                .enqueue(new BaseResponseCallback<String>() {

                    @Override
                    protected void onSuccess(String response) {

                        Log.e(TAG, "onSuccess: -------2----->" + mItem.getFrom() + "   " + response);

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


    @OnClick({R.id.iv_msg_failed})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_msg_failed://发送失败,再次发送
                break;
            default:
                break;
        }
    }

    @Override
    protected void onItemClick(View v) {
        super.onItemClick(v);
    }
}

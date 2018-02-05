package com.sumian.sleepdoctor.chat.base;

import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.bumptech.glide.request.RequestOptions;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/31.
 * desc:
 */

public abstract class BaseChatViewHolder<Item> extends BaseViewHolder<Item> {

    // private static final String TAG = BaseChatViewHolder.class.getSimpleName();

    protected int mRole;
    protected int mGroupId;
    protected boolean mIsLeft;

    protected OnReplayListener<Item> mOnReplayListener;

    public BaseChatViewHolder(ViewGroup parent, boolean isLeft, @LayoutRes int leftLayoutId, @LayoutRes int rightLayoutId) {
        super(LayoutInflater.from(parent.getContext()).inflate(isLeft ? leftLayoutId : rightLayoutId, parent, false));
        this.mIsLeft = isLeft;
    }

    public BaseChatViewHolder<Item> bindGroupRole(int role) {
        this.mRole = role;
        return this;
    }

    public BaseChatViewHolder<Item> bindGroupId(int groupId) {
        this.mGroupId = groupId;
        return this;
    }

    public void setOnReplayListener(OnReplayListener<Item> onReplayListener) {
        mOnReplayListener = onReplayListener;
    }

    protected void updateImageText(AVIMTextMessage msg, TextView tvContent) {
        String text = msg.getText();
        ImageSpan imgSpan = new ImageSpan(itemView.getContext(), R.mipmap.group_chatbubble_icon_label);
        SpannableString spannableString = new SpannableString("[icon]  " + text);
        spannableString.setSpan(imgSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvContent.setText(spannableString);
    }

    protected void updateText(AVIMTextMessage msg, TextView tvContent) {
        String text = msg.getText();
        tvContent.setText(text);
    }

    public void updateUserProfile(String from, int groupId, TextView tvLabel, TextView tvNickName, CircleImageView civAvatar) {
        AppManager
                .getHttpService()
                .getLeancloudGroupUsers(from, groupId)
                .enqueue(new BaseResponseCallback<String>() {

                    @Override
                    protected void onSuccess(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String json = jsonObject.getString(from);

                            UserProfile tempUserProfile = JSON.parseObject(json, UserProfile.class);

                            if (tempUserProfile != null) {
                                tvNickName.setText(tempUserProfile.nickname);
                                formatRoleLabel(tempUserProfile.role, tvLabel);
                                formatRoleAvatar(tempUserProfile.role, tempUserProfile.avatar, civAvatar);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onFailure(String error) {
                        formatRoleLabel(0, tvLabel);
                        formatRoleAvatar(0, null, civAvatar);
                    }
                });
    }

    private void formatRoleAvatar(int role, String url, CircleImageView cIv) {

        @DrawableRes int drawableId;
        if (role == 0) {
            drawableId = R.mipmap.info_avatar_patient;
        } else {
            drawableId = R.mipmap.info_avatar_doctor;
        }

        RequestOptions options = new RequestOptions();

        options.placeholder(drawableId).error(drawableId).getOptions();

        mLoader.load(url).apply(options).into(cIv);

    }

    private void formatRoleLabel(int role, TextView tvRoleLabel) {
        String roleLabel = null;
        switch (role) {
            case 0://患者
                roleLabel = itemView.getResources().getString(R.string.patient);
                break;
            case 1://运营
                roleLabel = itemView.getResources().getString(R.string.dbo);
                break;
            case 2://助理
                roleLabel = itemView.getResources().getString(R.string.assistant);
                break;
            case 3://医生
                roleLabel = itemView.getResources().getString(R.string.doctor);
                break;
            default:
                break;
        }
        tvRoleLabel.setText(roleLabel);
        tvRoleLabel.setVisibility(TextUtils.isEmpty(roleLabel) ? View.INVISIBLE : View.VISIBLE);
    }

    public interface OnReplayListener<Message> {

        void onReplyMsg(Message msg);
    }
}

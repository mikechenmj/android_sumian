package com.sumian.sleepdoctor.chat.holder;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;
import com.sumian.sleepdoctor.chat.widget.MsgSendErrorView;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class ImageReplyViewHolder extends BaseViewHolder<AVIMImageMessage> {

    @BindView(R.id.tv_time_line)
    TextView mTvTimeLine;
    @BindView(R.id.tv_label)
    TextView mTvLabel;
    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.msg_send_error_view)
    MsgSendErrorView mMsgSendErrorView;
    @BindView(R.id.biv_image)
    com.sumian.app.widget.BubbleImageView mBivImage;
    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;
    private boolean mIsLeft;
    private int mGroupId;

    public ImageReplyViewHolder(ViewGroup parent, boolean isLeft) {
        super(LayoutInflater.from(parent.getContext()).inflate(isLeft ? R.layout.lay_item_right_image_reply_chat : R.layout.lay_item_right_image_reply_chat, parent, false));
        this.mIsLeft = isLeft;
    }

    public void bindGroupId(int groupId) {
        this.mGroupId = groupId;

        AppManager
                .getHttpService()
                .getLeancloudGroupUsers(mItem.getFrom(), mGroupId)
                .enqueue(new BaseResponseCallback<JSONObject>() {

                    @Override
                    protected void onSuccess(JSONObject response) {

                        try {
                            UserProfile tempUserProfile = (UserProfile) response.get(mItem.getFrom());

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

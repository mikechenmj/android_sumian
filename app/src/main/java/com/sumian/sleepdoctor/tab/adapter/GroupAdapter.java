package com.sumian.sleepdoctor.tab.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.bumptech.glide.request.RequestOptions;
import com.sumian.common.base.BaseRecyclerAdapter;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;
import com.sumian.sleepdoctor.chat.activity.MsgActivity;
import com.sumian.sleepdoctor.pager.activity.ScanGroupResultActivity;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;
import com.sumian.sleepdoctor.tab.bean.GroupItem;
import com.sumian.sleepdoctor.widget.GroupDetailHaveDotView;
import com.sumian.sleepdoctor.widget.MsgCacheLabelView;

import net.qiujuer.genius.ui.widget.Button;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2018/1/20.
 * desc:
 */

public class GroupAdapter extends BaseRecyclerAdapter<GroupItem> {

    private static final String TAG = GroupAdapter.class.getSimpleName();

    public GroupAdapter(Context context) {
        super(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ViewHolder(mInflater.inflate(R.layout.lay_group_item, parent, false));
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, GroupItem item, int position) {
        ((ViewHolder) holder).initView(item);
    }

    public int updateReceiveMsg(AVIMTypedMessage msg) {
        int position = -1;
        for (int i = 0, len = mItems.size(); i < len; i++) {
            String conversationId = mItems.get(i).groupDetail.conversation_id;
            if (conversationId.equals(msg.getConversationId())) {
                AVIMTypedMessage lastMsg = mItems.get(i).lastMsg;
                if (lastMsg != null) {
                    mItems.get(i).secondLastMsg = lastMsg;
                }
                mItems.get(i).lastMsg = msg;
                mItems.get(i).unReadMsgCount = AppManager.getChatEngine().getAVIMConversation(msg.getConversationId()).getUnreadMessagesCount();
                mItems.get(i).isMsgMentioned = mItems.get(i).isMsgMentioned || msg.mentioned();
                Log.e(TAG, "updateReceiveMsg: ------>" + msg.mentioned());
                updateItem(i);
                position = i;
                break;
            }
        }
        return position;
    }

    public void updateSecondMsg(AVIMTypedMessage secondMsg) {
        if (secondMsg == null) return;
        for (int i = 0, len = mItems.size(); i < len; i++) {
            String conversationId = mItems.get(i).groupDetail.conversation_id;
            if (conversationId.equals(secondMsg.getConversationId())) {
                mItems.get(i).secondLastMsg = secondMsg;
                updateItem(i);
                break;
            }
        }
    }

    public void updateLastMsg(AVIMTypedMessage lastMsg) {
        if (lastMsg == null) return;
        for (int i = 0, len = mItems.size(); i < len; i++) {
            String conversationId = mItems.get(i).groupDetail.conversation_id;
            if (conversationId.equals(lastMsg.getConversationId())) {
                mItems.get(i).lastMsg = lastMsg;
                updateItem(i);
                break;
            }
        }
    }

    public void updateItemForUnReadMsgCount(AVIMConversation conversation, int unReadMsgCount) {
        for (int i = 0, len = mItems.size(); i < len; i++) {
            String conversationId = mItems.get(i).groupDetail.conversation_id;
            if (conversationId.equals(conversation.getConversationId())) {
                mItems.get(i).unReadMsgCount = unReadMsgCount;
                mItems.get(i).isMsgMentioned = false;
                updateItem(i);
                break;
            }
        }
    }

    static class ViewHolder extends BaseViewHolder<GroupItem> {

        private static final String TAG = ViewHolder.class.getSimpleName();

        @BindView(R.id.group_detail_have_dot_view)
        GroupDetailHaveDotView mGroupDetailHaveDotView;

        @BindView(R.id.tv_desc)
        TextView mTvDesc;
        @BindView(R.id.tv_doctor_name)
        TextView mTvDoctorName;
        @BindView(R.id.tv_expired)
        TextView mTvExpired;

        @BindView(R.id.v_line)
        View mLine;

        @BindView(R.id.cache_msg_two_view)
        MsgCacheLabelView mMsgCacheLabelTwoView;

        @BindView(R.id.cache_msg_one_view)
        MsgCacheLabelView mMsgCacheLabelOneView;

        @BindView(R.id.tv_msg_reply_label)
        TextView mTvMsgReplyLabel;

        @BindView(R.id.bt_expired)
        Button mBtExpired;
        @BindView(R.id.lay_expired_container)
        FrameLayout mLayExpiredContainer;

        ViewHolder(View itemView) {
            super(itemView);
        }

        public void initView(GroupItem item) {
            super.initView(item);
            updateGroupDetail(item);
            updateMsg(item);
        }

        private void updateGroupDetail(GroupItem item) {
            GroupDetail<UserProfile, UserProfile> groupDetail = item.groupDetail;
            RequestOptions options = new RequestOptions();
            options.error(R.mipmap.group_avatar).placeholder(R.mipmap.group_avatar).getOptions();
            load(groupDetail.avatar, options, mGroupDetailHaveDotView.getImageView());

            setText(mTvDesc, groupDetail.name);

            UserProfile doctor = groupDetail.doctor;
            if (doctor == null) {
                gone(mTvDoctorName);
            } else {
                String doctorNickname = doctor.nickname;
                gone(TextUtils.isEmpty(doctorNickname), mTvDoctorName);
                setText(mTvDoctorName, formatText("%s%s%s", getText(R.string.doctor), ": ", doctorNickname));
            }

            int dayLast = groupDetail.day_last;
            int role = item.groupDetail.role;

            if (role == 0) {//患者
                if (dayLast == 0) {//已过期
                    setText(mTvExpired, String.format(Locale.getDefault(), "%s", getText(R.string.expired)));
                    visible(mLayExpiredContainer);
                } else {//剩余天数
                    gone(mLayExpiredContainer);
                    if (dayLast <= 5) {
                        setText(mTvExpired, formatText("%d%s", dayLast, getText(R.string.time_expired_suffix)));
                    } else {
                        gone(mTvExpired);
                    }
                }
            } else {
                gone(mLayExpiredContainer);
                gone(mTvExpired);
            }
        }

        private void updateMsg(GroupItem item) {

            mMsgCacheLabelTwoView.updateMsg(item.secondLastMsg);
            mMsgCacheLabelOneView.updateMsg(item.lastMsg);

            if (item.secondLastMsg == null && item.lastMsg == null) {
                mMsgCacheLabelOneView.showWelcomeText(item.groupDetail.name);
                mMsgCacheLabelTwoView.hide();
            }

            //会话中,未读消息数量大于0,小红点提示
            mGroupDetailHaveDotView.showOrHideDot(item.unReadMsgCount > 0 || item.isMsgMentioned);

            //当有人回复你的提问时,即@了你.显示回复 dot label
            if (item.isMsgMentioned) {
                mTvMsgReplyLabel.setText("[收到新的医生回复]");
                mTvMsgReplyLabel.setVisibility(View.VISIBLE);
                mMsgCacheLabelTwoView.hide();
            } else {
                mTvMsgReplyLabel.setVisibility(View.GONE);
                mMsgCacheLabelTwoView.show();
            }

        }

        @OnClick({R.id.bt_expired})
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.lay_group_icon:

                    // ToastHelper.show("群头像");

                    break;
                case R.id.bt_expired:

                    Bundle extras = new Bundle();
                    extras.putInt(ScanGroupResultActivity.ARGS_GROUP_ID, mItem.groupDetail.id);
                    ScanGroupResultActivity.show(v.getContext(), ScanGroupResultActivity.class, extras);

                    break;
                default:
                    break;
            }
        }

        @Override
        protected void onItemClick(View v) {
            super.onItemClick(v);
            if (mItem.groupDetail.day_last == 0) {
                Bundle extras = new Bundle();
                extras.putInt(ScanGroupResultActivity.ARGS_GROUP_ID, mItem.groupDetail.id);
                ScanGroupResultActivity.show(v.getContext(), ScanGroupResultActivity.class, extras);
            } else {
                AppManager.getChatEngine().getAVIMConversation(mItem.groupDetail.conversation_id).read();
                mItem.unReadMsgCount = 0;
                mItem.isMsgMentioned = false;
                updateMsg(mItem);
                Bundle extras = new Bundle();
                extras.putSerializable(MsgActivity.ARGS_GROUP_DETAIL, mItem.groupDetail);
                MsgActivity.show(v.getContext(), MsgActivity.class, extras);
            }
        }
    }


}

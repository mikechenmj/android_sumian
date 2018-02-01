package com.sumian.sleepdoctor.tab.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.bumptech.glide.request.RequestOptions;
import com.sumian.common.base.BaseRecyclerAdapter;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;
import com.sumian.sleepdoctor.chat.activity.MsgActivity;
import com.sumian.sleepdoctor.chat.utils.TimeUtils;
import com.sumian.sleepdoctor.pager.activity.ScanGroupResultActivity;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;
import com.sumian.sleepdoctor.tab.bean.GroupItem;

import net.qiujuer.genius.ui.widget.Button;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jzz
 * on 2018/1/20.
 * desc:
 */

public class GroupAdapter extends BaseRecyclerAdapter<GroupItem> {

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

    public int updateMsg(AVIMMessage msg) {
        int position = -1;
        for (int i = 0, len = mItems.size(); i < len; i++) {
            String conversationId = mItems.get(i).mGroupDetail.conversation_id;
            if (conversationId.equals(msg.getConversationId())) {
                AVIMMessage lastMsg = mItems.get(i).LastMsg;
                if (lastMsg != null) {
                    mItems.get(i).SecondLastMsg = lastMsg;
                }
                mItems.get(i).LastMsg = msg;
                updateItem(i);
                position = i;
                break;
            }
        }
        return position;
    }

    static class ViewHolder extends BaseViewHolder<GroupItem> {

        private static final String TAG = ViewHolder.class.getSimpleName();

        @BindView(R.id.iv_group_icon)
        CircleImageView mIvGroupIcon;
        @BindView(R.id.tv_desc)
        TextView mTvDesc;
        @BindView(R.id.tv_doctor_name)
        TextView mTvDoctorName;
        @BindView(R.id.tv_expired)
        TextView mTvExpired;

        @BindView(R.id.v_line)
        View mLine;

        @BindView(R.id.tv_chat_history_two)
        TextView mTvChatHistoryTwo;
        @BindView(R.id.tv_chat_history_two_time)
        TextView mTvChatHistoryTwoTime;
        @BindView(R.id.tv_chat_history_one)
        TextView mTvChatHistoryOne;
        @BindView(R.id.tv_chat_history_one_time)
        TextView mTvChatHistoryOneTime;

        @BindView(R.id.bt_expired)
        Button mBtExpired;
        @BindView(R.id.lay_expired_container)
        FrameLayout mLayExpiredContainer;

        ViewHolder(View itemView) {
            super(itemView);
        }

        public void initView(GroupItem item) {
            super.initView(item);
            GroupDetail<UserProfile, UserProfile> groupDetail = item.mGroupDetail;
            RequestOptions options = new RequestOptions();
            options.error(R.mipmap.group_avatar).placeholder(R.mipmap.group_avatar).getOptions();
            load(groupDetail.avatar, options, mIvGroupIcon);

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
            int role = item.mGroupDetail.role;

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
                //if (dayLast <= 5) {
                //   setText(mTvExpired, formatText("%d%s", dayLast, getText(R.string.time_expired_suffix)));
                //} else {
                gone(mTvExpired);
                //}
            }


            mTvChatHistoryTwo.setVisibility(View.GONE);
            mTvChatHistoryTwoTime.setVisibility(View.GONE);

            AVIMMessage secondLastMsg = item.SecondLastMsg;

            if (secondLastMsg != null) {
                if (secondLastMsg instanceof AVIMTextMessage) {
                    String text = ((AVIMTextMessage) secondLastMsg).getText();
                    if (!TextUtils.isEmpty(text)) {
                        mTvChatHistoryTwo.setText(text);
                        mTvChatHistoryTwoTime.setText(TimeUtils.formatMsgTime(secondLastMsg.getTimestamp()));
                        mTvChatHistoryTwo.setVisibility(View.VISIBLE);
                        mTvChatHistoryTwoTime.setVisibility(View.VISIBLE);
                    }
                } else if (secondLastMsg instanceof AVIMImageMessage) {
                    String text = "[图片]";
                    mTvChatHistoryTwo.setText(text);
                    mTvChatHistoryTwoTime.setText(TimeUtils.formatMsgTime(secondLastMsg.getTimestamp()));
                    mTvChatHistoryTwo.setVisibility(View.VISIBLE);
                    mTvChatHistoryTwoTime.setVisibility(View.VISIBLE);
                } else if (secondLastMsg instanceof AVIMAudioMessage) {
                    String text = "[语音]";
                    mTvChatHistoryTwo.setText(text);
                    mTvChatHistoryTwoTime.setText(TimeUtils.formatMsgTime(secondLastMsg.getTimestamp()));
                    mTvChatHistoryTwo.setVisibility(View.VISIBLE);
                    mTvChatHistoryTwoTime.setVisibility(View.VISIBLE);
                }
            }

            mTvChatHistoryOne.setVisibility(View.GONE);
            mTvChatHistoryOneTime.setVisibility(View.GONE);

            AVIMMessage lastMsg = item.LastMsg;
            if (lastMsg != null) {
                if (lastMsg instanceof AVIMTextMessage) {
                    String text = ((AVIMTextMessage) lastMsg).getText();
                    if (!TextUtils.isEmpty(text)) {
                        mTvChatHistoryOne.setText(text);
                        mTvChatHistoryOneTime.setText(TimeUtils.formatMsgTime(lastMsg.getTimestamp()));
                        mTvChatHistoryOne.setVisibility(View.VISIBLE);
                        mTvChatHistoryOneTime.setVisibility(View.VISIBLE);
                    }
                } else if (lastMsg instanceof AVIMImageMessage) {
                    String text = "[图片]";
                    mTvChatHistoryOne.setText(text);
                    mTvChatHistoryOneTime.setText(TimeUtils.formatMsgTime(lastMsg.getTimestamp()));
                    mTvChatHistoryOne.setVisibility(View.VISIBLE);
                    mTvChatHistoryOneTime.setVisibility(View.VISIBLE);
                } else if (lastMsg instanceof AVIMAudioMessage) {
                    String text = "[语音]";
                    mTvChatHistoryOne.setText(text);
                    mTvChatHistoryOneTime.setText(TimeUtils.formatMsgTime(lastMsg.getTimestamp()));
                    mTvChatHistoryOne.setVisibility(View.VISIBLE);
                    mTvChatHistoryOneTime.setVisibility(View.VISIBLE);
                }
            }
        }

        @OnClick({R.id.bt_expired})
        @Override
        public void onClick(View v) {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.iv_group_icon:

                    // ToastHelper.show("群头像");

                    break;
                case R.id.bt_expired:

                    Bundle extras = new Bundle();
                    extras.putInt(ScanGroupResultActivity.ARGS_GROUP_ID, mItem.mGroupDetail.id);
                    ScanGroupResultActivity.show(v.getContext(), ScanGroupResultActivity.class, extras);

                    break;
                default:
                    break;
            }
        }

        @Override
        protected void onItemClick(View v) {
            super.onItemClick(v);

            if (mItem.mGroupDetail.day_last == 0) {
                Bundle extras = new Bundle();
                extras.putInt(ScanGroupResultActivity.ARGS_GROUP_ID, mItem.mGroupDetail.id);
                ScanGroupResultActivity.show(v.getContext(), ScanGroupResultActivity.class, extras);
            } else {

                Bundle extras = new Bundle();
                extras.putInt(MsgActivity.ARGS_GROUP_ID, mItem.mGroupDetail.id);
                extras.putString(MsgActivity.ARGS_CONVERSATION_ID, mItem.mGroupDetail.conversation_id);

                MsgActivity.show(v.getContext(), MsgActivity.class, extras);
            }

        }
    }


}

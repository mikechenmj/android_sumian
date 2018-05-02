package com.sumian.sleepdoctor.pager.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.sumian.common.base.BaseRecyclerAdapter;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;
import com.sumian.sleepdoctor.chat.bean.PinYinUserProfile;
import com.sumian.sleepdoctor.pager.activity.OtherUserProfileActivity;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/2/14.
 * desc:
 */

public class MemberAdapter extends BaseRecyclerAdapter<PinYinUserProfile> {

    public MemberAdapter(Context context) {
        super(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_item_group_members, parent, false));
        viewHolder.itemView.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, PinYinUserProfile item, int position) {
        ((ViewHolder) holder).initView(item);
    }


    class ViewHolder extends BaseViewHolder<PinYinUserProfile> {

        @BindView(R.id.civ_avatar)
        CircleImageView mCivAvatar;
        @BindView(R.id.tv_nickname)
        TextView mTvNickname;
        @BindView(R.id.tv_label)
        TextView mTvLabel;

        ViewHolder(View itemView) {
            super(itemView);
        }


        public void initView(PinYinUserProfile item) {
            super.initView(item);

            RequestOptions options = new RequestOptions();

            if (item.userProfile.role != 3) {
                options.placeholder(R.mipmap.ic_info_avatar_patient).error(R.mipmap.ic_info_avatar_patient).getOptions();
                mTvLabel.setBackground(itemView.getResources().getDrawable(R.drawable.bg_chat_assistant_label));
            } else {
                options.placeholder(R.mipmap.ic_info_avatar_doctor).error(R.mipmap.ic_info_avatar_doctor).getOptions();
                mTvLabel.setBackground(itemView.getResources().getDrawable(R.drawable.bg_chat_doctor_label));
            }

            load(item.userProfile.avatar, options, mCivAvatar);

            formatRoleLabel(item.userProfile.role, mTvLabel);

            mTvNickname.setText(item.userProfile.nickname);
        }


        private void formatRoleLabel(int role, TextView tvRoleLabel) {
            String roleLabel;
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
                    roleLabel = itemView.getResources().getString(R.string.patient);
                    break;
            }
            tvRoleLabel.setText(roleLabel);
            //tvRoleLabel.setVisibility(TextUtils.isEmpty(roleLabel) ? View.GONE : View.VISIBLE);
            if (role == 0) {
                mTvLabel.setVisibility(View.GONE);
            } else {
                mTvLabel.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onItemClick(View v) {
            super.onItemClick(v);
            Bundle extras = new Bundle();
            extras.putParcelable(OtherUserProfileActivity.ARGS_USER_PROFILE, mItem.userProfile);
            OtherUserProfileActivity.show(v.getContext(), OtherUserProfileActivity.class, extras);
        }
    }

}

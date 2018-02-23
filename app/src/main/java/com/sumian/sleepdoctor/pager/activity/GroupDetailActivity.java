package com.sumian.sleepdoctor.pager.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.chat.activity.MsgActivity;
import com.sumian.sleepdoctor.pager.contract.GroupDetailContract;
import com.sumian.sleepdoctor.pager.dialog.RenewDialog;
import com.sumian.sleepdoctor.pager.presenter.GroupDetailPresenter;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;
import com.sumian.sleepdoctor.widget.TitleBar;
import com.sumian.sleepdoctor.widget.divider.SettingDividerView;

import net.qiujuer.genius.ui.widget.Button;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/2/2.
 * desc:
 */

public class GroupDetailActivity extends BaseActivity<GroupDetailPresenter> implements GroupDetailContract.View,
        TitleBar.OnBackListener, View.OnClickListener, SettingDividerView.OnShowMoreListener {

    public static final String ARGS_GROUP_ID = "group_id";

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.lay_group_icon)
    QMUIRadiusImageView mIvGroupIcon;
    @BindView(R.id.tv_desc)
    TextView mTvDesc;
    @BindView(R.id.tv_doctor_name)
    TextView mTvDoctorName;
    @BindView(R.id.iv_qr_code)
    ImageView mIvQrCode;

    @BindView(R.id.tv_group_desc)
    TextView mTvGroupDesc;
    @BindView(R.id.sdv_renewal)
    SettingDividerView mSdvRenewal;
    @BindView(R.id.bt_join_up)
    Button mBtJoinUp;

    private int mGroupId;

    private GroupDetail<UserProfile, UserProfile> mGroupDetail;

    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mGroupId = bundle.getInt(ARGS_GROUP_ID);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_group_detail;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.addOnBackListener(this);
        mSdvRenewal.setOnShowMoreListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.getGroupDetail(mGroupId);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        GroupDetailPresenter.init(this);
    }

    @Override
    public void bindPresenter(GroupDetailContract.Presenter presenter) {
        this.mPresenter = (GroupDetailPresenter) presenter;
    }

    @Override
    public void onFailure(String error) {
        showToast(error);
    }

    @Override
    public void onGetGroupDetailSuccess(GroupDetail<UserProfile, UserProfile> groupDetail) {
        this.mGroupDetail = groupDetail;

        RequestOptions options = new RequestOptions();

        Glide.with(this)
                .asBitmap()
                .load(groupDetail.avatar)
                .apply(options)
                .into(mIvGroupIcon);

        mTvDesc.setText(groupDetail.name);
        UserProfile doctor = groupDetail.doctor;
        if (doctor == null) {
            mTvDoctorName.setVisibility(View.GONE);
        } else {
            mTvDoctorName.setText(groupDetail.doctor.nickname);
            mTvDoctorName.setVisibility(View.VISIBLE);
        }
        mTvGroupDesc.setText(groupDetail.description);

        String label;
        String content;
        if (groupDetail.role == 0) {
            mIvQrCode.setVisibility(View.GONE);
            mBtJoinUp.setVisibility(View.VISIBLE);
            label = "剩余" + groupDetail.day_last + " 天";
            content = "续费";
        } else {
            label = "群成员";
            content = groupDetail.user_count + "人";
            mIvQrCode.setVisibility(View.VISIBLE);
            mBtJoinUp.setVisibility(View.GONE);
        }
        mSdvRenewal.setLabel(label);
        mSdvRenewal.setContent(content);
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @OnClick({R.id.iv_qr_code})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_qr_code:
                Bundle extras = new Bundle();
                extras.putSerializable(MsgActivity.ARGS_GROUP_DETAIL, mGroupDetail);
                GroupQrCodeActivity.show(this, GroupQrCodeActivity.class, extras);
                break;
            default:
                break;
        }
    }

    @Override
    public void onShowMore(View v) {
        if (mGroupDetail.role == 0) {//点击再次续费

            RenewDialog renewDialog = new RenewDialog(v.getContext()).bindContentView(R.layout.dialog_renew);
            renewDialog.setOwnerActivity(this);
            if (!renewDialog.isShowing()) {
                renewDialog.show();
            }

        } else {//群成员联系人列表

            Bundle extras = new Bundle();

            extras.putParcelableArrayList(GroupMembersActivity.ARGS_MEMBERS, (ArrayList<? extends Parcelable>) mGroupDetail.users);
            GroupMembersActivity.show(this, GroupMembersActivity.class, extras);

        }

    }

    public GroupDetail<UserProfile, UserProfile> onGetGroupDetail() {
        return mGroupDetail;
    }
}

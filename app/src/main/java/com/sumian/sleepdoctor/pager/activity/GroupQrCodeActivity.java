package com.sumian.sleepdoctor.pager.activity;

import android.os.Bundle;
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
import com.sumian.sleepdoctor.tab.bean.GroupDetail;
import com.sumian.sleepdoctor.widget.TitleBar;

import butterknife.BindView;

/**
 * Created by sm
 * on 2018/2/12.
 * desc:
 */

public class GroupQrCodeActivity extends BaseActivity implements TitleBar.OnBackClickListener {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.lay_group_icon)
    QMUIRadiusImageView mIvGroupIcon;

    @BindView(R.id.tv_group_name)
    TextView mTvGroupName;

    @BindView(R.id.iv_qr_code)
    ImageView mIvQrCode;

    private GroupDetail<UserProfile, UserProfile> mGroupDetail;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mGroupDetail = (GroupDetail<UserProfile, UserProfile>) bundle.getSerializable(MsgActivity.ARGS_GROUP_DETAIL);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_group_qr_code;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.setOnBackClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mTvGroupName.setText(mGroupDetail.name);
        RequestOptions options = new RequestOptions();

        Glide.with(this)
                .asBitmap()
                .load(mGroupDetail.avatar)
                .apply(options)
                .into(mIvGroupIcon);

        Glide.with(this)
                .asBitmap()
                .load(mGroupDetail.code_url)
                .apply(options)
                .into(mIvQrCode);
    }

    @Override
    public void onBack(View v) {
        finish();
    }
}

package com.sumian.sleepdoctor.pager.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jaeger.library.StatusBarUtil;
import com.sumian.common.helper.ToastHelper;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.pager.contract.GroupDetailContract;
import com.sumian.sleepdoctor.pager.presenter.GroupDetailPresenter;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;
import com.sumian.sleepdoctor.widget.TitleBar;

import net.qiujuer.genius.ui.widget.Button;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/22.
 * desc:
 */

public class ScanGroupResultActivity extends BaseActivity<GroupDetailPresenter> implements View.OnClickListener, GroupDetailContract.View, TitleBar.OnBackListener {

    private static final String TAG = ScanGroupResultActivity.class.getSimpleName();

    public static final String ARGS_GROUP_ID = "args_group_id";

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.iv_group_icon)
    CircleImageView mIvGroupIcon;

    @BindView(R.id.tv_desc)
    TextView mTvDesc;
    @BindView(R.id.tv_doctor_name)
    TextView mTvDoctorName;

    @BindView(R.id.tv_group_desc)
    TextView mTvGroupDesc;

    @BindView(R.id.tv_money)
    TextView mTvMoney;

    @BindView(R.id.bt_join)
    Button mBtJoin;
    @BindView(R.id.bt_re_scan)
    Button mBtReScan;

    private int mGroupId;

    private GroupDetail<UserProfile, UserProfile> mGroupDetail;

    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mGroupId = bundle.getInt(ARGS_GROUP_ID, 0);
        Log.e(TAG, "initBundle: --------->" + mGroupId);
        return super.initBundle(bundle);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_join_one;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        this.mTitleBar.addOnBackListener(this);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        GroupDetailPresenter.init(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.getGroupDetail(mGroupId, "packages,users");
    }

    @OnClick({R.id.bt_join, R.id.bt_re_scan})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_join:
                Bundle args = new Bundle();
                args.putSerializable(PayGroupActivity.ARGS_GROUP_DETAIL, mGroupDetail);
                PayGroupActivity.show(this, PayGroupActivity.class, args);
                break;
            case R.id.bt_re_scan:
                ScanQrCodeActivity.show(this, ScanQrCodeActivity.class);
                break;
            default:
                break;
        }
    }

    @Override
    public void bindPresenter(GroupDetailContract.Presenter presenter) {
        mPresenter = (GroupDetailPresenter) presenter;
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onGetGroupDetailSuccess(GroupDetail<UserProfile, UserProfile> groupDetail) {
        this.mGroupDetail = groupDetail;
        runOnUiThread(() -> {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.group_avatar).error(R.mipmap.group_avatar).getOptions();
            Glide.with(this).load(groupDetail.avatar).apply(options).into(mIvGroupIcon);

            mTvDesc.setText(groupDetail.name);
            mTvDoctorName.setText(String.format(Locale.getDefault(), "%s%s", getString(R.string.doctor), groupDetail.doctor.nickname));
            mTvGroupDesc.setText(groupDetail.description);

            mTvMoney.setText(String.format(Locale.getDefault(), "%.2f", groupDetail.monthly_price / 100.0f));

        });
    }

    @Override
    public void onFailure(String error) {
        runOnUiThread(() -> ToastHelper.show(error));
    }

    @Override
    public void onBack(View v) {
        finish();
    }
}

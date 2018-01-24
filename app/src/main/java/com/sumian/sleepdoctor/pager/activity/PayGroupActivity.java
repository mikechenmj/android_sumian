package com.sumian.sleepdoctor.pager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pingplusplus.android.Pingpp;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.pager.bean.Order;
import com.sumian.sleepdoctor.pager.contract.PayGroupContract;
import com.sumian.sleepdoctor.pager.presenter.PayGroupPresenter;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;
import com.sumian.sleepdoctor.widget.TitleBar;
import com.sumian.sleepdoctor.widget.pay.PayCalculateItemView;
import com.sumian.sleepdoctor.widget.pay.PayItemGroupView;

import net.qiujuer.genius.ui.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sm
 * on 2018/1/22.
 * desc:
 */

public class PayGroupActivity extends BaseActivity<PayGroupPresenter> implements View.OnClickListener, PayItemGroupView.OnSelectPayWayListener, TitleBar.OnBackListener, PayCalculateItemView.OnMoneyChangeCallback, PayGroupContract.View {

    public static final String ARGS_GROUP_DETAIL = "args_group_detail";

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.iv_group_icon)
    CircleImageView mIvGroupIcon;

    @BindView(R.id.tv_desc)
    TextView mTvDesc;

    @BindView(R.id.pay_calculate_item_view)
    PayCalculateItemView mPayCalculateItemView;

    @BindView(R.id.tv_group_money)
    TextView mTvGroupMoney;

    @BindView(R.id.pay_group_view)
    PayItemGroupView mPayGroupView;

    @BindView(R.id.bt_pay)
    Button mBtPay;

    private String mPayChannel = "wx";

    private GroupDetail<UserProfile, UserProfile> mGroupDetail;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mGroupDetail = (GroupDetail<UserProfile, UserProfile>) bundle.getSerializable(ARGS_GROUP_DETAIL);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pager_join_two;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.addOnBackListener(this);
        mPayGroupView.setOnSelectPayWayListener(this);
        mPayCalculateItemView.setOnMoneyChangeCallback(this);
    }

    @Override
    protected void initData() {
        super.initData();
        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.group_avatar).error(R.mipmap.group_avatar).getOptions();
        Glide.with(this).load(mGroupDetail.avatar).apply(options).into(mIvGroupIcon);
        mTvDesc.setText(mGroupDetail.name);
        mTvGroupMoney.setText(String.valueOf(mGroupDetail.monthly_price));
        mPayCalculateItemView.setDefaultMoney(0.1f);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            String result = data.getExtras().getString("pay_result");
       /* 处理返回值
        * "success" - 支付成功
        * "fail"    - 支付失败
        * "cancel"  - 取消支付
        * "invalid" - 支付插件未安装（一般是微信客户端未安装的情况）
        * "unknown" - app进程异常被杀死(一般是低内存状态下,app进程被杀死)
        */
            String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
            String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
        }

    }

    @Override
    protected void initPresenter() {
        super.initPresenter();

        PayGroupPresenter.init(this);
    }

    @OnClick({R.id.bt_pay})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_pay:
                mPresenter.payAmount(this, mPayChannel, mGroupDetail, mPayCalculateItemView.getCurrentMoney(), mPayCalculateItemView.getCurrentDuration());
                break;
            default:
                break;
        }
    }

    @Override
    public void onSelectWechatPayWay() {
        this.mPayChannel = "wx";
    }

    @Override
    public void onSelectAlipayWay() {
        this.mPayChannel = "alipay";
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onMoneyChange(float money) {
        mBtPay.setEnabled(money > 0.0f);
        mBtPay.setAlpha(money > 0.0f ? 1.0f : 0.5f);
    }

    @Override
    public void bindPresenter(PayGroupContract.Presenter presenter) {
        this.mPresenter = (PayGroupPresenter) presenter;
    }

    @Override
    public void onFailure(String error) {
        showToast(error);
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onPayAmountSuccess(Order order) {

    }
}

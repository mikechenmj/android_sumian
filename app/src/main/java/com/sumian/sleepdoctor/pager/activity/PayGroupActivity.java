package com.sumian.sleepdoctor.pager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.main.MainActivity;
import com.sumian.sleepdoctor.pager.contract.PayGroupContract;
import com.sumian.sleepdoctor.pager.dialog.PayDialog;
import com.sumian.sleepdoctor.pager.presenter.PayGroupPresenter;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;
import com.sumian.sleepdoctor.widget.TitleBar;
import com.sumian.sleepdoctor.widget.dialog.ActionLoadingDialog;
import com.sumian.sleepdoctor.widget.pay.PayCalculateItemView;
import com.sumian.sleepdoctor.widget.pay.PayItemGroupView;

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

public class PayGroupActivity extends BaseActivity<PayGroupPresenter> implements View.OnClickListener, PayItemGroupView.OnSelectPayWayListener, TitleBar.OnBackListener, PayCalculateItemView.OnMoneyChangeCallback, PayGroupContract.View {

    private static final String TAG = PayGroupActivity.class.getSimpleName();

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

    private ActionLoadingDialog mActionLoadingDialog;

    private PayDialog mPayDialog;

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
        mPayDialog = new PayDialog(root.getContext()).bindContentView(R.layout.dialog_pay);
        mPayDialog.setOwnerActivity(this);
    }

    @Override
    protected void initData() {
        super.initData();
        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.group_avatar).error(R.mipmap.group_avatar).getOptions();
        Glide.with(this).load(mGroupDetail.avatar).apply(options).into(mIvGroupIcon);
        mTvDesc.setText(mGroupDetail.name);
        mTvGroupMoney.setText(String.format(Locale.getDefault(), "%.2f", mGroupDetail.monthly_price / 100.0f));
        mPayCalculateItemView.setDefaultMoney(mGroupDetail.monthly_price);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onPayActivityResultDelegate(requestCode, resultCode, data);
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
                mPresenter.CreatePayOrder(this, mPayChannel, mGroupDetail, mPayCalculateItemView.getCurrentMoney(), mPayCalculateItemView.getCurrentDuration());
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
        onBack();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBack();
    }

    private void onBack() {
        if (mActionLoadingDialog != null) {
            mActionLoadingDialog.dismissAllowingStateLoss();
        }
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
        this.mPayDialog.bindPresenter(presenter);
    }

    @Override
    public void onFailure(String error) {
        showToast(error);
    }

    @Override
    public void onBegin() {
        mActionLoadingDialog = new ActionLoadingDialog().show(getSupportFragmentManager());
    }

    @Override
    public void onFinish() {
        if (mActionLoadingDialog != null) {
            mActionLoadingDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onCreatePayOrderSuccess() {
        mPresenter.doPay(this);
        showToast(R.string.create_order_success);
    }

    @Override
    public void onOrderPaySuccess(String payMsg) {
        //showToast(R.string.pay_success);
        mPresenter.clearPayAction();
        mPayDialog.setPayStatus(PayDialog.PAY_SUCCESS).show();
    }

    @Override
    public void onOrderPayFailed(String payMsg) {
        showToast(payMsg);
        mPayDialog.setPayStatus(PayDialog.PAY_FAILED).show();
    }

    @Override
    public void onOrderPayInvalid(String payMsg) {
        showToast(payMsg);
        mPayDialog.setPayStatus(PayDialog.PAY_INVALID).show();
    }

    @Override
    public void onOrderPayCancel(String payMsg) {
        showToast(payMsg);
    }

    @Override
    public void onCheckOrderPayIsOk() {
        finish();
        MainActivity.showClearTop(this, MainActivity.class);
    }

    @Override
    public void onCheckOrderPayIsInvalid(String invalidError) {
        onCheckOrderPayIsOk();
    }
}

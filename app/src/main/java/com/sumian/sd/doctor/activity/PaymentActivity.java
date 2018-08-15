package com.sumian.sd.doctor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.sumian.common.image.ImageLoader;
import com.sumian.sd.R;
import com.sumian.sd.base.ActivityLauncher;
import com.sumian.sd.base.SdBaseActivity;
import com.sumian.sd.doctor.bean.DoctorService;
import com.sumian.sd.doctor.bean.DoctorServicePackage;
import com.sumian.sd.doctor.bean.PayOrder;
import com.sumian.sd.doctor.contract.PayContract;
import com.sumian.sd.doctor.dialog.PayDialog;
import com.sumian.sd.doctor.presenter.PayPresenter;
import com.sumian.sd.widget.TitleBar;
import com.sumian.sd.widget.dialog.ActionLoadingDialog;
import com.sumian.sd.widget.pay.PayCalculateItemView;
import com.sumian.sd.widget.pay.PayItemGroupView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author sm
 * on 2018/1/22.
 * desc:
 */

public class PaymentActivity extends SdBaseActivity<PayPresenter> implements View.OnClickListener, PayItemGroupView.OnSelectPayWayListener, TitleBar.OnBackClickListener, PayCalculateItemView.OnMoneyChangeCallback, PayContract.View {

    private static final String TAG = PaymentActivity.class.getSimpleName();

    private static final String ARGS_DOCTOR_SERVICE = "com.sumian.app.extra.doctor.service";
    private static final String ARGS_DOCTOR_SERVICE_PACKAGE_ID = "com.sumian.app.extra.doctor.service.packageId";

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.lay_group_icon)
    QMUIRadiusImageView mIvGroupIcon;

    @BindView(R.id.tv_name)
    TextView mTvName;

    @BindView(R.id.pay_calculate_item_view)
    PayCalculateItemView mPayCalculateItemView;

    @BindView(R.id.tv_desc)
    TextView mTvDesc;

    @BindView(R.id.pay_group_view)
    PayItemGroupView mPayGroupView;

    @BindView(R.id.bt_pay)
    Button mBtPay;

    private String mPayChannel = "wx";

    private ActionLoadingDialog mActionLoadingDialog;

    private PayDialog mPayDialog;

    private DoctorService mDoctorService;

    private DoctorServicePackage mServicePackage;

    private DoctorServicePackage.ServicePackage mPackage;

    public static void startForResult(ActivityLauncher launcher, @NonNull DoctorService doctorService, int packageId, int requestCode) {
        Intent intent = new Intent(launcher.getActivity(), PaymentActivity.class);
        intent.putExtra(ARGS_DOCTOR_SERVICE, doctorService);
        intent.putExtra(ARGS_DOCTOR_SERVICE_PACKAGE_ID, packageId);
        launcher.startActivityForResult(intent, requestCode);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle != null) {
            this.mDoctorService = bundle.getParcelable(ARGS_DOCTOR_SERVICE);
            int packageId = bundle.getInt(ARGS_DOCTOR_SERVICE_PACKAGE_ID);
            for (DoctorServicePackage servicePackage : mDoctorService.getService_packages()) {
                if (servicePackage.getId() == packageId) {
                    this.mServicePackage = servicePackage;
                    this.mPackage = mServicePackage.getPackages().get(0);
                    break;
                }
            }
        }
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_shopping_car;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.setOnBackClickListener(this);
        mPayGroupView.setOnSelectPayWayListener(this);
        mPayCalculateItemView.setOnMoneyChangeCallback(this);
        mPayDialog = new PayDialog(root.getContext()).bindContentView(R.layout.dialog_pay);
        mPayDialog.setOwnerActivity(this);
        mPayDialog.bindPresenter(mPresenter);
    }

    @Override
    protected void initData() {
        super.initData();
        ImageLoader.loadImage(mDoctorService.getIcon(), mIvGroupIcon);
        mTvName.setText(mServicePackage.getName());
        mTvDesc.setText(mServicePackage.getIntroduction());

        mPayCalculateItemView.setDefaultMoney(mPackage.getUnit_price());
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        PayPresenter.Companion.init(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onPayActivityResultDelegate(requestCode, resultCode, data);
    }

    @Override
    @OnClick({R.id.bt_pay})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_pay:
                PayOrder payOrder = new PayOrder(mPayCalculateItemView.getCurrentMoney(), mPayChannel, "cny", mDoctorService.getName(), mDoctorService.getDescription(), mPackage.getId(), mPayCalculateItemView.getCurrentBuyCount());
                mPresenter.createPayOrder(this, payOrder);
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
            mActionLoadingDialog.dismiss();
        }
        finish();
    }

    @Override
    public void onMoneyChange(double money) {
        mBtPay.setEnabled(money > 0.00f);
        mBtPay.setAlpha(money > 0.00f ? 1.00f : 0.50f);
    }

    @Override
    public void setPresenter(PayContract.Presenter presenter) {
        this.mPresenter = (PayPresenter) presenter;
    }

    @Override
    public void onFailure(String error) {
        showToast(error);
        if (mActionLoadingDialog != null) {
            mActionLoadingDialog.dismiss();
        }
    }

    @Override
    public void onBegin() {
        mActionLoadingDialog = new ActionLoadingDialog().show(getSupportFragmentManager());
    }

    @Override
    public void onFinish() {
        if (mActionLoadingDialog != null) {
            mActionLoadingDialog.getDialog().cancel();
        }
    }

    @Override
    public void onCreatePayOrderSuccess() {
        mPresenter.doPay(this);
        showToast(R.string.create_order_success);
    }

    @Override
    public void onOrderPaySuccess(@NonNull String payMsg) {
        if (!mPayDialog.isShowing()) {
            mPayDialog.setPayStatus(PayDialog.PAY_SUCCESS).show();
        }
    }

    @Override
    public void onOrderPayFailed(@NonNull String payMsg) {
        showToast(payMsg);
        if (!mPayDialog.isShowing()) {
            mPayDialog.setPayStatus(PayDialog.PAY_FAILED).show();
        }
    }

    @Override
    public void onOrderPayInvalid(@NonNull String payMsg) {
        showToast(payMsg);
        if (!mPayDialog.isShowing()) {
            mPayDialog.setPayStatus(PayDialog.PAY_INVALID).show();
        }
    }

    @Override
    public void onOrderPayCancel(@NonNull String payMsg) {
        showToast(payMsg);
        if (mActionLoadingDialog != null) {
            mActionLoadingDialog.dismiss();
        }
    }

    @Override
    public void onCheckOrderPayIsOk() {
        if (mPayDialog != null && mPayDialog.isShowing()) {
            mPayDialog.cancel();
        }
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onCheckOrderPayIsInvalid(@NonNull String invalidError) {
        onCheckOrderPayIsOk();
    }
}

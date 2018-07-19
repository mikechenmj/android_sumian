package com.sumian.sleepdoctor.doctor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.ActivityLauncher;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.doctor.bean.DoctorService;
import com.sumian.sleepdoctor.doctor.bean.DoctorServicePackage;
import com.sumian.sleepdoctor.doctor.bean.DoctorServiceShopData;
import com.sumian.sleepdoctor.doctor.bean.PayOrder;
import com.sumian.sleepdoctor.doctor.contract.PayContract;
import com.sumian.sleepdoctor.doctor.dialog.PayDialog;
import com.sumian.sleepdoctor.doctor.presenter.PayPresenter;
import com.sumian.sleepdoctor.widget.TitleBar;
import com.sumian.sleepdoctor.widget.dialog.ActionLoadingDialog;
import com.sumian.sleepdoctor.widget.pay.PayCalculateItemView;
import com.sumian.sleepdoctor.widget.pay.PayItemGroupView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author sm
 * on 2018/1/22.
 * desc:
 */

public class PaymentActivity extends BaseActivity<PayPresenter> implements View.OnClickListener, PayItemGroupView.OnSelectPayWayListener, TitleBar.OnBackClickListener, PayCalculateItemView.OnMoneyChangeCallback, PayContract.View {
    private static final String TAG = PaymentActivity.class.getSimpleName();
    private static final String ARGS_DOCTOR_SERVICE = "com.sumian.app.extra.doctor.service";
    private static final String ARGS_DOCTOR_SERVICE_PACKAGE_ID = "com.sumian.app.extra.doctor.service.packageId";
    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.lay_group_icon)
    QMUIRadiusImageView mIvGroupIcon;

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

    private ActionLoadingDialog mActionLoadingDialog;

    private PayDialog mPayDialog;

    private DoctorService mDoctorService;
    private int mPackageId;
    private DoctorServicePackage mServicePackage;

    private static void startForResult(ActivityLauncher launcher, DoctorService doctorService, int packageId, int requestCode) {
        Intent intent = new Intent(launcher.getActivity(), PaymentActivity.class);
        intent.putExtra(ARGS_DOCTOR_SERVICE, doctorService);
        intent.putExtra(ARGS_DOCTOR_SERVICE_PACKAGE_ID, packageId);
        launcher.startActivityForResult(intent, requestCode);
    }

    public static void startForResult(ActivityLauncher launcher, DoctorService doctorService, int requestCode) {
        startForResult(launcher, doctorService, doctorService.getPackages().get(0).getId(), requestCode);
    }

    public static void startForResult(ActivityLauncher launcher, DoctorServiceShopData doctorServiceShopData, int requestCode) {
        startForResult(launcher, doctorServiceShopData.getService(), doctorServiceShopData.getPackageId(), requestCode);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle != null) {
            this.mDoctorService = bundle.getParcelable(ARGS_DOCTOR_SERVICE);
            this.mPackageId = bundle.getInt(ARGS_DOCTOR_SERVICE_PACKAGE_ID);
            for (DoctorServicePackage servicePackage : mDoctorService.getPackages()) {
                if (servicePackage.getId() == mPackageId) {
                    mServicePackage = servicePackage;
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
        RequestOptions requestOptions = RequestOptions.placeholderOf(R.mipmap.ic_group_avatar).error(R.mipmap.ic_group_avatar);
        Glide.with(this).load(mDoctorService.getIcon()).apply(requestOptions).into(mIvGroupIcon);
        mTvDesc.setText(mDoctorService.getName());
        String priceText = getDoctorServicePackage().getPrice_text();
        SpannableString spannableString = new SpannableString(priceText);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.t4_color)), 0, priceText.indexOf("元"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvGroupMoney.setText(mDoctorService.getDescription());
        mPayCalculateItemView.setDefaultMoney(getDoctorServicePackage().getUnit_price());
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
                PayOrder payOrder = new PayOrder(mPayCalculateItemView.getCurrentMoney(), mPayChannel, "cny", mDoctorService.getName(), mDoctorService.getDescription(), getDoctorServicePackage().getId(), mPayCalculateItemView.getCurrentBuyCount());
                mPresenter.createPayOrder(this, payOrder);
                break;
            default:
                break;
        }
    }

    private DoctorServicePackage getDoctorServicePackage() {
        return mServicePackage;
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

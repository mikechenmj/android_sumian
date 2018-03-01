package com.sumian.sleepdoctor.pager.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.pingplusplus.android.Pingpp;
import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.pager.bean.Order;
import com.sumian.sleepdoctor.pager.bean.OrderDetail;
import com.sumian.sleepdoctor.pager.contract.PayGroupContract;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sm
 * on 2018/1/24.
 * desc:
 */

public class PayGroupPresenter implements PayGroupContract.Presenter {

    private static final String TAG = PayGroupPresenter.class.getSimpleName();

    private PayGroupContract.View mView;
    private Order mOrder;

    private String mOrderNo;

    private PayGroupPresenter(PayGroupContract.View view) {
        view.bindPresenter(this);
        mView = view;
    }

    public static void init(PayGroupContract.View view) {
        new PayGroupPresenter(view);
    }

    @Override
    public void CreatePayOrder(Activity activity, String channel, GroupDetail<UserProfile, UserProfile> groupDetail, float money, int count) {

        if (mOrder != null) {
            doPay(activity);
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("amount", money);
        map.put("channel", channel);
        map.put("currency", "cny");
        map.put("subject", groupDetail.name);
        map.put("body", groupDetail.description);
        map.put("package_id", groupDetail.packages.get(0).id);
        map.put("quantity", count);

        mView.onBegin();

        AppManager
                .getHttpService()
                .createOrder(map)
                .enqueue(new BaseResponseCallback<Order>() {
                    @Override
                    protected void onSuccess(Order response) {
                        mOrder = response;
                        mOrderNo = response.order_no;
                        mView.onCreatePayOrderSuccess();
                    }

                    @Override
                    protected void onFailure(String error) {
                        mView.onFailure(error);
                    }

                    @Override
                    protected void onFinish() {
                        super.onFinish();
                        mView.onFinish();
                    }

                    @Override
                    protected void onNotFound(String error) {
                        super.onNotFound(error);
                        mOrder = null;
                        mView.onFailure(error);
                    }
                });

    }

    @Override
    public void CheckPayOrder() {
        if (TextUtils.isEmpty(mOrderNo)) {
            return;
        }

        mView.onBegin();

        AppManager.getHttpService().getOrderDetail(mOrderNo).enqueue(new BaseResponseCallback<OrderDetail>() {
            @Override
            protected void onSuccess(OrderDetail response) {
                mView.onCheckOrderPayIsOk();
            }

            @Override
            protected void onFailure(String error) {
                mView.onFailure(error);
                mView.onCheckOrderPayIsInvalid(error);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mView.onFinish();
            }
        });
    }

    @Override
    public void doPay(Activity activity) {
        Pingpp.DEBUG = BuildConfig.DEBUG;
        Pingpp.enableDebugLog(BuildConfig.DEBUG);
        Pingpp.createPayment(activity, JSON.toJSONString(mOrder));
    }

    @Override
    public void clearPayAction() {
        this.mOrder = null;
       // this.mOrderNo = null;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onPayActivityResultDelegate(int requestCode, int resultCode, Intent data) {

        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {

            String result = data.getExtras().getString("pay_result");

            @StringRes int payResultMsg;
            switch (result) {
                case "success":
                    payResultMsg = R.string.pay_success;
                    mView.onOrderPaySuccess(App.Companion.getAppContext().getString(payResultMsg));
                    break;
                case "fail":
                    payResultMsg = R.string.pay_failed;

                    String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                    // String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息

                    mView.onOrderPayFailed(App.Companion.getAppContext().getString(payResultMsg) + "," + errorMsg);
                    break;
                case "cancel":
                    payResultMsg = R.string.pay_cancel;
                    mView.onOrderPayCancel(App.Companion.getAppContext().getString(payResultMsg));
                    clearPayAction();
                    break;
                case "invalid":
                    payResultMsg = R.string.pay_invalid;
                    mView.onOrderPayInvalid(App.Companion.getAppContext().getString(payResultMsg));
                    break;
                case "unknown":
                default:
                    payResultMsg = R.string.pay_unknown;
                    mView.onOrderPayFailed(App.Companion.getAppContext().getString(payResultMsg));
                    break;
            }

       /* 处理返回值
        * "success" - 支付成功
        * "fail"    - 支付失败
        * "cancel"  - 取消支付
        * "invalid" - 支付插件未安装（一般是微信客户端未安装的情况）
        * "unknown" - app进程异常被杀死(一般是低内存状态下,app进程被杀死)
        */

            //  Log.e(TAG, "onActivityResult: -------------->result=" + result + "  error_msg=" + errorMsg + "  extra_msg=" + extraMsg);

        }
    }
}

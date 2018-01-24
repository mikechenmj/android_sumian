package com.sumian.sleepdoctor.pager.presenter;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.pingplusplus.android.Pingpp;
import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.pager.bean.Order;
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

    private PayGroupContract.View mView;

    private PayGroupPresenter(PayGroupContract.View view) {
        view.bindPresenter(this);
        mView = view;
    }

    public static void init(PayGroupContract.View view) {
        new PayGroupPresenter(view);
    }

    @Override
    public void payAmount(Activity activity, String channel, GroupDetail<UserProfile, UserProfile> groupDetail, float money, int count) {

        Map<String, Object> map = new HashMap<>();
        map.put("amount", money);
        map.put("channel", channel);
        map.put("currency", "cny");
        map.put("subject", groupDetail.name);
        map.put("body", groupDetail.description);

        Order.MetaData metaData = new Order.MetaData();
        metaData.package_id = groupDetail.packages.get(0).id;
        metaData.quantity = count;

        HashMap<String, Integer> metadata = new HashMap<>();
        metadata.put("metadata", groupDetail.packages.get(0).id);
        metadata.put("metadata[quantity]", count);

        //String json = JSON.toJSONString(metadata);
        map.put()

        map.put("metadata[package_id]", groupDetail.packages.get(0).id);
        map.put("metadata[quantity]", count);

        mView.onBegin();

        AppManager
                .getHttpService()
                .createOrder(map)
                .enqueue(new BaseResponseCallback<Order>() {
                    @Override
                    protected void onSuccess(Order response) {
                        mView.onPayAmountSuccess(response);
                        Pingpp.DEBUG = BuildConfig.DEBUG;
                        Pingpp.enableDebugLog(true);
                        Pingpp.createPayment(activity, JSON.toJSONString(response));
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
                        mView.onFailure(error);
                    }
                });

    }
}

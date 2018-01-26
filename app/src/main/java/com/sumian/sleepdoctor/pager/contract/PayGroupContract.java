package com.sumian.sleepdoctor.pager.contract;

import android.app.Activity;
import android.content.Intent;

import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.base.BasePresenter;
import com.sumian.sleepdoctor.base.BaseView;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;

/**
 * Created by sm
 * on 2018/1/24.
 * desc:
 */

public interface PayGroupContract {


    interface View extends BaseView<Presenter> {

        void onCreatePayOrderSuccess();

        void onOrderPaySuccess(String payMsg);

        void onOrderPayFailed(String payMsg);

        void onOrderPayInvalid(String payMsg);

    }

    interface Presenter extends BasePresenter {

        void CreatePayOrder(Activity activity, String channel, GroupDetail<UserProfile, UserProfile> groupDetail, float money, int count);

        void CheckPayOrder();

        void doPay(Activity activity);

        void clearPayAction();

        void onPayActivityResultDelegate(int requestCode, int resultCode, Intent data);
    }
}

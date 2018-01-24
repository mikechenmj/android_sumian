package com.sumian.sleepdoctor.pager.contract;

import android.app.Activity;

import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.base.BasePresenter;
import com.sumian.sleepdoctor.base.BaseView;
import com.sumian.sleepdoctor.pager.bean.Order;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;

/**
 * Created by sm
 * on 2018/1/24.
 * desc:
 */

public interface PayGroupContract {


    interface View extends BaseView<Presenter> {

        void onPayAmountSuccess(Order order);

    }

    interface Presenter extends BasePresenter {

        void payAmount(Activity activity, String channel, GroupDetail<UserProfile, UserProfile> groupDetail, float money, int count);
    }
}

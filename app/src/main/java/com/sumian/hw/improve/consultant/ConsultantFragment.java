package com.sumian.hw.improve.consultant;

import android.view.View;

import com.hyphenate.helpdesk.easeui.UIProvider;
import com.sumian.hw.base.BasePagerFragment;
import com.sumian.hw.leancloud.HwLeanCloudHelper;
import com.sumian.hw.leancloud.activity.MsgActivity;
import com.sumian.hw.setting.dialog.ContactDialog;
import com.sumian.hw.widget.TitleBar;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.AppManager;

/**
 * Created by jzz
 * <p>
 * on 10/12/2017.
 * <p>
 * desc:咨询
 */

public class ConsultantFragment extends BasePagerFragment implements View.OnClickListener, HwLeanCloudHelper.OnShowMsgDotCallback {

    private static final String TAG = ConsultantFragment.class.getSimpleName();

    TitleBar mTitleBar;
    View mDoctorServiceDot;
    View mCustomerServiceDot;

    public static ConsultantFragment newInstance() {
        return new ConsultantFragment();
    }

    @Override
    public void onEnterTab() {
        AppManager.getOpenAnalytics().onClickEvent(getContext(), "advice_tabbar_Ry");
        UIProvider.getInstance().isHaveMsgSize();
        showDot(mCustomerServiceDot, HwLeanCloudHelper.isHaveCustomerMsg());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_fragment_tab_consultant;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar = root.findViewById(R.id.title_bar);
        mDoctorServiceDot = root.findViewById(R.id.doctor_service_dot);
        mCustomerServiceDot = root.findViewById(R.id.customer_service_dot);

        root.findViewById(R.id.lay_doctor_service).setOnClickListener(this);
        root.findViewById(R.id.lay_customer_call_service).setOnClickListener(this);
        root.findViewById(R.id.lay_customer_online_service).setOnClickListener(this);

        mTitleBar.hideBack();
        HwLeanCloudHelper.addOnAdminMsgCallback(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.lay_doctor_service) {
            MsgActivity.show(v.getContext(), HwLeanCloudHelper.SERVICE_TYPE_ONLINE_DOCTOR);

        } else if (i == R.id.lay_customer_call_service) {
            new ContactDialog().show(getFragmentManager(), ContactDialog.class.getSimpleName());

        } else if (i == R.id.lay_customer_online_service) {
            UIProvider.getInstance().clearCacheMsg();
            HwLeanCloudHelper.checkLoginEasemob(HwLeanCloudHelper::startEasemobChatRoom);

        } else {
        }
    }

    @Override
    public void onShowMsgDotCallback(int adminMsgLen, int doctorMsgLen, int customerMsgLen) {
        onHideMsgCallback(adminMsgLen, doctorMsgLen, customerMsgLen);
    }

    @Override
    public void onHideMsgCallback(int adminMsgLen, int doctorMsgLen, int customerMsgLen) {
        showDot(mDoctorServiceDot, doctorMsgLen > 0);
        showDot(mCustomerServiceDot, customerMsgLen > 0);
    }


    private void showDot(View dot, boolean isShow) {
        runOnUiThread(() -> {
            if (dot != null) {
                dot.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }
}

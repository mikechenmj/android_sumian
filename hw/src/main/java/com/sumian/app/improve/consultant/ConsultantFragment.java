package com.sumian.app.improve.consultant;

import android.view.View;

import com.hyphenate.helpdesk.easeui.UIProvider;
import com.sumian.app.R;
import com.sumian.app.app.AppManager;
import com.sumian.app.base.BasePagerFragment;
import com.sumian.app.leancloud.LeanCloudHelper;
import com.sumian.app.leancloud.activity.MsgActivity;
import com.sumian.app.setting.dialog.ContactDialog;
import com.sumian.app.widget.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * <p>
 * on 10/12/2017.
 * <p>
 * desc:咨询
 */

public class ConsultantFragment extends BasePagerFragment implements View.OnClickListener, LeanCloudHelper.OnShowMsgDotCallback {

    private static final String TAG = ConsultantFragment.class.getSimpleName();

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    @BindView(R.id.doctor_service_dot)
    View mDoctorServiceDot;
    @BindView(R.id.customer_service_dot)
    View mCustomerServiceDot;

    public static ConsultantFragment newInstance() {
        return new ConsultantFragment();
    }

    @Override
    public void onEnterTab() {
        AppManager.getOpenAnalytics().onClickEvent(getContext(), "advice_tabbar_Ry");
        UIProvider.getInstance().isHaveMsgSize();
        showDot(mCustomerServiceDot, LeanCloudHelper.isHaveCustomerMsg());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_fragment_tab_consultant;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.hideBack();
        LeanCloudHelper.addOnAdminMsgCallback(this);
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick({R.id.lay_doctor_service, R.id.lay_customer_call_service, R.id.lay_customer_online_service})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_doctor_service:
                MsgActivity.show(v.getContext(), LeanCloudHelper.SERVICE_TYPE_ONLINE_DOCTOR);
                break;
            case R.id.lay_customer_call_service:
                new ContactDialog().show(getFragmentManager(), ContactDialog.class.getSimpleName());
                break;
            case R.id.lay_customer_online_service:
                UIProvider.getInstance().clearCacheMsg();
                LeanCloudHelper.checkLoginEasemob(LeanCloudHelper::startEasemobChatRoom);
                break;
            default:
                break;
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

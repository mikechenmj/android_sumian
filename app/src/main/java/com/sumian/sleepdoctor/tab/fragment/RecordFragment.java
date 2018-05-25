package com.sumian.sleepdoctor.tab.fragment;

import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.BaseFragment;
import com.sumian.sleepdoctor.improve.widget.DoctorServiceItemView;
import com.sumian.sleepdoctor.widget.dialog.ActionLoadingDialog;
import com.sumian.sleepdoctor.widget.dialog.SumianAlertDialog;

import butterknife.BindView;

public class RecordFragment extends BaseFragment {
    @BindView(R.id.dsiv)
    DoctorServiceItemView mDoctorServiceItemView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_record;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mDoctorServiceItemView.setTitle("远程睡眠管理服务");
        mDoctorServiceItemView.setDesc("连续7天监测你的睡眠日记");
        mDoctorServiceItemView.setPrice(50f);
        mDoctorServiceItemView.loadImage(R.mipmap.ic_doctor_service_item_view_sleep_diary);

        mDoctorServiceItemView.setOnClickListener(v -> {
            ActionLoadingDialog loadingDialog = new ActionLoadingDialog();
            loadingDialog.show(getFragmentManager());
        });

        showOpenNotificationDialog();
    }

    private void showOpenNotificationDialog() {
        SumianAlertDialog.create()
                .setCloseIconVisible(true)
                .setTopIconResource(R.mipmap.ic_notification_alert)
                .setTitle(R.string.open_notification)
                .setMessage(R.string.open_notification_and_receive_doctor_response)
                .setRightBtn(R.string.open_notification, v -> openNotification())
                .show(getFragmentManager());
    }

    private void openNotification() {

    }
}

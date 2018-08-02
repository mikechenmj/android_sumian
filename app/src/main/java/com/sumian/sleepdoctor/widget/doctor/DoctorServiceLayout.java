package com.sumian.sleepdoctor.widget.doctor;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.sumian.common.image.ImageLoader;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.advisory.activity.AdvisoryListActivity;
import com.sumian.sleepdoctor.doctor.bean.DoctorService;
import com.sumian.sleepdoctor.record.SleepRecordActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/5/31 16:50
 * desc:  医生服务容器
 *
 * @author sm
 */
public class DoctorServiceLayout extends LinearLayout implements View.OnClickListener {

    @BindView(R.id.iv_service_icon)
    QMUIRadiusImageView ivServiceIcon;
    @BindView(R.id.tv_service_name)
    TextView tvServiceName;
    @BindView(R.id.tv_service_desc)
    TextView tvServiceDesc;

    @BindView(R.id.lay_service_action)
    LinearLayout layServiceAction;
    @BindView(R.id.tv_service_use_desc)
    TextView tvServiceUseDesc;
    @BindView(R.id.tv_service_action)
    TextView tvServiceAction;

    @BindView(R.id.v_divider)
    View bottomDivider;

    public DoctorServiceLayout(Context context) {
        this(context, null);
    }

    public DoctorServiceLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoctorServiceLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        ButterKnife.bind(inflate(context, R.layout.lay_item_doctor_service, this));
    }


    public void invalidDoctorService(DoctorService doctorService, boolean isGoneDivider) {
        @DrawableRes int serviceIconId = R.mipmap.ic_img_sleepdiary_avatar;
        @StringRes int nextActionId = R.string.ask_questions;
        switch (doctorService.getType()) {
            case DoctorService.SERVICE_TYPE_ADVISORY:
                serviceIconId = R.mipmap.ic_img_advisory_avatar;
                nextActionId = R.string.ask_questions;
                break;
            case DoctorService.SERVICE_TYPE_SLEEP_REPORT:
                serviceIconId = R.mipmap.ic_img_sleepdiary_avatar;
                nextActionId = R.string.go_records;
                break;
            default:
                break;
        }

        load(doctorService.getIcon(), serviceIconId, ivServiceIcon);

        tvServiceName.setText(doctorService.getName());
        tvServiceDesc.setText(doctorService.getNot_buy_description());

        if (doctorService.getLast_count() == 0) {
            layServiceAction.setVisibility(View.GONE);
        } else {
            layServiceAction.setVisibility(View.VISIBLE);
        }

        tvServiceUseDesc.setText(doctorService.getRemaining_description());

        tvServiceAction.setText(nextActionId);

        bottomDivider.setVisibility(isGoneDivider ? View.GONE : View.VISIBLE);
    }

    private void load(String url, @DrawableRes int defaultIconId, ImageView iv) {
        ImageLoader.loadImage(url, iv, defaultIconId, defaultIconId);
    }

    @OnClick({R.id.tv_service_action})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_service_action:
                String actionText = tvServiceAction.getText().toString();
                switch (actionText) {
                    case "去提问":
                        AdvisoryListActivity.show(getContext(), AdvisoryListActivity.class);
                        break;
                    case "去记录":
                        SleepRecordActivity.Companion.launch(getContext());
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }
}

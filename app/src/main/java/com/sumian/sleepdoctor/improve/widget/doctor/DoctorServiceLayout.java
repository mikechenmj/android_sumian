package com.sumian.sleepdoctor.improve.widget.doctor;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.improve.doctor.bean.DoctorService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/5/31 16:50
 * desc:  医生服务容器
 **/
public class DoctorServiceLayout extends LinearLayout {

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
            case DoctorService.GRAPHIC_SERVICE_TYPE:
                serviceIconId = R.mipmap.ic_img_advisory_avatar;
                nextActionId = R.string.ask_questions;
                break;
            case DoctorService.SLEEP_REPORT_TYPE:
                serviceIconId = R.mipmap.ic_img_sleepdiary_avatar;
                nextActionId = R.string.go_records;
                break;
            default:
                break;
        }

        load(doctorService.getIcon(), serviceIconId, ivServiceIcon);

        tvServiceName.setText(doctorService.getName());
        tvServiceDesc.setText(doctorService.getNot_buy_description());

        if (doctorService.getLast_count() == 0 && doctorService.getDay_last() == 0 && doctorService.getExpired_at() == 0) {
            layServiceAction.setVisibility(View.GONE);
        } else {
            layServiceAction.setVisibility(View.VISIBLE);
        }

        tvServiceUseDesc.setText(doctorService.getRemaining_description());

        tvServiceAction.setText(nextActionId);

        bottomDivider.setVisibility(isGoneDivider ? View.GONE : View.VISIBLE);

    }

    private void load(String url, @DrawableRes int defaultIconId, ImageView iv) {
        if (TextUtils.isEmpty(url)) {
            Glide.with(this).load(defaultIconId).into(iv);
        } else {
            Glide.with(this).load(url).into(iv);
        }
    }
}

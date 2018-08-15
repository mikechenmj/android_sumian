package com.sumian.sd.widget.doctor;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.sumian.common.image.ImageLoader;
import com.sumian.sd.R;
import com.sumian.sd.doctor.bean.DoctorService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/5/31 16:50
 * desc:  医生服务容器
 *
 * @author sm
 */
public class DoctorServiceLayout extends LinearLayout {

    @BindView(R.id.iv_service_icon)
    QMUIRadiusImageView ivServiceIcon;
    @BindView(R.id.tv_service_name)
    TextView tvServiceName;
    @BindView(R.id.tv_service_desc)
    TextView tvServiceDesc;

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
        switch (doctorService.getType()) {
            case DoctorService.SERVICE_TYPE_ADVISORY:
                serviceIconId = R.mipmap.ic_img_advisory_avatar;
                break;
            case DoctorService.SERVICE_TYPE_SLEEP_REPORT:
                serviceIconId = R.mipmap.ic_img_sleepdiary_avatar;
                break;
            case DoctorService.SERVICE_TYPE_PHONE_ADVISORY:
                serviceIconId = R.mipmap.ic_img_telephone_avatar;
            default:
                break;
        }

        load(doctorService.getIcon(), serviceIconId, ivServiceIcon);

        tvServiceName.setText(doctorService.getName());
        tvServiceDesc.setText(doctorService.getNot_buy_description());

        bottomDivider.setVisibility(isGoneDivider ? View.GONE : View.VISIBLE);
    }

    private void load(String url, @DrawableRes int defaultIconId, ImageView iv) {
        ImageLoader.loadImage(url, iv, defaultIconId, defaultIconId);
    }
}

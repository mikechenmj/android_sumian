package com.sumian.sd.widget.doctor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.helpdesk.easeui.UIProvider;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.sumian.common.image.ImageLoader;
import com.sumian.sd.R;
import com.sumian.sd.app.App;
import com.sumian.sd.doctor.activity.DoctorServiceWebActivity;
import com.sumian.sd.doctor.bean.Doctor;
import com.sumian.sd.doctor.bean.DoctorService;
import com.sumian.sd.kefu.KefuManager;
import com.sumian.sd.theme.three.SkinConfig;
import com.sumian.sd.widget.dialog.SumianAlertDialog;
import com.sumian.sd.widget.dialog.theme.LightTheme;
import com.sumian.sd.widget.fold.FoldLayout;
import com.sumian.sd.widget.refresh.SumianRefreshLayout;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/5/30 17:36
 * desc:医生详情
 **/
public class DoctorDetailLayout extends SumianRefreshLayout {

    private static final String TAG = DoctorDetailLayout.class.getSimpleName();

    @BindView(R.id.doctor_info)
    ConstraintLayout mDoctorDetailLayout;

    @BindView(R.id.iv_avatar)
    QMUIRadiusImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_department)
    TextView tvDepartment;

    @BindView(R.id.siv_customer_service)
    ImageView mIvCustomerService;

    @BindView(R.id.fold_layout)
    FoldLayout foldLayout;

    @BindView(R.id.lay_doctor_service_container)
    LinearLayout layDoctorServiceContainer;

    private Doctor mDoctor;

    public DoctorDetailLayout(@NonNull Context context) {
        this(context, null);
    }

    public DoctorDetailLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        ButterKnife.bind(inflate(context, R.layout.lay_doctor_detail_view, this));
    }

    @SuppressWarnings("ConstantConditions")
    public void invalidDoctor(Doctor doctor) {
        mDoctor = doctor;
        ImageLoader.loadImage(doctor.getAvatar(), ivAvatar, R.mipmap.ic_info_avatar_doctor, R.mipmap.ic_info_avatar_doctor);
        this.tvName.setText(doctor.getName());
        this.tvDepartment.setText(String.format(Locale.getDefault(), "%s %s", doctor.getHospital(), doctor.getDepartment()));
        this.foldLayout.setText(doctor.getIntroduction_no_tag());
        mIvCustomerService.setOnClickListener(v -> {
            UIProvider.getInstance().setThemeMode(SkinConfig.isInNightMode(App.getAppContext()) ? 0x02 : 0x01);
            UIProvider.getInstance().clearCacheMsg();
            KefuManager.launchKefuActivity();
        });

        mDoctorDetailLayout.setOnClickListener(v -> new SumianAlertDialog(getContext())
                .setTheme(new LightTheme())
                .goneTopIcon(true)
                .setTitle(R.string.doctor_info)
                .setCloseIconVisible(true)
                .setMessage(doctor.getIntroduction_no_tag())
                .setCancelable(true)
                //.setRightBtn(R.string.cancel, null)
                //.setLeftBtn(R.string.cancel, null)
                .show());

        appendDoctorServices(doctor);
        show();
    }

    private void appendDoctorServices(Doctor doctor) {
        if (doctor.getServices() != null && !doctor.getServices().isEmpty()) {
            this.layDoctorServiceContainer.removeViewsInLayout(2, layDoctorServiceContainer.getChildCount() - 2);
            this.layDoctorServiceContainer.setVisibility(VISIBLE);
            DoctorServiceLayout doctorServiceLayout;
            DoctorService doctorService;
            ArrayList<DoctorService> doctorServices = doctor.getServices();
            for (int i = 0; i < doctorServices.size(); i++) {
                doctorService = doctorServices.get(i);
                doctorServiceLayout = new DoctorServiceLayout(getContext());
                doctorServiceLayout.setTag(doctorService);
                doctorServiceLayout.setOnClickListener(v -> {
                    DoctorService cacheDoctorService = (DoctorService) v.getTag();
                    DoctorServiceWebActivity.show(getContext(), cacheDoctorService);
                    Log.e(TAG, "onClick: -------->" + cacheDoctorService.toString());
                });
                doctorServiceLayout.invalidDoctorService(doctorService, i == doctorServices.size() - 1);
                this.layDoctorServiceContainer.addView(doctorServiceLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            this.layDoctorServiceContainer.setVisibility(VISIBLE);
        } else {
            this.layDoctorServiceContainer.setVisibility(GONE);
        }
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void showMsgDot(boolean isHaveMsg) {
        mIvCustomerService.setImageResource(isHaveMsg ? R.drawable.ic_info_customerservice_reply : R.drawable.ic_info_customerservice);
    }
}

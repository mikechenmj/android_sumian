package com.sumian.sleepdoctor.improve.widget.doctor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.improve.doctor.activity.DoctorServiceWebActivity;
import com.sumian.sleepdoctor.improve.doctor.bean.Doctor;
import com.sumian.sleepdoctor.improve.doctor.bean.DoctorService;
import com.sumian.sleepdoctor.improve.widget.SumianRefreshLayout;
import com.sumian.sleepdoctor.improve.widget.fold.FoldLayout;

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

    @BindView(R.id.iv_avatar)
    QMUIRadiusImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_department)
    TextView tvDepartment;

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

    public void invalidDoctor(Doctor doctor) {
       // hideRefreshAnim();
        this.mDoctor = doctor;

        RequestOptions requestOptions = RequestOptions.errorOf(R.mipmap.ic_info_avatar_doctor).placeholder(R.mipmap.ic_info_avatar_doctor);
        Glide.with(this).load(doctor.getAvatar()).apply(requestOptions).into(ivAvatar);
        this.tvName.setText(doctor.getName());
        this.tvDepartment.setText(String.format(Locale.getDefault(), "%s %s", doctor.getHospital(), doctor.getDepartment()));
        this.foldLayout.setText(doctor.getIntroduction_no_tag());

        appendDoctorServices(doctor);
        show();
    }

    private void appendDoctorServices(Doctor doctor) {
        if (doctor.getServices() != null) {
            this.layDoctorServiceContainer.removeViewsInLayout(2, layDoctorServiceContainer.getChildCount() - 2);
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
        }
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

}

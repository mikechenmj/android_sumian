package com.sumian.sleepdoctor.improve.widget.doctor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.sumian.common.base.BaseRecyclerAdapter;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.improve.doctor.adapter.DoctorServiceAdapter;
import com.sumian.sleepdoctor.improve.doctor.bean.Doctor;
import com.sumian.sleepdoctor.improve.widget.fold.FoldLayout;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/5/30 17:36
 * desc:医生详情
 **/
public class DoctorDetailLayout extends FrameLayout {

    @BindView(R.id.iv_avatar)
    QMUIRadiusImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_department)
    TextView tvDepartment;

    @BindView(R.id.fold_layout)
    FoldLayout foldLayout;

    @BindView(R.id.recycler)
    RecyclerView recycler;

    private Doctor mDoctor;

    private DoctorServiceAdapter mDoctorServiceAdapter;

    public DoctorDetailLayout(@NonNull Context context) {
        this(context, null);
    }

    public DoctorDetailLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoctorDetailLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        ButterKnife.bind(inflate(context, R.layout.lay_doctor_detail_view, this));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(this.mDoctorServiceAdapter = new DoctorServiceAdapter());
    }

    public void invalidDoctor(Doctor doctor) {
        this.mDoctor = doctor;

        RequestOptions requestOptions = RequestOptions.errorOf(R.mipmap.ic_info_avatar_doctor).placeholder(R.mipmap.ic_info_avatar_doctor);
        Glide.with(this).load(doctor.getAvatar()).apply(requestOptions).into(ivAvatar);
        this.tvName.setText(doctor.getName());
        this.tvDepartment.setText(String.format(Locale.getDefault(), "%s %s", doctor.getHospital(), doctor.getTitle()));
        this.foldLayout.setText(doctor.getIntroduction());

        this.mDoctorServiceAdapter.addItems(doctor.getServices());
        show();
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }
}

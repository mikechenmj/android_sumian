package com.sumian.sleepdoctor.cbti.video;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.cbti.bean.CBTIMeta;
import com.sumian.sleepdoctor.cbti.bean.Course;
import com.sumian.sleepdoctor.cbti.contract.CBTIWeekLessonContract;
import com.sumian.sleepdoctor.cbti.presenter.CBTIWeekCoursePresenter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 视频全屏下的课程列表
 */
@SuppressWarnings("ConstantConditions")
public class LessonListDialog extends DialogFragment implements CBTIWeekLessonContract.View, View.OnClickListener {

    @BindView(R.id.lay_container)
    LinearLayout mLayContainer;

    private CBTIWeekLessonContract.Presenter mPresenter;
    private List<Course> mCourses;

    private OnCBTILessonListListener mListener;

    private Unbinder mUnbinder;

    private int mChapterId;
    private int mCurrentPosition;

    private AppCompatActivity mAppCompatActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_DialogWhenLarge_NoActionBar);
        this.mPresenter = CBTIWeekCoursePresenter.Companion.init(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(R.layout.lay_pop_cbti_lesson_list, container, false);
        rootView.setOnClickListener(this);
        mUnbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.getCBTIWeekLesson(mChapterId);
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    public LessonListDialog setup(AppCompatActivity appCompatActivity) {
        this.mAppCompatActivity = appCompatActivity;
        return this;
    }

    public LessonListDialog setChapterId(int chapterId, int position) {
        this.mChapterId = chapterId;
        this.mCurrentPosition = position;
        return this;
    }

    public LessonListDialog setListener(OnCBTILessonListListener listener) {
        mListener = listener;
        return this;
    }

    public LessonListDialog show() {
        show(mAppCompatActivity.getSupportFragmentManager(), this.getClass().getSimpleName());
        return this;
    }


    @Override
    public void onGetCBTIWeekLessonSuccess(@NotNull List<Course> courses) {
        mCourses = courses;
        updateCourses(mCurrentPosition, courses);
    }

    private void updateCourses(int position, @NotNull List<Course> courses) {
        mLayContainer.removeAllViews();
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);

            ConstraintLayout rootView = (ConstraintLayout) LayoutInflater.from(getContext()).inflate(R.layout.lay_dialog_cbti_lesson_list_item, mLayContainer, false);

            rootView.setTag(i);
            TextView tv_title = rootView.findViewById(R.id.tv_title);
            tv_title.setText(course.getTitle());

            if (position == i) {
                tv_title.setTextColor(getResources().getColor(R.color.b3_color));
            } else {
                tv_title.setTextColor(getResources().getColor(R.color.b2_color));
            }

            ImageView ivLock = rootView.findViewById(R.id.iv_lock);
            if (course.is_lock()) {
                tv_title.setTextColor(getResources().getColor(R.color.b2_alpha_50_color));
                ivLock.setVisibility(View.VISIBLE);
            } else {
                ivLock.setVisibility(View.INVISIBLE);
            }

            rootView.setOnClickListener(v -> {
                int p = (int) v.getTag();
                Course tmpCourse = mCourses.get(p);

                if (mListener != null) {
                    if (mListener.showCBTICourse(p, tmpCourse)) {
                        dismissAllowingStateLoss();
                    }
                }

                updateCourses(p, mCourses);

            });

            mLayContainer.addView(rootView);
        }
    }

    @Override
    public void onGetCBTIMetaSuccess(@NotNull CBTIMeta cbtiMeta) {

    }

    @Override
    public void onGetCBTIWeekLessonFailed(@NotNull String error) {

    }

    @Override
    public void onClick(View v) {
        dismissAllowingStateLoss();
    }

    interface OnCBTILessonListListener {

        boolean showCBTICourse(int position, Course course);
    }
}

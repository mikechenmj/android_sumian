package com.sumian.sddoctor.service.cbti.video;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sddoctor.R;
import com.sumian.sddoctor.service.cbti.bean.CBTIMeta;
import com.sumian.sddoctor.service.cbti.bean.Course;
import com.sumian.sddoctor.service.cbti.contract.CBTIWeekLessonContract;
import com.sumian.sddoctor.service.cbti.presenter.CBTIWeekCoursePresenter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

/**
 * 视频全屏下的课程列表
 */
@SuppressWarnings("ConstantConditions")
public class LessonListDialog extends DialogFragment implements CBTIWeekLessonContract.View, View.OnClickListener {

    private LinearLayout mLayContainer;

    private CBTIWeekLessonContract.Presenter mPresenter;
    private List<Course> mCourses;

    private OnCBTILessonListListener mListener;


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
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLayContainer = view.findViewById(R.id.lay_container);
        mPresenter.getCBTIWeekLesson(mChapterId);
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
        this.mCurrentPosition = position;
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
            ivLock.setVisibility(View.INVISIBLE);
            rootView.setOnClickListener(v -> {
                int p = (int) v.getTag();
                if (position == p) {
                    return;
                }
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

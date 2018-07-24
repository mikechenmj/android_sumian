package com.sumian.sleepdoctor.cbti.video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.cbti.bean.CBTIMeta;
import com.sumian.sleepdoctor.cbti.bean.Course;
import com.sumian.sleepdoctor.cbti.contract.CBTIWeekLessonContract;
import com.sumian.sleepdoctor.cbti.presenter.CBTIWeekCoursePresenter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by sm
 * <p>
 * on 2018/7/24
 * <p>
 * desc:  视频全屏 课程列表悬浮动画
 */
public class CoursesPopWindow extends QMUIPopup implements CBTIWeekLessonContract.View {

    private LinearLayout mLinearLayout;

    private CBTIWeekLessonContract.Presenter mPresenter;

    public CoursesPopWindow(Context context) {
        this(context, DIRECTION_TOP);
    }

    public CoursesPopWindow(Context context, int preferredDirection) {
        super(context, preferredDirection);
        this.mPresenter = CBTIWeekCoursePresenter.Companion.init(this);
    }

    public CoursesPopWindow setChapterId(int chapterId) {
        this.mPresenter.getCBTIWeekLesson(chapterId);
        return this;
    }

    @Override
    public void setContentView(View root) {
        super.setContentView(root);
    }

    @Override
    public void onGetCBTIWeekLessonSuccess(@NotNull List<Course> courses) {
        mLinearLayout.removeAllViews();
        for (int i = 0; i < courses.size(); i++) {

            Course course = courses.get(i);

            View itemView = LayoutInflater.from(mContext).inflate(R.layout.lay_dialog_cbti_lesson_list_item, (ViewGroup) mLinearLayout, false);

            TextView tv_title = itemView.findViewById(R.id.tv_title);
            tv_title.setText(course.getTitle());

            mLinearLayout.addView(itemView);
        }
    }

    @Override
    public void onGetCBTIMetaSuccess(@NotNull CBTIMeta cbtiMeta) {

    }

    @Override
    public void onGetCBTIWeekLessonFailed(@NotNull String error) {

    }
}

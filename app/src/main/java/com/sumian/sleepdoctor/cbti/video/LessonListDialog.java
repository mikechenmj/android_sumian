package com.sumian.sleepdoctor.cbti.video;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.cbti.bean.Course;

import java.util.List;

/**
 * 视频全屏下的课程列表
 */
@SuppressWarnings("ConstantConditions")
public class LessonListDialog extends Dialog {

    private LinearLayout mLinearLayout;
    private int mCurrentCheckedIndex;
    private List<Course> mCourses;

    public LessonListDialog(Context context) {
        super(context, R.style.dialog_change_clarity);
        init(context);
    }

    @SuppressLint("RtlHardcoded")
    private void init(Context context) {
        mLinearLayout = new LinearLayout(context);
        mLinearLayout.setGravity(Gravity.CENTER | Gravity.RIGHT);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT);
        setContentView(mLinearLayout, params);

            WindowManager.LayoutParams windowParams = getWindow().getAttributes();
           windowParams.width = NiceUtil.getScreenHeight(context);
          windowParams.height = NiceUtil.getScreenWidth(context);
         getWindow().setAttributes(windowParams);
    }

    public void setCourses(List<Course> items) {
        mCourses = items;
        mCurrentCheckedIndex = 0;
        for (int i = 0; i < items.size(); i++) {
            ConstraintLayout rootView = (ConstraintLayout) LayoutInflater.from(getContext()).inflate(R.layout.lay_dialog_cbti_lesson_list_item, mLinearLayout, false);
            rootView.setTag(i);
            TextView tv_title = rootView.findViewById(R.id.tv_title);
            tv_title.setText(items.get(i).getTitle());
            rootView.setOnClickListener(v -> {
                if (mListener != null) {
                    int checkIndex = (int) v.getTag();
                    if (checkIndex != mCurrentCheckedIndex) {
                        for (int j = 0; j < mLinearLayout.getChildCount(); j++) {
                            mLinearLayout.getChildAt(j).setSelected(checkIndex == j);
                        }
                        mListener.onClarityChanged(checkIndex);
                        mCurrentCheckedIndex = checkIndex;
                    } else {
                        mListener.onClarityNotChanged();
                    }
                }
                LessonListDialog.this.dismiss();
            });
            rootView.setSelected(i == mCurrentCheckedIndex);
            mLinearLayout.addView(rootView);
        }
    }

    public interface OnClarityChangedListener {
        /**
         * 切换清晰度后回调
         *
         * @param clarityIndex 切换到的清晰度的索引值
         */
        void onClarityChanged(int clarityIndex);

        /**
         * 清晰度没有切换，比如点击了空白位置，或者点击的是之前的清晰度
         */
        void onClarityNotChanged();
    }

    private OnClarityChangedListener mListener;

    public void setOnClarityCheckedListener(OnClarityChangedListener listener) {
        mListener = listener;
    }

    @Override
    public void onBackPressed() {
        // 按返回键时回调清晰度没有变化
        if (mListener != null) {
            mListener.onClarityNotChanged();
        }
        super.onBackPressed();
    }
}

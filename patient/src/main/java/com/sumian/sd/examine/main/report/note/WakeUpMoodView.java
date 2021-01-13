package com.sumian.sd.examine.main.report.note;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.sumian.common.widget.FlowLayout;
import com.sumian.sd.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzz
 * on 2018/3/22.
 * desc:
 */

public class WakeUpMoodView extends LinearLayout implements View.OnClickListener {

    private static final String TAG = WakeUpMoodView.class.getSimpleName();

    TextView mTvBad;
    TextView mTvGeneral;
    TextView mTvGood;
    TextView mTvVeryGood;

    private int mWakeUpState = -1;

    private OnSelectCallback mOnSelectCallback;

    public WakeUpMoodView(Context context) {
        this(context, null);
    }

    public WakeUpMoodView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WakeUpMoodView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View root = inflate(context, R.layout.lay_wake_up_mood_view, this);
        mTvBad = root.findViewById(R.id.tv_bad);
        mTvGeneral = root.findViewById(R.id.tv_general);
        mTvGood = root.findViewById(R.id.tv_good);
        mTvVeryGood = root.findViewById(R.id.tv_very_good);
        mTvBad.setOnClickListener(this);
        mTvGeneral.setOnClickListener(this);
        mTvGood.setOnClickListener(this);
        mTvVeryGood.setOnClickListener(this);
    }

    public void setOnSelectCallback(OnSelectCallback onSelectCallback) {
        mOnSelectCallback = onSelectCallback;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_bad:
                setSelected(mTvBad, R.mipmap.ic_report_bad_selected, R.mipmap.ic_report_bad, mTvGeneral, mTvGood, mTvVeryGood);
                setUnSelected(mTvGeneral, R.mipmap.ic_report_general);
                setUnSelected(mTvGood, R.mipmap.ic_report_good);
                setUnSelected(mTvVeryGood, R.mipmap.ic_report_very);
                if (mTvBad.getTag() == null) {
                    mWakeUpState = -1;
                } else {
                    mWakeUpState = 0;
                }
                break;
            case R.id.tv_general:
                setSelected(mTvGeneral, R.mipmap.ic_report_general_selected, R.mipmap.ic_report_general, mTvBad, mTvGood, mTvVeryGood);
                setUnSelected(mTvBad, R.mipmap.ic_report_bad);
                setUnSelected(mTvGood, R.mipmap.ic_report_good);
                setUnSelected(mTvVeryGood, R.mipmap.ic_report_very);
                if (mTvGeneral.getTag() == null) {
                    mWakeUpState = -1;
                } else {
                    mWakeUpState = 1;
                }
                break;
            case R.id.tv_good:
                setSelected(mTvGood, R.mipmap.ic_report_good_selected, R.mipmap.ic_report_good, mTvBad, mTvGeneral, mTvVeryGood);
                setUnSelected(mTvBad, R.mipmap.ic_report_bad);
                setUnSelected(mTvGeneral, R.mipmap.ic_report_general);
                setUnSelected(mTvVeryGood, R.mipmap.ic_report_very);
                if (mTvGood.getTag() == null) {
                    mWakeUpState = -1;
                } else {
                    mWakeUpState = 2;
                }
                break;
            case R.id.tv_very_good:
                setSelected(mTvVeryGood, R.mipmap.ic_report_very_selected, R.mipmap.ic_report_very, mTvBad, mTvGeneral, mTvGood);
                setUnSelected(mTvBad, R.mipmap.ic_report_bad);
                setUnSelected(mTvGeneral, R.mipmap.ic_report_general);
                setUnSelected(mTvGood, R.mipmap.ic_report_good);
                if (mTvVeryGood.getTag() == null) {
                    mWakeUpState = -1;
                } else {
                    mWakeUpState = 3;
                }
                break;
            default:
                break;
        }

        if (mOnSelectCallback != null) {
            mOnSelectCallback.selectWakeUpMood(mWakeUpState);
        }
    }

    public void setWakeUpMood(int moodStatue) {
        this.mWakeUpState = moodStatue;
        Log.e(TAG, "setWakeUpMood: --------->" + mWakeUpState);
        switch (moodStatue) {
            case -1:
                setUnSelected(mTvBad, R.mipmap.ic_report_bad);
                setUnSelected(mTvGeneral, R.mipmap.ic_report_general);
                setUnSelected(mTvGood, R.mipmap.ic_report_good);
                setUnSelected(mTvVeryGood, R.mipmap.ic_report_very);
                break;
            case 0:
                setSelected(mTvBad, R.mipmap.ic_report_bad_selected, R.mipmap.ic_report_bad, mTvGeneral, mTvGood, mTvVeryGood);
                setUnSelected(mTvGeneral, R.mipmap.ic_report_general);
                setUnSelected(mTvGood, R.mipmap.ic_report_good);
                setUnSelected(mTvVeryGood, R.mipmap.ic_report_very);
                break;
            case 1:
                setSelected(mTvGeneral, R.mipmap.ic_report_general_selected, R.mipmap.ic_report_general, mTvBad, mTvGood, mTvVeryGood);
                setUnSelected(mTvBad, R.mipmap.ic_report_bad);
                setUnSelected(mTvGood, R.mipmap.ic_report_good);
                setUnSelected(mTvVeryGood, R.mipmap.ic_report_very);
                break;
            case 2:
                setSelected(mTvGood, R.mipmap.ic_report_good_selected, R.mipmap.ic_report_good, mTvBad, mTvGeneral, mTvVeryGood);
                setUnSelected(mTvBad, R.mipmap.ic_report_bad);
                setUnSelected(mTvGeneral, R.mipmap.ic_report_general);
                setUnSelected(mTvVeryGood, R.mipmap.ic_report_very);
                break;
            case 3:
                setSelected(mTvVeryGood, R.mipmap.ic_report_very_selected, R.mipmap.ic_report_very, mTvBad, mTvGeneral, mTvGood);
                setUnSelected(mTvBad, R.mipmap.ic_report_bad);
                setUnSelected(mTvGeneral, R.mipmap.ic_report_general);
                setUnSelected(mTvGood, R.mipmap.ic_report_good);
                break;
            default:
                break;
        }
    }


    private void setSelected(TextView selectedView, @DrawableRes int selectedDrawableId, @DrawableRes int unSelectedDrawableIdOne, TextView... unSelectedView) {
        Object tag = selectedView.getTag();
        Drawable topDrawable;
        if (tag == null) {//未被选中
            topDrawable = getResources().getDrawable(selectedDrawableId);
            selectedView.setCompoundDrawablesWithIntrinsicBounds(null, topDrawable, null, null);
            selectedView.setTag(true);
        } else {//已被选中
            topDrawable = getResources().getDrawable(unSelectedDrawableIdOne);
            selectedView.setTag(null);
            selectedView.setCompoundDrawablesWithIntrinsicBounds(null, topDrawable, null, null);
        }

        for (TextView textView : unSelectedView) {
            textView.setTag(null);
        }
    }

    private void setUnSelected(TextView tv, @DrawableRes int unSelectedDrawableId) {
        Drawable topDrawable = getResources().getDrawable(unSelectedDrawableId);
        tv.setCompoundDrawablesWithIntrinsicBounds(null, topDrawable, null, null);
    }

    public interface OnSelectCallback {

        void selectWakeUpMood(int moodState);
    }
}

package com.sumian.sleepdoctor.widget.nav;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.sumian.sleepdoctor.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/4/28.
 * <p>
 * desc:
 */

public class NavTab extends LinearLayout implements View.OnClickListener {

    @BindView(R.id.tb_record)
    ItemTab mTbRecord;

    @BindView(R.id.tb_doctor)
    ItemTab mTbDoctor;

    @BindView(R.id.tb_me)
    ItemTab mTbMe;


    private OnTabChangeListener mOnTabChangeListener;

    public NavTab(Context context) {
        this(context, null);
    }

    public NavTab(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavTab(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ButterKnife.bind(inflate(getContext(), R.layout.lay_nav_tab_menu, this));
        mTbRecord.setActivated(true);
        mTbDoctor.setActivated(false);
        mTbMe.setActivated(false);
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
    }

    public void setOnTabChangeListener(OnTabChangeListener OnTabChangeListener) {
        mOnTabChangeListener = OnTabChangeListener;
    }

    /**
     * 与 viewPager 联动
     *
     * @param position viewPager select position
     */
    public void pageSelected(int position) {
        View v = getChildAt(position);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ItemTab) {
                if (child.getId() == v.getId()) {
                    child.setActivated(true);
                } else {
                    child.setActivated(false);
                }
            }
        }
    }

    @OnClick({R.id.tb_record, R.id.tb_doctor, R.id.tb_me})
    @Override
    public void onClick(View v) {
        int position = -1;
        switch (v.getId()) {
            case R.id.tb_record:
                position = 0;
                mTbRecord.setActivated(true);
                mTbDoctor.setActivated(false);
                mTbMe.setActivated(false);
                break;
            case R.id.tb_doctor:
                position = 1;
                mTbRecord.setActivated(false);
                mTbDoctor.setActivated(true);
                mTbMe.setActivated(false);
                break;
            case R.id.tb_me:
                position = 2;
                mTbRecord.setActivated(false);
                mTbDoctor.setActivated(false);
                mTbMe.setActivated(true);
                break;
        }
        mOnTabChangeListener.tab((ItemTab) v, position);
    }

    /**
     * 自己主动向外联动
     */
    public interface OnTabChangeListener {
        void tab(ItemTab itemTab, int position);
    }

}

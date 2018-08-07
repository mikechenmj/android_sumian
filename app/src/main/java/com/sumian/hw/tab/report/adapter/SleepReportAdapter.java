package com.sumian.hw.tab.report.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sumian.hw.base.HwBaseFragment;

/**
 * Created by jzz
 * on 2017/10/10.
 * desc:
 */

public class SleepReportAdapter extends FragmentPagerAdapter {

    private HwBaseFragment[] mBaseFragments;

    public SleepReportAdapter(FragmentManager fm, HwBaseFragment[] baseFragments) {
        super(fm);
        this.mBaseFragments = baseFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mBaseFragments == null ? null : mBaseFragments[position];
    }

    @Override
    public int getCount() {
        return mBaseFragments == null ? 0 : mBaseFragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "日";
            case 1:
                return "周";
            case 2:
                return "月";
        }
        return super.getPageTitle(position);
    }


}

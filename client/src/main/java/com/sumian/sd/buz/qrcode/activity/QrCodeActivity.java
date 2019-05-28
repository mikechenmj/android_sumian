package com.sumian.sd.buz.qrcode.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.sumian.common.base.BaseViewModelActivity;
import com.sumian.common.widget.TitleBar;
import com.sumian.device.callback.AsyncCallback;
import com.sumian.device.manager.DeviceManager;
import com.sumian.sd.R;
import com.sumian.sd.buz.qrcode.fragment.InputSnFragment;
import com.sumian.sd.buz.qrcode.fragment.QrCodeFragment;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

@SuppressWarnings("ConstantConditions")
public class QrCodeActivity extends BaseViewModelActivity implements TitleBar.OnBackClickListener {

    private ViewPager mViewPager;
    private TitleBar mTitleBar;
    private TabLayout mTabLayout;

    public static void show(Context context) {
        context.startActivity(new Intent(context, QrCodeActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_scan_qr_code;
    }

    public void showInputTab() {
        mViewPager.setCurrentItem(1, true);
    }

    public void bindSn(String sn) {
        if (!DeviceManager.INSTANCE.isMonitorConnected()) {
            ToastUtils.showShort("监测仪未连接,无法绑定速眠仪,请先连接监测仪");
            return;
        }
        DeviceManager.INSTANCE.changeSleepMaster(sn, new AsyncCallback<Object>() {
            @Override
            public void onSuccess(@org.jetbrains.annotations.Nullable Object data) {
                ToastUtils.showShort("绑定速眠仪成功");
                finish();
            }

            @Override
            public void onFail(int code, @NotNull String msg) {
                ToastUtils.showShort("绑定速眠仪失败,请重新扫码绑定");
            }
        });
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTabLayout = findViewById(R.id.table);
        mTitleBar = findViewById(R.id.title_bar);
        mViewPager = findViewById(R.id.view_pager);
        mTitleBar.setOnBackClickListener(this);
        mTitleBar.openTopPadding(true);
        this.mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new QrCodeFragment();
                    case 1:
                        return new InputSnFragment();
                    default:
                        break;
                }
                return new QrCodeFragment();
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "扫码";
                    case 1:
                        return "输入";
                }
                return super.getPageTitle(position);
            }
        });

        mTabLayout.setupWithViewPager(mViewPager, true);
        this.mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                autoScan(position == 0);
                changeTabColor(position);
            }
        });
        changeTabColor(0);
    }

    private void changeTabColor(int position) {
        int tabBgColor;
        int normalTextColor;
        int selectedTextColor = getResources().getColor(R.color.colorPrimary);
        if (position == 0) {
            tabBgColor = Color.TRANSPARENT;
            normalTextColor = Color.WHITE;
            mTitleBar.setBgColor(Color.TRANSPARENT);
        } else {
            tabBgColor = Color.WHITE;
            normalTextColor = getResources().getColor(R.color.t2_color);
            mTitleBar.setBgColor(selectedTextColor);
        }
        mTabLayout.setBackgroundColor(tabBgColor);
        mTabLayout.setSelectedTabIndicatorColor(selectedTextColor);
        mTabLayout.setTabTextColors(normalTextColor, selectedTextColor);
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    private void autoScan(boolean isStartSpot) {
        QrCodeFragment qrCodeFragment = (QrCodeFragment) ((FragmentPagerAdapter) mViewPager.getAdapter()).getItem(0);
        if (qrCodeFragment != null) {
            if (isStartSpot) {
                qrCodeFragment.startSpot();
            } else {
                qrCodeFragment.stopSpot();
            }
        }
    }
}

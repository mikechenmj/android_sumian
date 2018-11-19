package com.sumian.hw.qrcode.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.sumian.blue.callback.BluePeripheralDataCallback;
import com.sumian.blue.model.BluePeripheral;
import com.sumian.common.base.BasePresenterActivity;
import com.sumian.common.widget.TitleBar;
import com.sumian.hw.qrcode.fragment.InputSnFragment;
import com.sumian.hw.qrcode.fragment.QrCodeFragment;
import com.sumian.sd.R;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.device.command.BlueCmd;

@SuppressWarnings("ConstantConditions")
public class QrCodeActivity extends BasePresenterActivity implements TitleBar.OnBackClickListener, BluePeripheralDataCallback {

    private ViewPager mViewPager;
    private TitleBar mTitleBar;
    private BluePeripheral mBluePeripheral;
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
        mBluePeripheral = AppManager.getBlueManager().getBluePeripheral();
        if (mBluePeripheral == null || !mBluePeripheral.isConnected()) {
            ToastUtils.showShort("监测仪未连接,无法绑定速眠仪,请先连接监测仪");
            return;
        }
        mBluePeripheral.addPeripheralDataCallback(this);
        mBluePeripheral.writeDelay(BlueCmd.cDoMonitor2BindSleepySnNumber(sn), 200);
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        if (mBluePeripheral != null) {
            mBluePeripheral.removePeripheralDataCallback(this);
        }
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

    @Override
    public void onSendSuccess(BluePeripheral bluePeripheral, byte[] data) {
        String cmd = BlueCmd.bytes2HexString(data);
        String cmdIndex = BlueCmd.formatCmdIndex(cmd);
        switch (cmdIndex) {
            case "52":
                ToastUtils.showShort("正在绑定速眠仪中...");
                break;
            default:
                break;
        }
    }

    @Override
    public void onReceiveSuccess(BluePeripheral bluePeripheral, byte[] data) {
        String cmd = BlueCmd.bytes2HexString(data);
        String cmdIndex = BlueCmd.formatCmdIndex(cmd);
        switch (cmdIndex) {
            case "52":
                if ("55520188".equals(cmd)) {
                    if (mBluePeripheral != null) {
                        mBluePeripheral.writeDelay(BlueCmd.cSleepySnNumber(), 200);
                    }
                    ToastUtils.showShort("绑定速眠仪成功");
                    finish();
                } else {
                    ToastUtils.showShort("绑定速眠仪失败,请重新扫码绑定");
                }
                break;
            default:
                break;
        }

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

package com.sumian.hw.improve.qrcode.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.HwAppManager;
import com.sumian.hw.base.BaseActivity;
import com.sumian.hw.command.BlueCmd;
import com.sumian.hw.improve.qrcode.fragment.InputSnFragment;
import com.sumian.hw.improve.qrcode.fragment.QrCodeFragment;
import com.sumian.hw.widget.TitleBar;
import com.sumian.blue.callback.BluePeripheralDataCallback;
import com.sumian.blue.model.BluePeripheral;

@SuppressWarnings("ConstantConditions")
public class QrCodeActivity extends BaseActivity implements TitleBar.OnBackListener, BluePeripheralDataCallback {

    private static final String TAG = QrCodeActivity.class.getSimpleName();

    TitleBar mTitleBar;
    TabLayout mTabLayout;
    ViewPager mViewPager;

    private BluePeripheral mBluePeripheral;


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
        mBluePeripheral = HwAppManager.getBlueManager().getBluePeripheral();
        if (mBluePeripheral == null || !mBluePeripheral.isConnected()) {
            showToast("监测仪未连接,无法绑定速眠仪,请先连接监测仪");
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
        mTitleBar = findViewById(R.id.title_bar);
        mTabLayout = findViewById(R.id.table);
        mViewPager = findViewById(R.id.view_pager);

        this.mTitleBar.addOnBackListener(this);
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

        this.mTabLayout.setupWithViewPager(mViewPager, true);
        this.mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                autoScan(position == 0);
            }
        });

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
                showCenterToast("正在绑定速眠仪中...");
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
                    showCenterToast("绑定速眠仪成功");
                    finish();
                } else {
                    showCenterToast("绑定速眠仪失败,请重新扫码绑定");
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

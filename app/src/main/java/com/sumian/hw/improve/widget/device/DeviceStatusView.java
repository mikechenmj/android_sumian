package com.sumian.hw.improve.widget.device;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUISpanHelper;
import com.sumian.hw.improve.device.bean.BlueDevice;
import com.sumian.hw.improve.device.fragment.DeviceFragment;
import com.sumian.hw.improve.device.model.DeviceModel;
import com.sumian.hw.log.LogManager;
import com.sumian.sleepdoctor.R;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by sm
 * on 2018/3/24.
 * <p>
 * desc:设备状态容器
 */

public class DeviceStatusView extends FrameLayout implements OnClickListener, EasyPermissions.PermissionCallbacks, DeviceModel.SyncSleepDataListener {

    static final int SYNC_SUCCESS_DELAY_TIME = 300;

    DeviceChaView mMonitor;
    DeviceChaView mSpeedSleeper;
    DeviceRippleConnectingView mDeviceRippleConnectingView;
    ImageView mIvEquipSync;
    ImageView mIvDevice;
    ImageView mIvMore;
    TextView mTvLabelOne;
    TextView mTvLabelTwo;
    TextView mTvLabelThree;
    Button mBtSwitchPa;
    DeviceSyncCallbackView mDeviceSyncCallbackView;

    private boolean mIsSyncing;
    private OnDeviceStatusCallback mCallback;
    private WeakReference<Fragment> mFragmentWeakReference;
    private AnimatorSet mSyncAnimatorSet;
    public static final long ROTATE_TIMES = 100L;
    public static final long SINGLE_ROTATION_DURATION = 3000L;
    private AnimatorSet mEndSyncAnimatorSet;
    private long mOnFinishTime;
    private int mPackageIndex;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public DeviceStatusView(Context context) {
        this(context, null);
    }

    public DeviceStatusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeviceStatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView(context);
    }

    private void inflateView(Context context) {
        View inflate = inflate(context, R.layout.hw_lay_device_cha_container, this);
        mMonitor = inflate.findViewById(R.id.monitor);
        mSpeedSleeper = inflate.findViewById(R.id.speed_sleeper);
        mDeviceRippleConnectingView = inflate.findViewById(R.id.device_ripple_connecting_view);
        mIvEquipSync = inflate.findViewById(R.id.iv_equip_sync_bg);
        mIvDevice = inflate.findViewById(R.id.iv_device);
        mIvMore = inflate.findViewById(R.id.iv_more);
        mTvLabelOne = inflate.findViewById(R.id.tv_label_one);
        mTvLabelTwo = inflate.findViewById(R.id.tv_label_two);
        mTvLabelThree = inflate.findViewById(R.id.tv_label_three);
        mBtSwitchPa = inflate.findViewById(R.id.bt_switch_pa);
        mDeviceSyncCallbackView = inflate.findViewById(R.id.device_sync_callback_view);

        inflate.findViewById(R.id.iv_device).setOnClickListener(this);
        inflate.findViewById(R.id.iv_more).setOnClickListener(this);
        inflate.findViewById(R.id.bt_switch_pa).setOnClickListener(this);

        mMonitor.setOnSyncSleepChaListener(v -> syncSleepDataWithPermissionCheck());

    }

    @AfterPermissionGranted(DeviceFragment.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION)
    public void syncSleepDataWithPermissionCheck() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            syncSleepData();
        } else {
            Fragment fragment = mFragmentWeakReference.get();
            if (fragment == null) {
                return;
            }
            String rationale = getResources().getString(R.string.request_write_external_storage_permission_hint);
            EasyPermissions.requestPermissions(fragment, rationale, DeviceFragment.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION, perms);
        }
    }

    private void syncSleepData() {
        mCallback.doSyncSleepCha();
    }

    public void setOnDeviceStatusCallback(OnDeviceStatusCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_device) {
            if (getMonitor().status != BlueDevice.STATUS_UNCONNECTED) {
                return;
            }
            mCallback.doConnect(getMonitor());
        } else if (i == R.id.iv_more) {
            mCallback.doMoreAction(v);
        } else if (i == R.id.bt_switch_pa) {
            mCallback.doTurnOnSleep();
        }
    }

    public BlueDevice getMonitor() {
        return mMonitor.getBlueDevice();
    }


    /**
     * 网络同步数据成功
     */
    public void showSyncDeviceSleepChaSuccess() {
        mIsSyncing = false;
        onSyncSleepDataFinish();
    }

    /**
     * 网络同步数据失败
     */
    public void showSyncDeviceSleepChaFailed() {
        mIsSyncing = false;
        invalidDevice(getMonitor());
        mDeviceSyncCallbackView.showSyncFailed(getContext().getString(R.string.sync_data_fail_please_ensure_the_connection_is_stable));
    }

    public void invalidDevice(BlueDevice monitor) {
        runUiThread(() -> {
            mMonitor.invalidDeice(monitor);
            mSpeedSleeper.invalidDeice(monitor.speedSleeper);
            switch (monitor.status) {
                case BlueDevice.STATUS_CONNECTING://连接中
                    mDeviceRippleConnectingView.showIdleStatus();
                    mSpeedSleeper.invisible();
                    mIvDevice.setImageResource(R.mipmap.equip_icon_monitor);
                    mTvLabelOne.setText("监测仪连接中…");
                    mTvLabelTwo.setVisibility(INVISIBLE);
                    mDeviceRippleConnectingView.startConnectingAnimation();
                    mBtSwitchPa.setVisibility(INVISIBLE);
                    break;
                case BlueDevice.STATUS_CONNECTED://已连接,在线状态
                    if (monitor.isMonitoring) {
                        mDeviceRippleConnectingView.showIdleStatus();
                        if (mIsSyncing) {
                            mSpeedSleeper.show();
                            mIvDevice.setImageResource(R.mipmap.equip_icon_monitor_synchronization);
                            mTvLabelOne.setVisibility(GONE);
                            mTvLabelTwo.setVisibility(GONE);
                            mBtSwitchPa.setVisibility(INVISIBLE);
                        } else {
                            mSpeedSleeper.show();
                            mIvDevice.setImageResource(R.mipmap.equip_icon_monitor_monitoringmode);
                            CharSequence charSequence = formatMonitoringModeText();
                            mTvLabelOne.setText(charSequence);
                            mTvLabelTwo.setText("此模式下监测仪不会控制速眠仪。");
                            mTvLabelOne.setVisibility(VISIBLE);
                            mTvLabelTwo.setVisibility(VISIBLE);
                            mBtSwitchPa.setVisibility(INVISIBLE);
                            break;
                        }
                    } else {
                        mDeviceRippleConnectingView.showIdleStatus();
                        BlueDevice speedSleeper = monitor.speedSleeper;
                        switch (speedSleeper.status) {
                            case BlueDevice.STATUS_CONNECTED://速眠仪连接状态
                                mIvDevice.setImageResource(R.mipmap.equip_icon_sleeper);
                                mTvLabelOne.setText("速眠仪已连接，待机中");
                                mTvLabelOne.setVisibility(VISIBLE);
                                mTvLabelTwo.setVisibility(INVISIBLE);
                                mBtSwitchPa.setVisibility(VISIBLE);
                                break;
                            case BlueDevice.STATUS_PA://速眠仪 pa 模式工作中
                                mSpeedSleeper.show();
                                mIvDevice.setImageResource(R.mipmap.equip_icon_sleeper);
                                mTvLabelOne.setText("速眠仪工作中");
                                mTvLabelOne.setVisibility(VISIBLE);
                                mTvLabelTwo.setVisibility(INVISIBLE);
                                mBtSwitchPa.setVisibility(INVISIBLE);
                                break;
                            default://速眠仪未连接状态
                                mSpeedSleeper.show();
                                mIvDevice.setImageResource(mIsSyncing ? R.mipmap.equip_icon_monitor_synchronization : R.mipmap.equip_icon_monitor);
                                mTvLabelOne.setText("监测仪已连接");
                                mTvLabelTwo.setText("请检查速眠仪是否开启");
                                mTvLabelOne.setVisibility(VISIBLE);
                                mTvLabelTwo.setVisibility(VISIBLE);
                                mBtSwitchPa.setVisibility(INVISIBLE);
                                break;
                        }
                        break;
                    }
                case BlueDevice.STATUS_SYNCHRONIZING://同步数据中
                    mSpeedSleeper.show();
                    mIvDevice.setImageResource(R.mipmap.equip_icon_monitor_synchronization);
                    mTvLabelOne.setVisibility(GONE);
                    mTvLabelTwo.setVisibility(GONE);
                    mBtSwitchPa.setVisibility(INVISIBLE);
                    break;
                case BlueDevice.STATUS_UNCONNECTED://未连接,不在线
                default:
                    mDeviceRippleConnectingView.showIdleStatus();
                    mIvDevice.setImageResource(R.mipmap.equip_icon_monitor_notconnected);
                    mTvLabelOne.setText("监测仪未连接，点击上方尝试连接");
                    mTvLabelTwo.setVisibility(INVISIBLE);
                    mDeviceRippleConnectingView.showIdleStatus();
                    mBtSwitchPa.setVisibility(INVISIBLE);
                    break;
            }
            if (mIsSyncing && monitor.status == BlueDevice.STATUS_UNCONNECTED) {
                hideEquipSyncAnimation();
                mDeviceSyncCallbackView.showSyncFailed(getContext().getString(R.string.sync_data_fail_please_ensure_the_connection_is_stable));
                mIsSyncing = false;
            }
            if (mIsSyncing) {
                mTvLabelOne.setVisibility(GONE);
                mTvLabelTwo.setVisibility(GONE);
                mIvDevice.setImageResource(R.mipmap.equip_icon_monitor_synchronization);
            }
            mTvLabelThree.setVisibility(mIsSyncing ? VISIBLE : GONE);
            LogManager.appendFormatPhoneLog("设备状态: %s, isSyncing: %b", monitor.toString(), mIsSyncing);
        });
    }

    @NonNull
    private CharSequence formatMonitoringModeText() {
        String text = "监测模式已开启，";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.warn_color)), 0, 4, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        Drawable drawable = getResources().getDrawable(R.mipmap.ic_equip_monitor);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth() + 10, drawable.getIntrinsicHeight() + 10);

        return QMUISpanHelper.generateSideIconText(true, getResources().getDimensionPixelSize(R.dimen.space_3), spannableStringBuilder, drawable);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    private void runUiThread(Runnable run) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            post(run);
        } else {
            run.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Fragment fragment = mFragmentWeakReference.get();
        if (fragment == null) {
            return;
        }
        if (EasyPermissions.somePermissionPermanentlyDenied(fragment, perms)) {
            new AppSettingsDialog.Builder(fragment).build().show();
        }
    }

    public void setFragment(DeviceFragment deviceFragment) {
        mFragmentWeakReference = new WeakReference<>(deviceFragment);
    }

    public void onSyncSleepDataStart() {
        mIsSyncing = true;
        showEquipSyncAnimation();
        invalidDevice(getMonitor());
    }

    public void onSyncSleepDataFinish() {
        mIsSyncing = false;
        hideEquipSyncAnimation();
        mDeviceRippleConnectingView.showIdleStatus();
        mDeviceSyncCallbackView.showSyncSuccess();
        invalidDevice(getMonitor());
    }


    @Override
    public void onSyncProgressChange(int packageNumber, int currentPosition, int total) {
        // packageNumber 在同步过程中执行其它命令会错乱
        updateProgress(mPackageIndex, currentPosition, total);
    }

    @Override
    public void onSyncStart() {
//            if (System.currentTimeMillis() - mOnStartTime < ON_START_GAP) {
//                mOnStartTime = System.currentTimeMillis();
//                return;
//            }
        // 如果同步开始距离上次结束的时间间隔太大，说明是新开始的同步，重置 mPackageIndex，
        // 否则说明是连续同步的数据，mPackageIndex自增1
        if (System.currentTimeMillis() - mOnFinishTime > SYNC_SUCCESS_DELAY_TIME) {
            mPackageIndex = 1;
        } else {
            mPackageIndex++;
        }
        mHandler.removeCallbacks(mSyncSuccessRunnable);
        onSyncSleepDataStart();
    }

    @Override
    public void onSyncFinish() {
        // 每同步完一段数据都会走到这里来
        // 由于需要再同步完所有数据的时候才显示同步成功的UI，所以这边做延时显示，
        // 如果在SYNC_SUCCESS_DELAY_TIME时间内有第二段数据开始同步，则取消延时任务
        delayPostSyncSuccessRunnable();
        mOnFinishTime = System.currentTimeMillis();
    }

    private void delayPostSyncSuccessRunnable() {
        mHandler.postDelayed(mSyncSuccessRunnable, SYNC_SUCCESS_DELAY_TIME);
    }

    private Runnable mSyncSuccessRunnable = this::onSyncSleepDataFinish;

    public interface OnDeviceStatusCallback {

        void doMoreAction(View v);

        void doTurnOnSleep();

        void doSyncSleepCha();

        void doConnect(BlueDevice monitor);
    }

    private void showEquipSyncAnimation() {
        if (mEndSyncAnimatorSet != null && mEndSyncAnimatorSet.isRunning()) {
            mEndSyncAnimatorSet.end();
        }
        if (mSyncAnimatorSet != null && mSyncAnimatorSet.isRunning()) {
            return;
        }
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mIvEquipSync, "alpha", 0, 1);
        alphaAnimator.setDuration(300);
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(mIvEquipSync, "rotation", 0, 360 * ROTATE_TIMES);
        rotateAnimator.setDuration(SINGLE_ROTATION_DURATION * ROTATE_TIMES);
        rotateAnimator.setInterpolator(new LinearInterpolator());
        rotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimator.setRepeatMode(ValueAnimator.RESTART);
        mSyncAnimatorSet = new AnimatorSet();
        mSyncAnimatorSet.playTogether(alphaAnimator, rotateAnimator);
        mSyncAnimatorSet.start();
    }

    private void hideEquipSyncAnimation() {
        if (mSyncAnimatorSet != null && mSyncAnimatorSet.isRunning()) {
            mSyncAnimatorSet.end();
        }
        if (mEndSyncAnimatorSet != null && mEndSyncAnimatorSet.isRunning()) {
            return;
        }
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(mIvEquipSync, "rotation", mIvEquipSync.getRotation(), mIvEquipSync.getRotation() + 360);
        rotateAnimator.setDuration(SINGLE_ROTATION_DURATION);
        rotateAnimator.setInterpolator(new LinearInterpolator());
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mIvEquipSync, "alpha", mIvEquipSync.getAlpha(), 0);
        alphaAnimator.setDuration(1000);
        mEndSyncAnimatorSet = new AnimatorSet();
        mEndSyncAnimatorSet.playTogether(alphaAnimator, rotateAnimator);
        mEndSyncAnimatorSet.start();
    }

    public void updateProgress(int packageNumber, int currentPosition, int total) {
        String text = String.format(Locale.getDefault(),
                getContext().getString(R.string.sync_sleep_data_progress),
                packageNumber, currentPosition, total);
        mTvLabelThree.setText(text);
    }
}

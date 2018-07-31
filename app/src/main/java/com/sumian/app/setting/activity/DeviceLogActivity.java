package com.sumian.app.setting.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.sumian.app.R;
import com.sumian.app.app.HwAppManager;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.command.BlueCmd;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.common.util.BlueByteUtil;
import com.sumian.app.common.util.CheckUtils;
import com.sumian.app.common.util.UiUtil;
import com.sumian.app.setting.adapter.LogAdapter;
import com.sumian.app.widget.TitleBar;
import com.sumian.blue.callback.BluePeripheralCallback;
import com.sumian.blue.callback.BluePeripheralDataCallback;
import com.sumian.blue.model.BluePeripheral;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jzz
 * on 2017/10/13.
 * desc:
 */

public class DeviceLogActivity extends BaseActivity implements View.OnClickListener,
        ViewTreeObserver.OnGlobalLayoutListener, TitleBar.OnBackListener, BluePeripheralCallback, BluePeripheralDataCallback {

    private static final String TAG = DeviceLogActivity.class.getSimpleName();

    LinearLayout mLayMsgContainer;
    TitleBar mTitleBar;
    RecyclerView mRecyclerView;
    ImageView mIvKeyboardImage;
    EditText mEtInput;
    ImageView mIvKeyboardSend;

    private LogAdapter mLogAdapter;

    private int mOpenKeyboardHeight = 0;
    private int mInitBottomHeight = 0;

    private int mCount;

    // private List<String> m8fData;

    private BluePeripheral mPeripheral;

    public static void show(Context context) {
        context.startActivity(new Intent(context, DeviceLogActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_blue_log;
    }

    @SuppressWarnings("LambdaParameterTypeCanBeSpecified")
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initWidget() {
        super.initWidget();
        mLayMsgContainer = findViewById(R.id.lay_msg_container);
        mTitleBar = findViewById(R.id.title_bar);
        mRecyclerView = findViewById(R.id.recycler);
        mIvKeyboardImage = findViewById(R.id.iv_keyboard_image);
        mEtInput = findViewById(R.id.et_input);
        mIvKeyboardSend = findViewById(R.id.iv_keyboard_send);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_keyboard_image).setOnClickListener(this);
        findViewById(R.id.iv_keyboard_send).setOnClickListener(this);

        this.mTitleBar.addOnBackListener(this);
        this.mLayMsgContainer.getViewTreeObserver().addOnGlobalLayoutListener(this);
        this.mRecyclerView.setAdapter(mLogAdapter = new LogAdapter());
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        this.mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.mRecyclerView.setOnTouchListener((v, event) -> {
            UiUtil.closeKeyboard(mEtInput);
            return false;
        });
    }

    @Override
    protected void initData() {
        super.initData();

        BluePeripheral peripheral = HwAppManager.getBlueManager().getBluePeripheral();
        if (peripheral == null) {
            return;
        }
        peripheral.addPeripheralCallback(this);
        peripheral.addPeripheralDataCallback(this);

        this.mPeripheral = peripheral;

        //mPresenter.syncMsgHistory();
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_back) {
            finish();
        } else if (i == R.id.iv_keyboard_image) {
        } else if (i == R.id.iv_keyboard_send) {
            String command = this.mEtInput.getText().toString().trim();
            if (TextUtils.isEmpty(command)) {
                ToastHelper.show("数据不能为 null");
                return;
            }
            if (!CheckUtils.isCommand(command)) {
                ToastHelper.show("只能输入英文字母和数字");
                return;
            }
            if (this.mPeripheral == null || !this.mPeripheral.isConnected()) {
                ToastHelper.show("当前设备未连接,请先连接设备,再发送数据");
                return;
            }
            mEtInput.setText("");
            this.mPeripheral.write(BlueByteUtil.hex2byte(command));
        }
    }


    @Override
    protected void onRelease() {
        mTitleBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        // mPresenter.release();
        if (mPeripheral != null) {
            mPeripheral.removePeripheralCallback(this);
            mPeripheral.removePeripheralDataCallback(this);
            this.mPeripheral = null;
        }
        super.onRelease();
    }

    @Override
    public void onGlobalLayout() {
        Rect KeypadRect = new Rect();

        this.mLayMsgContainer.getWindowVisibleDisplayFrame(KeypadRect);

        int screenHeight = mLayMsgContainer.getRootView().getHeight();

        int keypadHeight = screenHeight - KeypadRect.bottom;

        if (keypadHeight > 0 && mInitBottomHeight == 0) {//第一次进入界面,未打开软键盘
            mOpenKeyboardHeight = 0;
            mInitBottomHeight = keypadHeight;
        }

        if (mOpenKeyboardHeight == 0 && keypadHeight > mInitBottomHeight) {//打开软键盘
            mOpenKeyboardHeight = keypadHeight;
            this.mRecyclerView.scrollToPosition(mLogAdapter.getItemCount() - 1);
        }

        if (keypadHeight == mInitBottomHeight && keypadHeight < mOpenKeyboardHeight) {//关闭软键盘
            mOpenKeyboardHeight = 0;
            this.mRecyclerView.scrollToPosition(mLogAdapter.getItemCount() - 1);
        }

        // int position = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

        // Log.e(TAG, "onGlobalLayout: ------->screenHeight=" + screenHeight + "  keypadHeight=" + keypadHeight + " position=");
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onConnecting(BluePeripheral peripheral, int connectState) {

    }

    @Override
    public void onConnectSuccess(BluePeripheral peripheral, int connectState) {
        runUiThread(() -> mLogAdapter.clear());
    }

    @Override
    public void onConnectFailed(BluePeripheral peripheral, int connectState) {

    }

    @Override
    public void onDisconnecting(BluePeripheral peripheral, int connectState) {

    }

    @Override
    public void onDisconnectSuccess(BluePeripheral peripheral, int connectState) {

    }

    @Override
    public void onTransportChannelReady(BluePeripheral peripheral) {
        runUiThread(() -> mLogAdapter.clear());
    }

    @Override
    public void onSendSuccess(BluePeripheral bluePeripheral, byte[] data) {
        runUiThread(() -> {
            AVIMTextMessage textMessage = new AVIMTextMessage();
            textMessage.setText(BlueCmd.bytes2HexString(data) + " [" + new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(new Date()) + "]");
            textMessage.setFrom(HwAppManager.getAccountModel().getLeanCloudId());
            textMessage.setTimestamp(System.currentTimeMillis());
            mLogAdapter.addMsg(textMessage);
            this.mRecyclerView.scrollToPosition(mLogAdapter.getItemCount() - 1);
        });
    }

    @Override
    public void onReceiveSuccess(BluePeripheral bluePeripheral, byte[] data) {
        String cmd = BlueCmd.bytes2HexString(data);

        //  if (TextUtils.isEmpty(cmd) || cmd.length() <= 2 || !"55".equals(cmd.substring(0, 2))) {
        //设备命令出问题
        //不是设备命令,有可能发生粘包,分包,拆包现象. 需要重新发送该命令,再次请求消息
        // return;
        // }

        //554F 05 01 1411 1E 15 02 A 1411 1E 16 14 04 03
        //554F 05 05 1411 08 1F 07 0A 1411 08 1F 07 32 01
        //558f 0c 02d0 0001 faaf8bb8a55a3ee3

        //558E 15DC 01 21A56150 00AAC2E7
        //558E 15DC 0F 21A56150
        //558f1033 08ffffffffffffffff

        String cmdIndex = BlueCmd.formatCmdIndex(cmd);
        if ("8f".equals(cmdIndex)) {//容器存储大量的透传数据
            // bluePeripheral.write(new byte[]{(byte) 0xaa, (byte) 0x8f, data[2], data[3]});
            // Log.e(TAG, "onReceive: ------8f---->" + String.format(Locale.getDefault(), "%03d", mCount++) + "  " + cmd);
            cmd += String.format(Locale.getDefault(), "%s%05d", "--", mCount++);
        }

        // boolean isOk = false;
        switch (cmdIndex) {
            case "40"://校正时区
                // Log.e(TAG, "onReceive: ---40---->" + cmd);
                break;
            case "44"://获取监测仪电量
                //cmd = ByteUtil.byte2hex(data);
                // int monitorBattery = Integer.parseInt(cmd.substring(cmd.length() - 2), 16);

                //  Log.e(TAG, "onReceive: -----monitor  battery---->" + monitorBattery);

                break;
            case "45"://获取速眠仪电量
                // cmd = ByteUtil.byte2hex(data);
                // int sleepyBattery = Integer.parseInt(cmd.substring(cmd.length() - 2), 16);
                //  Log.e(TAG, "onReceive: -----sleepy  battery---->" + sleepyBattery);

                break;
            case "47"://获取速眠仪档位
                //554701 00  未开启 / 01 weak 02 strong
                // Log.e(TAG, "onReceive: ------sleepy  power  --->" + cmd);
                break;
            case "48"://主动设置速眠仪的档位强弱
                //aa48 01/02
                //55 48 01  88
                //55 48 01  ff

                //  Log.e(TAG, "onReceive: ----48----set sleepy  power----->" + cmd);
                break;
            case "4e"://获取速眠仪的连接状态
                //  Log.e(TAG, "onReceive: ---sleepy  connected state------->" + cmd);
                break;
            case "50"://获取监测仪固件版本信息
                break;
            case "51"://监测仪自己固件 dfu 模式开启成功
                break;
            case "53"://获取监测仪的 sn 号
                break;
            case "54"://获取速眠仪的固件版本信息
                break;
            case "55"://获取监测仪绑定的并且连接着的速眠仪的 sn 号
                break;
            case "56"://获取监测仪绑定的速眠仪的 mac 地址
                break;
            case "57"://开启/关闭监测仪的监测模式  0x01 开启  0x00 关闭
                break;
            case "58"://使速眠仪进入 pa 模式之后的反馈
                //aa58 01  默认 app 只能开启 pa 模式,不可以关闭
                //5558 01 88/e0/e1/e2/e3/e4/e5/e6/e7/ff
                // 0x88 -- 设置成功
                // 0xE0 -- 监测仪未连接速眠仪
                // 0xE1 -- 监测仪未佩戴
                // 0xE2 -- 头部未在枕头上
                // 0xE3 -- 独立模式已开启,不能开启 pa 模式,提醒先关闭监测仪独立监测模式
                // 0xE4 -- 用户已经处于睡眠状态
                // 0xE5 -- 设置参数错误
                // 0xE6 -- 设置数据长度错误
                // 0xE7 -- 发送数据到速眠仪发生错误
                // 0xFF -- 未知错误
                break;
            case "59"://使速眠仪进入 dfu 模式开启成功
                break;
            case "61"://同步到的监测仪的所有状态,以及与之绑定的速眠仪的所有状态
                //byte1表示监测仪的监测模式状态
                //byte2表示速眠仪的 pa 模式状态
                //5561 xx 01 01
                break;
            case "d0"://临床原始数据采集时间点
                break;
            case "d1"://采集临床肌电数据   不回响应包
                break;
            case "d2"://采集临床脉率数据   不回响应包
                break;
            case "d3"://采集临床加速度数据  不回响应包
                break;
            case "8e":
                // Log.e(TAG, "onReceive: ---------8e---->" + cmd);

                if (data[4] == 0x01) {//开始接8f透传数据
                    //558e1 2d0 015a1562f600ab1107
                    // bluePeripheral.write(new byte[]{(byte) 0xaa, (byte) 0x8e, data[2], data[3], data[4], data[5], data[6],
                    //   data[7], data[8], (byte) 0x88});
                    mCount = 0;
                } else if (data[4] == 0x0f) {//透传8f 数据接收完成,保存未文件,准备上传数据到后台
                    ////558e1 2d0 0f5a15627e
                    //int dataCount = Integer.parseInt(cmd.substring(5, 8), 16);
                    //if (dataCount == mCount) {
                    //    bluePeripheral.write(new byte[]{(byte) 0xaa, (byte) 0x8e, data[2], data[3], data[4], data[5], data[6],
                    //      data[7], data[8], (byte) 0x88});
                    //  return;
                    // }
                    // bluePeripheral.write(new byte[]{(byte) 0xaa, (byte) 0x8e, data[2], data[3], data[4], data[5], data[6],
                    //   data[7], data[8], (byte) 0xff});
                }
                break;
            default:
                // bluePeripheral.write(BlueCmd.cResponseOk(data[1]));
                break;
        }

        AVIMTextMessage textMessage = new AVIMTextMessage();
        textMessage.setText(cmd + " [" + new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(new Date()) + "]");
        textMessage.setTimestamp(System.currentTimeMillis());
        textMessage.setFrom("device");

        runUiThread(() -> {
            mLogAdapter.addMsg(textMessage);
            this.mRecyclerView.scrollToPosition(mLogAdapter.getItemCount() - 1);
        });
    }
}

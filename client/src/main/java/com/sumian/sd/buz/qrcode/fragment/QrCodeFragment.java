package com.sumian.sd.buz.qrcode.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.sumian.common.base.BaseFragment;
import com.sumian.sd.R;
import com.sumian.sd.buz.device.DeviceManager;
import com.sumian.sd.buz.qrcode.activity.QrCodeActivity;
import com.sumian.sd.widget.RequestQrCodeView;

import java.util.Locale;

import androidx.annotation.NonNull;

@SuppressWarnings("ConstantConditions")
public class QrCodeFragment extends BaseFragment implements View.OnClickListener, RequestQrCodeView.OnShowQrCodeCallback {

    private static final String TAG = QrCodeFragment.class.getSimpleName();

    private RequestQrCodeView zxingView;
    private LinearLayout mLayScanActionContainer;
    private Button btReScan;
    private Button btAction;

    private String mQrCode;

    @Override
    public int getLayoutId() {
        return R.layout.hw_fragment_mian_qr_code;
    }

    @Override
    protected void initWidget() {
        zxingView = getView().findViewById(R.id.request_qr_code_view);
        mLayScanActionContainer = getView().findViewById(R.id.lay_scan_action_container);
        btReScan = getView().findViewById(R.id.bt_re_scan);
        btAction = getView().findViewById(R.id.bt_action);

        getView().findViewById(R.id.bt_re_scan).setOnClickListener(this);
        getView().findViewById(R.id.bt_action).setOnClickListener(this);

        zxingView.bindFragment(this);
        zxingView.setOnShowQrCodeCallback(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        zxingView.requestCodeQRCodePermissions();
    }

    @Override
    public void onStop() {
        super.onStop();
        zxingView.stopCamera();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        zxingView.onDestroy();
    }

    public void stopSpot() {
        if (zxingView != null) {
            zxingView.stopCamera();
        }
    }

    public void startSpot() {
        if (zxingView != null) {
            zxingView.delayStartSpot();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        zxingView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onShowQrCode(String qrCode) {
        handleText(this.mQrCode = qrCode.toUpperCase(Locale.getDefault()));
    }

    @SuppressLint("SetTextI18n")
    private void handleText(String qrCode) {
        if (qrCode.length() == 12) {
            zxingView.showTipText("速眠仪SN码：" + qrCode + "\n\n确定要绑定这台速眠仪吗？");
            updateActionText("确定");
        } else {
            updateActionText("手动输入");
            zxingView.showTipText("该SN码不符合规范");
        }
    }

    private void updateActionText(String action) {
        btAction.setText(action);
        showActionButton();
    }

    private void showActionButton() {
        btReScan.setVisibility(View.VISIBLE);
        btAction.setVisibility(View.VISIBLE);
        mLayScanActionContainer.setVisibility(View.VISIBLE);
    }

    private void hideActionButton() {
        btReScan.setVisibility(View.GONE);
        btAction.setVisibility(View.GONE);
        mLayScanActionContainer.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_re_scan) {
            hideActionButton();
            zxingView.showTipText("扫描速眠仪上的二维码");
            zxingView.startSpotAndShowRect();

        } else if (i == R.id.bt_action) {
            if (!DeviceManager.INSTANCE.isConnected()) {
                ToastUtils.showShort("监测仪未连接,无法绑定速眠仪,请先连接监测仪");
                return;
            }

            switch (btAction.getText().toString()) {
                case "确定":
                    ((QrCodeActivity) getActivity()).bindSn(mQrCode);
                    break;
                case "手动输入":
                    ((QrCodeActivity) getActivity()).showInputTab();
                    break;
            }


        }
    }

}

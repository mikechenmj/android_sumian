package com.sumian.hw.improve.qrcode.fragment;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.HwAppManager;
import com.sumian.hw.base.BaseFragment;
import com.sumian.hw.improve.qrcode.activity.QrCodeActivity;
import com.sumian.hw.improve.widget.RequestQrCodeView;
import com.sumian.blue.model.BluePeripheral;

import java.util.Locale;

@SuppressWarnings("ConstantConditions")
public class QrCodeFragment extends BaseFragment implements View.OnClickListener, RequestQrCodeView.OnShowQrCodeCallback {

    private static final String TAG = QrCodeFragment.class.getSimpleName();

    RequestQrCodeView zxingView;
    LinearLayout mLayScanActionContainer;
    Button btReScan;
    Button btAction;

    private String mQrCode;

    @Override
    protected int getLayoutId() {
        return R.layout.hw_fragment_mian_qr_code;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        zxingView = root.findViewById(R.id.request_qr_code_view);
        mLayScanActionContainer = root.findViewById(R.id.lay_scan_action_container);
        btReScan = root.findViewById(R.id.bt_re_scan);
        btAction = root.findViewById(R.id.bt_action);

        root.findViewById(R.id.bt_re_scan).setOnClickListener(this);
        root.findViewById(R.id.bt_action).setOnClickListener(this);

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
            zxingView.showTipText("助眠仪SN码：" + qrCode + "\r\n确定要绑定这台助眠仪吗？");
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
            zxingView.showTipText("扫描助眠仪上的二维码");
            zxingView.startSpotAndShowRect();

        } else if (i == R.id.bt_action) {
            BluePeripheral bluePeripheral = HwAppManager.getBlueManager().getBluePeripheral();
            if (bluePeripheral == null || !bluePeripheral.isConnected()) {
                showToast("监测仪未连接,无法绑定速眠仪,请先连接监测仪");
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

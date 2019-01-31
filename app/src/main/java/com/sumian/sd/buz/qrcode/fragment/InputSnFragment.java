package com.sumian.sd.buz.qrcode.fragment;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.sumian.sd.R;
import com.sumian.sd.base.HwBaseFragment;
import com.sumian.sd.buz.qrcode.activity.QrCodeActivity;
import com.sumian.sd.common.utils.UiUtil;

@SuppressWarnings("ConstantConditions")
public class InputSnFragment extends HwBaseFragment implements View.OnClickListener {

    EditText etSn;

    @Override
    protected int getLayoutId() {
        return R.layout.hw_fragment_main_input_sn;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        etSn = root.findViewById(R.id.et_sn);
        root.findViewById(R.id.bt_action).setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        UiUtil.closeKeyboard(etSn);
        String input = etSn.getText().toString().trim();
        if (!TextUtils.isEmpty(input) && input.length() == 12) {
            ((QrCodeActivity) getActivity()).bindSn(input.toUpperCase());
        } else {
            ToastUtils.showLong("输入的SN码不符合规范");
        }
    }
}

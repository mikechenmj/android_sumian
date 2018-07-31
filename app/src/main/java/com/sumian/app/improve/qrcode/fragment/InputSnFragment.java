package com.sumian.app.improve.qrcode.fragment;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.app.base.BaseFragment;
import com.sumian.app.common.util.UiUtil;
import com.sumian.app.improve.qrcode.activity.QrCodeActivity;
import com.sumian.app.widget.adapter.OnTextWatcherAdapter;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressWarnings("ConstantConditions")
public class InputSnFragment extends BaseFragment implements View.OnClickListener {

    TextView tvPopError;
    FrameLayout layPop;
    EditText etSn;

    @Override
    protected int getLayoutId() {
        return R.layout.hw_fragment_main_input_sn;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        tvPopError = root.findViewById(R.id.tv_pop_error);
        layPop = root.findViewById(R.id.lay_pop);
        etSn = root.findViewById(R.id.et_sn);

        root.findViewById(R.id.bt_action).setOnClickListener(this);

        etSn.addTextChangedListener(new OnTextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                layPop.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        UiUtil.closeKeyboard(etSn);
        String input = etSn.getText().toString().trim();
        if (!TextUtils.isEmpty(input)) {
            input = input.toUpperCase(Locale.getDefault());
            if (input.length() == 12) {
                layPop.setVisibility(View.GONE);
                ((QrCodeActivity) getActivity()).bindSn(input);
            } else {
                tvPopError.setText("输入的SN码不符合规范");
                layPop.setVisibility(View.VISIBLE);
            }
        } else {
            tvPopError.setText("输入的SN码不符合规范");
            layPop.setVisibility(View.VISIBLE);
        }
    }

}

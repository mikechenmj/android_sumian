package com.sumian.sd.examine.widget;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sumian.sd.R;
import com.sumian.sd.widget.BaseDialogFragment;

import org.w3c.dom.Text;

/**
 * Created by jzz
 * on 2017/11/15.
 * <p>
 * desc:
 */

public class ContactDialog extends BaseDialogFragment {



    @Override
    protected int getLayout() {
        return R.layout.lay_dialog_contact;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        rootView.findViewById(R.id.tv_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call("400–878–9088");
                dismiss();
            }
        });
        rootView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 调用拨号界面
     *
     * @param phone 电话号码
     */
    private void call(String phone) {
        if (TextUtils.isEmpty(phone)) return;
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

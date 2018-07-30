package com.sumian.app.setting.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.sumian.app.R;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.leancloud.LeanCloudHelper;
import com.sumian.app.leancloud.activity.MsgActivity;
import com.sumian.app.setting.dialog.ContactDialog;
import com.sumian.app.widget.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/10/12.
 * desc:
 */

public class ContactActivity extends BaseActivity implements View.OnClickListener, TitleBar.OnBackListener {

    TitleBar mTitleBar;

    public static void show(Context context) {
        context.startActivity(new Intent(context, ContactActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_contact;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleBar = findViewById(R.id.title_bar);
        findViewById(R.id.lay_call_me).setOnClickListener(this);
        findViewById(R.id.lay_send_msg).setOnClickListener(this);
        this.mTitleBar.addOnBackListener(this);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.lay_call_me) {
            new ContactDialog().show(getSupportFragmentManager(), ContactDialog.class.getSimpleName());
        } else if (i == R.id.lay_send_msg) {
            MsgActivity.show(this, LeanCloudHelper.SERVICE_TYPE_ONLINE_DOCTOR);
        }
    }

    @Override
    public void onBack(View v) {
        finish();
    }
}

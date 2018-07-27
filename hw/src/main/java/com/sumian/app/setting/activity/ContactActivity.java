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

    @BindView(R.id.title_bar)
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
        this.mTitleBar.addOnBackListener(this);
    }

    @OnClick({R.id.lay_call_me, R.id.lay_send_msg})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_call_me:
                new ContactDialog().show(getSupportFragmentManager(), ContactDialog.class.getSimpleName());
                break;
            case R.id.lay_send_msg:
                MsgActivity.show(this, LeanCloudHelper.SERVICE_TYPE_ONLINE_DOCTOR);
                break;
        }
    }

    @Override
    public void onBack(View v) {
        finish();
    }
}

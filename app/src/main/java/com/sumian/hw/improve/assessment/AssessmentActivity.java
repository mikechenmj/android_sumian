package com.sumian.hw.improve.assessment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.hw.app.HwApp;
import com.sumian.hw.app.HwApplicationDelegate;
import com.sumian.hw.base.BaseActivity;
import com.sumian.hw.common.util.NetUtil;
import com.sumian.hw.widget.TitleBar;
import com.sumian.sleepdoctor.app.AppManager;

/**
 * Created by sm
 * on 2018/3/15.
 * desc:
 */

public class AssessmentActivity extends BaseActivity implements View.OnClickListener, TitleBar.OnBackListener {

    TitleBar mTitleBar;
    FrameLayout mPopContainer;
    TextView mTvStepOne;
    ImageView mIvUserInfo;
    LinearLayout mLayShowUserInfoTable;
    TextView mTvStepTwo;
    TextView mTvStepThree;
    ImageView mIvSleepAssessment;
    LinearLayout mLayShowSleepAssessmentTable;
    TextView mTvStepFour;
    LinearLayout mLayMsgContainer;
    Button mBtUserInfoTable;
    Button mBtAssessmentTable;
    LinearLayout mLayContainer;

    private boolean mIsRegister;

    public static void show(Context context) {
        context.startActivity(new Intent(context, AssessmentActivity.class));
    }

    public static void show(Context context, boolean isRegister) {
        Intent intent = new Intent(context, AssessmentActivity.class);
        intent.putExtra("isRegister", isRegister);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle != null) {
            mIsRegister = bundle.getBoolean("isRegister", false);
        }
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_assessment;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleBar = findViewById(R.id.title_bar);
        mPopContainer = findViewById(R.id.pop_container);
        mTvStepOne = findViewById(R.id.tv_step_one);
        mIvUserInfo = findViewById(R.id.iv_user_info);
        mLayShowUserInfoTable = findViewById(R.id.lay_show_user_info_table);
        mTvStepTwo = findViewById(R.id.tv_step_two);
        mTvStepThree = findViewById(R.id.tv_step_three);
        mIvSleepAssessment = findViewById(R.id.iv_sleep_assessment);
        mLayShowSleepAssessmentTable = findViewById(R.id.lay_show_sleep_assessment_table);
        mTvStepFour = findViewById(R.id.tv_step_four);
        mLayMsgContainer = findViewById(R.id.lay_msg_container);
        mBtUserInfoTable = findViewById(R.id.bt_user_info_table);
        mBtAssessmentTable = findViewById(R.id.bt_assessment_table);
        mLayContainer = findViewById(R.id.lay_container);

        findViewById(R.id.lay_show_user_info_table).setOnClickListener(this);
        findViewById(R.id.lay_show_sleep_assessment_table).setOnClickListener(this);
        findViewById(R.id.bt_user_info_table).setOnClickListener(this);
        findViewById(R.id.bt_assessment_table).setOnClickListener(this);

        mTitleBar.addOnBackListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!AppManager.getAccountViewModel().isHaveFullUserInfo()) {
            mBtUserInfoTable.setVisibility(View.VISIBLE);
            mIvUserInfo.setImageResource(R.mipmap.chatbubble_icon_info);
            mLayShowUserInfoTable.setVisibility(View.VISIBLE);
        } else {
            mLayShowUserInfoTable.setVisibility(View.VISIBLE);
            mIvUserInfo.setImageResource(R.mipmap.chatbubble_icon_info_complete);
            mBtUserInfoTable.setVisibility(View.GONE);
        }

        if (!AppManager.getAccountViewModel().isHaveAnswers()) {
            mBtAssessmentTable.setVisibility(View.VISIBLE);
            mIvSleepAssessment.setImageResource(R.mipmap.chatbubble_icon_evaluationform);
            mLayShowSleepAssessmentTable.setVisibility(View.VISIBLE);
        } else {
            mBtAssessmentTable.setVisibility(View.GONE);
            mIvSleepAssessment.setImageResource(R.mipmap.chatbubble_icon_evaluationform_complete);
            mLayShowSleepAssessmentTable.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (!NetUtil.hasInternet()) {
            mPopContainer.setVisibility(View.VISIBLE);
            return;
        } else {
            mPopContainer.setVisibility(View.GONE);
        }

        int id = v.getId();
        if (id == R.id.lay_show_user_info_table) {
            AssessmentUserInfoActivity.show(this);
        } else if (id == R.id.lay_show_sleep_assessment_table) {
            QuestionActivity.show(this);
        } else if (id == R.id.bt_user_info_table) {
            AssessmentUserInfoActivity.show(this);
        } else if (id == R.id.bt_assessment_table) {
            QuestionActivity.show(this);
        }
    }

    @Override
    public void onBack(View v) {
        if (mIsRegister) {
            HwApplicationDelegate.goHome(HwApp.getAppContext());
        } else {
            finish();
        }
    }
}

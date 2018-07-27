package com.sumian.app.improve.assessment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.app.AppManager;
import com.sumian.app.app.App;
import com.sumian.app.app.delegate.ApplicationDelegate;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.common.util.NetUtil;
import com.sumian.app.widget.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/3/15.
 * desc:
 */

public class AssessmentActivity extends BaseActivity implements View.OnClickListener, TitleBar.OnBackListener {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    @BindView(R.id.pop_container)
    FrameLayout mPopContainer;

    @BindView(R.id.tv_step_one)
    TextView mTvStepOne;
    @BindView(R.id.iv_user_info)
    ImageView mIvUserInfo;
    @BindView(R.id.lay_show_user_info_table)
    LinearLayout mLayShowUserInfoTable;
    @BindView(R.id.tv_step_two)
    TextView mTvStepTwo;
    @BindView(R.id.tv_step_three)
    TextView mTvStepThree;
    @BindView(R.id.iv_sleep_assessment)
    ImageView mIvSleepAssessment;
    @BindView(R.id.lay_show_sleep_assessment_table)
    LinearLayout mLayShowSleepAssessmentTable;
    @BindView(R.id.tv_step_four)
    TextView mTvStepFour;
    @BindView(R.id.lay_msg_container)
    LinearLayout mLayMsgContainer;

    @BindView(R.id.bt_user_info_table)
    Button mBtUserInfoTable;
    @BindView(R.id.bt_assessment_table)
    Button mBtAssessmentTable;
    @BindView(R.id.lay_container)
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
        mTitleBar.addOnBackListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!AppManager.getAccountModel().isHaveFullUserInfo()) {
            mBtUserInfoTable.setVisibility(View.VISIBLE);
            mIvUserInfo.setImageResource(R.mipmap.chatbubble_icon_info);
            mLayShowUserInfoTable.setVisibility(View.VISIBLE);
        } else {
            mLayShowUserInfoTable.setVisibility(View.VISIBLE);
            mIvUserInfo.setImageResource(R.mipmap.chatbubble_icon_info_complete);
            mBtUserInfoTable.setVisibility(View.GONE);
        }

        if (!AppManager.getAccountModel().isHaveAnswers()) {
            mBtAssessmentTable.setVisibility(View.VISIBLE);
            mIvSleepAssessment.setImageResource(R.mipmap.chatbubble_icon_evaluationform);
            mLayShowSleepAssessmentTable.setVisibility(View.VISIBLE);
        } else {
            mBtAssessmentTable.setVisibility(View.GONE);
            mIvSleepAssessment.setImageResource(R.mipmap.chatbubble_icon_evaluationform_complete);
            mLayShowSleepAssessmentTable.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.lay_show_user_info_table, R.id.lay_show_sleep_assessment_table, R.id.bt_user_info_table, R.id.bt_assessment_table})
    public void onClick(View v) {
        if (!NetUtil.hasInternet()) {
            mPopContainer.setVisibility(View.VISIBLE);
            return;
        } else {
            mPopContainer.setVisibility(View.GONE);
        }

        switch (v.getId()) {
            case R.id.lay_show_user_info_table:
                AssessmentUserInfoActivity.show(this);
                break;
            case R.id.lay_show_sleep_assessment_table:
                QuestionActivity.show(this);
                break;
            case R.id.bt_user_info_table:
                AssessmentUserInfoActivity.show(this);
                break;
            case R.id.bt_assessment_table:
                QuestionActivity.show(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBack(View v) {
        if (mIsRegister) {
            ApplicationDelegate.goHome(App.getAppContext());
        } else {
            finish();
        }
    }
}

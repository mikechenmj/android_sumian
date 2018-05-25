package com.sumian.sleepdoctor.improve.error;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;

/**
 * Created by sm
 * on 2018/5/24 17:08
 * desc:
 **/
public class EmptyErrorView extends LinearLayout implements View.OnClickListener {

    private ImageView mIvEmptyIcon;
    private TextView mTvEmptyMsgTitle;
    private TextView mTvEmptyMsgDesc;


    private OnEmptyCallback mOnEmptyCallback;

    public EmptyErrorView(Context context) {
        this(context, null);
    }

    public EmptyErrorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        initView(context);
    }

    private void initView(Context context) {
        View rootView = inflate(context, R.layout.lay_empty_view, this);
        this.mIvEmptyIcon = rootView.findViewById(R.id.iv_empty_icon);
        this.mIvEmptyIcon.setOnClickListener(this);
        this.mTvEmptyMsgTitle = rootView.findViewById(R.id.tv_empty_msg_title);
        this.mTvEmptyMsgDesc = rootView.findViewById(R.id.tv_empty_msg_desc);
    }

    public void setOnEmptyCallback(OnEmptyCallback onEmptyCallback) {
        mOnEmptyCallback = onEmptyCallback;
    }

    /**
     * desc: 消息中心暂无消息
     */
    public void invalidMsgCenterError() {
        invalid(R.mipmap.ic_empty_state_news, R.string.empty_msg_center_msg, R.string.empty_msg_center_desc);
    }

    /**
     * desc: 暂无电子报告
     */
    public void invalidOnlineReportError() {
        invalid(R.mipmap.ic_empty_state_report, R.string.empty_report_msg, R.string.empty_msg_center_desc);
    }

    /**
     * desc: 暂无咨询记录
     */
    public void invalidEvaluationError() {
        invalid(R.mipmap.ic_empty_state_report, R.string.empty_evaluation_msg, R.string.empty_evaluation_desc);
    }

    /**
     * desc: 网络请求返回403/500等code,或者访问的页面不存在
     */
    public void invalidRequestError() {
        invalid(R.mipmap.ic_empty_state_404, R.string.empty_404_msg, R.string.empty_404_desc);
    }

    /**
     * desc:网络环境异常.
     */
    public void invalidNetworkError() {
        invalid(R.mipmap.ic_empty_state_network_anomaly, R.string.empty_network_error_msg, R.string.empty_network_error_desc);
    }

    /**
     * desc: 暂无测评记录
     */
    public void invalidAdvisoryError() {
        invalid(R.mipmap.ic_empty_state_advisory, R.string.emtpy_advisory_msg, R.string.emtpy_advisory_desc);
    }

    private void invalid(@DrawableRes int emptyId, @StringRes int emptyTitleId, @StringRes int emptyDescId) {
        this.mIvEmptyIcon.setImageResource(emptyId);
        this.mTvEmptyMsgTitle.setText(emptyTitleId);
        this.mTvEmptyMsgDesc.setText(emptyDescId);
        show();
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_empty_icon:
                if (mOnEmptyCallback != null) {
                    this.mOnEmptyCallback.onReload();
                }
                hide();
                break;
        }
    }


    public interface OnEmptyCallback {

        void onReload();
    }
}

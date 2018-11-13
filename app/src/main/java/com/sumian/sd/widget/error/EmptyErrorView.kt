package com.sumian.sd.widget.error

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sd.R
import com.sumian.sd.main.MainActivity

/**
 * Created by sm
 * on 2018/5/24 17:08
 * desc:
 */
@Suppress("DEPRECATION", "UNUSED_ANONYMOUS_PARAMETER")
class EmptyErrorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    companion object {

        fun create(context: Context, @DrawableRes icon: Int, @StringRes msgTitle: Int, @StringRes msgDesc: Int): EmptyErrorView {
            val emptyErrorView = EmptyErrorView(context)
            emptyErrorView.setIvEmptyIcon(icon)
            emptyErrorView.setTvEmptyMsgTitle(msgTitle)
            emptyErrorView.setTvEmptyMsgDesc(msgDesc)
            return emptyErrorView
        }

        fun createNormalEmptyView(context: Context): EmptyErrorView {
            return EmptyErrorView.create(context,
                    R.mipmap.ic_empty_state_report,
                    0,
                    R.string.no_data_hint)
        }
    }


    private var mIvEmptyIcon: ImageView? = null
    private var mTvEmptyMsgTitle: TextView? = null
    private var mTvEmptyMsgDesc: TextView? = null
    private var mBtShowService: Button? = null

    private val mAutoHide: Boolean
    private var mIsShowService: Boolean

    private var mOnEmptyCallback: OnEmptyCallback? = null

    init {
        orientation = LinearLayout.VERTICAL
        setBackgroundColor(resources.getColor(R.color.b1_color))
        initView(context)
        val a = context.obtainStyledAttributes(attrs, R.styleable.EmptyErrorView)
        val icon = a.getDrawable(R.styleable.EmptyErrorView_eev_icon)
        val title = a.getString(R.styleable.EmptyErrorView_eev_title)
        val desc = a.getString(R.styleable.EmptyErrorView_eev_desc)
        mAutoHide = a.getBoolean(R.styleable.EmptyErrorView_eev_auto_hide, true)
        mIsShowService = a.getBoolean(R.styleable.EmptyErrorView_eev_show_service, false)
        a.recycle()
        mIvEmptyIcon?.setImageDrawable(icon)
        mTvEmptyMsgTitle?.text = title
        mTvEmptyMsgDesc?.text = desc

        if (mIsShowService) {
            registerShowServiceEvent()
        }
    }


    private fun initView(context: Context) {
        val rootView = View.inflate(context, R.layout.lay_empty_view, this)
        this.mIvEmptyIcon = rootView.findViewById(R.id.iv_empty_icon)
        this.mTvEmptyMsgTitle = rootView.findViewById(R.id.tv_empty_msg_title)
        this.mTvEmptyMsgDesc = rootView.findViewById(R.id.tv_empty_msg_desc)
        this.mBtShowService = rootView.findViewById(R.id.bt_show_service)
    }

    fun setOnEmptyCallback(onEmptyCallback: OnEmptyCallback) {
        mOnEmptyCallback = onEmptyCallback
        setOnClickListener(this)
    }

    /**
     * desc: 消息中心暂无消息
     */
    fun invalidMsgCenterError() {
        invalid(R.mipmap.ic_empty_state_alarm, R.string.empty_msg_center_msg, R.string.empty_msg_center_desc)
    }

    /**
     * desc: 暂无电子报告
     */
    fun invalidOnlineReportError() {
        invalid(R.mipmap.ic_empty_state_report, R.string.empty_report_msg, R.string.empty_msg_center_desc)
    }

    /**
     * desc: 暂无咨询记录
     */
    fun invalidEvaluationError() {
        invalid(R.mipmap.ic_empty_state_report, R.string.empty_evaluation_msg, R.string.empty_evaluation_desc)
        registerShowServiceEvent()
    }

    /**
     * desc: 网络请求返回403/500等code,或者访问的页面不存在
     */
    fun invalidRequestError() {
        invalid(R.mipmap.ic_empty_state_404, R.string.empty_404_msg, R.string.empty_404_desc)
    }

    /**
     * desc:网络环境异常.
     */
    fun invalidNetworkError() {
        invalid(R.mipmap.ic_empty_state_network_anomaly, R.string.empty_network_error_msg, R.string.empty_network_error_desc)
    }

    /**
     * desc: 暂无测评记录
     */
    fun invalidAdvisoryError() {
        invalid(R.mipmap.ic_empty_state_advisory, R.string.emtpy_advisory_msg, R.string.emtpy_advisory_desc)
        registerShowServiceEvent()
    }

    /**
     * desc: 暂无兑换记录
     */
    fun invalidCouponError() {
        invalid(R.drawable.ic_empty_state_redemption, R.string.empty_coupon_msg, R.string.empty_coupon_desc)
    }

    private fun invalid(@DrawableRes emptyId: Int, @StringRes emptyTitleId: Int, @StringRes emptyDescId: Int) {
        this.mIvEmptyIcon?.setImageResource(emptyId)
        this.mTvEmptyMsgTitle?.setText(emptyTitleId)
        this.mTvEmptyMsgDesc?.setText(emptyDescId)
        show()
    }

    fun show() {
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.GONE
    }

    override fun onClick(v: View) {
        this.mOnEmptyCallback?.reload()
        if (mAutoHide) {
            hide()
        }
    }

    fun setIvEmptyIcon(@DrawableRes icon: Int) {
        mIvEmptyIcon?.setImageResource(icon)
    }

    fun setTvEmptyMsgTitle(@StringRes msgTitle: Int) {
        mTvEmptyMsgTitle?.let {
            if (msgTitle == 0) {
                it.visibility = View.GONE
            } else {
                it.visibility = View.VISIBLE
                it.setText(msgTitle)
            }
        }
    }

    fun setTvEmptyMsgDesc(@StringRes msgDesc: Int) {
        mTvEmptyMsgDesc?.setText(msgDesc)
    }

    private fun registerShowServiceEvent() {
        if (!mIsShowService) {
            mIsShowService = true
        }
        mBtShowService?.let {
            it.visibility = View.VISIBLE
            it.setOnClickListener { v ->
                ActivityUtils.getTopActivity().finish()
                MainActivity.launch(MainActivity.TAB_2, null)
            }
        }
    }

    interface OnEmptyCallback {

        fun reload()
    }

}

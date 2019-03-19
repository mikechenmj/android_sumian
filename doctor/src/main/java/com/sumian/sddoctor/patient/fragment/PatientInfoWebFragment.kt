package com.sumian.sddoctor.patient.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.widget.PopupWindowCompat
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.h5.handler.SBridgeHandler
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.JsonUtil
import com.sumian.module_core.chat.bean.CreateConversationResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.buz.patientdoctorim.ConversationActivity
import com.sumian.sddoctor.constants.H5Uri
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.h5.BaseWebViewFragment
import com.sumian.sddoctor.me.authentication.AuthenticationHelper
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.patient.activity.ModifyPatientTagActivity
import com.sumian.sddoctor.patient.contract.PatientRecommendContract
import com.sumian.sddoctor.patient.presenter.PatientRecommendPresenter
import com.sumian.sddoctor.patient.sleepdiary.PatientSleepDiaryDetailActivity
import com.sumian.sddoctor.service.plan.activity.PlanListActivity
import com.sumian.sddoctor.service.report.activity.ReportActivity
import com.sumian.sddoctor.service.report.bean.H5ToReportDetail
import com.sumian.sddoctor.service.scale.activity.ScaleListActivity
import com.sumian.sddoctor.widget.TitleBar
import kotlinx.android.synthetic.main.lay_title_bar.view.*
import java.util.*

@SuppressLint("InflateParams")
/**
 * <pre>
 *     @author : sm

 *     e-mail : yaoqi.y@sumian.com
 *     time: 2018/6/29 17:42
 *
 *     version: 1.0
 *
 *     desc: 患者档案
 *
 * </pre>
 */
class PatientInfoWebFragment : BaseWebViewFragment(), TitleBar.OnMenuClickListener, PopupWindow.OnDismissListener, View.OnClickListener, PatientRecommendContract.View {

    companion object {

        private const val EXTRAS_ID = "com.sumian.sddoctor.extras.patient.id"

        private const val EXTRAS_SHOW_TITLE_BAR = "com.sumian.sddoctor.extras.show.title.bar"

        private const val POPUP_WINDOW_DISMISS_TIME = 150L

        @JvmStatic
        fun newInstance(patientId: Int, isShowTitleBar: Boolean = true): PatientInfoWebFragment {
            val fragment = PatientInfoWebFragment()
            fragment.arguments = Bundle().apply {
                putInt(EXTRAS_ID, patientId)
                putBoolean(EXTRAS_SHOW_TITLE_BAR, isShowTitleBar)

            }
            return fragment
        }
    }

    private var mPatientId = 0
    private var mIsShowTitleBar = true

    private val mPresenter: PatientRecommendContract.Presenter  by lazy {
        PatientRecommendPresenter.init(this)
    }

    private val mPopMenu: PopupWindow  by lazy {
        val popView = LayoutInflater.from(context).inflate(R.layout.lay_pop_menu, null, false)
        popView.findViewById<TextView>(R.id.tv_modify_tags).setOnClickListener(this)
//        popView.findViewById<TextView>(R.id.tv_recommend_patients).setOnClickListener(this)
        popView.findViewById<TextView>(R.id.tv_send_scale).setOnClickListener(this)
        popView.findViewById<TextView>(R.id.tv_send_follow_up_plan).setOnClickListener(this)

        val popMenu = PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        popMenu.isOutsideTouchable = true
        popMenu.setOnDismissListener(this)

        return@lazy popMenu
    }

    private var mPopupDismissTime: Long = 0

    override fun initBundle(bundle: Bundle?) {
        super.initBundle(bundle)
        bundle?.let {
            mPatientId = it.getInt(EXTRAS_ID, 0)
            mIsShowTitleBar = it.getBoolean(EXTRAS_SHOW_TITLE_BAR, true)
        }
    }

    override fun registerHandler(sWebView: SWebView?) {
        super.registerHandler(sWebView)
        sWebView?.registerHandler("toReportDetail", object : SBridgeHandler() {
            override fun handler(data: String?) {
                data?.let {
                    val toReportDetail = JsonUtil.fromJson(it, H5ToReportDetail::class.java)!!
                    val beginTime = formatBeginTime(toReportDetail.begin)
                    ReportActivity.show(toReportDetail.userId, beginTime * 1000L)
                }
            }
        })
        sWebView?.registerHandler("toSleepDiariesDetail", object : SBridgeHandler() {
            override fun handler(data: String?) {
                data?.let {
                    val diaryDetailData = JsonUtil.fromJson(it, DiaryDetailData::class.java)!!
                    PatientSleepDiaryDetailActivity.launch(diaryDetailData.userId, diaryDetailData.timeUnix * 1000L)
                }
            }
        })
    }

    /**
     * 修正Unix时间戳为  yyyy/MM/dd 00:00:00
     */
    private fun formatBeginTime(beginTime: Int): Int {
        var beginTime1 = beginTime
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = beginTime1 * 1000L
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0)
        beginTime1 = (calendar.timeInMillis / 1000L).toInt()
        return beginTime1
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        if (mIsShowTitleBar) {
            mTitleBar.visibility = View.VISIBLE
            mTitleBar.showMoreIcon(R.drawable.ic_nav_icon_more)
            mTitleBar.setOnMenuClickListener(this)
            mDivider.visibility = View.GONE
        } else {
            mTitleBar.visibility = View.GONE
            mDivider.visibility = View.GONE
        }

        mTitleBar.iv_menu.visibility = View.VISIBLE
        val imageView = ImageView(activity)
        imageView.setImageResource(R.drawable.nav_icon_conversation)
        mTitleBar.v_menu_container.addView(imageView)
        imageView.setOnClickListener {
            StatUtil.event(StatConstants.click_patient_info_page_conversation_btn)
            val call = AppManager.getHttpService().createConversation(mPatientId)
            addCall(call)
            call.enqueue(object : BaseSdResponseCallback<CreateConversationResponse>() {
                override fun onSuccess(response: CreateConversationResponse?) {
                    LogUtils.d(response)
                    ConversationActivity.launch(response?.conversationId ?: return)
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    LogUtils.d(errorResponse)
                }
            })
        }
    }

    override fun initData() {
        super.initData()
//        mViewModel.getPatientRecommendStatus(mPatientId)
    }

    override fun getUrlContentPart(): String {
        var uri = H5Uri.PATIENT_FILE
        uri = uri.replace("{id}", mPatientId.toString())
        return uri
    }

    fun onBack(): Boolean {
        return mSWebViewLayout?.webViewCanGoBack()!!
    }

    override fun onMenuClick(v: View?) {
        // popup 显示的时候，点击按钮，popup会先自动dismiss，然后触发view的onclick事件，两者时间间隔在30-80ms之间，
        // 所以此时要过滤点击事件
        if (System.currentTimeMillis() - mPopupDismissTime < POPUP_WINDOW_DISMISS_TIME) {
            return
        }
        v?.let {
            if (v.tag == null) {
                v.tag = true
                PopupWindowCompat.showAsDropDown(mPopMenu, v, 0, 0, Gravity.BOTTOM)
            } else {
                if (mPopMenu.isShowing) {
                    mPopMenu.dismiss()
                }
                v.tag = null
            }
        }
    }

    override fun onDismiss() {
        if (mTitleBar.ivMenu.tag != null) {
            mTitleBar.ivMenu.tag = null
        }
        mPopupDismissTime = System.currentTimeMillis()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_modify_tags -> {//修改标签
                ModifyPatientTagActivity.show(mPatientId)
            }
//            R.id.tv_recommend_patients -> {//推荐患者
//                mViewModel.bookRecommendPatient(mPatientId)
//            }
            R.id.tv_send_scale -> {//发送量表
//                if (!AuthenticationHelper.checkAuthenticationStatusWithToast(activity!!,R.string.after_authentication_you_can_send_scale)) return
                ScaleListActivity.show(mPatientId)
            }
            R.id.tv_send_follow_up_plan -> {//发送随访计划
                if (!AuthenticationHelper.checkAuthenticationStatusWithToast(activity!!, R.string.after_authentication_you_can_send_suifang_plan)) return
                PlanListActivity.show(mPatientId)
            }
        }
        if (mPopMenu.isShowing) {
            mPopMenu.dismiss()
        }

    }

//    override fun onGetPatientRecommendSuccess(recommend: Int) {

//        val tvRecommend = mPopMenu.contentView.findViewById<TextView>(R.id.tv_recommend_patients)
//
//        tvRecommend.text = when (recommend) {
//            0 -> {
//                "推荐患者"
//            }
//            1 -> {
//                "已推荐"
//            }
//            2 -> {
//                "无法推荐"
//            }
//            else -> {
//                "可推荐"
//            }
//        }
//    }

//    override fun onGetPatientRecommendFailed(error: String) {
//        onPatientRecommendFailed(error)
//    }

//    override fun onPatientRecommendSuccess(recommendation: Int) {
//        val tvRecommend = mPopMenu.contentView.findViewById<TextView>(R.id.tv_recommend_patients)
//
//        tvRecommend.text = when (recommendation) {
//            0 -> {
//                "推荐患者"
//            }
//            1 -> {
//                onPatientRecommendFailed("推荐成功")
//                "已推荐"
//            }
//            2 -> {
//                "无法推荐"
//            }
//            else -> {
//                "可推荐"
//            }
//        }
//    }
//
//    override fun onPatientRecommendFailed(error: String) {
//        ToastHelper.show(context, error, Gravity.CENTER)
//    }

    data class DiaryDetailData(val userId: Int, val timeUnix: Int)
}
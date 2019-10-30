package com.sumian.sd.buz.anxiousandfaith

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.helper.ToastHelper
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyData
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyData.Companion.EXTRA_KEY_ANXIETY
import com.sumian.sd.buz.anxiousandfaith.databinding.ActivityAnxiousDetailData
import com.sumian.sd.buz.anxiousandfaith.event.AnxietyChangeEvent
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.databinding.ActivityAnxietyDetailBinding
import kotlinx.android.synthetic.main.activity_anxiety_detail.*
import org.greenrobot.eventbus.Subscribe

class AnxietyDetailActivity : WhileTitleNavBgActivity() {

    private var mAnxietyData: AnxietyData? = null
    private var mAnxietyDataId = INVALID_ANXIETY_DATA_ID

    companion object {

        const val EXTRA_ANXIETY_DATA_ID = "extra_anxiety_data_id"
        const val INVALID_ANXIETY_DATA_ID = -1

        fun launch(anxiety: AnxietyData? = null) {
            val intent = Intent(ActivityUtils.getTopActivity(), AnxietyDetailActivity::class.java)
            intent.putExtra(EXTRA_KEY_ANXIETY, anxiety)
            ActivityUtils.startActivity(intent)
        }

        fun getLaunchIntent(anxietyDataId: Int? = null): Intent {
            var intent = Intent(ActivityUtils.getTopActivity(), AnxietyDetailActivity::class.java)
            intent.putExtra(EXTRA_ANXIETY_DATA_ID, anxietyDataId ?: INVALID_ANXIETY_DATA_ID)
            return intent
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_anxiety_detail
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mAnxietyData = bundle.getParcelable(EXTRA_KEY_ANXIETY)
        mAnxietyDataId = bundle.getInt(EXTRA_ANXIETY_DATA_ID)
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(getString(R.string.anxiety_detail_title_text))
        if (mAnxietyData == null && mAnxietyDataId != INVALID_ANXIETY_DATA_ID) {
            var call = AppManager.getSdHttpService().getAnxiety(mAnxietyDataId)
            call.enqueue(object : BaseSdResponseCallback<AnxietyData>() {

                override fun onSuccess(response: AnxietyData?) {
                    mAnxietyData = response
                    refreshDataBinding()
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    ToastHelper.show(errorResponse.message)
                }
            })
            addCall(call)
        }else {
            refreshDataBinding()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBusUtil.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusUtil.unregister(this)
    }

    @Subscribe
    fun onAnxietyChangeEvent(event: AnxietyChangeEvent) {
        mAnxietyData = event.anxiety
        refreshDataBinding()
    }

    private fun refreshDataBinding() {
        var bind = DataBindingUtil.bind<ActivityAnxietyDetailBinding>(activity_anxiety_detail)
                ?: return
        var data: AnxietyData = mAnxietyData ?: return
        var hasDetailedPlanChecked = data.hasDetailedPlanChecked()
        var hasHardChecked = data.hardChecked()
        var anxiety = data.anxiety
        var resolvePlanContent = if (hasDetailedPlanChecked) data.solution else getString(R.string.anxiety_no_detailed_plan_text)
        var notHardProblemContent = if (!hasHardChecked && !hasDetailedPlanChecked) data.solution else getString(R.string.anxiety_is_hard_problem_text)
        var howToResolveContent = AnxietyData.ANSWER_INVALID_VALUE
        var howToResolveTitle =
                if (!hasDetailedPlanChecked && hasHardChecked) {
                    when (data.getHowToResolveCheckedId()) {
                        AnxietyData.ANSWER_HOW_TO_SOLVE_ONE_INDEX -> {
                            howToResolveContent = data.solution
                            getString(R.string.anxiety_suggest_to_friend_title)
                        }
                        AnxietyData.ANSWER_HOW_TO_SOLVE_TWO_INDEX -> {
                            howToResolveContent = data.solution
                            getString(R.string.anxiety_need_help_title)
                        }
                        AnxietyData.ANSWER_HOW_TO_SOLVE_THREE_INDEX -> {
                            howToResolveContent = getString(R.string.anxiety_no_idea_content)
                            ""
                        }
                        else -> {
                            ""
                        }
                    }
                } else {
                    ""
                }

        bind.data = ActivityAnxiousDetailData(hasDetailedPlanChecked, hasHardChecked, anxiety, resolvePlanContent, notHardProblemContent,
                howToResolveTitle, howToResolveContent, TimeUtilV2.formatYYYYMMDDHHMM(data.getRemindAtInMillis()),
                TimeUtilV2.formatYYYYMMDDHHMMss(data?.getUpdateAtInMillis()),
                View.OnClickListener { AnxietyEditActivity.launch(mAnxietyData) })
    }

}
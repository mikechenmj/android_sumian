package com.sumian.sd.buz.device.devicemanage

import androidx.core.view.isVisible
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.sumian.common.base.BaseActivity
import com.sumian.sd.R
import com.sumian.sd.examine.main.me.ExamineQuestionActivity
import com.sumian.sd.examine.main.me.userinfo.ExamineUserInfoActivity
import kotlinx.android.synthetic.main.activity_main_assessment.*

class ExamineAssessmentActivity : BaseActivity() {

    companion object {
        fun show() {
            ActivityUtils.startActivity(ExamineAssessmentActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_assessment
    }

    override fun onResume() {
        super.onResume()
        val userInfoClicked = SPUtils.getInstance().getInt("examine_user_info", 0)
        val assessmentClicked = SPUtils.getInstance().getInt("examine_assessment", 0)
        if (userInfoClicked > 0) {
            iv_user_info.setImageResource(R.drawable.chatbubble_icon_info_complete)
            bt_user_info_table.isVisible = false
        }
        if (assessmentClicked > 0) {
            iv_sleep_assessment.setImageResource(R.drawable.chatbubble_icon_evaluationform_complete)
            bt_assessment_table.isVisible = false
        }
    }

    override fun initWidget() {
        super.initWidget()
        lay_show_user_info_table.setOnClickListener {
            ExamineUserInfoActivity.show()
            SPUtils.getInstance().put("examine_user_info", 1)
        }
        bt_user_info_table.setOnClickListener {
            ExamineUserInfoActivity.show()
            SPUtils.getInstance().put("examine_user_info", 1)
        }
        lay_show_sleep_assessment_table.setOnClickListener {
            ExamineQuestionActivity.show()
            SPUtils.getInstance().put("examine_assessment", 1)
        }
        bt_assessment_table.setOnClickListener {
            ExamineQuestionActivity.show()
            SPUtils.getInstance().put("examine_assessment", 1)
        }
    }
}
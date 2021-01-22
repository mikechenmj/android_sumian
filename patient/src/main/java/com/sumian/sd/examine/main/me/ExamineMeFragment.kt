package com.sumian.sd.examine.main.me

import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.SPUtils
import com.sumian.common.base.BaseFragment
import com.sumian.common.image.loadImage
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.bean.UserInfo
import com.sumian.sd.buz.account.model.AccountManager
import com.sumian.sd.buz.setting.remind.SleepDiaryRemindSettingActivity
import com.sumian.sd.examine.guide.ExamineUserGuidelineActivity
import com.sumian.sd.examine.main.me.userinfo.ExamineUserInfoActivity
import com.sumian.sd.main.OnEnterListener
import kotlinx.android.synthetic.main.examine_me_fragment.*
import java.util.*

class ExamineMeFragment : BaseFragment(), OnEnterListener {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ExamineMeViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.examine_me_fragment
    }

    override fun onEnter(data: String?) {
        AppManager.syncOrganization()
    }

    override fun initData() {
        super.initData()
        viewModel.userInfoData.observe(this, Observer<UserInfo> {
            updateUi(it)
        })
    }

    override fun initWidget() {
        super.initWidget()
        updateUi(AccountManager.userInfo)
        lay_setting.setOnClickListener {
            ExamineSettingActivity.show()
        }
        lay_user_guide.setOnClickListener {
            ExamineUserGuidelineActivity.show()
        }
        lay_firmware_update.setOnClickListener {
            ExamineVersionUpdateActivity.show()
        }
        lay_my_msg_notice.setOnClickListener {
            ExamineNewActivity.show()
        }
        lay_sleepy_notice.setOnClickListener {
            ExamineSleepRemindActivity.show()
        }
        lay_sleepy_answer.setOnClickListener {
            ExamineQuestionActivity.show()
        }
        lay_go_to_info_center.setOnClickListener {
            ExamineUserInfoActivity.show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (SPUtils.getInstance().getBoolean("examine_sleep_remind", false)) {
            val hours = String.format("%02d", SPUtils.getInstance().getInt("examine_sleep_remind_hours", -1))
            val minutes = String.format("%02d", SPUtils.getInstance().getInt("examine_sleep_remind_minutes", -1))
            val content = "$hours:$minutes"
            tv_sleep_notice_state.text = content
        } else {
            tv_sleep_notice_state.text = getString(R.string.sleepy_notice_state_off_hint)
        }
    }

    private fun updateUi(info: UserInfo?) {
        if (info == null) {
            return
        }
        iv_avatar.loadImage(info.avatar, R.drawable.ic_default_avatar, R.drawable.ic_default_avatar)
        val nickname = info.nickname
        tv_nickname.text = if (TextUtils.isEmpty(nickname)) info.mobile else nickname
        tv_age_and_gender.text = formatGender(info.getGender())
        val age = info.getAge()
        if (age != null) {
            tv_age_and_gender.append(java.lang.String.format(Locale.getDefault(), "%s%s%s", "  丨  ", age, "岁"))
        }
    }

    private fun formatGender(gender: String): String? {
        var genderText: String = getString(R.string.gender_secrecy_hint)
        if (TextUtils.isEmpty(gender)) {
            return genderText
        }
        genderText = when (gender) {
            "male" -> getString(R.string.gender_male_hint)
            "female" -> getString(R.string.gender_female_hint)
            "secrecy" -> getString(R.string.gender_secrecy_hint)
            else -> getString(R.string.user_none_default_hint)
        }
        return genderText
    }
}
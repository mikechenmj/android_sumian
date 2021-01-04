package com.sumian.sd.examine.main.me

import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sumian.common.base.BaseFragment
import com.sumian.common.image.loadImage
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.bean.UserInfo
import com.sumian.sd.buz.account.model.AccountManager
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
    }

    private fun updateUi(info: UserInfo?) {
        if (info == null) {
            return
        }
        iv_avatar.loadImage(info.avatar, R.drawable.ic_default_avatar, R.drawable.ic_default_avatar)
        val nickname = info.nickname
        tv_nickname.text = if (TextUtils.isEmpty(nickname)) info.mobile else nickname
        tv_age_and_gender.text = formatGender(info.getGender())
        val age: String = info.getAge().toString()
        if (!TextUtils.isEmpty(age)) {
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
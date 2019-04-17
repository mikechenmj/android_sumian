package com.sumian.sddoctor.me.authentication

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.statistic.StatUtil
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.login.register.AuthenticateViewModel
import kotlinx.android.synthetic.main.activity_authentication_activity.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/23 14:36
 * desc   :
 * version: 1.0
 */
class AuthenticationActivity : SddBaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_authentication_activity
    }

    companion object {
        fun start() {
            ActivityUtils.startActivity(ActivityUtils.getTopActivity(), AuthenticationActivity::class.java)
        }
    }

    override fun initWidget() {
        super.initWidget()
        StatUtil.event(StatConstants.enter_doctor_verify_page)
        setTitle(R.string.authentication_center)
        btn_confirm.setOnClickListener { finish() }
    }

    override fun initData() {
        super.initData()
        ViewModelProviders.of(this).get(AuthenticateViewModel::class.java).mProgressLiveData.observe(this, Observer {
            if (it == 1) {
                supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentById(R.id.authentication_fragment)!!).commit()
                vg_commit_success.visibility = View.VISIBLE
                AppManager.updateDoctorInfo()
            }
        })
    }

    override fun showBackNav(): Boolean {
        return true
    }
}
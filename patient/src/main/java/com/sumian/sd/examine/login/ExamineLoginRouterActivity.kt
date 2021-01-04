package com.sumian.sd.examine.login

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseActivity
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.examine.main.viewmodel.ExamineLoginRouterViewModel
import kotlinx.android.synthetic.main.activity_examine_login_router.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExamineLoginRouterActivity : BaseActivity() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(ExamineLoginRouterViewModel::class.java) }

    override fun getLayoutId(): Int {
        return R.layout.activity_examine_login_router
    }

    companion object {
        fun show() {
            ActivityUtils.startActivity(ExamineLoginRouterActivity::class.java)
        }
    }

    override fun initWidget() {
        super.initWidget()
        bt_login.setOnClickListener {
            startActivity(Intent(this, ExamineLoginActivity::class.java))
        }
        bt_wechat.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                viewModel.loginByWechat(this@ExamineLoginRouterActivity)
            }
        }
        AppManager.checkAgreementShouldShow(supportFragmentManager)
    }
}
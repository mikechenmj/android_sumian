package com.sumian.sddoctor.me.mywallet

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.mvp.IPresenter
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.me.mywallet.bean.WithdrawRule
import kotlinx.android.synthetic.main.activity_withdraw_rules.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/25 14:10
 * desc   :
 * version: 1.0
 */
class WithdrawRulesActivity : SddBaseActivity<IPresenter>() {
    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_withdraw_rules
    }

    companion object {
        fun launch() {
            ActivityUtils.startActivity(WithdrawRulesActivity::class.java)
        }
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.withdraw_rules)
    }

    override fun initData() {
        super.initData()
        val viewModel = ViewModelProviders.of(this).get(WithdrawRuleViewModel::class.java)
        viewModel.getRuleLiveData().observe(this, Observer<WithdrawRule> { t -> tv_rules.text = t?.rule })
        viewModel.queryRule()
    }

}
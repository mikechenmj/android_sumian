package com.sumian.sddoctor.me.mywallet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.webkit.WebView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.me.mywallet.bean.WithdrawRecord
import com.sumian.sddoctor.me.mywallet.bean.WithdrawRule
import com.sumian.sddoctor.util.MoneyUtil
import com.sumian.sddoctor.widget.dialog.SumianDialog
import com.sumian.sddoctor.widget.edittext.FloatInputFilter
import com.sumian.sddoctor.widget.text.EmptyTextWatcher
import kotlinx.android.synthetic.main.activity_my_wallet.*
import kotlinx.android.synthetic.main.activity_withdraw.*
import kotlinx.android.synthetic.main.layout_withdraw_success.*
import retrofit2.Call
import java.math.BigDecimal

@SuppressLint("SetJavaScriptEnabled")
/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/25 10:07
 * desc   :
 * version: 1.0
 */
class WithdrawActivity : SddBaseActivity<WithdrawPresenter>(), WithdrawContract.View {
    private var mBalance = 0L
    private var mWithdrawAmount = 0L
    private val mWebView by lazy {
        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView
    }
    private var mRules = ""
    private var mJs = ""
    //    private val mPresenter by lazy { WithdrawPresenter(this) }
    private var mWithdrawRecord: WithdrawRecord? = null

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_withdraw
    }

    companion object {
        private const val KEY_BALANCE = "balance"

        fun launch(balance: Long) {
            val intent = Intent(ActivityUtils.getTopActivity(), WithdrawActivity::class.java)
            intent.putExtra(KEY_BALANCE, balance)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mBalance = bundle.getLong(KEY_BALANCE)
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        mPresenter = WithdrawPresenter(this)
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.withdraw)
        mTitleBar.showMoreIcon(R.drawable.ic_nav_question)
        mTitleBar.setOnMenuClickListener { WithdrawRulesActivity.launch() }
        et_amount.addTextChangedListener(object : EmptyTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                val input = et_amount.text.toString()
                val fen = MoneyUtil.yuanStringToFen(input)
                mWithdrawAmount = fen
                updateUI(fen)
            }
        })
        et_amount.filters = arrayOf(FloatInputFilter(8, 2))
        tv_available_withdraw_amount.text = resources.getString(R.string.available_withdraw_amount_xx, MoneyUtil.fenToYuanString(mBalance))
        bt_withdraw_all.setOnClickListener { et_amount.setText((MoneyUtil.fenToYuanString(mBalance))) }
        bt_withdraw.setOnClickListener { withdraw(mWithdrawAmount) }
        btn_complete.setOnClickListener {
            WithdrawDetailActivity.launch(mWithdrawRecord!!.id)
            finish()
        }
    }

    override fun initData() {
        super.initData()
        val viewModel = ViewModelProviders.of(this).get(WithdrawRuleViewModel::class.java)
        viewModel.getRuleLiveData().observe(this, Observer<WithdrawRule> { t ->
            t?.let {
                mRules = t.rule
                mJs = t.code
            }
        })
    }

    private fun withdraw(amount: Long) {
        SumianDialog(this)
                .setTitleText(R.string.withdraw_explain)
                .setMessageText(R.string.withdraw_explain_desc)
                .whitenLeft()
                .setLeftBtn(R.string.cancel, null)
                .setRightBtn(R.string.confirm_withdraw, View.OnClickListener { mPresenter!!.withdraw(amount) })
                .show()
    }

    private fun updateUI(amount: Long) {
        var isInputValid = true
        var hintStringRes = R.string.input_withdraw_amount
        when {
            amount > mBalance -> {
                isInputValid = false
                hintStringRes = R.string.withdraw_amount_is_over_balance
            }
            amount < 100 * 100 -> {
                isInputValid = false
                hintStringRes = R.string.withdraw_amount_need_upon_100
            }
        }
        tv_hint.text = getText(hintStringRes)
        tv_hint.setTextColor(ColorCompatUtil.getColor(this, if (isInputValid) R.color.t2_color else R.color.t4_color))
        bt_withdraw.isEnabled = isInputValid
    }

    override fun addCalls(call: Call<*>) {
        addCall(call)
    }

    override fun withdrawSuccess(withdrawRecord: WithdrawRecord) {
        mWithdrawRecord = withdrawRecord
        vg_withdraw.visibility = View.GONE
        vg_withdraw_success.visibility = View.VISIBLE
    }

    override fun withdrawFail(code: Int, message: String) {
        if (code == 1 || code == 2) {
            SumianDialog(this)
                    .setTopIcon(R.drawable.ic_msg_icon_abnormal)
                    .setMessageText(message)
                    .setLeftBtn(R.string.confirm, null)
                    .show()
        } else {
            ToastUtils.showShort(message)
        }
    }
}
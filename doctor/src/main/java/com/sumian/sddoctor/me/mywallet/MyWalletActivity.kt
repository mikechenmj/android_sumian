package com.sumian.sddoctor.me.mywallet

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.StatusBarUtil
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.me.mywallet.bean.WalletBalance
import com.sumian.sddoctor.me.mywallet.bean.WalletDetail
import com.sumian.sddoctor.me.mywallet.bean.WalletDetailResponse
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.MoneyUtil
import com.sumian.sddoctor.widget.EmptyViewCreator
import kotlinx.android.synthetic.main.activity_my_wallet.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/18 13:42
 * desc   :
 * version: 1.0
 */
@Suppress("DEPRECATION")
class MyWalletActivity : SddBaseActivity() {
    private val mAdapter = MyWalletAdapter()
    private var mPage = 1

    override fun getLayoutId(): Int {
        return R.layout.activity_my_wallet
    }

    override fun getPageName(): String {
        return StatConstants.page_profile_wallet
    }

    override fun initWidget() {
        super.initWidget()
        iv_nav_icon.setOnClickListener { onBackPressed() }
        tv_inspect.setOnClickListener { ActivityUtils.startActivity(WithdrawAmountActivity::class.java) }
        v_settling.setOnClickListener { ActivityUtils.startActivity(PendingIncomeAmountActivity::class.java) }
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = mAdapter
        recycler_view.isNestedScrollingEnabled = false
        mAdapter.setOnLoadMoreListener({ queryDetailList() }, recycler_view)
        mAdapter.setOnItemClickListener { adapter, view, position -> WalletRecordDetailActivity.launch((mAdapter.getItem(position) as WalletDetail).id) }
        mAdapter.emptyView = EmptyViewCreator.createSingleLineTextViewEmptyView(this, R.string.no_wallet_record_yet)

        StatusBarUtil.setStatusBarTextColorDark(this@MyWalletActivity, false)
        scroll_view.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            val headHeight = resources.getDimension(R.dimen.my_wallet_head_height)
            val barHeight = resources.getDimension(R.dimen.dp_72)
            val barScrollThreshold = headHeight - barHeight
            val walletDetailThreshold = barScrollThreshold + resources.getDimension(R.dimen.space_90)
            LogUtils.d(scrollY)
            val fraction = Math.min(scrollY.toFloat(), barScrollThreshold) / barScrollThreshold
            val bgColor = ArgbEvaluator().evaluate(fraction, Color.TRANSPARENT, Color.WHITE) as Int
            val textColor = ArgbEvaluator().evaluate(fraction, Color.WHITE, resources.getColor(R.color.t1_color)) as Int
            val navIcColor = ArgbEvaluator().evaluate(fraction, Color.WHITE, resources.getColor(R.color.colorPrimary)) as Int
            v_toolbar_container.setBackgroundColor(bgColor)
            tv_toolbar.setTextColor(textColor)
            iv_nav_icon.setColorFilter(navIcColor)
            v_wallet_detail_cover.visibility = if (scrollY > walletDetailThreshold) View.VISIBLE else View.GONE
            StatusBarUtil.setStatusBarTextColorDark(this@MyWalletActivity, fraction > 0.5f)
        })
    }

    override fun onStart() {
        super.onStart()
        mPage = 1
        queryBalance()
        queryDetailList()
    }

    private fun queryBalance() {
        val call = AppManager.getHttpService().getWalletBalance()
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<WalletBalance>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            @SuppressLint("SetTextI18n")
            override fun onSuccess(response: WalletBalance?) {
                tv_withdraw_amount.text = "￥" + MoneyUtil.fenToYuanString(response?.balance ?: 0L)
                tv_settling_amount.text = MoneyUtil.fenToYuanString(response?.pending_income ?: 0)
            }
        })
    }

    private fun queryDetailList() {
        val call2 = AppManager.getHttpService().getWalletDetailList(mPage)
        addCall(call2)
        call2.enqueue(object : BaseSdResponseCallback<WalletDetailResponse>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: WalletDetailResponse?) {
                if (response == null) {
                    return
                }
                val pagination = response.meta.pagination
                if (mPage == 1) {
                    mAdapter.setNewData(response.data)
                } else {
                    mAdapter.addData(response.data)
                }
                mAdapter.setEnableLoadMore(!pagination.isLastPage())
                mAdapter.loadMoreComplete()
                mPage++
            }

            override fun onFinish() {
                super.onFinish()
                mAdapter.loadMoreComplete()
            }
        })
    }

    class MyWalletAdapter : BaseQuickAdapter<WalletDetail, BaseViewHolder>(R.layout.list_item_wallet_record_detail) {

        override fun convert(helper: BaseViewHolder, item: WalletDetail) {
            helper.setText(R.id.tv_content, item.content)
            helper.setText(R.id.tv_time, TimeUtilV2.formatYYYYMMDDHHMM(item.getCreateInMillis()))
            helper.setText(R.id.tv_account, MoneyUtil.fenToYuanStringWithSign(item.getSignedAmount()) + "元")
        }
    }
}
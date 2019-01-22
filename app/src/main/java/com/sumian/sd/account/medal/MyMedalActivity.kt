package com.sumian.sd.account.medal

import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import com.sumian.sd.account.medal.adapter.MyMedalAdapter
import com.sumian.sd.account.medal.bean.Medal
import com.sumian.sd.account.medal.contract.MyMedalContract
import com.sumian.sd.account.medal.presenter.MyMedalPresenter
import kotlinx.android.synthetic.main.activity_main_my_medal.*

/**
 * Created by jzz
 *
 * on 2019/1/22
 *
 * desc: 我的勋章成就
 */
class MyMedalActivity : BasePresenterActivity<MyMedalContract.Presenter>(), BaseRecyclerAdapter.OnItemClickListener, MyMedalContract.View {

    private val adapter by lazy {
        val myMedalAdapter = MyMedalAdapter(this)
        myMedalAdapter.setOnItemClickListener(this)
        myMedalAdapter
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_my_medal
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mPresenter = MyMedalPresenter.create(this)
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.my_metal)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler.itemAnimator = null
        recycler.adapter = adapter
    }

    override fun onItemClick(position: Int, itemId: Long) {

    }

    override fun initData() {
        super.initData()
        mPresenter?.getMyMetal()
    }

    override fun onGetMyMedalListSuccess(myMedalList: List<Medal>) {
        adapter.resetItem(myMedalList)
    }

    override fun onGetMyMedalListFailed(error: String) {
        ToastHelper.show(this, error, Gravity.CENTER)
    }

}
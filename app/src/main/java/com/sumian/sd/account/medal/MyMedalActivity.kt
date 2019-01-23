package com.sumian.sd.account.medal

import android.content.Intent
import android.view.Gravity
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import com.sumian.sd.account.medal.adapter.MyMedalAdapter
import com.sumian.sd.account.medal.bean.Data
import com.sumian.sd.account.medal.bean.Meta
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

    companion object {
        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, MyMedalActivity::class.java))
            }
        }
    }

    private val adapter by lazy {
        val myMedalAdapter = MyMedalAdapter(this)
        myMedalAdapter.setOnItemClickListener(this)
        myMedalAdapter
    }

    private lateinit var meta: Meta

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
        recycler.layoutManager = GridLayoutManager(this, 3)
        recycler.itemAnimator = null
        recycler.adapter = adapter
    }

    override fun onItemClick(position: Int, itemId: Long) {
        val achievementX = adapter.getItem(position)
        MyMedalShareActivity.show(achievementX, meta.qrCode, meta.avatar)
    }

    override fun initData() {
        super.initData()
        mPresenter?.getMyMetal()
    }

    override fun onGetMyMedalListSuccess(myMedalList: List<Data>) {
        tv_medal_title.text = myMedalList[0].name
        adapter.resetItem(myMedalList[0].achievements)
    }

    override fun onGetMyMedalListFailed(error: String) {
        ToastHelper.show(this, error, Gravity.CENTER)
    }

    override fun onGetMetaCallback(meta: Meta) {
        this.meta = meta
    }
}
package com.sumian.sd.buz.sleepertalk

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.base.BaseViewModel
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.image.ImageLoader
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.sleepertalk.bean.SleepTalkData
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.activity_sleeper_talk_list.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/7 09:35
 * desc   :
 * version: 1.0
 */
class SleeperTalkListActivity : BaseViewModelActivity<BaseViewModel>() {
    private var mPage = 1
    private val mAdapter = SleeperTalkAdapter()

    override fun getLayoutId(): Int {
        return R.layout.activity_sleeper_talk_list
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.sleeper_talk)

        recycler_view.adapter = mAdapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        mAdapter.setOnLoadMoreListener({ loadMore() }, recycler_view)
    }

    override fun initData() {
        super.initData()
        loadMore()
    }

    private fun loadMore() {
        val call = AppManager.getSdHttpService().getSleeperTalkList(mPage)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<SleepTalkData>>() {
            override fun onSuccess(response: PaginationResponseV2<SleepTalkData>?) {
                mAdapter.addData(response!!.data)
                mAdapter.setEnableLoadMore(response.meta.pagination.isLastPage())
                mPage++
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    companion object {
        fun launch() {
            ActivityUtils.startActivity(Intent(ActivityUtils.getTopActivity(), SleeperTalkListActivity::class.java))
        }
    }

    class SleeperTalkAdapter : BaseQuickAdapter<SleepTalkData, BaseViewHolder>(R.layout.list_item_sleeper_talk_list) {
        override fun convert(helper: BaseViewHolder, item: SleepTalkData) {
            helper.setText(R.id.tv_title, item.title)
            val simpleDateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
            val time = simpleDateFormat.format(Date(item.createdAt * 1000L))
            helper.setText(R.id.tv_sub_title, "${item.author}· $time")
            ImageLoader.loadImage(item.coverUrl, helper.getView(R.id.iv_icon))
        }
    }
}
package com.sumian.sd.service.cbti.activity

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.widget.recycler.LoadMoreRecyclerView
import com.sumian.sd.R
import com.sumian.sd.service.cbti.adapter.CBTIMessageBoardAdapter
import com.sumian.sd.service.cbti.bean.MessageBoard
import com.sumian.sd.service.cbti.contract.CBTISelfMessageBoardActionContract
import com.sumian.sd.service.cbti.presenter.CBTISelfMessageBoardActionPresenter
import kotlinx.android.synthetic.main.activity_main_message_keyboard.*

/**
 * Created by sm
 *
 * on 2018/12/7
 *
 * desc:留言板，直接留言、显示留言列表、删除留言
 *
 */
class MessageBoardActionActivity : BasePresenterActivity<CBTISelfMessageBoardActionContract.Presenter>(), CBTIMessageBoardAdapter.OnDelCallback, LoadMoreRecyclerView.OnLoadCallback, CBTISelfMessageBoardActionContract.View {

    companion object {

        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, MessageBoardActionActivity::class.java))
            }
        }
    }

    private val messageBoardAdapter by lazy {
        CBTIMessageBoardAdapter(context = this@MessageBoardActionActivity)
                .setMsgType(CBTIMessageBoardAdapter.MSG_SELF_LIST_TYPE)
                .setDelCallback(this@MessageBoardActionActivity)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_message_keyboard
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mPresenter = CBTISelfMessageBoardActionPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        recycler.setOnLoadCallback(this)
        recycler.adapter = messageBoardAdapter
        recycler.itemAnimator = null
        recycler.layoutManager = LinearLayoutManager(this@MessageBoardActionActivity)
    }

    override fun initData() {
        super.initData()
        //mPresenter?.getSelfMsgListMsg()
    }

    override fun loadMore() {
        super.loadMore()
        mPresenter?.getNextSelfMsgListMsg()
    }


    override fun delCallback(item: MessageBoard) {
        mPresenter?.delSelfMsg()
    }

    override fun onPublishMessageBoardSuccess(success: String) {
        // mPresenter?.getSelfMsgListMsg()
    }

    override fun onPublishMessageBoardFailed(error: String) {
    }

    override fun onDelSuccess(success: String) {
    }

    override fun onDelFailed(error: String) {
    }

    override fun onGetSelfMsgListSuccess(selfMsgList: MutableList<MessageBoard>) {
    }

    override fun onGetSelfMsgListFailed(error: String) {
    }

}
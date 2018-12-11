package com.sumian.sd.service.cbti.activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.dialog.SumianImageTextToast
import com.sumian.common.helper.ToastHelper
import com.sumian.common.widget.recycler.LoadMoreRecyclerView
import com.sumian.sd.R
import com.sumian.sd.service.cbti.adapter.CBTIMessageBoardAdapter
import com.sumian.sd.service.cbti.bean.MessageBoard
import com.sumian.sd.service.cbti.contract.CBTISelfMessageBoardContract
import com.sumian.sd.service.cbti.presenter.CBTISelfMessageBoardPresenter
import com.sumian.sd.service.cbti.widget.keyboard.MaxMsgBoardKeyBoard
import com.sumian.sd.widget.dialog.SumianAlertDialog
import kotlinx.android.synthetic.main.activity_main_message_board.*
import kotlinx.android.synthetic.main.lay_max_msg_keyboard_view.*

/**
 * Created by sm
 *
 * on 2018/12/7
 *
 * desc:留言板，直接留言、显示留言列表、删除留言
 *
 */
class CBTIMessageBoardActivity : BasePresenterActivity<CBTISelfMessageBoardContract.Presenter>(), CBTIMessageBoardAdapter.OnDelCallback,
        LoadMoreRecyclerView.OnLoadCallback, CBTISelfMessageBoardContract.View, MaxMsgBoardKeyBoard.OnKeyBoardCallback {

    companion object {
        private const val ARGS_TYPE = "com.sumian.sd.args_cbti_type"

        @JvmStatic
        fun show(cbtiPartType: Int) {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, CBTIMessageBoardActivity::class.java).apply {
                    putExtra(ARGS_TYPE, cbtiPartType)
                })
            }
        }
    }

    private val messageBoardAdapter by lazy {
        CBTIMessageBoardAdapter(context = this@CBTIMessageBoardActivity)
                .setMsgType(CBTIMessageBoardAdapter.MSG_SELF_LIST_TYPE)
                .setDelCallback(this@CBTIMessageBoardActivity)
    }

    private var mCbtiPartType: Int = 0

    private var mIsLoadMoreOrInit = false

    override fun getLayoutId(): Int {
        return R.layout.activity_main_message_board
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mCbtiPartType = bundle.getInt(ARGS_TYPE)
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mPresenter = CBTISelfMessageBoardPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { onBackPressed() }
        max_msg_keyboard.bindSendAction(btn_send).setOnKeyBoardCallback(this)
        recycler.setOnLoadCallback(this)
        recycler.adapter = messageBoardAdapter
        recycler.itemAnimator = null
        recycler.layoutManager = LinearLayoutManager(this@CBTIMessageBoardActivity)
    }

    override fun initData() {
        super.initData()
        mIsLoadMoreOrInit = true
        mPresenter?.getSelfMsgListMsg(mCbtiPartType)
    }

    override fun sendContent(content: String, anonymousType: Int) {
        mPresenter?.publishMessage(content, mCbtiPartType, anonymousType)
    }

    override fun close() {
        max_msg_keyboard.hideKeyBoard()
    }

    override fun showLoading() {
        if (mIsLoadMoreOrInit) {
            return
        }
        super.showLoading()
    }

    override fun dismissLoading() {
        if (!mIsLoadMoreOrInit) {
            super.dismissLoading()
        }
        mIsLoadMoreOrInit = false
    }

    override fun loadMore() {
        super.loadMore()
        mIsLoadMoreOrInit = true
        mPresenter?.getNextSelfMsgListMsg()
    }

    override fun delCallback(item: MessageBoard, position: Int) {
        SumianAlertDialog(this)
                .hideTopIcon(true)
                .setCancelable(true)
                .setLeftBtn(R.string.cancel, null)
                .setRightBtn(R.string.sure) { mPresenter?.delSelfMsg(item.id, position) }
                .setMessage(R.string.del_msg_keyboard_msg)
                .setTitle(R.string.del_msg_keyboard_title)
                .whitenLeft()
                .show()
    }

    override fun onPublishMessageBoardSuccess(success: String) {
        et_msg_board_input.text = null
        SumianImageTextToast.showToast(this, R.drawable.ic_dialog_success, success, false)
        finish()
        //mPresenter?.refreshSelfMsgListMsg()
    }

    override fun onPublishMessageBoardFailed(error: String) {
        onGetSelfMsgListFailed(error)
    }

    override fun onDelSuccess(success: String, position: Int) {
        messageBoardAdapter.removeItem(position)
        if (messageBoardAdapter.itemCount == 0) {
            showEmptyView()
        } else {
            hideEmptyView()
        }
    }

    override fun onDelFailed(error: String) {
        onGetSelfMsgListFailed(error)
    }

    override fun onGetSelfMsgListSuccess(selfMsgList: MutableList<MessageBoard>) {
        updateUi(selfMsgList)
    }

    override fun onRefreshMessageBoardListSuccess(selfMsgList: MutableList<MessageBoard>) {
        updateUi(selfMsgList)
    }

    override fun onGetNextMessageBoardListSuccess(selfMsgList: MutableList<MessageBoard>) {
        messageBoardAdapter.addAll(selfMsgList)
        hideEmptyView()
    }

    override fun onGetSelfMsgListFailed(error: String) {
        ToastHelper.show(this@CBTIMessageBoardActivity, error, Gravity.CENTER)
    }

    private fun hideEmptyView() {
        tv_msg_board_empty?.visibility = View.GONE
    }

    private fun showEmptyView() {
        tv_msg_board_empty?.visibility = View.VISIBLE
    }

    private fun updateUi(msgBoardList: List<MessageBoard>) {
        if (msgBoardList.isEmpty()) {
            showEmptyView()
        } else {
            messageBoardAdapter.resetItem(msgBoardList)
            hideEmptyView()
        }
    }

}
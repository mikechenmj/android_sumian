package com.sumian.sd.buz.cbti.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import com.sumian.sd.buz.cbti.bean.MessageBoard
import com.sumian.sd.buz.cbti.contract.CBTIMessageBoardDetailContract
import com.sumian.sd.buz.cbti.presenter.CBTIMessageBoardDetailPresenter
import kotlinx.android.synthetic.main.activity_main_message_board_detail.*

/**
 * Created by jzz
 *
 * on  2018/12/8
 *
 * desc:留言详情
 *
 */
class CBTIMessageBoardDetailActivity : BaseViewModelActivity<CBTIMessageBoardDetailPresenter>(), CBTIMessageBoardDetailContract
.View, SwipeRefreshLayout
.OnRefreshListener {

    companion object {
        private const val ARGS_MSG_ID = "com.sumian.sd.args.msg.id"

        @JvmStatic
        fun show(msgId: Int) {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, CBTIMessageBoardDetailActivity::class.java).apply {
                    putExtra(ARGS_MSG_ID, msgId)
                })
            }
        }

        fun getIntent(context: Context, msgId: Int): Intent {
            val intent = Intent(context, CBTIMessageBoardDetailActivity::class.java)
            intent.putExtra(ARGS_MSG_ID, msgId)
            return intent
        }

    }

    private var msgId: Int = 0

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.msgId = bundle.getInt(ARGS_MSG_ID)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_message_board_detail
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mViewModel = CBTIMessageBoardDetailPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(getString(R.string.msg_board_replay_notice))
        refresh.setOnRefreshListener(this)
    }

    override fun initData() {
        super.initData()
        mViewModel?.getMsgBoardDetail(msgId)
    }

    override fun onRefresh() {
        mViewModel?.getMsgBoardDetail(msgId)
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun onGetMsgBoardDetailSuccess(messageBoard: MessageBoard) {
        tv_msg.text = messageBoard.message
        if (messageBoard.reply.isNullOrEmpty()) {
            tv_doctor_reply.visibility = View.GONE
            divider.visibility = View.GONE
            tv_replay_msg.text = null
            tv_replay_msg.visibility = View.GONE
        } else {
            tv_doctor_reply.visibility = View.VISIBLE
            divider.visibility = View.VISIBLE
            tv_replay_msg.text = messageBoard.reply
        }
        onHideErrorView()
    }

    override fun onGetMsgBoardDetailFailed(error: String) {
        ToastHelper.show(this, error, Gravity.CENTER)
    }

    override fun onShowErrorView() {
        msg_board_container.visibility = View.GONE
        empty_error_view.invalidMessageBoardError()
        tv_msg_tips.visibility = View.VISIBLE
    }

    override fun onHideErrorView() {
        empty_error_view.hide()
        tv_msg_tips.visibility = View.GONE
        msg_board_container.visibility = View.VISIBLE
    }
}
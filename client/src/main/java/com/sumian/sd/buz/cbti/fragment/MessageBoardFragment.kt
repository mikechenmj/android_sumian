package com.sumian.sd.buz.cbti.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.sumian.common.widget.recycler.LoadMoreRecyclerView
import com.sumian.sd.R
import com.sumian.sd.base.SdBaseFragment
import com.sumian.sd.buz.cbti.adapter.CBTIMessageBoardAdapter
import com.sumian.sd.buz.cbti.bean.CBTIMeta
import com.sumian.sd.buz.cbti.bean.MessageBoard
import com.sumian.sd.buz.cbti.model.CbtiChapterViewModel
import com.sumian.sd.buz.cbti.presenter.CBTIMsgBoardPresenter
import kotlinx.android.synthetic.main.fragment_main_cbti_message_board.*

/**
 * CBTI 留言板   留言板列表 和留言功能
 */
class MessageBoardFragment : SdBaseFragment<CBTIMsgBoardPresenter>(), LoadMoreRecyclerView.OnLoadCallback,
        Observer<CBTIMeta> {

    companion object {

        private const val ARGS_TYPE = "com.sumian.sd.args_cbti_type"

        @JvmStatic
        fun newInstance(cbtiPartType: Int): MessageBoardFragment = MessageBoardFragment().apply {
            arguments = Bundle().apply {
                putInt(ARGS_TYPE, cbtiPartType)
            }
        }
    }

    private val messageBoardAdapter by lazy {
        CBTIMessageBoardAdapter(context = context!!).setMsgType(CBTIMessageBoardAdapter.MSG_NORMAL_LIST_TYPE)
    }

    private var mCbtiPartType: Int = 0
    private var mIsInit = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_cbti_message_board
    }

    override fun initBundle(bundle: Bundle?) {
        super.initBundle(bundle)
        bundle?.let {
            this.mCbtiPartType = it.getInt(ARGS_TYPE)
        }
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        recycler.setOnLoadCallback(this)
        recycler.adapter = messageBoardAdapter
        recycler.itemAnimator = null
        recycler.layoutManager = LinearLayoutManager(context!!)
    }

    override fun initPresenter() {
        super.initPresenter()
        CBTIMsgBoardPresenter.init(this)
    }

    override fun initData() {
        super.initData()
        ViewModelProviders.of(activity!!).get(CbtiChapterViewModel::class.java).getCBTICourseMetaLiveData().observe(this, this)
        mViewModel.setType(mCbtiPartType)
        mIsInit = true
    }

    override fun onRelease() {
        ViewModelProviders.of(this).get(CbtiChapterViewModel::class.java).getCBTICourseMetaLiveData().removeObserver(this)
        super.onRelease()
    }

    override fun loadMore() {
        super.loadMore()
        mViewModel.getNextMessageBoardList()
    }

    fun setPresenter(presenter: CBTIMsgBoardPresenter?) {
        //super.setPresenter(presenter)
        this.mViewModel = presenter
    }

    fun onGetMessageBoardListSuccess(msgBoardList: List<MessageBoard>) {
        mIsInit = false
        updateUi(msgBoardList)
    }

    fun onRefreshMessageBoardListSuccess(msgBoardList: List<MessageBoard>) {
        updateUi(msgBoardList)
    }

    fun onGetNextMessageBoardListSuccess(msgBoardList: List<MessageBoard>) {
        messageBoardAdapter.addAll(msgBoardList)
        hideEmptyView()
    }

    fun onGetMessageBoardListFailed(error: String) {
        mIsInit = false
    }

    override fun onChanged(t: CBTIMeta?) {
        t?.let {
            if (mIsInit) {
                return@let
            }
            this.mCbtiPartType = t.chapter.index
            mViewModel.setType(mCbtiPartType)
        }
    }

    private fun hideEmptyView() {
        tv_msg_board_empty?.visibility = View.GONE
        recycler.visibility = View.VISIBLE
    }

    private fun showEmptyView() {
        tv_msg_board_empty?.visibility = View.VISIBLE
        recycler.visibility = View.GONE
    }

    private fun updateUi(msgBoardList: List<MessageBoard>) {
        if (msgBoardList.isNullOrEmpty()) {
            showEmptyView()
        } else {
            messageBoardAdapter.resetItem(msgBoardList)
            hideEmptyView()
        }
    }

    fun onBegin() {

    }

    fun onFinish() {

    }
}
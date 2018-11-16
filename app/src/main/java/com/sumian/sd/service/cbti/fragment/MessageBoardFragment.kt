package com.sumian.sd.service.cbti.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.sumian.common.widget.recycler.LoadMoreRecyclerView
import com.sumian.sd.R
import com.sumian.sd.base.SdBaseFragment
import com.sumian.sd.service.cbti.adapter.CBTIMessageBoardAdapter
import com.sumian.sd.service.cbti.bean.CBTIMeta
import com.sumian.sd.service.cbti.bean.MessageBoard
import com.sumian.sd.service.cbti.contract.CBTIMessageBoardContract
import com.sumian.sd.service.cbti.model.CbtiChapterViewModel
import com.sumian.sd.service.cbti.presenter.CBTIMsgBoardPresenter
import kotlinx.android.synthetic.main.fragment_main_cbti_message_board.*

/**
 * CBTI 留言板   留言板列表 和留言功能
 */
class MessageBoardFragment : SdBaseFragment<CBTIMessageBoardContract.Presenter>(), LoadMoreRecyclerView.OnLoadCallback,
        CBTIMessageBoardContract.View, Observer<CBTIMeta> {

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
        CBTIMessageBoardAdapter(context = context!!)
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
        mPresenter.setType(mCbtiPartType)
        mIsInit = true
    }

    override fun onRelease() {
        ViewModelProviders.of(this).get(CbtiChapterViewModel::class.java).getCBTICourseMetaLiveData().removeObserver(this)
        super.onRelease()
    }

    override fun loadMore() {
        super.loadMore()
        mPresenter.getNextMessageBoardList()
    }

    override fun setPresenter(presenter: CBTIMessageBoardContract.Presenter?) {
        //super.setPresenter(presenter)
        this.mPresenter = presenter
    }

    override fun onGetMessageBoardListSuccess(msgBoardList: List<MessageBoard>) {
        messageBoardAdapter.resetItem(msgBoardList)
        mIsInit = false
        hideEmptyView()
    }

    override fun onRefreshMessageBoardListSuccess(msgBoardList: List<MessageBoard>) {
        messageBoardAdapter.resetItem(msgBoardList)
        hideEmptyView()
    }

    override fun onGetNextMessageBoardListSuccess(msgBoardList: List<MessageBoard>) {
        messageBoardAdapter.addAll(msgBoardList)
        hideEmptyView()
    }

    override fun onGetMessageBoardListFailed(error: String) {
        mIsInit = false
    }

    override fun onChanged(t: CBTIMeta?) {
        t?.let {
            if (mIsInit) {
                return@let
            }
            this.mCbtiPartType = t.chapter.index
            mPresenter.setType(mCbtiPartType)
        }
    }

    private fun hideEmptyView() {
        tv_msg_board_empty?.visibility = View.GONE
    }
}
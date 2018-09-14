package com.sumian.sd.service.diary

import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import com.sumian.common.base.BaseFragment
import com.sumian.sd.R
import kotlinx.android.synthetic.main.fragment_diary_evaluation_list.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/9/14 9:15
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class DiaryEvaluationListFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {
    private val mHandler = Handler()
    private val mAdapter by lazy {
        DiaryEvaluationListAdapter(activity!!)
    }

    companion object {
        const val TYPE_UNFINISHED = 0
        const val TYPE_FINISHED = 1
        private const val KEY_TYPE = "KEY_TYPE"

        fun newInstance(type: Int): DiaryEvaluationListFragment {
            val fragment = DiaryEvaluationListFragment()
            val bundle = Bundle()
            bundle.putInt(KEY_TYPE, type)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_diary_evaluation_list
    }


    override fun initWidget() {
        super.initWidget()
        refresh.setOnRefreshListener(this)
        recycler.adapter = mAdapter
    }

    override fun onRefresh() {
        mHandler.postDelayed({
            mAdapter.addAll(null)
            refresh.hideRefreshAnim()
        }, 1000)
    }
}
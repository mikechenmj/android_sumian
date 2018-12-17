package com.sumian.sd.diary.fillsleepdiary.fragment

import android.os.Bundle
import android.widget.TextView
import com.sumian.sd.R
import kotlinx.android.synthetic.main.fragment_morning_feeling.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/17 14:38
 * desc   :
 * version: 1.0
 */
class MorningFeelingFragment : BaseFillSleepDiaryFragment() {

    companion object {
        fun newInstance(progress: Int): MorningFeelingFragment {
            val bundle = Bundle()
            bundle.putInt(KEY_PROGRESS, progress)
            val fragment = MorningFeelingFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val mTvs: Array<TextView> by lazy {
        arrayOf(tv_0, tv_1, tv_2, tv_3, tv_4)
    }

    override fun getContentViewLayout(): Int {
        return R.layout.fragment_morning_feeling
    }

    override fun initWidget() {
        super.initWidget()
        for ((index, tv) in mTvs.withIndex()) {
            tv.setOnClickListener { changeFeeling(index) }
        }
    }

    private fun changeFeeling(index: Int) {
        mFillDiaryViewModel.mFeelingLiveData.value = index
        updateSelectTvUI(index)
    }

    private fun updateSelectTvUI(selIndex: Int) {
        for ((index, tv) in mTvs.withIndex()) {
            tv.isSelected = index == selIndex
        }
    }
}
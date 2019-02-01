package com.sumian.sd.buz.diary.fillsleepdiary.fragment

import androidx.lifecycle.ViewModelProviders
import com.sumian.common.base.BaseFragment
import com.sumian.sd.R
import com.sumian.sd.buz.diary.fillsleepdiary.FillDiaryViewModel
import kotlinx.android.synthetic.main.layout_fill_diary_bg.*
import kotlinx.android.synthetic.main.view_fill_diary_container.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/17 14:28
 * desc   :
 * version: 1.0
 */
abstract class BaseFillSleepDiaryFragment : BaseFragment() {
    companion object {
        const val KEY_PROGRESS = "PROGRESS"
    }

    protected val mFillDiaryViewModel: FillDiaryViewModel by lazy {
        ViewModelProviders.of(activity!!).get(FillDiaryViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_fill_diary_bg
    }

    override fun initWidget() {
        super.initWidget()
        iv_fill_diary_next.setOnClickListener { mFillDiaryViewModel.next() }
        iv_fill_diary_pre.setOnClickListener { mFillDiaryViewModel.previous() }
        fill_diary_bg.setContentView(getContentViewLayout())
        fill_diary_bg.setProgress(getProgress())
    }

    abstract fun getContentViewLayout(): Int

    open fun getProgress(): Int {
        return arguments?.getInt(KEY_PROGRESS) ?: 0
    }


}
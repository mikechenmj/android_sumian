package com.sumian.sd.buz.diary.fillsleepdiary.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import com.google.android.material.chip.Chip
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.sd.R
import com.sumian.sd.buz.stat.StatConstants
import kotlinx.android.synthetic.main.fragment_remark.*
import kotlinx.android.synthetic.main.view_fill_diary_container.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/18 09:16
 * desc   :
 * version: 1.0
 */
@SuppressLint("SetTextI18n")
class RemarkFragment : BaseFillSleepDiaryFragment() {
    companion object {
        fun newInstance(progress: Int): RemarkFragment {
            val bundle = Bundle()
            bundle.putInt(KEY_PROGRESS, progress)
            val fragment = RemarkFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getContentViewLayout(): Int {
        return R.layout.fragment_remark
    }


    override fun initWidget() {
        super.initWidget()
        initChipGroup()
        bt_fill_diary_complete.setOnClickListener {
            StatUtil.event(StatConstants.click_sleep_diary_edit_page_commit_btn)
            mFillDiaryViewModel.mRemarkLiveData.value = et_remark.text.toString()
            mFillDiaryViewModel.next()
        }
    }


    @Suppress("DEPRECATION")
    private fun initChipGroup() {
        val remarkArray = resources.getStringArray(R.array.fill_diary_remark_options)
        for (s in remarkArray) {
            val chip = Chip(activity!!)
            chip.setTextColor(ColorCompatUtil.getColor(activity!!, R.color.t2_color))
            chip.chipBackgroundColor = resources.getColorStateList(R.color.b1_color)
            chip.text = s
            chip.setOnClickListener { et_remark.setText(et_remark.text.toString() + chip.text + ",") }
            chip_group.addView(chip)
        }
    }
}
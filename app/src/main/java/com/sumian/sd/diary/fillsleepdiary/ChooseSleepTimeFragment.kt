package com.sumian.sd.diary.fillsleepdiary

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.sumian.common.base.BaseFragment
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import kotlinx.android.synthetic.main.fill_diary_bg.*
import kotlinx.android.synthetic.main.layout_choose_sleep_time.*
import java.lang.IllegalArgumentException

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/12 15:56
 * desc   : 参考需求文档 1.14.0 睡眠日记改版
 * version: 1.0
 */
class ChooseSleepTimeFragment : BaseFragment() {
    private var mType = TYPE_SLEEP_TIME
    private var mTimeIndex = 0
    private val mFillDiaryModel: FillDiaryModel by lazy {
        ViewModelProviders.of(activity!!).get(FillDiaryModel::class.java)
    }

    companion object {
        private const val KEY_TYPE = "ChooseSleepTimeFragment.KEY_TYPE"
        const val TYPE_SLEEP_TIME = 0
        const val TYPE_FALL_ASLEEP_TIME = 1
        const val TYPE_WAKEUP_TIME = 2
        const val TYPE_GET_UP_TIME = 3

        fun newInstance(index: Int): ChooseSleepTimeFragment {
            val fragment = ChooseSleepTimeFragment()
            val bundle = Bundle()
            bundle.putInt(KEY_TYPE, index)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_choose_sleep_time
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mType = bundle.getInt(KEY_TYPE)
        mTimeIndex = getTimeIndexByType()
    }

    override fun initWidget() {
        super.initWidget()
        fill_diary_bg.setProgress(0, FillDiaryConst.TOTAL_PAGE)
        initPicker()
    }

    private fun initPicker() {
        val hour = TimeUtilV2.getHourOfDay(getTime())
        val minute = TimeUtilV2.getMinute(getTime())
        val hours = getHours()
        val minutes = getMinutes(hour)
        picker_hour.refreshByNewDisplayedValues(hours)
        picker_hour.value = getIndexOfArray(hour.toString(), hours)
        picker_minute.refreshByNewDisplayedValues(minutes)
        picker_minute.value = getIndexOfArray(minute.toString(), minutes)

        picker_hour.setOnValueChangedListener { picker, oldVal, newVal ->
            run {

            }
        }
    }

    private fun getHours(): Array<String?> {
        return mFillDiaryModel.mSleepTimeLiveData.value!!.createHoursByIndex(mTimeIndex)
    }

    private fun getMinutes(hour: Int): Array<String?> {
        return mFillDiaryModel.mSleepTimeLiveData.value!!.createMinutesByIndexAndHour(mTimeIndex, hour)
    }

    private fun setTime(time: Long) {
        mFillDiaryModel.setSleepTime(mTimeIndex, time)
    }

    private fun getTime(): Long {
        return mFillDiaryModel.getSleepTime(mTimeIndex)
    }

    private fun getTimeIndexByType(): Int {
        return when (mType) {
            TYPE_SLEEP_TIME -> 0
            TYPE_FALL_ASLEEP_TIME -> 1
            TYPE_WAKEUP_TIME -> 2
            TYPE_GET_UP_TIME -> 3
            else -> throw IllegalArgumentException("illegal type")
        }
    }

    private fun getIndexOfArray(value: String, array: Array<String?>): Int {
        for ((index, v) in array.withIndex()) {
            if (value == v) {
                return index
            }
        }
        return -1
    }

}
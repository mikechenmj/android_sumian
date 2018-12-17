package com.sumian.sd.diary.fillsleepdiary.fragment

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import com.sumian.sd.diary.fillsleepdiary.widget.WheelPickerBottomSheet
import kotlinx.android.synthetic.main.layout_night_wake.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/14 15:18
 * desc   :
 * version: 1.0
 */
class NightWakeOrDaySleepFragment : BaseFillSleepDiaryFragment() {

    private var mType = TYPE_NIGHT_WAKE
    private val mTimes = createTimesArray()
    private val mDurations = createDurationArray()

    companion object {
        const val TYPE_NIGHT_WAKE = 0
        const val TYPE_DAY_SLEEP = 1
        private const val KEY_TYPE = "NightWakeOrDaySleepFragment.KEY_TYPE"

        fun newInstance(progress: Int, type: Int): NightWakeOrDaySleepFragment {
            val fragment = NightWakeOrDaySleepFragment()
            val bundle = Bundle()
            bundle.putInt(KEY_PROGRESS, progress)
            bundle.putInt(KEY_TYPE, type)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mType = bundle.getInt(KEY_TYPE)
    }

    override fun getContentViewLayout(): Int {
        return R.layout.layout_night_wake
    }

    override fun initWidget() {
        super.initWidget()
        tv_left.setCompoundDrawablesRelativeWithIntrinsicBounds(0, getLeftIcon(), 0, 0)
        tv_right.setCompoundDrawablesRelativeWithIntrinsicBounds(0, getRightIcon(), 0, 0)
        tv_left.setOnClickListener {
            setDataByType(0, 0)
            mFillDiaryViewModel.next()
        }
        tv_right.setOnClickListener { showPickerBottomSheet() }
        getLiveDataByType().observe(this, Observer {
            tv_left.isSelected = it?.first == 0
            tv_right.isSelected = it != null && it.first > 0
            tv_left.text = getLeftText()
            tv_right.text = getRightFullText(it)
        })
    }

    private fun createPickerData(): List<Pair<Array<String?>, Int>> {
        val list = ArrayList<Pair<Array<String?>, Int>>()
        list.add(Pair(mTimes, getInitValue1()))
        list.add(Pair(mDurations, getInitValue2()))
        return list
    }

    private fun showPickerBottomSheet() {
        WheelPickerBottomSheet(
                activity!!,
                getString(if (mType == TYPE_NIGHT_WAKE) R.string.night_wake_times_and_duration else R.string.day_sleep_times_and_duration),
                createPickerData(),
                object : WheelPickerBottomSheet.Listener {
                    override fun onConfirmClick(values: List<Int>) {
                        val duration = (values[0] + 1) * 5
                        val times = values[1] + 1
                        if (mType == TYPE_NIGHT_WAKE) {
                            if (duration > mFillDiaryViewModel.getSleepDuration()) {
                                ToastUtils.showShort(R.string.night_wake_time_cant_bigger_than_sleep_time)
                                return
                            }
                        }
                        setDataByType(times, duration)
                    }
                })
                .show()
    }


    private fun getTimesAndDurationString(times: Int, duration: Int): String {
        return "${times}次, ${TimeUtilV2.getHourMinuteStringFromSecondInZh(duration * 60)}"
    }

    private fun getLeftText(): String {
        return getString(
                if (mType == TYPE_NIGHT_WAKE) {
                    R.string.no_night_wake
                } else {
                    R.string.no_day_sleep
                }
        ) + "\n"
    }

    private fun getRightFullText(it: Pair<Int, Int>?): String {
        val sb = StringBuilder()
        sb.append(getString(
                if (mType == TYPE_NIGHT_WAKE) {
                    R.string.have_night_wake
                } else {
                    R.string.have_day_sleep
                }))
        sb.append("\n")
        if (it != null && it.first > 0) {
            sb.append(getTimesAndDurationString(it.first, it.second))
        }
        return sb.toString()
    }

    private fun getLeftIcon(): Int {
        return if (mType == TYPE_NIGHT_WAKE) {
            R.drawable.sel_night_wake_not
        } else {
            R.drawable.sel_day_sleep_not
        }
    }

    private fun getRightIcon(): Int {
        return if (mType == TYPE_NIGHT_WAKE) {
            R.drawable.sel_night_wake
        } else {
            R.drawable.sel_day_sleep
        }
    }

    private fun getInitValue1(): Int {
        val pair = getDataByType()
        return if (pair == null) {
            0
        } else {
            Math.max(0, pair.first - 1)
        }
    }

    private fun getInitValue2(): Int {
        val pair = getDataByType()
        return if (pair == null) {
            0
        } else {
            Math.max(0, (pair.second / 5) - 1)
        }
    }

    private fun getDataByType(): Pair<Int, Int>? {
        return getLiveDataByType().value
    }

    private fun getLiveDataByType(): MutableLiveData<Pair<Int, Int>> {
        return if (mType == TYPE_NIGHT_WAKE) {
            mFillDiaryViewModel.mNightWakeLiveData
        } else {
            mFillDiaryViewModel.mDaySleepLiveData
        }
    }

    private fun setDataByType(times: Int, duration: Int) {
        val pair = Pair(times, duration)
        if (mType == TYPE_NIGHT_WAKE) {
            mFillDiaryViewModel.mNightWakeLiveData.value = pair
        } else {
            mFillDiaryViewModel.mDaySleepLiveData.value = pair
        }
    }

    private fun createTimesArray(): Array<String?> {
        val count = 20
        val array = arrayOfNulls<String>(count)
        for (i in 0 until count) {
            array[i] = "${i + 1}次"
        }
        return array
    }

    private fun createDurationArray(): Array<String?> {
        val count = 72
        val array = arrayOfNulls<String>(count)
        for (i in 0 until count) {
            array[i] = "${(i + 1) * 5}分钟"
        }
        return array
    }
}
package com.sumian.sd.buz.diary.sleeprecord.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import com.sumian.sd.buz.diary.sleeprecord.bean.SleepPill
import com.sumian.sd.buz.diary.sleeprecord.bean.SleepRecord
import com.sumian.sd.buz.diary.sleeprecord.pill.PillsDialog
import com.sumian.sd.buz.setting.remind.SleepDiaryRemindSettingActivity
import com.sumian.sd.buz.setting.remind.bean.Reminder
import com.sumian.sd.common.utils.TimeUtil
import kotlinx.android.synthetic.main.view_sleep_record_view.view.*
import java.util.*

/**
 * <pre>
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/5/31 20:02
 * desc   :
 * version: 1.0
</pre> *
 */
@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class SleepRecordView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {
    private var mSleepRecord: SleepRecord? = null
    private var mTime: Long = 0

    init {
        init(context)
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.view_sleep_record_view, this)
        tv_go_to_set_diary_reminder.setOnClickListener { v -> SleepDiaryRemindSettingActivity.launch(Reminder.TYPE_SLEEP_DIARY) }
        tv_pills.setOnClickListener { showPillsDialogIfNeed() }
    }

    fun setSleepRecord(sleepRecord: SleepRecord?) {
        mSleepRecord = sleepRecord
        val hasRecord = mSleepRecord != null
        ll_sleep_record.visibility = if (hasRecord) View.VISIBLE else View.GONE
        ll_no_sleep_record.visibility = if (hasRecord) View.GONE else View.VISIBLE
        val showRefill = hasRecord && TextUtils.isEmpty(sleepRecord!!.doctorEvaluation)
        tv_refill.visibility = if (showRefill) View.VISIBLE else View.GONE
        if (hasRecord) {
            showSleepRecord(sleepRecord!!)
        }
        val isFillSleepRecordEnable = isFillSleepRecordEnable(mTime)
        btn_for_no_data.isEnabled = isFillSleepRecordEnable
        tv_sleep_record_not_enable_hint.visibility = if (isFillSleepRecordEnable) View.GONE else View.VISIBLE
        tv_refill.setTextColor(ColorCompatUtil.getColor(context, if (isFillSleepRecordEnable) R.color.b3_color else R.color.t2_color))
    }

    @SuppressLint("SetTextI18n")
    private fun showSleepRecord(sleepRecord: SleepRecord) {
//        val answer = sleepRecord.answer
        // 睡眠效率
        progress_view_sleep.setProgress(sleepRecord.sleepEfficiency)
        // 睡眠时长
        tv_sleep_duration.text = TimeUtil.getHourMinuteStringFromSecondInZh(sleepRecord.sleepDuration)
        // 卧床时长
        tv_on_bed_duration.text = TimeUtil.getHourMinuteStringFromSecondInZh(sleepRecord.onBedDuration)
        // 睡眠图
        sleep_record_diagram_view.setData(
                sleepRecord.tryToSleepAt * 1000L, sleepRecord.sleepAt * 1000L,
                sleepRecord.wakeUpAt * 1000L, sleepRecord.getUpAt * 1000L,
                sleepRecord.wakeTimes, sleepRecord.wakeMinutes * 60 * 1000L)
        // 情绪
        tv_sleep_quality.text = getSleepQualityString(sleepRecord.energetic)
        iv_emotion.setImageResource(getSleepQualityIcon(sleepRecord.energetic))
        // 夜醒
        tv_night_wake_up_duration.text = getWakeupOrOtherSleepString("没醒过", sleepRecord.wakeTimes, sleepRecord.wakeMinutes * 60)
        // 小睡
        tv_little_sleep_duration.text = getWakeupOrOtherSleepString("没小睡", sleepRecord.otherSleepTimes, sleepRecord.otherSleepTotalMinutes * 60)
        // 服药
        tv_pills.text = getPillsString(sleepRecord.sleepPills)
        tv_pills.isClickable = sleepRecord.sleepPills != null && sleepRecord.sleepPills.isNotEmpty()
        // 睡眠备注
        vg_sleep_desc.visibility = if (TextUtils.isEmpty(sleepRecord.remark)) View.GONE else View.VISIBLE
        tv_sleep_desc.text = sleepRecord.remark
    }

    private fun getWakeupOrOtherSleepString(emptyString: String, times: Int, duration: Int): String {
        return if (times == 0) {
            emptyString
        } else {
            "${times}次，${TimeUtil.getHourMinuteStringFromSecondInZh(duration)}"
        }
    }

    private fun getSleepQualityString(quality: Int): String {
        val qualityStrings = arrayOf("十分差", "较差", "正常", "较好", "超级棒")
        if (quality < 0 || quality >= qualityStrings.size) {
            IllegalArgumentException("不合法的睡眠质量:$quality").printStackTrace()
            return qualityStrings[0]
        }
        return qualityStrings[quality]
    }

    private fun getSleepQualityIcon(quality: Int): Int {
        val qualityIcons = intArrayOf(R.drawable.record_icon_facial_1, R.drawable.record_icon_facial_2, R.drawable.record_icon_facial_3, R.drawable.record_icon_facial_4, R.drawable.record_icon_facial_5)
        if (quality < 0 || quality >= qualityIcons.size) {
            IllegalArgumentException("不合法的睡眠质量:$quality").printStackTrace()
            return qualityIcons[0]
        }
        return qualityIcons[quality]
    }

    private fun getStringArray(strings: List<String>): String {
        val stringBuilder = StringBuilder()
        val size = strings.size
        for (i in 0 until size) {
            stringBuilder.append(strings[i])
            if (i != size - 1) {
                stringBuilder.append("、")
            }
        }
        return stringBuilder.toString()
    }

    private fun getPillsString(pills: List<SleepPill>?): String {
        if (pills == null || pills.isEmpty()) {
            return resources.getString(R.string.do_not_eat_pills)
        }
        val strList = ArrayList<String>()
        for (pill in pills) {
            strList.add(pill.name)
        }
        return getStringArray(strList)
    }

    private fun showPillsDialogIfNeed() {
        val pills = mSleepRecord!!.sleepPills
        if (pills == null || pills.isEmpty()) {
            return
        }
        PillsDialog.show(context, pills)
    }

    fun setOnClickRefillSleepRecordListener(listener: View.OnClickListener) {
        tv_refill.setOnClickListener(listener)
    }

    fun setOnClickFillSleepRecordBtnListener(listener: View.OnClickListener) {
        btn_for_no_data.setOnClickListener(listener)
        ll_no_sleep_record.setOnClickListener(listener)
    }

    fun setTime(timeInMillis: Long) {
        mTime = timeInMillis
        tv_no_record_date.text = TimeUtil.formatDate("M月d日", timeInMillis)
    }

    private fun isFillSleepRecordEnable(recordTime: Long): Boolean {
        return TimeUtilV2.getDayDistance(System.currentTimeMillis(), recordTime) < 3
    }
}

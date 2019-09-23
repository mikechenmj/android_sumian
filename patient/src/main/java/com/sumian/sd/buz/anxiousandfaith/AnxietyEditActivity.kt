package com.sumian.sd.buz.anxiousandfaith

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.R.layout
import com.sumian.sd.R.string
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyData
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyData.Companion.EXTRA_KEY_ANXIETY
import com.sumian.sd.buz.anxiousandfaith.databinding.ActivityAnxiousEditData
import com.sumian.sd.buz.anxiousandfaith.event.AnxietyChangeEvent
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.databinding.ActivityAnxietyBinding
import com.sumian.sd.widget.sheet.SelectTimeHHmmBottomSheet
import kotlinx.android.synthetic.main.activity_anxiety.*
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 13:52
 * desc   :
 * version: 1.0
 */
class AnxietyEditActivity : WhileTitleNavBgActivity() {
    private var mAnxietyData: AnxietyData? = null

    override fun getLayoutId(): Int {
        return layout.activity_anxiety
    }

    companion object {
        fun launch(anxiety: AnxietyData? = null) {
            val intent = Intent(ActivityUtils.getTopActivity(), AnxietyEditActivity::class.java)
            intent.putExtra(EXTRA_KEY_ANXIETY, anxiety)
            ActivityUtils.startActivity(intent)
        }

        fun getLaunchIntent(): Intent {
            return Intent(ActivityUtils.getTopActivity(), AnxietyEditActivity::class.java)
        }
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mAnxietyData = bundle.getParcelable(EXTRA_KEY_ANXIETY)
    }

    override fun getPageName(): String {
        return StatConstants.page_add_anxiety
    }

    @SuppressLint("SetTextI18n")
    override fun initWidget() {
        super.initWidget()
        setTitle(string.add_anxious)
        var binding = DataBindingUtil.bind<ActivityAnxietyBinding>(findViewById(R.id.activity_anxiety))
        binding?.data = ActivityAnxiousEditData(this, mAnxietyData).apply {
            detailText = mAnxietyData?.anxiety ?: detailText
            solutionText = mAnxietyData?.solution ?: solutionText
        }
    }

    fun showBottomSheet() {
        val bottomSheet = SelectTimeHHmmBottomSheet(this, string.set_remind_time, SelectTimeHHmmBottomSheet.DEFAULT_DAY,
                SelectTimeHHmmBottomSheet.DEFAULT_HOUR, SelectTimeHHmmBottomSheet.DEFAULT_MINUTE,
                object : SelectTimeHHmmBottomSheet.OnTimePickedListener {
                    override fun onTimePicked(hour: Int, minute: Int) {}

                    override fun onTimePicked(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
                        super.onTimePicked(year, month, day, hour, minute)
                        var formatDateContent = getString(string.pattern_yyyy_MM_dd_hh_mm).format(year, month, day, hour, minute)
                        var binding = DataBindingUtil.findBinding<ActivityAnxietyBinding>(findViewById(R.id.activity_anxiety))
                        binding?.data?.remindSettingTypeContent = formatDateContent
                        binding?.data?.remindTimeInMillis = Calendar.getInstance().apply { set(year, month, day, hour, minute) }.timeInMillis
                    }
                })
        bottomSheet.setOnDismissListener {}
        bottomSheet.show()
    }

    fun onSaveAnxietyFail(errMessage: String) {
        ToastUtils.showShort(errMessage)
    }
}
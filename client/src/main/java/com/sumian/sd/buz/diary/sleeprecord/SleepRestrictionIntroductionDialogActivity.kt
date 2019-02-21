package com.sumian.sd.buz.diary.sleeprecord

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseDialogViewModelActivity
import com.sumian.common.base.BaseViewModel
import com.sumian.sd.R
import kotlinx.android.synthetic.main.activity_sleep_restriction_introduction_dialog.*

class SleepRestrictionIntroductionDialogActivity : BaseDialogViewModelActivity<BaseViewModel>() {

    companion object {
        fun start() {
            ActivityUtils.startActivity(ActivityUtils.getTopActivity(), SleepRestrictionIntroductionDialogActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep_restriction_introduction_dialog)
        iv_close.setOnClickListener { finish() }
    }
}

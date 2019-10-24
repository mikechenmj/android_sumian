package com.sumian.sd.buz.anxiousandfaith

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sd.R
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyData
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyData.Companion.EXTRA_KEY_ANXIETY
import com.sumian.sd.buz.anxiousandfaith.bean.MoodDiaryData
import com.sumian.sd.buz.anxiousandfaith.databinding.ActivityAnxiousDetailData
import com.sumian.sd.buz.anxiousandfaith.event.AnxietyChangeEvent
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.databinding.ActivityAnxietyDetailBinding
import kotlinx.android.synthetic.main.activity_anxiety_detail.*
import org.greenrobot.eventbus.Subscribe

class AnxietyDetailActivity : WhileTitleNavBgActivity() {

    private var mAnxietyData: AnxietyData? = null

    companion object {
        fun launch(anxiety: AnxietyData? = null) {
            val intent = Intent(ActivityUtils.getTopActivity(), AnxietyDetailActivity::class.java)
            intent.putExtra(EXTRA_KEY_ANXIETY, anxiety)
            ActivityUtils.startActivity(intent)
        }

        fun getLaunchIntent(): Intent {
            return Intent(ActivityUtils.getTopActivity(), AnxietyDetailActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_anxiety_detail
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mAnxietyData = bundle.getParcelable(EXTRA_KEY_ANXIETY)

    }

    override fun initWidget() {
        super.initWidget()
        setTitle(getString(R.string.anxiety_detail_title_text))
        mTitleBar.setMenuTextDpSize(15)
        mTitleBar.setMenuText(getString(R.string.anxiety_mood_diary_detail_edit_menu_text))
        mTitleBar.setOnMenuClickListener { AnxietyEditActivity.launch(mAnxietyData) }
        refreshDataBinding()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBusUtil.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusUtil.unregister(this)
    }

    @Subscribe
    fun onAnxietyChangeEvent(event: AnxietyChangeEvent) {
        mAnxietyData = event.anxiety
        refreshDataBinding()
    }

    private fun refreshDataBinding() {
        var bind = DataBindingUtil.bind<ActivityAnxietyDetailBinding>(activity_anxiety_detail)
        bind?.data = ActivityAnxiousDetailData(mAnxietyData?.anxiety
                ?: "", "问题处理类型", mAnxietyData?.solution ?: "", "2019.07.01 09:00")
    }

}
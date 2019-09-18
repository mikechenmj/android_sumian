package com.sumian.sd.buz.anxiousandfaith

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sd.R
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyData
import com.sumian.sd.buz.anxiousandfaith.bean.AnxietyData.Companion.EXTRA_KEY_ANXIETY
import com.sumian.sd.buz.anxiousandfaith.databinding.ActivityAnxiousDetailData
import com.sumian.sd.databinding.ActivityAnxietyDetailBinding
import kotlinx.android.synthetic.main.activity_anxiety_detail.*

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
        mTitleBar.setMenuText(getString(R.string.anxiety_faith_detail_edit_menu_text))
        mTitleBar.setOnMenuClickListener { AnxietyActivity.launch(mAnxietyData) }
        var bind = DataBindingUtil.bind<ActivityAnxietyDetailBinding>(activity_anxiety_detail)
        bind?.data = ActivityAnxiousDetailData(mAnxietyData?.anxiety
                ?: "", "问题处理类型", mAnxietyData?.solution ?: "", "2019.07.01 09:00")

    }

}
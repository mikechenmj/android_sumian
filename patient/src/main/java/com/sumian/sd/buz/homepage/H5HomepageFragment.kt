package com.sumian.sd.buz.homepage

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.*
import com.sumian.common.base.BaseViewModel
import com.sumian.common.base.BaseViewModelFragment
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.JsonUtil
import com.sumian.device.callback.ConnectDeviceCallback
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.achievement.MyAchievementShareActivity
import com.sumian.sd.buz.account.achievement.bean.LastAchievementData
import com.sumian.sd.buz.account.achievement.contract.LastAchievementContract
import com.sumian.sd.buz.account.achievement.presenter.LastAchievementPresenter
import com.sumian.sd.buz.account.bean.Token
import com.sumian.sd.buz.account.userProfile.UserInfoActivity
import com.sumian.sd.buz.anxiousandfaith.AnxiousAndMoodDiaryActivity
import com.sumian.sd.buz.cbti.activity.CBTIIntroductionActivity
import com.sumian.sd.buz.cbti.activity.CbtiFinalReportDialogActivity
import com.sumian.sd.buz.cbti.event.CBTIProgressChangeEvent
import com.sumian.sd.buz.cbti.event.CBTIServiceBoughtEvent
import com.sumian.sd.buz.device.scan.ScanDeviceActivity
import com.sumian.sd.buz.devicemanager.BlueDevice
import com.sumian.sd.buz.homepage.banner.BannerContract
import com.sumian.sd.buz.homepage.banner.BannerPresenter
import com.sumian.sd.buz.homepage.bean.GetCbtiChaptersResponse
import com.sumian.sd.buz.homepage.bean.SentencePoolText
import com.sumian.sd.buz.homepage.bean.SleepPrescriptionStatus
import com.sumian.sd.buz.relaxation.RelaxationListActivity
import com.sumian.sd.buz.scale.ScaleListActivity
import com.sumian.sd.buz.sleepguide.SleepGuideActivity
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.h5.H5Uri
import com.sumian.sd.common.h5.SimpleWebActivity
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.main.MainActivity
import com.sumian.sd.main.OnEnterListener
import com.sumian.sd.widget.banner.Banner
import com.sumian.sd.widget.banner.BannerViewPager
import kotlinx.android.synthetic.main.fragment_homepage.*
import kotlinx.android.synthetic.main.layout_homepage_fragment_grid_items.*
import org.greenrobot.eventbus.Subscribe

@Suppress("unused")
class H5HomepageFragment : BaseViewModelFragment<BaseViewModel>() {

    override fun getLayoutId(): Int {
        return R.layout.h5_fragment_homepage
    }
}

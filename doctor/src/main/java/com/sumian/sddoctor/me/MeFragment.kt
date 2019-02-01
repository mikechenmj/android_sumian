@file:Suppress("DEPRECATION")

package com.sumian.sddoctor.me

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.helper.ToastHelper
import com.sumian.sddoctor.R
import com.sumian.sddoctor.account.activity.SettingsActivity
import com.sumian.sddoctor.account.activity.UserInfoActivity
import com.sumian.sddoctor.account.contract.LogoutContract
import com.sumian.sddoctor.account.presenter.LogoutPresenter
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.BaseFragment
import com.sumian.sddoctor.login.login.LoginActivity
import com.sumian.sddoctor.login.login.bean.DoctorInfo
import com.sumian.sddoctor.me.authentication.AuthenticationActivity
import com.sumian.sddoctor.me.myservice.MyServiceListActivity
import com.sumian.sddoctor.me.mywallet.MyWalletActivity
import com.sumian.sddoctor.util.ImageLoader
import kotlinx.android.synthetic.main.fragment_me.*
import kotlinx.android.synthetic.main.lay_visitor_tips.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/18 17:04
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class MeFragment : BaseFragment(), LogoutContract.View {

    override fun onLogoutSuccess() {
        showCenterToast(getString(R.string.logout_success))
        ActivityUtils.finishAllActivities()
        ActivityUtils.startActivity(LoginActivity::class.java)
    }

    override fun onLogoutFailed(error: String?) {
        error?.let {
            showCenterToast(it)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_me
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        iv_avatar.setOnClickListener { ActivityUtils.startActivity(UserInfoActivity::class.java) }
        vg_warning.setOnClickListener { onWarningBannerClick() }
        sdv_user_info.setOnClickListener { ActivityUtils.startActivity(UserInfoActivity::class.java) }
        sdv_setting.setOnClickListener { ActivityUtils.startActivity(SettingsActivity::class.java) }
        sdv_my_service.setOnClickListener { ActivityUtils.startActivity(MyServiceListActivity::class.java) }
        sdv_my_wallet.setOnClickListener { ActivityUtils.startActivity(MyWalletActivity::class.java) }
    }

    override fun initData() {
        super.initData()
        AppManager.getAccountViewModel().getDoctorInfo().observe(this, Observer { invalidDoctorInfo(it) })
    }

    @SuppressLint("SetTextI18n")
    private fun invalidDoctorInfo(doctorInfo: DoctorInfo?) {
        val visitorAccount = doctorInfo?.isVisitorAccount() == true
        vg_warning.visibility = if (visitorAccount || doctorInfo?.review_status == 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
        tv_warning.text = getString(if (visitorAccount) R.string.click_logout_visitor else R.string.complete_authentication_to_get_more_service)
        doctorInfo?.let {
            ImageLoader.load(context!!, R.mipmap.ic_info_avatar_doctor_s, doctorInfo.avatar, iv_avatar)
            tv_name.text = doctorInfo.name
            tv_desc.text = doctorInfo.hospital
        }
    }

    private fun onWarningBannerClick() {
        val accountViewModel = AppManager.getAccountViewModel()
        if (accountViewModel.isVisitorAccount()) {
            LogoutPresenter.init(this).doLogout()
        } else if (accountViewModel.getAuthenticateStatus() == 0) {
            ActivityUtils.startActivity(activity!!, AuthenticationActivity::class.java)
        }
    }

    private fun showCenterToast(text: String) {
        ToastHelper.show(context, text, Gravity.CLIP_HORIZONTAL)
    }
}
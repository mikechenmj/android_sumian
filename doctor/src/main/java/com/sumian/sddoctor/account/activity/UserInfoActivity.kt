package com.sumian.sddoctor.account.activity

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.image.ImageLoader
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.common.widget.dialog.SumianDialog
import com.sumian.sddoctor.R
import com.sumian.sddoctor.account.contract.UserAvatarContract
import com.sumian.sddoctor.account.presenter.UserAvatarPresenter
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.login.login.bean.DoctorInfo
import com.sumian.sddoctor.me.authentication.AuthenticationActivity
import com.sumian.sddoctor.widget.divider.SettingDividerView
import com.sumian.sddoctor.widget.sheet.SelectPictureBottomSheet
import kotlinx.android.synthetic.main.activity_main_user_info.*


class UserInfoActivity : SddBaseActivity(), UserAvatarContract.View {

    private var mPresenter: UserAvatarPresenter? = null
    private var mDoctorInfo: DoctorInfo? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_main_user_info
    }

    override fun getPageName(): String {
        return StatConstants.page_profile_info
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        mPresenter = UserAvatarPresenter.init(this@UserInfoActivity)
        AppManager.getAccountViewModel().getDoctorInfo().observe(this, Observer<DoctorInfo> { this.updateDoctorInfo(it) })
        lay_avatar.setOnClickListener { SelectPictureBottomSheet.show(supportFragmentManager) { filePath -> mPresenter!!.uploadAvatar(filePath) } }
        dv_authentication!!.setOnClickListener { authenticate() }
        vg_personal_intro.setOnClickListener { SetPersonalIntroductionActivity.startForResult(this, mDoctorInfo?.introduction, RQ_CODE_SET_INTRO) }
    }

    private fun authenticate() {
        if (mDoctorInfo!!.getAuthenticationState() == DoctorInfo.AUTHENTICATION_STATE_NOT_AUTHENTICATED) {
            ActivityUtils.startActivity(this, AuthenticationActivity::class.java)
        } else if (mDoctorInfo!!.getAuthenticationState() == DoctorInfo.AUTHENTICATION_STATE_IS_AUTHENTICATING) {
            SumianDialog(this)
                    .setTitleText(R.string.identity_authentication)
                    .setMessageText(R.string.your_authentication_is_goning)
                    .setLeftBtn(R.string.confirm, null)
                    .show()
        }
    }

    private fun updateDoctorInfo(doctorInfo: DoctorInfo?) {
        mDoctorInfo = doctorInfo
        if (doctorInfo != null) {
            ImageLoader.loadImage(doctorInfo.avatar, iv_avatar!!, R.mipmap.ic_info_avatar_doctor_s, R.mipmap.ic_info_avatar_doctor_s)
            dv_name!!.setContentText(doctorInfo.name)
            sedItemContentText(doctorInfo.getAuthenticationString(), "未认证", dv_authentication!!)
            sedItemContentText(doctorInfo.department, "未选择", dv_department!!)
            sedItemContentText(doctorInfo.title, "未选择", dv_job_title!!)
            sedItemContentText(doctorInfo.hospital, "未填写", dv_hospital_name!!)
            sedItemContentText(doctorInfo.invitation_code, "未设置", dv_invite_code!!)

            // update doctor authentication info visibility
            val authenticationState = doctorInfo.getAuthenticationState()
            val showDoctorInfo = authenticationState == DoctorInfo.AUTHENTICATION_STATE_AUTHENTICATED
            val doctorInfoVisibility = if (showDoctorInfo) View.VISIBLE else View.GONE
            dv_authentication!!.visibility = if (!showDoctorInfo) View.VISIBLE else View.GONE
            dv_name!!.visibility = doctorInfoVisibility
            dv_department!!.visibility = doctorInfoVisibility
            dv_job_title!!.visibility = doctorInfoVisibility
            dv_hospital_name!!.visibility = doctorInfoVisibility
            var authenticationTvColor = R.color.t1_color
            if (authenticationState == DoctorInfo.AUTHENTICATION_STATE_NOT_AUTHENTICATED) {
                authenticationTvColor = R.color.t4_color
            } else if (authenticationState == DoctorInfo.AUTHENTICATION_STATE_IS_AUTHENTICATING) {
                authenticationTvColor = R.color.b3_color
            }
            dv_authentication!!.setTvContentColor(ColorCompatUtil.getColor(this, authenticationTvColor))
        }
    }

    override fun onDestroy() {
        if (mPresenter != null) {
            mPresenter!!.onCleared()
        }
        super.onDestroy()
    }

    private fun sedItemContentText(content: String, defaultContent: String, sdv: SettingDividerView) {
        sdv.setContentText(if (TextUtils.isEmpty(content)) defaultContent else content)
    }

    override fun onUploadAvatarSuccess(userInfo: DoctorInfo) {
        updateDoctorInfo(userInfo)
    }

    override fun onUploadAvatarFailed(error: String) {
        dismissLoading()
        ToastUtils.showShort(error)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RQ_CODE_SET_INTRO) {
            if (resultCode == Activity.RESULT_OK) {
                updateDoctorInfo(SetPersonalIntroductionActivity.getResultFromIntent(data))
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private const val RQ_CODE_SET_INTRO = 1000
    }
}

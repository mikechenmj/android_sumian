package com.sumian.sddoctor.booking

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sumian.common.base.BaseActivity
import com.sumian.sddoctor.BuildConfig
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.constants.H5Uri
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.login.login.bean.DoctorInfo
import com.sumian.sddoctor.util.ImageLoader
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.activity_doctor_qr_code.*


class DoctorQrCodeActivity : BaseActivity() {
    private var mDoctorInfo: DoctorInfo? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_doctor_qr_code
    }

    override fun getPageName(): String {
        return StatConstants.page_my_qrcode
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { finish() }
        AppManager.getAccountViewModel().getDoctorInfo().observe(this, Observer<DoctorInfo> { t ->
            val qrCodeUrl = t?.qrCodeRaw
            ImageLoader.load(this, qrCodeUrl ?: return@Observer, iv_qr)
            tv_add_doctor_hint.text = getString(R.string.add_xx_doctor, t.name)
            mDoctorInfo = t
        })
        tv_share.setOnClickListener { showShareBottomSheet() }
    }

    @SuppressLint("InflateParams")
    fun showShareBottomSheet() {
        val available = isWeixinAvailable(this)
        if (!available) {
            ToastUtils.showShort(getString(R.string.wechat_not_install))
            return
        }
        val bottomSheetDialog = BottomSheetDialog(this)
        val contentView = LayoutInflater.from(this).inflate(R.layout.layout_share_bottom_sheet, null, false)
        contentView.findViewById<View>(R.id.tv_wechat_friend).setOnClickListener {
            share(SHARE_MEDIA.WEIXIN)
            bottomSheetDialog.dismiss()
        }
        contentView.findViewById<View>(R.id.tv_wechat_circle).setOnClickListener {
            share(SHARE_MEDIA.WEIXIN_CIRCLE)
            bottomSheetDialog.dismiss()
        }
        contentView.findViewById<View>(R.id.tv_cancel).setOnClickListener { bottomSheetDialog.dismiss() }
        bottomSheetDialog.setContentView(contentView)
        bottomSheetDialog.show()
    }

    private fun share(shareMedia: SHARE_MEDIA) {
        val url = (BuildConfig.BASE_H5_URL.replace("sdd", "sd") + H5Uri.DOCTOR_SHARE).replace("{id}", mDoctorInfo?.id.toString())
        val title = getString(R.string.share_doctor_qr_title, mDoctorInfo?.name)
        val description = getString(R.string.share_doctor_qr_desc)
        AppManager.getOpenEngine().shareUrl(this, url, title, description, mDoctorInfo?.avatar, shareMedia)
    }

    fun isWeixinAvailable(context: Context): Boolean {
        val packageManager = context.packageManager
        val packageInfoList = packageManager.getInstalledPackages(0)
        packageInfoList.forEach {
            if (it.packageName.equals("com.tencent.mm")) {
                return true
            }
        }
        return false
    }
}
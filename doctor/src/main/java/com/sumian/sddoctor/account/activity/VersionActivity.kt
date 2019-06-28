package com.sumian.sddoctor.account.activity

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.sddoctor.R
import com.sumian.sddoctor.account.bean.Version
import com.sumian.sddoctor.account.contract.VersionContract
import com.sumian.sddoctor.account.presenter.VersionPresenter
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.util.UiUtils
import kotlinx.android.synthetic.main.activity_main_version.*
import java.util.*

/**
 * <pre>
 *     @author : sm
 *
 *     e-mail : yaoqi.y@sumian.com
 *     time   : 2018/6/29 14:15
 *
 *     version: 1.0
 *
 *     desc   :
 *
 * </pre>
 */
class VersionActivity : BaseViewModelActivity<VersionPresenter>(), VersionContract.View, View.OnClickListener {

    private var mIsHaveUpgrade = false

    override fun getLayoutId(): Int {
        return R.layout.activity_main_version
    }

    override fun getPageName(): String {
        return StatConstants.page_profile_version
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        mViewModel = VersionPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { finish() }
        sdv_go_market.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        super.initData()
        tv_current_app_version.text = formatVersion(getString(R.string.current_version), UiUtils.getPackageInfo(this).versionName)
        tv_new_app_version.text = formatVersion(getString(R.string.new_version), UiUtils.getPackageInfo(this).versionName)
        onHaveUpgrade(false, false, false, "")
        this.mViewModel?.getVersion()
    }

    @SuppressLint("SetTextI18n")
    override fun onGetVersionSuccess(version: Version) {
        tv_current_app_version.text = formatVersion(getString(R.string.current_version), UiUtils.getPackageInfo(this).versionName)
        tv_new_app_version.text = formatVersion(getString(R.string.new_version), version.version!!)
    }

    override fun onGetVersionFailed(error: String) {
        ToastUtils.showShort(error)
    }

    override fun onHaveUpgrade(isHaveUpgrade: Boolean, isHaveForce: Boolean, isShowDialog: Boolean, versionMsg: String?) {
        mIsHaveUpgrade = isHaveUpgrade
        sdv_go_market.visibility = View.VISIBLE//if (isHaveUpgrade) View.VISIBLE else View.GONE
        sdv_go_market.showRedDot(isHaveUpgrade)
    }

    override fun onClick(v: View?) {
        if (mIsHaveUpgrade) {
            UiUtils.openAppInMarket(this)
        } else {
            ToastUtils.showShort(getString(R.string.this_is_last_version))
        }
    }

    private fun formatVersion(versionLabel: String, version: String): String {
        return String.format(Locale.getDefault(), "%s%s%s", versionLabel, " ", version)
    }

    override fun showLoading() {
        super<BaseViewModelActivity>.showLoading()
    }

    override fun dismissLoading() {
        super<BaseViewModelActivity>.dismissLoading()
    }
}
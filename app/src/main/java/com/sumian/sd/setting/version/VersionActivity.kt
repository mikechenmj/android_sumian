package com.sumian.sd.setting.version

import android.annotation.SuppressLint
import android.view.View
import com.sumian.sd.setting.version.bean.Version
import com.sumian.sd.R
import com.sumian.sd.base.SdBaseActivity
import com.sumian.sd.setting.version.contract.VersionContract
import com.sumian.sd.setting.version.presenter.VersionPresenter
import com.sumian.sd.utils.UiUtils
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
class VersionActivity : SdBaseActivity<VersionContract.Presenter>(), VersionContract.View, View.OnClickListener {

    private var mIsHaveUpgrade = false

    override fun getLayoutId(): Int {
        return R.layout.activity_main_version
    }

    override fun initPresenter() {
        super.initPresenter()
        this.mPresenter = VersionPresenter.init(this)
    }

    override fun initWidget(root:View) {
        super.initWidget(root)
        title_bar.setOnBackClickListener { finish() }
        sdv_go_market.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        super.initData()
        tv_current_app_version.text = formatVersion(getString(R.string.current_version), UiUtils.getPackageInfo(this).versionName)
        tv_new_app_version.text = formatVersion(getString(R.string.new_version), UiUtils.getPackageInfo(this).versionName)
        onHaveUpgrade(false, false)
        this.mPresenter?.getVersion()
    }

    @SuppressLint("SetTextI18n")
    override fun onGetVersionSuccess(version: Version) {
        tv_current_app_version.text = formatVersion(getString(R.string.current_version), UiUtils.getPackageInfo(this).versionName)
        tv_new_app_version.text = formatVersion(getString(R.string.new_version), version.version!!)
    }

    override fun onGetVersionFailed(error: String) {
        showCenterToast(error)
    }

    override fun onHaveUpgrade(isHaveUpgrade: Boolean, isHaveForce: Boolean) {
        mIsHaveUpgrade = isHaveUpgrade
        sdv_go_market.visibility = View.VISIBLE//if (isHaveUpgrade) View.VISIBLE else View.GONE
    }

    override fun onClick(v: View?) {
        if (mIsHaveUpgrade) {
            UiUtils.openAppInMarket(this)
        } else {
            showCenterToast(getString(R.string.this_is_last_version))
        }
    }

    private fun formatVersion(versionLabel: String, version: String): String {
        return String.format(Locale.getDefault(), "%s%s%s", versionLabel, " ", version)
    }
}
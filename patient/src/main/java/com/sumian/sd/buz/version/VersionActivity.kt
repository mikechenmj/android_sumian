package com.sumian.sd.buz.version

import android.annotation.SuppressLint
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.sd.R
import com.sumian.sd.buz.version.bean.Version
import com.sumian.sd.buz.version.contract.VersionContract
import com.sumian.sd.buz.version.presenter.VersionPresenter
import com.sumian.sd.common.utils.UiUtils
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


    override fun initWidget() {
        super.initWidget()
        mViewModel = VersionPresenter.init(this)
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
}
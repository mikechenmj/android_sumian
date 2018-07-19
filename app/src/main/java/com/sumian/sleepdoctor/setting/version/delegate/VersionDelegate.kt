package com.sumian.sleepdoctor.setting.version.delegate

import android.app.Activity
import android.content.DialogInterface
import android.view.KeyEvent
import android.view.View
import com.sumian.sleepdoctor.setting.version.bean.Version
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.app.App
import com.sumian.sleepdoctor.setting.version.contract.VersionContract
import com.sumian.sleepdoctor.setting.version.presenter.VersionPresenter
import com.sumian.sleepdoctor.utils.UiUtils
import com.sumian.sleepdoctor.widget.dialog.SumianAlertDialog

/**
 * <pre>
 *     @author : sm

 *     e-mail : yaoqi.y@sumian.com
 *     time: 2018/6/29 16:36
 *
 *     version: 1.0
 *
 *     desc:
 *
 * </pre>
 */
open class VersionDelegate private constructor() : VersionContract.View, View.OnClickListener, DialogInterface.OnKeyListener {

    override fun onClick(v: View?) {
        UiUtils.openAppInMarket(App.getAppContext())
    }

    private lateinit var mVersion: Version

    private lateinit var mActivity: Activity

    override fun onGetVersionSuccess(version: Version) {
        this.mVersion = version
    }

    override fun onGetVersionFailed(error: String) {
    }

    override fun onHaveUpgrade(isHaveUpgrade: Boolean, isHaveForce: Boolean) {
        if (isHaveForce) {
            SumianAlertDialog(mActivity)
                    .setTitle(R.string.version_upgrade)
                    .setMessage(R.string.force_upgrade_version)
                    .setRightBtn(R.string.sure, this)
                    .setCancelable()
                    .setOnKeyListener(this)
                    .show()
        } else {
            if (isHaveUpgrade && isHaveForce) {
                SumianAlertDialog(mActivity)
                        .setTitle(R.string.version_upgrade)
                        .setMessage(R.string.have_a_new_version)
                        .setRightBtn(R.string.sure, this)
                        .setCancelable()
                        .setOnKeyListener(this)
                        .show()
            }
        }
    }

    private val mPresenter: VersionContract.Presenter by lazy {
        VersionPresenter.init(this)
    }

    companion object {

        fun init(): VersionDelegate {
            return VersionDelegate()
        }
    }


    fun checkVersion(activity: Activity) {
        this.mActivity = activity
        this.mPresenter.getVersion()
    }

    override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
            dialog.cancel()
            mActivity.finishAffinity()
            return true
        }
        return false
    }


}
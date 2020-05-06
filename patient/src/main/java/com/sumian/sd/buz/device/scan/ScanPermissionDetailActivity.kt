package com.sumian.sd.buz.device.scan

import android.app.Activity
import com.sumian.common.base.BaseActivity
import com.sumian.sd.R
import kotlinx.android.synthetic.main.scan_permission_detail_activity_layout.*

class ScanPermissionDetailActivity: BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.scan_permission_detail_activity_layout
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(getString(R.string.scan_permission_detail_title))
        bt_ok.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
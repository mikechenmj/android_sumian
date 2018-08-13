package com.sumian.sleepdoctor.service.tel.activity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseBackPresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.sleepdoctor.R

/**
 * Created by sm
 *
 * on 2018/8/13
 *
 * desc:电话预约服务
 *
 */
class TelServiceActivity : BaseBackPresenterActivity<IPresenter>() {

    companion object {

        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, TelServiceActivity::class.java))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        super.onCreate(savedInstanceState)
    }

    override fun getChildContentId(): Int {
        return R.layout.activity_main_tel_service
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle("预约详情")
    }


}
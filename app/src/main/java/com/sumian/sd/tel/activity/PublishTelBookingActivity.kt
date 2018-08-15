package com.sumian.sd.tel.activity

import android.content.Intent
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseBackPresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.R
import kotlinx.android.synthetic.main.activity_main_publish_tel_booking.*

/**
 * Created by sm
 *
 * on 2018/8/13
 *
 * desc:电话预约服务
 *
 */
class PublishTelBookingActivity : BaseBackPresenterActivity<IPresenter>(), View.OnClickListener {


    companion object {

        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, PublishTelBookingActivity::class.java))
            }
        }

    }

    override fun getChildContentId(): Int {
        return R.layout.activity_main_publish_tel_booking
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.tel_ask_detail)
        sdv_make_date.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sdv_make_date -> {

            }
            R.id.bt_submit -> {

            }
        }

    }


}
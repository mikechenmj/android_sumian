package com.sumian.sd.buz.doctormessage

import android.content.Intent
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.base.BaseViewModel
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.sd.R

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/6 18:19
 * desc   :
 * version: 1.0
 */
class DoctorMessageListActivity : BaseViewModelActivity<BaseViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_doctor_message_list
    }

    companion object {
        fun launch() {
            ActivityUtils.startActivity(Intent(ActivityUtils.getTopActivity(), DoctorMessageListActivity::class.java))
        }
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.doctor_message)
    }

    class MessageAdapter : BaseQuickAdapter<Any, BaseViewHolder>(R.layout.list_item_doctor_message) {
        override fun convert(helper: BaseViewHolder, item: Any?) {
            helper.getView<View>(R.id.iv_message_dot).visibility = View.VISIBLE
        }
    }
}
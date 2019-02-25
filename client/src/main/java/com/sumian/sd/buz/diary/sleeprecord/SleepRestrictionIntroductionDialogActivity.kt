package com.sumian.sd.buz.diary.sleeprecord

import android.graphics.drawable.Drawable
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.JsonObject
import com.sumian.common.base.BaseDialogViewModelActivity
import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.activity_sleep_restriction_introduction_dialog.*

class SleepRestrictionIntroductionDialogActivity : BaseDialogViewModelActivity<BaseViewModel>() {

    companion object {
        fun start() {
            ActivityUtils.startActivity(ActivityUtils.getTopActivity(), SleepRestrictionIntroductionDialogActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep_restriction_introduction_dialog)
        iv_close.setOnClickListener { finish() }

        val call = AppManager.getSdHttpService().getConfigs()
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<List<JsonObject>>() {
            override fun onSuccess(response: List<JsonObject>?) {
                val url = response!!.filter { it.get("name").asString == "sleep_restriction_introduction" }[0].get("value").asString
                Glide.with(this@SleepRestrictionIntroductionDialogActivity).load(url).into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        iv_sleep_restriction.setImageDrawable(resource)
                    }
                })
            }

            override fun onFailure(errorResponse: ErrorResponse) {
            }
        })
    }
}

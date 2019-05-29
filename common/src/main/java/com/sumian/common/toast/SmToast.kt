package com.sumian.common.toast

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.sumian.common.R

@Suppress("DEPRECATION")
/**
 * Created by sm
 *
 * on 2018/8/8
 *
 * desc:
 *
 */
class SmToast constructor(context: Context) : androidx.asynclayoutinflater.view.AsyncLayoutInflater.OnInflateFinishedListener, View.OnClickListener {


    private val mMainHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    companion object {


        fun show() {

        }


    }

    private val mContext: Context = context

    private val mParams: WindowManager.LayoutParams by lazy {
        WindowManager.LayoutParams()
    }

    private val mWm: WindowManager by lazy {
        val systemService: WindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        systemService
    }

    init {

        initParams()
        initLayout()
    }

    private fun initParams() {

        val params = mParams
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.format = PixelFormat.TRANSLUCENT
        val toastAnimId = try {
            mContext.resources.getIdentifier("Animation.Toast", "style", "com.android.internal")
        } catch (e: Exception) {
            e.printStackTrace()
            R.style.sm_toast_animation
        }

        params.windowAnimations = toastAnimId
        params.type = WindowManager.LayoutParams.TYPE_TOAST
        params.title = "Toast"
        params.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        params.gravity = Gravity.BOTTOM        //对其方式
        params.y = 45

    }

    @SuppressLint("InflateParams")
    private fun initLayout() {
        androidx.asynclayoutinflater.view.AsyncLayoutInflater(mContext).inflate(R.layout.layout_sm_toast, null, this)
    }

    override fun onInflateFinished(view: View, resid: Int, parent: ViewGroup?) {
        view.setOnClickListener(this)
        mWm.addView(view, mParams)
    }

    override fun onClick(v: View) {
        Log.e("TAG", "onClick")
    }

}
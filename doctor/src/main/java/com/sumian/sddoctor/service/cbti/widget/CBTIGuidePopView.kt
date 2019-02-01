package com.sumian.sddoctor.service.cbti.widget

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.DimenRes
import androidx.core.content.edit
import androidx.core.widget.PopupWindowCompat
import com.sumian.common.utils.SpUtil
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.App

/**
 * Created by sm
 *
 * on 2018/12/25
 *
 * desc:
 *
 */
@Suppress("DEPRECATION")
@SuppressLint("InflateParams")
class CBTIGuidePopView : PopupWindow.OnDismissListener {

    companion object {
        private const val IS_SHOW_POP_GUIDE_FILE = "com.sumian.sdd.is.show.pop.guide.file"
        private const val IS_SHOW_POP = "com.sumian.sdd.is.show.pop"

        @JvmStatic
        fun create(): CBTIGuidePopView {
            return CBTIGuidePopView()
        }
    }

    private var mIsClickClose = false

    private val mPopMenu: PopupWindow  by lazy {
        val popMenu = PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val popView = LayoutInflater.from(App.getAppContext()).inflate(R.layout.lay_pop_cbti_banner, null, false)
        popView.setOnClickListener {
            if (mPopMenu.isShowing) {
                mIsClickClose = true
                mPopMenu.dismiss()
            }
        }
        popMenu.contentView = popView
        popMenu.isOutsideTouchable = false
        popMenu.isTouchable = true
        popMenu.isFocusable = true
        popMenu.setIgnoreCheekPress()
        popMenu.setOnDismissListener(this)
        popMenu.setBackgroundDrawable(ColorDrawable(App.getAppContext().resources.getColor(android.R.color.transparent)))
        return@lazy popMenu
    }

    fun showPop(anchor: View) {
        val sp = SpUtil.initSp(App.getAppContext(), IS_SHOW_POP_GUIDE_FILE)
        val isPop = sp.getBoolean(IS_SHOW_POP, false)
        if (isPop) return
        Looper.myQueue().addIdleHandler {
            if (!mPopMenu.isShowing) {
                PopupWindowCompat.showAsDropDown(mPopMenu, anchor, (anchor.width / 2) - dp2px(R.dimen.space_174) / 2, dp2px(R.dimen.space_5), Gravity.BOTTOM)
            }
            false
        }
    }

    fun dismiss() {
        mIsClickClose = false
        if (mPopMenu.isShowing) {
            mPopMenu.dismiss()
        }
    }

    override fun onDismiss() {
        if (!mIsClickClose) return
        val sharedPreferences = SpUtil.initSp(App.getAppContext(), IS_SHOW_POP_GUIDE_FILE)
        sharedPreferences.edit {
            putBoolean(IS_SHOW_POP, true)
        }
    }

    private fun dp2px(@DimenRes spaceId: Int) = App.getAppContext().resources.getDimensionPixelOffset(spaceId)

}
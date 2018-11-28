@file:Suppress("DEPRECATION")

package com.sumian.hw.widget.refresh

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window

import com.sumian.sd.R

/**
 * Created by jzz
 * on 2017/11/3.
 *
 *
 * desc:
 */

class ActionLoadingDialog : androidx.fragment.app.DialogFragment() {

    private var mIsShowing: Boolean = false // 判断当前dialog是否正在显示，如果正在显示，则不重复调用show()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(androidx.fragment.app.DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_DialogWhenLarge_NoActionBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.hw_lay_action_loading, container, false)
    }


    /**
     * 注意：这里不能用isAttach()代替mIsShowing，因为调用完super.show()之后，迅速调用isAttach()拿到的数据是还是false
     *
     * @param fragmentManager fragmentManager
     * @return ActionLoadingDialog.this
     */
    fun show(fragmentManager: androidx.fragment.app.FragmentManager): ActionLoadingDialog {
        if (mIsShowing) {
            return this
        }
        show(fragmentManager, this.javaClass.simpleName)
        mIsShowing = true
        return this
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        mIsShowing = false
    }
}

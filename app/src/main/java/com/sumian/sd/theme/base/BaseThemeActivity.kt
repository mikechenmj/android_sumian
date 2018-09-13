package com.sumian.sd.theme.base

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sumian.common.base.BaseActivity
import com.sumian.sd.R
import com.sumian.sd.theme.ITheme
import com.sumian.sd.theme.config.ThemeConfig


/**
 * Created by sm
 *
 * on 2018/9/13
 *
 * desc:
 *
 */
abstract class BaseThemeActivity : BaseActivity(), ITheme {

    companion object {

        private val TAG = BaseThemeActivity::class.java.simpleName

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val themeResId: Int = if (ThemeConfig.isDayTheme()) R.style.AppBaseTheme else 0
        setTheme(themeResId)
        super.onCreate(savedInstanceState)
    }


    override fun getView(): View {
        return findViewById(android.R.id.content)
    }

    override fun setTheme(theme: Resources.Theme) {
        val view = getView()
        // notifyTheme(view, theme)
        setContentView(view)
    }

    override fun onContentChanged() {
        super.onContentChanged()
        initWidget()
        initData()
        Log.e(TAG, "onContentChanged()")
    }

    private fun notifyTheme(view: View, theme: Resources.Theme) {

        if (view is ViewGroup) {
            val count = view.childCount
            for (i in 0 until count) {
                notifyTheme((view).getChildAt(i), theme)
            }
        } else {
            when (view) {
                is TextView -> {//textView
                    // ViewAttributeUtil.getAttributeValue(view.attri)
                    //ViewAttributeUtil.applyBackgroundDrawable(view,theme,)
                }
                is ImageView -> {//imageView

                }
            }
        }

    }
}
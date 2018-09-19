package com.sumian.sd.theme.widget

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.sumian.sd.theme.ITheme

/**
 * Created by sm
 *
 * on 2018/9/13
 *
 * desc:
 *
 */
class IThemeTextView : TextView, ITheme {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun getView(): View {
        return this
    }

    override fun setTheme(theme: Resources.Theme) {

    }

}
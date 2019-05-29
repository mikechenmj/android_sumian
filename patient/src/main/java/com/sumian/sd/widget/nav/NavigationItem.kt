package com.sumian.sd.widget.nav

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import com.sumian.sd.R
import kotlinx.android.synthetic.main.lay_nav_tab_item.view.*

/**
 * Created by jzz
 * on 2017/4/28.
 *
 *
 * desc:
 */
class NavigationItem @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {


    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        View.inflate(context, R.layout.lay_nav_tab_item, this)
        gravity = Gravity.CENTER
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NavigationItem)
        val text = typedArray.getString(R.styleable.NavigationItem_tab_text)
        @DrawableRes val iconId = typedArray.getResourceId(R.styleable.NavigationItem_tab_icon, 0)
        val textSize = typedArray.getDimension(R.styleable.NavigationItem_tab_text_size, 16f)
        val tvColorStateList = typedArray.getColorStateList(R.styleable.NavigationItem_tab_text_color)
        typedArray.recycle()

        iv_tab_icon.setImageResource(iconId)
        tv_tab_text.text = text
        tv_tab_text.textSize = textSize
        if (tvColorStateList != null) {
            tv_tab_text.setTextColor(tvColorStateList)
        }
    }

    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)
        iv_tab_icon.isActivated = activated
        tv_tab_text.isActivated = activated
    }


    fun showDot(show: Int) {
        tab_dot.visibility = show
    }

    override fun onDetachedFromWindow() {
        isActivated = false
        super.onDetachedFromWindow()
    }
}

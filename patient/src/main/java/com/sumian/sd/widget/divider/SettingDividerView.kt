@file:Suppress("MemberVisibilityCanBePrivate", "DEPRECATION")

package com.sumian.sd.widget.divider

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.sumian.sd.R
import kotlinx.android.synthetic.main.lay_setting_divider_item.view.*


/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

@Suppress("unused")
class SettingDividerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    companion object {

        private const val INVALID_RES_ID = -1
    }

    private var mOnShowMoreListener: OnShowMoreListener? = null
    private var mOnCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null

    init {
        init(context, attrs)
    }

    fun setOnShowMoreListener(onShowMoreListener: OnShowMoreListener) {
        mOnShowMoreListener = onShowMoreListener
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        View.inflate(context, R.layout.lay_setting_divider_item, this)
        gravity = Gravity.CENTER
        setHorizontalGravity(LinearLayout.VERTICAL)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingDividerView)
        @DrawableRes val iconId = typedArray.getResourceId(R.styleable.SettingDividerView_type_icon, INVALID_RES_ID)
        val typeDesc = typedArray.getString(R.styleable.SettingDividerView_type_desc)
        @ColorInt val typeDescColor = typedArray.getColor(R.styleable.SettingDividerView_type_desc_text_color, resources.getColor(R.color.t1_color))
        // @Dimension float typeDescTextSize = typedArray.getDimension(R.styleable.SettingDividerView_type_desc_text_size, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));
        @ColorInt val dotColor = typedArray.getColor(R.styleable.SettingDividerView_dot_color, Color.RED)
        val typeContent = typedArray.getString(R.styleable.SettingDividerView_type_content)
        @ColorInt val typeContentColor = typedArray.getColor(R.styleable.SettingDividerView_type_content_text_color, resources.getColor(R.color.t1_color))
        // float typeContentTextSize = typedArray.getDimension(R.styleable.SettingDividerView_type_content_text_size, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));
        @ColorInt val dividerLineColor = typedArray.getColor(R.styleable.SettingDividerView_divider_line_color, resources.getColor(R.color.b1_color))
        @Dimension val dividerLineSize = typedArray.getDimension(R.styleable.SettingDividerView_divider_line_size, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics))
        val dividerGone = typedArray.getInt(R.styleable.SettingDividerView_divider_line_visible, View.VISIBLE)
        val moreGone = typedArray.getInt(R.styleable.SettingDividerView_divider_more_visible, View.VISIBLE)
        val showSwitch = typedArray.getBoolean(R.styleable.SettingDividerView_show_switch, false)

        @ColorInt val bgColor = typedArray.getColor(R.styleable.SettingDividerView_bg_color, resources.getColor(R.color.b2_color))

        typedArray.recycle()

        if (iconId == INVALID_RES_ID) {
            iv_type.visibility = View.GONE
        } else {
            iv_type.visibility = View.VISIBLE
            iv_type.setImageResource(iconId)
        }
        tv_type_desc.setTextColor(typeDescColor)
        // mTvTypeDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, typeDescTextSize);
        tv_type_desc.text = typeDesc
        tv_setting_content.text = typeContent
        tv_setting_content.setTextColor(typeContentColor)
        tv_setting_content.visibility = if (TextUtils.isEmpty(typeContent)) View.GONE else View.VISIBLE
        //mTvSettingContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, typeContentTextSize);
        v_divider_line.setBackgroundColor(dividerLineColor)
        val layoutParams = v_divider_line.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = dividerLineSize.toInt()
        v_divider_line.layoutParams = layoutParams
        v_divider_line.visibility = dividerGone
        iv_more.visibility = moreGone
        sw.visibility = if (showSwitch) View.VISIBLE else View.GONE

        setBgColor(bgColor)

        setOnClickListener(this)
    }

    fun getContentView(): TextView {
        return tv_setting_content
    }

    fun setBgColor(color: Int) {
        lay_container.setBackgroundColor(color)
    }

    override fun onClick(view: View) {
        if (mOnShowMoreListener != null) {
            mOnShowMoreListener!!.onShowMore(this)
        }
    }

    fun hideMoreIcon() {
        iv_more.visibility = View.INVISIBLE
    }

    fun goneMoreIcon() {
        iv_more.visibility = View.GONE
    }

    fun setMoreVisible(visible : Boolean) {
        iv_more.isVisible = visible
    }

    fun setContent(content: CharSequence) {
        tv_setting_content.text = content
        tv_setting_content.visibility = if (TextUtils.isEmpty(content)) View.GONE else View.VISIBLE
    }

    fun setContent(content: String) {
        tv_setting_content.text = content
        tv_setting_content.visibility = if (TextUtils.isEmpty(content)) View.GONE else View.VISIBLE
    }

    fun setLabel(label: String) {
        tv_type_desc.text = label
    }

    fun setOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener?) {
        mOnCheckedChangeListener = listener
        sw.setOnCheckedChangeListener(listener)
    }

    fun setSwitchChecked(checked: Boolean) {
        sw.isChecked = checked
    }

    /**
     * 控件初始化数据的时候，可能不需要触发回调，可以调用该方法
     */
    fun setSwitchCheckedWithoutCallback(checked: Boolean) {
        sw.setOnCheckedChangeListener(null)
        sw.isChecked = checked
        sw.setOnCheckedChangeListener(mOnCheckedChangeListener)
    }

    fun showRedDot(show: Boolean) {
        v_dot.visibility = if (show) View.VISIBLE else View.GONE
    }
}

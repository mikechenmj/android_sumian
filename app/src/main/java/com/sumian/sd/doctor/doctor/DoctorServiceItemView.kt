@file:Suppress("MemberVisibilityCanBePrivate")

package com.sumian.sd.doctor.doctor

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import com.sumian.common.image.ImageLoader
import com.sumian.sd.R
import kotlinx.android.synthetic.main.lay_doctor_service_item.view.*
import java.util.*


/**
 * <pre>
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/5/3 14:45
 * desc   : 医生服务Item
 * version: 1.0
</pre> *
 */
class DoctorServiceItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {


    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        View.inflate(context, R.layout.lay_doctor_service_item, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.DoctorServiceItemView, 0, 0)
        val showPrice = attributes.getBoolean(R.styleable.DoctorServiceItemView_dsiv_show_price, false)
        val price = attributes.getFloat(R.styleable.DoctorServiceItemView_dsiv_price, 0f)
        val title = attributes.getString(R.styleable.DoctorServiceItemView_dsiv_title)
        val desc = attributes.getString(R.styleable.DoctorServiceItemView_dsiv_desc)
        val drawable = attributes.getDrawable(R.styleable.DoctorServiceItemView_dsiv_image)
        attributes.recycle()

        showPrice(showPrice)
        setPrice(price)
        setTitle(title)
        setDesc(desc)
        if (drawable != null) {
            iv.setImageDrawable(drawable)
        }
    }

    fun showPrice(showPrice: Boolean) {
        val visibility = if (showPrice) View.VISIBLE else View.GONE
        tv_price.visibility = visibility
        v_price_bg.visibility = visibility
    }

    fun setPrice(price: Float) {
        val format = String.format(Locale.getDefault(), "%.0f元", price)
        tv_price.text = format
    }

    fun setTitle(title: String?) {
        tv_title.text = title
    }

    fun setDesc(desc: String?) {
        tv_desc.text = desc
    }

    fun loadImage(@DrawableRes drawableId: Int) {
        iv.setImageResource(drawableId)
    }

    fun loadImage(uri: String) {
        ImageLoader.loadImage(uri, iv)
    }
}

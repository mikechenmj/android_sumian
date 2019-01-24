package com.sumian.sd.widget.banner

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.sumian.common.image.loadImage
import com.sumian.sd.R

/**
 * Created by jzz
 *
 * on 2019/1/23
 *
 * desc:
 */
class BannerAdapter constructor(context: Context, bannerUrlList: List<String>) : PagerAdapter() {

    private val bannerViewList by lazy {
        val bannerList = mutableListOf<View>()
        var banner: RoundImageView
        bannerUrlList.forEachIndexed { index, url ->
            banner = RoundImageView(context = context)
            val drawableId = when (index) {
                0 -> {
                    R.drawable.ic_cbti_img_banner1
                }
                1 -> {
                    R.drawable.ic_cbti_img_banner2
                }
                2 -> {
                    R.drawable.ic_home_cbti_img_banner
                }
                3 -> {
                    R.drawable.bg_home_cbti_banner_high
                }
                else -> {
                    R.drawable.ic_cbti_img_banner2
                }
            }
            banner.setTag(drawableId, index)
            banner.loadImage(url, drawableId, drawableId)
            bannerList.add(banner)

        }
        for (iconUrl in bannerUrlList) {

        }
        return@lazy bannerList
    }

    private var onclickListener: View.OnClickListener? = null

    fun setOnClickListener(listener: View.OnClickListener): BannerAdapter {
        this.onclickListener = listener
        return this
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val banner = bannerViewList[position]
        banner.setOnClickListener {
            onclickListener?.onClick(it)
        }
        container.addView(banner)
        return banner
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        //super.destroyItem(container, position, `object`)
        container.removeView(`object` as View?)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return bannerViewList.size
    }
}
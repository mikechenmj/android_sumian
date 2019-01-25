package com.sumian.sd.widget.banner

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.sumian.sd.R

/**
 * Created by jzz
 *
 * on 2019/1/23
 *
 * desc:
 */
class BannerAdapter constructor(context: Context, private val banners: List<Banner>) : PagerAdapter() {

    companion object {
        private const val DEFAULT_TOTAL_COUNT = 1000
    }

    private var onBannerCallback: OnBannerCallback? = null

    fun setOnBannerCallback(onBannerCallback: OnBannerCallback): BannerAdapter {
        this.onBannerCallback = onBannerCallback
        return this
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val bannerView = RoundImageView(container.context)
        val p = position % banners.size
        if (p >= 0 && p <= banners.size) {
            val banner = banners[p]
            bannerView.setOnClickListener {
                onBannerCallback?.onBannerCallback(it, p, banner)
            }
            //bannerView.loadImage(banner.url, R.drawable.ic_cbti_img_banner2, R.drawable.ic_cbti_img_banner2)
            val options = RequestOptions
                    .placeholderOf(R.drawable.ic_cbti_img_banner2)
                    .error(R.drawable.ic_cbti_img_banner2)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            Glide.with(container).asBitmap().load(banner.url).apply(options).listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    bannerView.setImageResource(R.drawable.ic_cbti_img_banner2)
                    return true
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    bannerView.setImageBitmap(resource)
                    return true
                }

            }).preload(container.width, container.height)

            container.addView(bannerView)
        }
        return bannerView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        //super.destroyItem(container, position, `object`)
        container.removeView(`object` as View?)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return if (banners.size == 1) 1 else DEFAULT_TOTAL_COUNT
    }

    fun getBannerSize(): Int = banners.size

    interface OnBannerCallback {
        fun onBannerCallback(bannerView: View, position: Int, banner: Banner)
    }
}
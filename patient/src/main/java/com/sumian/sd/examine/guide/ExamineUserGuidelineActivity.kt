package com.sumian.sd.examine.guide

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.blankj.utilcode.util.ActivityUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.sumian.common.base.BaseActivity
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.examine.guide.bean.Guideline
import com.sumian.sd.examine.login.ExamineLoginRouterActivity
import com.sumian.sd.examine.main.ExamineMainActivity
import kotlinx.android.synthetic.main.activity_user_guide.*
import kotlinx.android.synthetic.main.lay_guideline_container.*

class ExamineUserGuidelineActivity : BaseActivity() {

    private val mGuidelines = mutableListOf<Guideline>()
    private val mAdapter by lazy {
        object : PagerAdapter() {
            override fun isViewFromObject(view: View, any: Any): Boolean {
                return view === any
            }

            override fun getCount(): Int {
                return mGuidelines.size
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val guideline = mGuidelines[position]
                val viewHolder = ViewHolder(this@ExamineUserGuidelineActivity)
                viewHolder.initView(guideline)
                container.addView(viewHolder.getItemView())
                return container.getChildAt(position)
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            }

            override fun getItemPosition(`object`: Any): Int {
                return POSITION_NONE
            }
        }
    }

    companion object {
        fun show(context: Context) {
            context.startActivity(Intent(context, ExamineUserGuidelineActivity::class.java))
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_user_guide
    }

    override fun initWidget() {
        super.initWidget()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //5.0 全透明实现
            val window = window
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else { //4.4 全透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        vp_guide_container.adapter = mAdapter
    }

    override fun initData() {
        super.initData()
        val guidelineOne = Guideline()
        guidelineOne.h1Label = R.string.guideline_one_h1
        guidelineOne.h2Label = R.string.guideline_one_h2
        guidelineOne.iconId = R.drawable.rotation_1
        guidelineOne.indicatorPosition = 1
        mGuidelines.add(guidelineOne)

        val guidelineTwo = Guideline()
        guidelineTwo.h1Label = R.string.guideline_two_h1
        guidelineTwo.h2Label = R.string.guideline_two_h2
        guidelineTwo.iconId = R.drawable.rotation_2
        guidelineTwo.indicatorPosition = 2
        mGuidelines.add(guidelineTwo)

        val guidelineThree = Guideline()
        guidelineThree.h1Label = R.string.guideline_three_h1
        guidelineThree.h2Label = R.string.guideline_three_h2
        guidelineThree.iconId = R.drawable.rotation_3
        guidelineThree.indicatorPosition = 3
        mGuidelines.add(guidelineThree)

        val guidelineFour = Guideline()
        guidelineFour.h1Label = R.string.guideline_four_h1
        guidelineFour.h2Label = R.string.guideline_four_h2
        guidelineFour.iconId = R.drawable.rotation_4
        guidelineFour.indicatorPosition = 4
        mGuidelines.add(guidelineFour)
        mAdapter.notifyDataSetChanged()
    }

    inner class ViewHolder(val context: Context) : View.OnClickListener {

        private val itemView: View = LayoutInflater.from(context).inflate(R.layout.lay_guideline_container, null, false)

        fun getItemView(): View? {
            return itemView
        }

        fun initView(guideline: Guideline) {
            itemView.findViewById<TextView>(R.id.tv_guideline_one).setText(guideline.h1Label)
            itemView.findViewById<TextView>(R.id.tv_guideline_two).setText(guideline.h2Label)
            itemView.findViewById<ImageView>(R.id.iv_guideline_icon).setImageResource(guideline.iconId)
            val displayMetrics = itemView.resources.displayMetrics
            val heightPixels = displayMetrics.heightPixels
            val widthPixels = displayMetrics.widthPixels
            val option = RequestOptions.centerCropTransform()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .skipMemoryCache(true)
                    .override(widthPixels, heightPixels)
            val icon = itemView.findViewById<ImageView>(R.id.iv_guideline_icon)
            Glide.with(itemView.context)
                    .asBitmap()
                    .load(guideline.iconId)
                    .apply(option)
                    .into(object : SimpleTarget<Bitmap?>(icon.width, icon.height) {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                            icon.setImageBitmap(resource)
                        }
                    })
            val button = itemView.findViewById<Button>(R.id.bt_action)
            if (guideline.indicatorPosition == 4) {
                button.setOnClickListener(this)
                button.visibility = View.VISIBLE
            } else {
                button.visibility = View.INVISIBLE
            }
            itemView.findViewById<GuidelineIndicator>(R.id.guideline_indicator).showIndicator(guideline.indicatorPosition)
        }

        override fun onClick(v: View) {
            val login: Boolean = AppManager.getAccountViewModel().isLogin
            if (login) {
                ActivityUtils.startActivity(ExamineMainActivity::class.java)
            } else {
                ExamineLoginRouterActivity.show()
            }
            finish()
        }

    }
}
package com.sumian.sleepdoctor.cbti.activity

import android.view.View
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseActivity
import com.sumian.sleepdoctor.base.BasePresenter
import kotlinx.android.synthetic.main.activity_main_cbti_curriculum_center.*

/**
 * Created by sm
 *
 * on 2018/7/10
 *
 * desc:CBTI 已购买成功.课程中心  包含:1.总课时  2.课程每个课时完成进度
 *
 */
class CBTICurriculumActivity : BaseActivity<BasePresenter<*>>(), View.OnClickListener {


    override fun getLayoutId(): Int {
        return R.layout.activity_main_cbti_curriculum_center
    }

    override fun initPresenter() {
        super.initPresenter()
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        tv_see_more.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        //cbtiPartView.invalid(cbtiPart)
        //cbtiPartView.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, resources.getDimensionPixelOffset(R.dimen.space_94))
        //cbti_container.addView(cbtiPartView)
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.tv_see_more -> {
                    show(this, CBTIIntroductionWebActivity::class.java)
                }
                else -> {

                }

            }
        }
    }
}
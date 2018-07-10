package com.sumian.sleepdoctor.cbti.activity

import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseActivity
import com.sumian.sleepdoctor.base.BasePresenter

/**
 * Created by sm
 *
 * on 2018/7/10
 *
 * desc:CBTI 已购买成功.课程中心  包含:1.总课时  2.课程每个课时完成进度
 *
 */
class CBTICurriculumActivity : BaseActivity<BasePresenter<*>>() {

    override fun getLayoutId(): Int {
        return R.layout.activity_main_cbti_curriculum_center
    }
}
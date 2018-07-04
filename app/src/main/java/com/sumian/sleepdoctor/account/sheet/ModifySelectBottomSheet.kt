package com.sumian.sleepdoctor.account.sheet

import android.os.Bundle
import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import cn.carbswang.android.numberpickerview.library.NumberPickerView
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.account.userProfile.contract.ImproveUserProfileContract
import com.sumian.sleepdoctor.account.userProfile.presenter.ImproveUserProfilePresenter
import com.sumian.sleepdoctor.widget.BaseBottomSheetView

/**
 * Created by sm
 *
 * on 2018/7/4
 *
 * desc:修改用户数据 e.g. gender/weight/height/birthday/education
 *
 */
class ModifySelectBottomSheet : BaseBottomSheetView(), ImproveUserProfileContract.View, View.OnClickListener {


    @BindView(R.id.tv_title)
    lateinit var mTvTitle: TextView

    @BindView(R.id.picker_one)
    lateinit var mPickerOne: NumberPickerView

    @BindView(R.id.picker_two)
    lateinit var mPickerTwo: NumberPickerView

    @BindView(R.id.tv_sure)
    lateinit var mTvSure: TextView

    private lateinit var mPresenter: ImproveUserProfileContract.Presenter
    private lateinit var mModifyKey: String

    companion object {

        private const val EXTRAS_MODIFY = "com.sumian.sleepdoctor.extras.MODIFY"

        fun newInstance(modifyKey: String): BaseBottomSheetView {
            return ModifySelectBottomSheet().apply {
                arguments = Bundle().apply { putString(EXTRAS_MODIFY, modifyKey) }
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.lay_bottom_sheet_modify_user_info
    }

    override fun initBundle(arguments: Bundle?) {
        super.initBundle(arguments)
        arguments?.let {
            this.mModifyKey = it.getString(EXTRAS_MODIFY)
        }
        ImproveUserProfilePresenter.init(this)
    }

    override fun setPresenter(presenter: ImproveUserProfileContract.Presenter?) {
        super.setPresenter(presenter)
        this.mPresenter = presenter!!
    }

    override fun initView(rootView: View?) {
        super.initView(rootView)

    }

    override fun initData() {
        super.initData()

        val title: String = when (mModifyKey) {
            ImproveUserProfileContract.IMPROVE_WEIGHT_KEY -> {

            }
            ImproveUserProfileContract.IMPROVE_HEIGHT_KEY -> {

            }
            ImproveUserProfileContract.IMPROVE_BIRTHDAY_KEY -> {

            }
            ImproveUserProfileContract.IMPROVE_GENDER_KEY -> {

            }
            ImproveUserProfileContract.IMPROVE_EDUCATION_KEY -> {

            }
            else -> {
                ""
            }
        }
        mTvTitle.text = title
    }

    @OnClick(R.id.tv_sure)
    override fun onClick(v: View?) {

        val modifyInfo: String = when (mModifyKey) {
            ImproveUserProfileContract.IMPROVE_WEIGHT_KEY,
            ImproveUserProfileContract.IMPROVE_HEIGHT_KEY -> {
                "${mPickerOne.contentByCurrValue}.${mPickerTwo.contentByCurrValue}"
            }
            ImproveUserProfileContract.IMPROVE_BIRTHDAY_KEY -> {
                "${mPickerOne.contentByCurrValue}${mPickerTwo.contentByCurrValue}"
            }
            ImproveUserProfileContract.IMPROVE_GENDER_KEY,
            ImproveUserProfileContract.IMPROVE_EDUCATION_KEY -> {
                mPickerOne.contentByCurrValue
            }
            else -> {
                ""
            }
        }

        mPresenter.improveUserProfile(mModifyKey, modifyInfo)
    }

    override fun onImproveUserProfileSuccess() {
        dismissAllowingStateLoss()
    }
}
package com.sumian.sd.account.sheet

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import cn.carbswang.android.numberpickerview.library.NumberPickerView
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import com.sumian.sd.account.userProfile.contract.ImproveUserProfileContract
import com.sumian.sd.account.userProfile.contract.ModifyUserInfoContract
import com.sumian.sd.account.userProfile.presenter.ModifyUserInfoPresenter
import com.sumian.sd.widget.base.BaseBottomSheetView
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * Created by sm
 *
 * on 2018/7/4
 *
 * desc:修改用户数据 e.g. gender/weight/height/birthday/education
 *
 */
class ModifySelectBottomSheet : BaseBottomSheetView(), ModifyUserInfoContract.View, View.OnClickListener, NumberPickerView.OnValueChangeListener {
    override fun onValueChange(picker: NumberPickerView?, oldVal: Int, newVal: Int) {
        val year: Int
        when (mModifyKey) {
            ImproveUserProfileContract.IMPROVE_BIRTHDAY_KEY -> {
                year = Integer.parseInt(picker?.contentByCurrValue, 10)
                if (year == Calendar.getInstance().get(Calendar.YEAR)) {
                    val monthCount = Calendar.getInstance().get(Calendar.MONTH) + 1
                    val months = arrayOfNulls<String>(monthCount)
                    for (i in months.indices) {
                        months[i] = String.format(Locale.getDefault(), "%02d", i + 1)
                    }
                    mPickerTwo.refreshByNewDisplayedValues(months)
                } else {
                    val months = arrayOfNulls<String>(12)
                    for (i in months.indices) {
                        months[i] = String.format(Locale.getDefault(), "%02d", i + 1)
                    }
                    mPickerTwo.refreshByNewDisplayedValues(months)
                }
            }
        }
    }

    @BindView(R.id.tv_title)
    lateinit var mTvTitle: TextView

    @BindView(R.id.picker_one)
    lateinit var mPickerOne: NumberPickerView

    @BindView(R.id.picker_two)
    lateinit var mPickerTwo: NumberPickerView

    @BindView(R.id.tv_sure)
    lateinit var mTvSure: TextView

    private lateinit var mPresenter: ModifyUserInfoContract.Presenter
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
        ModifyUserInfoPresenter.init(this)
    }

    override fun setPresenter(presenter: ImproveUserProfileContract.Presenter) {
        //super.setPresenter(presenter)
        this.mPresenter = presenter as ModifyUserInfoContract.Presenter
    }

    override fun initView(rootView: View?) {
        super.initView(rootView)
        mPickerOne.setOnValueChangedListener(this)

        mPresenter.transformTitle(mModifyKey)
    }

    @OnClick(R.id.tv_sure)
    override fun onClick(v: View?) {
        val modifyInfo = mPresenter.transformModify(mModifyKey, mPickerOne, mPickerTwo)
        mPresenter.improveUserProfile(mModifyKey, modifyInfo)
    }

    override fun transformOneDisplayedValues(currentPosition: Int, hintText: String?, displayedValues: Array<out String>) {
        runUiThread {
            mPickerOne.refreshByNewDisplayedValues(displayedValues)
            mPickerOne.pickedIndexRelativeToRaw = currentPosition
            mPickerOne.setHintText(hintText)
            mPickerOne.visibility = View.VISIBLE
        }
    }

    override fun transformTwoDisplayedValues(currentPosition: Int, hintText: String?, displayedValues: Array<out String>) {
        runUiThread {
            mPickerTwo.refreshByNewDisplayedValues(displayedValues)
            mPickerTwo.pickedIndexRelativeToRaw = currentPosition
            mPickerTwo.setHintText(hintText)
            mPickerTwo.visibility = View.VISIBLE
        }
    }

    override fun showOnePicker(visible: Int) {
        mPickerOne.visibility = visible
    }

    override fun showTwoPicker(visible: Int) {
        mPickerTwo.visibility = visible
    }

    override fun transformTitle(title: String) {
        mTvTitle.text = title
    }

    override fun onImproveUserProfileSuccess() {
        dismissAllowingStateLoss()
    }

    override fun onFailure(error: String?) {
        super.onFailure(error)
        ToastHelper.show(context, error, Gravity.CENTER)
    }
}
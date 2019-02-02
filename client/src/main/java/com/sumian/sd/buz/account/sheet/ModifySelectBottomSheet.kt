package com.sumian.sd.buz.account.sheet

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.sumian.common.helper.ToastHelper
import com.sumian.common.widget.picker.NumberPickerView
import com.sumian.sd.R
import com.sumian.sd.buz.account.userProfile.ImproveUserProfileContract
import com.sumian.sd.buz.account.userProfile.ModifyUserInfoContract
import com.sumian.sd.buz.account.userProfile.ModifyUserInfoPresenter
import com.sumian.sd.widget.base.BaseBottomSheetView
import kotlinx.android.synthetic.main.lay_bottom_sheet_modify_user_info.*
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * Created by sm
 *
 * on 2018/7/4
 *
 * desc:修改用户数据 e.g. gender/weight/height/birthday/education/area
 *
 */
class ModifySelectBottomSheet : BaseBottomSheetView(), ModifyUserInfoContract.View, View.OnClickListener,
        NumberPickerView.OnValueChangeListener, NumberPickerView.OnScrollListener {


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
        picker_one.setOnValueChangedListener(this)
        picker_one.setOnScrollListener(this)

        picker_two.setOnValueChangedListener(this)
        picker_two.setOnScrollListener(this)

        picker_three.setOnScrollListener(this)
        tv_sure.setOnClickListener(this)

        mPresenter.transformTitle(mModifyKey)
    }

    override fun onClick(v: View?) {
        val modifyInfo = mPresenter.transformModify(mModifyKey, picker_one, picker_two, picker_three)
        mPresenter.improveUserProfile(mModifyKey, modifyInfo)
    }

    override fun onValueChange(picker: NumberPickerView, oldVal: Int, newVal: Int) {
        when (mModifyKey) {
            ImproveUserProfileContract.IMPROVE_BIRTHDAY_KEY -> {
                if (picker.id == R.id.picker_one) {
                    val year: Int = Integer.parseInt(picker.contentByCurrValue, 10)
                    if (year == Calendar.getInstance().get(Calendar.YEAR)) {
                        val monthCount = Calendar.getInstance().get(Calendar.MONTH) + 1
                        val months = Array(monthCount, init = {
                            String.format(Locale.getDefault(), "%02d", it + 1)
                        })

                        picker_two.refreshByNewDisplayedValues(months)
                    } else {
                        val months = Array(12, init = {
                            String.format(Locale.getDefault(), "%02d", it + 1)
                        })
                        picker_two.refreshByNewDisplayedValues(months)
                    }
                }
            }
            ImproveUserProfileContract.IMPROVE_AREA_KEY -> {
                val province: String
                when (picker.id) {
                    R.id.picker_one -> {
                        province = picker.contentByCurrValue
                        mPresenter.transformCityForProvince(province)
                    }
                    R.id.picker_two -> {
                        val city = picker.contentByCurrValue
                        mPresenter.transformAreaForCity(city)
                    }
                }

            }
        }
    }

    override fun onScrollStateChange(view: NumberPickerView, scrollState: Int) {
        runUiThread {
            tv_sure.isEnabled = (scrollState == NumberPickerView.OnScrollListener.SCROLL_STATE_IDLE)
        }
    }

    override fun transformOneDisplayedValues(currentPosition: Int, hintText: String?, displayedValues: Array<out String>) {
        runUiThread {
            picker_one.refreshByNewDisplayedValues(displayedValues)
            picker_one.pickedIndexRelativeToRaw = currentPosition
            picker_one.setHintText(hintText)
            picker_one.visibility = View.VISIBLE
        }
    }

    override fun transformTwoDisplayedValues(currentPosition: Int, hintText: String?, displayedValues: Array<out String>) {
        runUiThread {
            picker_two.refreshByNewDisplayedValues(displayedValues)
            picker_two.pickedIndexRelativeToRaw = currentPosition
            picker_two.setHintText(hintText)
            picker_two.visibility = View.VISIBLE
        }
    }

    override fun transformThreeDisplayedValues(currentPosition: Int, hintText: String?, displayedValues: Array<out String>?) {
        runUiThread {
            picker_three.refreshByNewDisplayedValues(displayedValues)
            picker_three.setHintText(hintText)
            picker_three.visibility = View.VISIBLE
            tv_sure.isEnabled = true
            tv_sure.visibility = View.VISIBLE
        }
    }

    override fun showOnePicker(visible: Int) {
        picker_one.visibility = visible
    }

    override fun showTwoPicker(visible: Int) {
        picker_two.visibility = visible
    }

    override fun showThreePicker(visible: Int) {
        picker_three.visibility = visible
    }

    override fun transformTitle(title: String) {
        tv_title.text = title
    }

    override fun onImproveUserProfileSuccess() {
        dismissAllowingStateLoss()
    }

    override fun onFailure(error: String) {
        super.onFailure(error)
        ToastHelper.show(context, error, Gravity.CENTER)
    }
}
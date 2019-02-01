package com.sumian.sddoctor.account.sheet

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.sumian.common.helper.ToastHelper
import com.sumian.common.widget.picker.NumberPickerView
import com.sumian.sddoctor.R
import com.sumian.sddoctor.R.string.select_department
import com.sumian.sddoctor.R.string.select_job_title
import com.sumian.sddoctor.account.activity.ModifyDoctorInfoActivity.Companion.EXTRAS_MODIFY_TYPE
import com.sumian.sddoctor.account.activity.ModifyDoctorInfoActivity.Companion.MODIFY_TYPE_DEPARTMENT
import com.sumian.sddoctor.account.contract.AccountContract
import com.sumian.sddoctor.account.presenter.AccountPresenter
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.login.login.bean.DoctorInfo
import com.sumian.sddoctor.widget.LoadingDialog
import com.sumian.sddoctor.widget.sheet.AbstractBottomSheetView

class SelectDoctorInfoBottomSheet : AbstractBottomSheetView(), NumberPickerView.OnScrollListener, View.OnClickListener, AccountContract.View {

    private var mSelectType = MODIFY_TYPE_DEPARTMENT

    private lateinit var mTvTitle: TextView
    private lateinit var numberPickerView: NumberPickerView
    private lateinit var mTvSure: TextView

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(context!!)
    }

    private val mPresenter: AccountContract.Presenter by lazy {
        AccountPresenter.init(this)
    }

    companion object {

        fun newInstance(selectType: Int): SelectDoctorInfoBottomSheet {
            val selectBottomSheet = SelectDoctorInfoBottomSheet()
            val args = Bundle().apply {
                this.putInt(EXTRAS_MODIFY_TYPE, selectType)
            }
            selectBottomSheet.arguments = args

            return selectBottomSheet
        }
    }


    override fun getLayout(): Int {
        return R.layout.lay_bottom_sheet_modify_user_info
    }

    override fun initBundle(arguments: Bundle?) {
        super.initBundle(arguments)
        arguments?.let {
            mSelectType = it.getInt(EXTRAS_MODIFY_TYPE, MODIFY_TYPE_DEPARTMENT)
        }
    }

    override fun initView(rootView: View?) {
        super.initView(rootView)
        mTvTitle = rootView!!.findViewById(R.id.tv_title)
        numberPickerView = rootView.findViewById(R.id.picker)
        numberPickerView.setOnScrollListener(this)
        mTvSure = rootView.findViewById(R.id.tv_sure)
        mTvSure.setOnClickListener(this)
        mTvTitle.text = if (mSelectType == MODIFY_TYPE_DEPARTMENT) {
            getText(select_department)
        } else {
            getText(select_job_title)
        }
    }

    override fun initData() {
        super.initData()

        val selectArrays: Array<out String> = if (mSelectType == MODIFY_TYPE_DEPARTMENT) {
            resources.getStringArray(R.array.departments)
        } else {
            resources.getStringArray(R.array.job_titles)
        }
        numberPickerView.refreshByNewDisplayedValues(selectArrays)

        val defaultContent = if (mSelectType == MODIFY_TYPE_DEPARTMENT) {
            AppManager.getAccountViewModel().getDoctorInfo().value?.department
        } else AppManager.getAccountViewModel().getDoctorInfo().value?.title


        numberPickerView.pickedIndexRelativeToRaw = if (selectArrays.indexOf(defaultContent) == -1) 0 else selectArrays.indexOf(defaultContent)
    }

    override fun onScrollStateChange(view: NumberPickerView?, scrollState: Int) {
        runUiThread {
            mTvSure.isEnabled = scrollState == 0
        }
    }

    override fun onClick(v: View?) {
        mPresenter.modifyDoctorInfo(mSelectType, numberPickerView.contentByCurrValue)
    }

    override fun showLoading() {
        super.showLoading()
        if (!loadingDialog.isShowing) {
            loadingDialog.show()
        }
    }

    override fun dismissLoading() {
        super.dismissLoading()
        if (loadingDialog.isShowing) {
            loadingDialog.cancel()
        }
    }

    override fun onModifySuccess(doctorInfo: DoctorInfo) {
        dismissAllowingStateLoss()
    }

    override fun onModifyFailed(error: String) {
        ToastHelper.show(context!!, error, Gravity.CENTER)
    }

    override fun onDestroyView() {
        mPresenter.onRelease()
        super.onDestroyView()
    }
}
package com.sumian.sddoctor.me.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_choose_string.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/23 16:59
 * desc   :
 * version: 1.0
 */
class ChooseStringActivity : SddBaseActivity() {
    private lateinit var mParams: ChooseStringParams

    override fun getLayoutId(): Int {
        return R.layout.activity_choose_string
    }

    override fun showBackNav(): Boolean {
        return true
    }

    companion object {
        private const val KEY_INPUT_VALUE = "KEY_INPUT_VALUE"
        private const val KEY_PARAMS = "KEY_PARAMS"
        private const val REQUEST_CODE = 100

        fun launchForResult(fragment: Fragment,
                            requestCode: Int,
                            params: ChooseStringParams) {
            val intent = Intent(fragment.activity, ChooseStringActivity::class.java)
            intent.putExtra(KEY_PARAMS, params)
            fragment.startActivityForResult(intent, requestCode)
        }

        fun createParams(context: Context,
                         title: Int,
                         stringArr: Int,
                         canAddMore: Boolean = false,
                         addMoreHint: Int,
                         inputStringPageTitle: Int,
                         inputStringPageMenu: Int,
                         inputStringPageLabel: Int,
                         inputStringPageFieldName: Int,
                         inputStringPageInputHint: Int,
                         inputStringPageInputMinLength: Int = 0,
                         inputStringPageInputMaxLength: Int = 0
        ): ChooseStringParams {
            return ChooseStringParams(
                    context.getString(title),
                    context.resources.getStringArray(stringArr),
                    canAddMore,
                    if (canAddMore) context.getString(addMoreHint) else "",
                    if (canAddMore) InputStringActivity.InputStringParams(
                            context.getString(inputStringPageTitle),
                            context.getString(inputStringPageMenu),
                            context.getString(inputStringPageLabel),
                            context.getString(inputStringPageFieldName),
                            context.getString(inputStringPageInputHint),
                            inputStringPageInputMinLength,
                            inputStringPageInputMaxLength) else null)
        }

        fun getResult(intent: Intent?): String? {
            return intent?.getStringExtra(KEY_INPUT_VALUE)
        }

    }

    override fun initWidget() {
        super.initWidget()
        mParams = intent.getParcelableExtra(KEY_PARAMS)

        setTitle(mParams.title)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = createAdapter()

    }

    private fun createAdapter(): ItemAdapter {
        val adapter = ItemAdapter()
        adapter.setOnItemClickListener { adt, view, position ->
            run {
                if (position < adapter.data.size - 1) {
                    finishWithData(adapter.getItem(position))
                } else {
                    if (mParams.canAddMore) {
                        InputStringActivity.launchForResult(this, REQUEST_CODE, mParams.inputStringParams!!)
                    } else {
                        finishWithData(adapter.getItem(position))
                    }
                }
            }

        }
        val dataList = mParams.stringArr.toMutableList()
        if (mParams.canAddMore) {
            dataList.add(mParams.addMoreHint)
        }
        adapter.addData(dataList)
        return adapter
    }

    private fun finishWithData(text: String?) {
        val intent = Intent()
        intent.putExtra(KEY_INPUT_VALUE, text)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    class ItemAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.list_item_string_arrow) {
        override fun convert(helper: BaseViewHolder, item: String?) {
            helper.setText(R.id.tv, item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val string = InputStringActivity.getString(data)
                finishWithData(string)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @Parcelize
    data class ChooseStringParams(
            val title: String, // list page title
            val stringArr: Array<String>, // list
            val canAddMore: Boolean = false, //can add more
            val addMoreHint: String = "",   // list page add more hint
            val inputStringParams: InputStringActivity.InputStringParams?
    ) : Parcelable

}
package com.sumian.sddoctor.me.authentication

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import kotlinx.android.synthetic.main.activity_choose_string.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/23 16:59
 * desc   :
 * version: 1.0
 */
class ChooseStringActivity : SddBaseActivity() {
    private lateinit var mTitle: String
    private lateinit var mStringArr: Array<String>
    private var mCanAddMore: Boolean = false
    private lateinit var mAddMoreHint: String

    // input string data
    private lateinit var mTitle2: String
    private lateinit var mMenu: String
    private lateinit var mLabel: String
    private lateinit var mFieldName: String
    private lateinit var mInputHint: String
    private var mInputMinLength: Int = 0
    private var mInputMaxLength: Int = 0

    override fun getLayoutId(): Int {
        return R.layout.activity_choose_string
    }

    override fun showBackNav(): Boolean {
        return true
    }

    companion object {
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_TITLE2 = "KEY_TITLE2"
        private const val KEY_STRING_ARR = "KEY_STRING_ARR"
        private const val KEY_CAN_ADD_MORE = "KEY_CAN_ADD_MORE"
        private const val KEY_ADD_MORE_HINT = "KEY_ADD_MORE_HINT"
        private const val KEY_INPUT_VALUE = "KEY_INPUT_VALUE"
        private const val REQUEST_CODE = 100

        fun launchForResult(fragment: Fragment,
                            requestCode: Int,
                            title: String,
                            title2: String,
                            stringArr: Array<String>,
                            canAddMore: Boolean = false,
                            addMoreHint: String = "",
                            menu: String = "",
                            label: String = "",
                            fieldName: String = "",
                            inputHint: String = "",
                            inputMinLength: Int = 0,
                            inputMaxLength: Int = 0) {
            val intent = Intent(fragment.activity, ChooseStringActivity::class.java)
            intent.putExtra(KEY_TITLE, title)
            intent.putExtra(KEY_TITLE2, title2)
            intent.putExtra(KEY_STRING_ARR, stringArr)
            intent.putExtra(KEY_CAN_ADD_MORE, canAddMore)
            intent.putExtra(KEY_ADD_MORE_HINT, addMoreHint)
            // input string data
            intent.putExtra(InputStringActivity.KEY_MENU, menu)
            intent.putExtra(InputStringActivity.KEY_LABEL, label)
            intent.putExtra(InputStringActivity.KEY_FIELD_NAME, fieldName)
            intent.putExtra(InputStringActivity.KEY_INPUT_HINT, inputHint)
            intent.putExtra(InputStringActivity.KEY_INPUT_MIN_LENGTH, inputMinLength)
            intent.putExtra(InputStringActivity.KEY_INPUT_MAX_LENGTH, inputMaxLength)
            fragment.startActivityForResult(intent, requestCode)
        }

        fun getString(intent: Intent?): String? {
            return intent?.getStringExtra(KEY_INPUT_VALUE)
        }

    }

    override fun initWidget() {
        super.initWidget()
        mTitle = intent.getStringExtra(KEY_TITLE)
        mStringArr = intent.getStringArrayExtra(KEY_STRING_ARR)
        mCanAddMore = intent.getBooleanExtra(KEY_CAN_ADD_MORE, false)
        mAddMoreHint = intent.getStringExtra(KEY_ADD_MORE_HINT)
        // input string data
        mTitle2 = intent.getStringExtra(KEY_TITLE2)
        mMenu = intent.getStringExtra(InputStringActivity.KEY_MENU)
        mLabel = intent.getStringExtra(InputStringActivity.KEY_LABEL)
        mFieldName = intent.getStringExtra(InputStringActivity.KEY_FIELD_NAME)
        mInputHint = intent.getStringExtra(InputStringActivity.KEY_INPUT_HINT)
        mInputMinLength = intent.getIntExtra(InputStringActivity.KEY_INPUT_MIN_LENGTH, 0)
        mInputMaxLength = intent.getIntExtra(InputStringActivity.KEY_INPUT_MAX_LENGTH, 0)

        setTitle(mTitle)
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
                    if (mCanAddMore) {
                        InputStringActivity.launchForResult(this, REQUEST_CODE,
                                mTitle2, mMenu, mLabel, mFieldName, mInputHint,
                                mInputMinLength, mInputMaxLength)
                    } else {
                        finishWithData(adapter.getItem(position))
                    }
                }
            }

        }
        val dataList = mStringArr.toMutableList()
        if (mCanAddMore) {
            dataList.add(mAddMoreHint)
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

}
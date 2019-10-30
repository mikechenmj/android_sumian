package com.sumian.sd.buz.anxiousandfaith

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sumian.common.helper.ToastHelper
import com.sumian.common.widget.SumianFlexboxLayout
import com.sumian.sd.R
import com.sumian.sd.buz.anxiousandfaith.databinding.ActivityMoodCognitionBiasData
import com.sumian.sd.databinding.ActivityMoodCognitionBiasLayoutBinding
import kotlinx.android.synthetic.main.activity_mood_cognition_bias_layout.*

@SuppressLint("SetTextI18n")
class MoodCognitionBiasActivity : TitleBaseActivity() {

    var mCheckedLabels = mutableListOf<String>()
    val mMoodCognitionBias = arrayOf(
            SumianFlexboxLayout.SimpleLabelBean("非黑即白", false),
            SumianFlexboxLayout.SimpleLabelBean("以偏概全", false),
            SumianFlexboxLayout.SimpleLabelBean("选择性关注", false),
            SumianFlexboxLayout.SimpleLabelBean("妄下结论", false),
            SumianFlexboxLayout.SimpleLabelBean("揣摩", false),
            SumianFlexboxLayout.SimpleLabelBean("预测未来", false),
            SumianFlexboxLayout.SimpleLabelBean("贴标签", false),
            SumianFlexboxLayout.SimpleLabelBean("放大消极", false),
            SumianFlexboxLayout.SimpleLabelBean("缩小积极", false),
            SumianFlexboxLayout.SimpleLabelBean("灾难化", false),
            SumianFlexboxLayout.SimpleLabelBean("情绪化", false),
            SumianFlexboxLayout.SimpleLabelBean("自责", false),
            SumianFlexboxLayout.SimpleLabelBean("不公平的比较", false),
            SumianFlexboxLayout.SimpleLabelBean("后悔倾向", false))

    companion object {
        const val EXTRA_COGNITION_BIAS_CHECKED_LABELS = "extra_cognition_bias_checked_labels"

        fun startForResult(activity : Activity,requestCode: Int) {
            var intent = Intent(activity, MoodCognitionBiasActivity::class.java)
            activity.startActivityForResult(intent, requestCode)
        }

        fun startForResult(fragment : Fragment ,requestCode: Int) {
            var intent = Intent(fragment.activity, MoodCognitionBiasActivity::class.java)
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_mood_cognition_bias_layout
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(getString(R.string.mood_cognition_bias_title))

        var cognitionBiasAdapter = object : BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

                var view = SumianFlexboxLayout.getSimpleLabelTextView(this@MoodCognitionBiasActivity)
                SumianFlexboxLayout.updateLabelUi(view, false)
                view.text = mMoodCognitionBias[position].label
                return view
            }

            override fun getItem(position: Int): Any {
                return mMoodCognitionBias[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getCount(): Int {
                return mMoodCognitionBias.size
            }
        }

        var cognitionBiasItemListener = object : SumianFlexboxLayout.OnItemClickListener {
            override fun onItemClick(parent: SumianFlexboxLayout, view: View, position: Int, id: Long) {
                var label = mMoodCognitionBias[position]
                label.isChecked = !label.isChecked
                SumianFlexboxLayout.updateLabelUi(view as TextView, label.isChecked)
                if (label.isChecked) {
                    mCheckedLabels.add(label.label)
                } else {
                    mCheckedLabels.remove(label.label)
                }
            }
        }

        var onConfirmClickListener = View.OnClickListener {
            if (mCheckedLabels.isEmpty()) {
                ToastHelper.show(getString(R.string.mood_cognition_bias_no_checked_tip))
            } else {
                var intent = Intent()
                intent.putExtra(EXTRA_COGNITION_BIAS_CHECKED_LABELS, mCheckedLabels.toTypedArray())
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        var binding = DataBindingUtil.bind<ActivityMoodCognitionBiasLayoutBinding>(mood_cognition_bias_container)
        if (binding == null) {
            return
        }
        binding.data = ActivityMoodCognitionBiasData(cognitionBiasAdapter, cognitionBiasItemListener, onConfirmClickListener)
    }
}
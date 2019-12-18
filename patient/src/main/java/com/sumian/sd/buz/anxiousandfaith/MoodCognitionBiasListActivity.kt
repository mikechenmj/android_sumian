package com.sumian.sd.buz.anxiousandfaith

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sumian.common.helper.ToastHelper
import com.sumian.common.widget.SmoothCheckBox
import com.sumian.sd.R
import com.sumian.sd.buz.anxiousandfaith.databinding.ActivityMoodCognitionBiasListData
import com.sumian.sd.databinding.ActivityMoodCognitionBiasListLayoutBinding
import kotlinx.android.synthetic.main.activity_mood_cognition_bias_list_layout.*

@SuppressLint("SetTextI18n")
class MoodCognitionBiasListActivity : TitleBaseActivity() {

    var mCheckedLabels = mutableListOf<String>()
    val mMoodCognitionBias = arrayOf(
            CognitionBiasBean("非黑即白", "我如果不能立刻睡着,我今晚等于没睡过", false),
            CognitionBiasBean("以偏概全", "这次失败，说明我在这方面不行", false),
            CognitionBiasBean("选择性关注", "下雨了，我只看到我没带伞（没看到空气清新了）", false),
            CognitionBiasBean("妄下结论", "他今天没有和我说话，一定是对我有意见", false),
            CognitionBiasBean("预测未来", "我今晚肯定又睡不着了", false),
            CognitionBiasBean("放大消极", "今天睡不着,明天我可能工作会出很多错", false),
            CognitionBiasBean("贴标签", "我昨晚也没有睡好,我是一个永远睡不好觉的人了", false),
            CognitionBiasBean("灾难化", "今晚睡不好明天可能要被炒鱿鱼了", false),
            CognitionBiasBean("情绪化", "我心情郁闷，所以我一定不能很快睡着", false),
            CognitionBiasBean("自责", "都是因为我的粗心，才会打碎花瓶", false),
            CognitionBiasBean("不公平的比较", "我老公一躺床上就睡着了,我需要20分钟才能睡", false),
            CognitionBiasBean("后悔倾向", "早上的时候我应该先喝咖啡再开始工作,没早点喝咖啡,到了中午又不敢喝,一整天工作都不在状态,我真应该早点喝.哎…", false))

    companion object {
        const val EXTRA_COGNITION_BIAS_CHECKED_LABELS = "extra_cognition_bias_checked_labels"

        fun startForResult(activity: Activity, requestCode: Int) {
            var intent = Intent(activity, MoodCognitionBiasListActivity::class.java)
            activity.startActivityForResult(intent, requestCode)
        }

        fun startForResult(fragment: Fragment, requestCode: Int) {
            var intent = Intent(fragment.activity, MoodCognitionBiasListActivity::class.java)
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_mood_cognition_bias_list_layout
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(getString(R.string.mood_cognition_bias_title))
        var moodCognitionBiasListAdapter = MoodCognitionBiasListAdapter(mMoodCognitionBias, mCheckedLabels)
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

        var binding = DataBindingUtil.bind<ActivityMoodCognitionBiasListLayoutBinding>(mood_cognition_bias_container)
        if (binding == null) {
            return
        }
        binding.data = ActivityMoodCognitionBiasListData(moodCognitionBiasListAdapter, onConfirmClickListener)
        var recy = RecyclerView(this)
        recy.adapter
    }

    class MoodCognitionBiasListAdapter(private var mMoodCognitionBias: Array<CognitionBiasBean>,
                                       private var mCheckedLabels: MutableList<String>)
        : RecyclerView.Adapter<MoodCognitionBiasListAdapter.ViewHolder>() {

        private var inflater: LayoutInflater? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            if (inflater == null) {
                inflater = LayoutInflater.from(parent.context)
            }
            var view = inflater!!.inflate(R.layout.item_mood_cognition_bias_layout, parent, false)
            var viewHolder = ViewHolder(view)
            var checked = view.findViewById<SmoothCheckBox>(R.id.cb_checked)
            view.setOnClickListener {
                checked.setChecked(!checked.isChecked, true)
            }
            checked.setOnCheckedChangeListener { _, isChecked ->
                var title = viewHolder.title?.text.toString()
                if (isChecked) {
                    if (!mCheckedLabels.contains(title)) {
                        mCheckedLabels.add(title)
                    }
                }else {
                    mCheckedLabels.remove(title)
                }
            }
            return viewHolder
        }

        override fun getItemCount(): Int {
            return mMoodCognitionBias.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            var item = mMoodCognitionBias[position]
            holder.title?.text = item.title
            holder.summary?.text = item.summary
            holder.checked?.isChecked = item.isChecked
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var title: TextView? = view.findViewById(R.id.tv_title)
            var summary: TextView? = view.findViewById(R.id.tv_summary)
            var checked: SmoothCheckBox? = view.findViewById(R.id.cb_checked)
        }
    }

    class CognitionBiasBean(var title: String, var summary: String, var isChecked: Boolean)
}
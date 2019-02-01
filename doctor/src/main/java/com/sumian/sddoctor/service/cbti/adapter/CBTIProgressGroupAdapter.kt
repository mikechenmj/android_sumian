package com.sumian.sddoctor.service.cbti.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sumian.sddoctor.R
import com.sumian.sddoctor.patient.bean.Patient
import com.sumian.sddoctor.patient.widget.GroupTagTipsView
import com.sumian.sddoctor.service.cbti.ITrigger
import com.sumian.sddoctor.service.cbti.bean.CBTIProgressGroup
import com.sumian.sddoctor.service.cbti.widget.CBTIProgressPatientView
import java.util.*

class CBTIProgressGroupAdapter : RecyclerView.Adapter<CBTIProgressGroupAdapter.ViewHolder>() {

    private var iTrigger: ITrigger? = null

    private val mItems: MutableList<CBTIProgressGroup>  by lazy {
        mutableListOf<CBTIProgressGroup>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(itemView = LayoutInflater.from(parent.context).inflate(R.layout.lay_item_group_patient_view, parent, false))
        viewHolder.itemView.tag = viewHolder
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = mItems[position]
        holder.initView(group, iTrigger!!, position == mItems.size - 1)
    }

    fun setOnTrigger(iTrigger: ITrigger): CBTIProgressGroupAdapter {
        this.iTrigger = iTrigger
        return this
    }

    fun getItem(position: Int): CBTIProgressGroup {
        return mItems[position]
    }

    fun clear() {
        mItems.clear()
        notifyDataSetChanged()
    }

    fun resetItems(items: MutableList<CBTIProgressGroup>) {
        clear()
        addItems(items)
    }

    fun addItems(items: MutableList<CBTIProgressGroup>) {
        if (items.isEmpty()) return
        val insertPosition = mItems.size
        mItems.addAll(items)
        notifyItemRangeInserted(insertPosition, items.size)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), GroupTagTipsView.OnTagTipsCallback {

        private var group: CBTIProgressGroup? = null

        private var iTrigger: ITrigger? = null

        private val tagTipsView: GroupTagTipsView by lazy {
            val groupTagTipsView = itemView.findViewById<GroupTagTipsView>(R.id.group_tag_tips_view)
            groupTagTipsView.setOnTagTipsCallback(this)
            return@lazy groupTagTipsView
        }

        private val divider: View by lazy {
            itemView.findViewById<View>(R.id.divider)
        }

        private val container: LinearLayout by lazy {
            itemView.findViewById<LinearLayout>(R.id.lay_container)
        }

        private val patientCount: TextView by lazy {
            itemView.findViewById<TextView>(R.id.tv_patient_count)
        }

        fun initView(group: CBTIProgressGroup, iTrigger: ITrigger, isShowPatientCount: Boolean) {
            this.iTrigger = iTrigger
            this.group = group
            tagTipsView.setTagTips(group.formatTagTips())
            divider.visibility = if (group.count <= 0) View.GONE else View.VISIBLE
            if (!group.isShow || group.count <= 0) {
                tagTipsView.setIsShow(false)
                appendPatients(group.key, null)
            } else {
                tagTipsView.setIsShow(true)
                appendPatients(group.key, group.users!!)
            }

            if (isShowPatientCount) {
                patientCount.text = String.format(Locale.getDefault(), "%s", " - 总共${group.allPatientsCount}名CBTI患者 -")
                patientCount.visibility = View.VISIBLE
            } else {
                patientCount.visibility = View.GONE
            }

        }

        override fun showGroup() {//加载对应 patient list
            container.removeAllViews()
            mItems[adapterPosition].isShow = true
            iTrigger?.onTrigger(adapterPosition, group!!)
        }

        override fun hideGroup() {//隐藏或者删除对应 patient list
            mItems[adapterPosition].isShow = false
            container.removeAllViews()
        }

        private fun appendPatients(cbtiProgressType: String, patients: MutableList<Patient>?) {
            container.removeAllViews()
            if (patients == null || patients.isEmpty()) {
                container.visibility = View.GONE
            } else {
                patients.forEachIndexed { index, patient ->
                    val cbtiProgressPatientView = CBTIProgressPatientView(itemView.context)
                    cbtiProgressPatientView.invalidCBTIProgressPatient(cbtiProgressType, patient, index == patients.size - 1)
                    container.addView(cbtiProgressPatientView)
                }
                container.visibility = View.VISIBLE
            }
        }
    }
}
@file:Suppress("UsePropertyAccessSyntax")

package com.sumian.sddoctor.patient.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sumian.common.image.ImageLoader
import com.sumian.sddoctor.R
import com.sumian.sddoctor.patient.activity.PatientInfoActivity
import com.sumian.sddoctor.patient.bean.Patient
import com.sumian.sddoctor.util.TimeUtil
import com.sumian.sddoctor.widget.adapter.BaseRecyclerAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.lay_item_patient.*
import java.util.*


class PatientAdapter constructor(context: Context) : BaseRecyclerAdapter<Patient>(context, BaseRecyclerAdapter.ONLY_FOOTER) {

    override fun onCreateDefaultViewHolder(parent: ViewGroup?, type: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.lay_item_patient, parent, false))
    }

    override fun onBindDefaultViewHolder(holder: RecyclerView.ViewHolder?, item: Patient?, position: Int) {
        holder?.let {
            (holder as ViewHolder).initView(item, position == mItems.size - 1)
        }
    }

    @Suppress("DEPRECATION")
    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView

        @SuppressLint("SetTextI18n")
        fun initView(item: Patient?, @Suppress("UNUSED_PARAMETER") isHideDivider: Boolean) {
            itemView.setOnClickListener {
                PatientInfoActivity.show(itemView.context, item?.id!!, item.consulted)
            }

            item?.let { it ->
                load(item.avatar)

                item.invalidTagView(iv_patient_level)

                tv_name.text = item.getNameOrNickname()

                tv_gender_and_age.visibility = if ((TextUtils.isEmpty(item.gender) || "secrecy" == item.gender) && TextUtils.isEmpty(it.age)) {
                    View.GONE
                } else {
                    val gender = item.formatGender()

                    val sexAndAge: String = if (gender == "保密" || gender == "") {
                        item.age + "岁"
                    } else {
                        "${item.formatGender()}${item.formatAge()}"
                    }

                    val spannableStringBuilder = SpannableStringBuilder(sexAndAge)
                    if (sexAndAge.contains("丨")) {
                        spannableStringBuilder.setSpan(ForegroundColorSpan(itemView.getResources().getColor(R.color.l2_color)), sexAndAge.indexOf("丨"), sexAndAge.indexOf("丨") + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                    }

                    tv_gender_and_age.text = spannableStringBuilder

                    View.VISIBLE
                }

                tv_add_date.text = TimeUtil.formatLineToday(Date().apply {
                    time = it.bound_at * 1000L
                    tv_add_date.visibility = View.VISIBLE
                })

                if (item.consulted == 1) {
                    tv_none_faced.visibility = View.GONE
                } else {
                    tv_none_faced.visibility = View.VISIBLE
                }

                // vDivider.visibility = if (isHideDivider) View.INVISIBLE else View.VISIBLE
            }
        }

        private fun load(url: String?) {
            ImageLoader.loadImage(url
                    ?: "", iv_avatar, R.mipmap.ic_info_avatar_patient, R.mipmap.ic_info_avatar_patient)
        }

    }
}
package com.sumian.sd.examine.main.consultant

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.common.image.ImageLoader
import com.sumian.sd.R
import com.sumian.sd.buz.account.model.AccountManager
import kotlinx.android.synthetic.main.examine_consult_msg.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter

class ExamineConsultantMsgActivity : BaseActivity() {

    private val mList = mutableListOf<String>().apply {
        add("您好，我是您的专属睡眠医生，有什么睡眠问题都可以留言咨询我。")
    }
    private val mConsultAdapter by lazy {
        object : RecyclerView.Adapter<ConsultViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultViewHolder {
                val view = if (viewType == 0) {
                    LayoutInflater.from(this@ExamineConsultantMsgActivity)
                            .inflate(R.layout.examine_consult_msg_item_left, parent, false).apply {
                                findViewById<ImageView>(R.id.iv_avatar).setImageResource(R.drawable.ic_chat_left_default)
                            }
                } else {
                    LayoutInflater.from(this@ExamineConsultantMsgActivity)
                            .inflate(R.layout.examine_consult_msg_item_right, parent, false).apply {
                                ImageLoader.loadImage(AccountManager.userInfo!!.avatar,
                                        findViewById<ImageView>(R.id.iv_avatar), errorId = R.drawable.ic_chat_left_default)
                            }
                }
                return ConsultViewHolder(view)
            }

            override fun getItemCount(): Int {
                return mList.size
            }

            override fun onBindViewHolder(holder: ConsultViewHolder, position: Int) {
                holder.content.text = mList[position]
            }

            override fun getItemViewType(position: Int): Int {
                return if (position == 0) {
                    0
                } else {
                    1
                }
            }
        }
    }

    companion object {
        fun show(title: String, tip: String) {
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("tip", tip)
            ActivityUtils.startActivity(bundle, ExamineConsultantMsgActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.examine_consult_msg
    }

    override fun initWidget() {
        super.initWidget()
        val currentTime = SimpleDateFormat(" yyyy年MM月dd日 HH:mm:ss", Locale.getDefault()).format(Date())
        tv_current_time.text = currentTime
        examine_title_bar.setOnBackClickListener { finish() }
        val bundle = intent.extras
        if (bundle != null) {
            examine_title_bar.setTitle(bundle.getString("title"))
            mList[0] = bundle.getString("tip") ?: "您好，我是您的专属睡眠医生，有什么睡眠问题都可以留言咨询我。"
        }
        rv_chat_list.adapter = mConsultAdapter
        rv_chat_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    fun onClick(view: View) {
        val text = et_input.text
        if (text.isEmpty()) {
            ToastHelper.show("请输入内容")
            return
        }
        mList.add(text.toString())
        et_input.setText("")
        mConsultAdapter.notifyDataSetChanged()
    }

    class ConsultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.iv_avatar)
        val content: TextView = view.findViewById(R.id.tv_content)
    }
}
package com.sumian.sd.service.cbti.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.image.ImageLoader
import com.sumian.sd.R
import com.sumian.sd.service.cbti.bean.MessageBoard
import de.hdodenhof.circleimageview.CircleImageView

class CBTIMessageBoardAdapter(context: Context) : BaseRecyclerAdapter<MessageBoard>(context) {

    companion object {
        const val MSG_SELF_LIST_TYPE = 0x01
        const val MSG_NORMAL_LIST_TYPE = 0x02
    }

    private var msgType: Int = MSG_NORMAL_LIST_TYPE

    private var mDelCallback: OnDelCallback? = null

    override fun onCreateDefaultViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lay_item_cbti_message_board, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: MessageBoard, position: Int) {
        val messageBoard = mItems[position]
        (holder as ViewHolder).initView(messageBoard)
    }

    fun setMsgType(msgType: Int = MSG_NORMAL_LIST_TYPE): CBTIMessageBoardAdapter {
        this.msgType = msgType
        return this
    }

    fun setDelCallback(delCallback: OnDelCallback): CBTIMessageBoardAdapter {
        this.mDelCallback = delCallback
        return this
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvNickName by lazy {
            itemView.findViewById<TextView>(R.id.tv_nickname)
        }

        private val civAvatar by lazy {
            itemView.findViewById<CircleImageView>(R.id.civ_avatar)
        }

        private val tvTopping by lazy {
            itemView.findViewById<TextView>(R.id.tv_topping)
        }

        private val tvWriteTime by lazy {
            itemView.findViewById<TextView>(R.id.tv_write_time)
        }

        private val tvDel by lazy {
            itemView.findViewById<TextView>(R.id.tv_del)
        }

        private val tvMessageBoard by lazy {
            itemView.findViewById<TextView>(R.id.tv_message_board)
        }

        private val tvReply by lazy {
            itemView.findViewById<TextView>(R.id.tv_replay_msg)
        }

        private val layReply by lazy {
            itemView.findViewById<LinearLayout>(R.id.lay_replay)
        }

        fun initView(item: MessageBoard, msgType: Int = MSG_NORMAL_LIST_TYPE) {
            tvNickName.text = item.formatNickName()
            ImageLoader.loadImage(item.commenter.avatar,
                    civAvatar,
                    R.mipmap.ic_info_avatar_patient,
                    R.mipmap.ic_info_avatar_patient)
            tvTopping.visibility = if (item.isTopping()) View.VISIBLE else View.INVISIBLE
            item.showReply(tvReply, layReply)
            tvWriteTime.text = item.formatWriteTime()
            tvWriteTime.visibility = View.GONE
            tvDel.visibility = if (msgType == MSG_SELF_LIST_TYPE) View.VISIBLE else View.GONE
            tvDel.setOnClickListener {
                mDelCallback?.delCallback(item)
            }
            tvMessageBoard.text = item.message
        }

    }

    interface OnDelCallback {
        fun delCallback(item: MessageBoard)
    }
}
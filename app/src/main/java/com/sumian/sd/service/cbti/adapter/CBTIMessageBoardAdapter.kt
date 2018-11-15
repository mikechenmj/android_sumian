package com.sumian.sd.service.cbti.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.image.ImageLoader
import com.sumian.sd.R
import com.sumian.sd.service.cbti.bean.MessageBoard
import de.hdodenhof.circleimageview.CircleImageView

class CBTIMessageBoardAdapter(context: Context) : BaseRecyclerAdapter<MessageBoard>(context) {

    override fun onCreateDefaultViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lay_item_cbti_message_board, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: MessageBoard, position: Int) {
        val messageBoard = mItems[position]
        (holder as ViewHolder).initView(messageBoard)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvNickName by lazy {
            itemView.findViewById<TextView>(R.id.tv_nickname)
        }

        private val civAvatar by lazy {
            itemView.findViewById<CircleImageView>(R.id.civ_avatar)
        }

        private val tvWriteTime by lazy {
            itemView.findViewById<TextView>(R.id.tv_write_time)
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

        fun initView(item: MessageBoard) {
            tvNickName.text = item.formatNickName()
            ImageLoader.loadImage(item.commenter.avatar,
                    civAvatar,
                    R.mipmap.ic_info_avatar_patient,
                    R.mipmap.ic_info_avatar_patient)
            item.showReply(tvReply, layReply)
            tvWriteTime.text = item.formatWriteTime()
            tvMessageBoard.text = item.message

        }
    }
}
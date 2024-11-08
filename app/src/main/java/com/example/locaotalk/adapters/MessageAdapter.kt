package com.example.locaotalk.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.locaotalk.databinding.ItemMessageBinding
import com.example.locaotalk.model.Message

class MessageAdapter : ListAdapter<Message, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    class MessageViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            if (message.contentType == "text") {
                // 텍스트 메시지 설정
                binding.textViewContent.text = message.content
                binding.textViewContent.visibility = View.VISIBLE
                binding.imageViewMessage.visibility = View.GONE
            } else if (message.contentType == "image") {
                // 이미지 메시지 설정
                val decodedBytes = Base64.decode(message.fileUrl, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                binding.imageViewMessage.setImageBitmap(bitmap)

                binding.textViewContent.visibility = View.GONE
                binding.imageViewMessage.visibility = View.VISIBLE
            }
        }
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.messageId == newItem.messageId
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}

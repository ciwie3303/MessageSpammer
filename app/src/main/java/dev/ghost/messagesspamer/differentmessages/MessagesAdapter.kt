package dev.ghost.messagesspamer.differentmessages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.ghost.messagesspamer.MessageModel
import dev.ghost.messagesspamer.databinding.ItemMessageBinding

class MessagesAdapter : RecyclerView.Adapter<MessageViewHolder>() {
    private val items = mutableListOf<MessageModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    fun submitList(newItems: List<MessageModel>) {
        items.clear()
        for (message in newItems)
            items.add(message)
        notifyDataSetChanged()
    }
}
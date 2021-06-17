package dev.ghost.messagesspamer.differentmessages

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dev.ghost.messagesspamer.MessageModel
import dev.ghost.messagesspamer.databinding.ItemMessageBinding

class MessageViewHolder(val itemMessageBinding: ItemMessageBinding) :
    RecyclerView.ViewHolder(itemMessageBinding.root) {

    fun bind(messageModel: MessageModel) {
        itemMessageBinding.itemMessageText.setText(messageModel.message)
        itemMessageBinding.itemPhoneText.setText(messageModel.phone)
    }
}
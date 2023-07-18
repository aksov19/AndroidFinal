package ge.aksovreli.messengerapp.viewmodels.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import ge.aksovreli.messengerapp.ChatItem

class ChatViewModel: ViewModel() {
    companion object {
        fun getViewModelFactory(context: Context): ChatViewModelFactory {
            return ChatViewModelFactory(context)
        }
    }

    fun getMessagesBetween(uid1: String, uid2: String) {

    }

    fun addMessageBetween(uid1: String, uid2: String, chatItem: ChatItem) {

    }
}
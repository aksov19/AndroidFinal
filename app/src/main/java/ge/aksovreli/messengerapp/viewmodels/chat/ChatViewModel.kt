package ge.aksovreli.messengerapp.viewmodels.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import ge.aksovreli.messengerapp.MessageItem

class ChatViewModel: ViewModel() {
    companion object {
        fun getViewModelFactory(context: Context): ChatViewModelFactory {
            return ChatViewModelFactory(context)
        }
    }

    fun getMessagesBetweenUsers(uid1: String, uid2: String) {

    }

    fun addMessageBetweenUsers(uid1: String, uid2: String, messageItem: MessageItem) {

    }
}
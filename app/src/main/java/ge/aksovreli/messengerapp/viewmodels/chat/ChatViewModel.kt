package ge.aksovreli.messengerapp.viewmodels.chat

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.aksovreli.messengerapp.MessageItem
import ge.aksovreli.messengerapp.models.User
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {
    companion object {
        fun getViewModelFactory(context: Context): ChatViewModelFactory {
            return ChatViewModelFactory(context)
        }
    }

    fun getUserByUid(uid: String): LiveData<Pair<String?, User?>> {
        val ret = MutableLiveData<Pair<String?, User?>>()

        viewModelScope.launch {
            val userDB = Firebase.database.getReference("users").child(uid)
            userDB.get().addOnSuccessListener { snapshot ->
                ret.postValue(Pair(null, snapshot.getValue(User::class.java)))
            }.addOnFailureListener {
                ret.postValue(Pair(it.message, null))
            }
        }

        return ret
    }

    fun getMessagesBetweenUsers(uid1: String, uid2: String): LiveData<Pair<String?, ArrayList<MessageItem>>> {
        val ret = MutableLiveData<Pair<String?, ArrayList<MessageItem>>>()

        viewModelScope.launch {
            val messagesDBPath = if (uid1 > uid2) {
                Firebase.database.getReference("messages").child(uid1).child(uid2)
            } else {
                Firebase.database.getReference("messages").child(uid2).child(uid1)
            }

            val messages = ArrayList<MessageItem>()

            messagesDBPath.get().addOnSuccessListener { snapshot ->
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(MessageItem::class.java)
                    if (item != null)
                        messages.add(item)
                }
                messages.sortBy { msg -> msg.time }
                ret.postValue(Pair(null, messages))
            }.addOnFailureListener{
                ret.postValue(Pair(it.message, messages))
            }
        }

        return ret
    }

    fun addMessageBetweenUsers(uid1: String, uid2: String, messageItem: MessageItem): LiveData<String?> {
        val errorMessage = MutableLiveData<String?>()

        viewModelScope.launch {
            val messagesDBPath = if (uid1 > uid2) {
                Firebase.database.getReference("messages").child(uid1).child(uid2)
            } else {
                Firebase.database.getReference("messages").child(uid2).child(uid1)
            }

            messagesDBPath.push().key?.let {
                messagesDBPath.child(it).setValue(messageItem)
            }

            errorMessage.postValue(null)
        }

        return errorMessage
    }
}
package ge.aksovreli.messengerapp.viewmodels.user

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.aksovreli.messengerapp.models.ChatItem
import ge.aksovreli.messengerapp.models.MessageItem
import ge.aksovreli.messengerapp.models.User
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class UserViewModel : ViewModel() {
    private val userReference = Firebase.database.getReference("users")

    companion object {
        fun getViewModelFactory(context: Context): UserViewModelFactory {
            return UserViewModelFactory(context)
        }
    }


    fun getUsers(query: String, onUsersLoaded: (MutableList<User>, MutableList<String>) -> Unit) {
        userReference.orderByChild("nickname")
            .startAt(query)
            .endAt("$query\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userList = mutableListOf<User>()
                    val userUids = mutableListOf<String>()

                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        val uid = userSnapshot.key
                        user?.let { userList.add(it) }
                        uid?.let { userUids.add(it) }
                    }
                    onUsersLoaded(userList, userUids)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any error that occurs during the query
                }
            })
    }


    fun getFriends(clear:() -> Unit, onFriendLoaded: (ChatItem) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myUid = currentUser!!.uid
        val friendsReference = Firebase.database.getReference("messages").child(myUid)

        Log.v("get_friend_list", myUid)
        friendsReference
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    clear()
                    for (childSnapshot in snapshot.children) {
                        val userId = childSnapshot.key
                        if (userId != myUid) {
                            getLastMessage(
                                userId!!,
                                friendsReference.child(userId.toString()),
                                onFriendLoaded
                            )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any error that occurs during the query
                }
            })
    }

    private fun getLastMessage(
        uid: String,
        ref: DatabaseReference,
        onFriendLoaded: (ChatItem) -> Unit
    ) {
        ref.orderByChild("time").limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val lastMessageSnapshot = dataSnapshot.children.first()
                        val lastMessage = lastMessageSnapshot.getValue(MessageItem::class.java)
//                                        onLastMessageLoaded(lastMessage)
                        getUserById(uid) { user ->
                            if (lastMessage != null) {
                                onFriendLoaded(
                                    ChatItem(
                                        name = user.nickname.toString(),
                                        last_message = lastMessage.message.toString(),
                                        date = lastMessage.time!!,
                                        avatar = user.imgURI.toString(),
                                        uid = uid
                                    )
                                )
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })

    }

    fun getUserById(uid: String, onUserLoaded: (User) -> Unit) {
        userReference.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    onUserLoaded(user!!)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

    }

    fun formatTime(milliseconds: Long): String {
        val currentTime = System.currentTimeMillis()
        val diff = currentTime - milliseconds
        val diffMinutes = diff / (60 * 1000)
        val diffHours = diff / (60 * 60 * 1000)

        return when {
            diffHours < 1 -> "$diffMinutes min"
            diffHours == 1L -> "$diffHours hour"
            diffHours > 1 -> "$diffHours hours"
            else -> {
                val sdf = SimpleDateFormat("d MMM", Locale.getDefault())
                sdf.timeZone = TimeZone.getDefault()
                val date = Date(milliseconds)
                sdf.format(date)
            }
        }
    }
}
package ge.aksovreli.messengerapp.viewmodels.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.aksovreli.messengerapp.models.User
import ge.aksovreli.messengerapp.viewmodels.signin.SignInViewModelFactory

class SearchViewModel: ViewModel() {
    private val userReference = Firebase.database.getReference("users")

    companion object {
        fun getViewModelFactory(context: Context): SearchViewModelFactory {
            return SearchViewModelFactory(context)
        }
    }


    fun getUsers(query: String, onUsersLoaded: (MutableList<User>, MutableList<String>) -> Unit){
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


    fun getFriends(onUsersLoaded: (MutableList<String>) -> Unit){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myUid = currentUser!!.uid
        val messageReference = Firebase.database.getReference("messages").child(myUid)

        Log.v("get_friend_list", myUid)
        messageReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val usersSet = mutableListOf<String>()

                    for (childSnapshot in snapshot.children) {
                        val userId = childSnapshot.key
                        if (userId != null) {
                            Log.v("get_friend_list", userId)
                        }
                        if (userId != myUid) {
                            usersSet.add(userId!!)
                        }
                    }
                    onUsersLoaded(usersSet)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any error that occurs during the query
                }
            })
    }

}
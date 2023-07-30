package ge.aksovreli.messengerapp.viewmodels.search

import android.content.Context
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


    fun getUsers(query: String, onUsersLoaded: (MutableList<User>) -> Unit){
        userReference.orderByChild("nickname")
            .startAt(query)
            .endAt("$query\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userList = mutableListOf<User>()
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        user?.let { userList.add(it) }
                    }
                    onUsersLoaded(userList)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any error that occurs during the query
                }
            })
    }


    fun getFriends(query: String, onUsersLoaded: (MutableList<String>) -> Unit){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myUid = currentUser!!.uid
        val messageReference = Firebase.database.getReference("messages").child(myUid)

        messageReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val usersSet = mutableListOf<String>()

                    for (childSnapshot in snapshot.children) {
                        val userId = childSnapshot.key
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
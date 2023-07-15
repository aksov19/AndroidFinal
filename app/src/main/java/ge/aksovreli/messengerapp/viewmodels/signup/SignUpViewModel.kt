package ge.aksovreli.messengerapp.viewmodels.signup
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.aksovreli.messengerapp.models.User
import kotlinx.coroutines.launch

class SignUpViewModel: ViewModel() {
    companion object {
        fun getViewModelFactory(context: Context): SignUpViewModelFactory {
            return SignUpViewModelFactory(context)
        }
    }

    fun registerUser(user: User): LiveData<String?> {
        val errorMessage = MutableLiveData<String?>()

        viewModelScope.launch {
            val auth = Firebase.auth
            auth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val usersDB = Firebase.database.getReference("users")
                        val curUID = auth.currentUser?.uid
                        if (curUID == null) {
                            errorMessage.postValue("Error getting current user UID")
                        }
                        else {
                            usersDB.child(curUID).setValue(user)
                            errorMessage.postValue(null)
                        }
                    }
                    else {
                        errorMessage.postValue(task.exception?.message)
                    }
                }
        }

        return errorMessage
    }
}
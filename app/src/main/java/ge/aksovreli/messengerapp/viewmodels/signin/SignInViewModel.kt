package ge.aksovreli.messengerapp.viewmodels.signin

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.aksovreli.messengerapp.models.User
import kotlinx.coroutines.launch

class SignInViewModel: ViewModel() {
    companion object {
        fun getViewModelFactory(context: Context): SignInViewModelFactory {
            return SignInViewModelFactory(context)
        }
    }

    fun signInUser(user: User): LiveData<String?> {
        val errorMessage = MutableLiveData<String?>()

        viewModelScope.launch {
            val auth = Firebase.auth
            auth.signInWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        errorMessage.postValue(null)
                    }
                    else {
                        errorMessage.postValue(task.exception?.message)
                    }
                }
        }

        return errorMessage
    }
}
package ge.aksovreli.messengerapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ge.aksovreli.messengerapp.databinding.MainActivityBinding
import ge.aksovreli.messengerapp.R

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        binding = MainActivityBinding.inflate(layoutInflater)

        chooseActivity()
    }

    private fun chooseActivity(){
        val currentUser = Firebase.auth.currentUser

        // TODO: change start page according to saved user
        if (currentUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            println("user was not saved")
        } else {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            println("user was saved")
        }

        finish()
    }
}

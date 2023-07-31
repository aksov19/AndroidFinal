package ge.aksovreli.messengerapp.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ge.aksovreli.messengerapp.R
import ge.aksovreli.messengerapp.databinding.MainActivityBinding

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

        if (currentUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            println("user was not saved")
        } else {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            println("user was saved")
        }

        finish()
    }
}

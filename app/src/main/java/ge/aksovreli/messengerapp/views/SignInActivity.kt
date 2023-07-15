package ge.aksovreli.messengerapp.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ge.aksovreli.messengerapp.R
import ge.aksovreli.messengerapp.databinding.MainActivityBinding
import ge.aksovreli.messengerapp.databinding.SignInActivityBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: SignInActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        binding = SignInActivityBinding.inflate(layoutInflater)
    }

    fun onSignIn(view: View){

    }

    fun onSignUp(view: View){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)

        finish()
    }
}
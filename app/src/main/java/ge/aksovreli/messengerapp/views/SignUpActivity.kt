package ge.aksovreli.messengerapp.views

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ge.aksovreli.messengerapp.R
import ge.aksovreli.messengerapp.databinding.SignUpActivityBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: SignUpActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_activity)
        binding = SignUpActivityBinding.inflate(layoutInflater)
    }

    fun onSignUp(view: View){

    }
}
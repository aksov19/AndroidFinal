package ge.aksovreli.messengerapp.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ge.aksovreli.messengerapp.R
import ge.aksovreli.messengerapp.databinding.SignInActivityBinding
import ge.aksovreli.messengerapp.models.User
import ge.aksovreli.messengerapp.viewmodels.signin.SignInViewModel

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: SignInActivityBinding

    private val viewModel: SignInViewModel by viewModels {
        SignInViewModel.getViewModelFactory(
            applicationContext
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_activity)
        binding = SignInActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onSignIn(view: View){
        val nickname = binding.nicknameField.text.toString()
        val password = binding.passwordField.text.toString()

        if (nickname == "" || password == "") {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show()
            return
        }

        val user = User(
            nickname = nickname,
            password = password
        )

        viewModel.signInUser(user).observe(this) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
            // TODO: else redirect to user's page
        }
    }

    fun onSignUp(view: View){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)

        finish()
    }
}
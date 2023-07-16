package ge.aksovreli.messengerapp.views

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ge.aksovreli.messengerapp.R
import ge.aksovreli.messengerapp.databinding.SignUpActivityBinding
import ge.aksovreli.messengerapp.models.User
import ge.aksovreli.messengerapp.viewmodels.signup.SignUpViewModel

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: SignUpActivityBinding

    private val viewModel: SignUpViewModel by viewModels {
        SignUpViewModel.getViewModelFactory(
            applicationContext
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_activity)

        binding = SignUpActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onSignUp(view: View){
        val nickname = binding.nicknameField.text.toString()
        val password = binding.passwordField.text.toString()
        val profession = binding.whatIDoField.text.toString()

        if (nickname == "" || password == "" || profession == "") {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show()
            return
        }

        val user = User(
            nickname = nickname,
            password = password,
            profession = profession
        )

        viewModel.registerUser(user).observe(this) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
            // TODO: else redirect to user's page
        }
    }
}
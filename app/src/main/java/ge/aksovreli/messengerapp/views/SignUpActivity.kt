package ge.aksovreli.messengerapp.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tashila.pleasewait.PleaseWaitDialog
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

        val wait = PleaseWaitDialog(this)
        wait.setTitle("Signing Up")
        wait.setMessage("Please wait")
        wait.isCancelable = false
        wait.show()

        viewModel.registerUser(user).observe(this) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                wait.dismiss()
            } else {
                val intent = Intent(this, UserActivity::class.java)
                startActivity(intent)
                wait.dismiss()
                finish()
            }
        }
    }
}
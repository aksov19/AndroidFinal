package ge.aksovreli.messengerapp.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tashila.pleasewait.PleaseWaitDialog
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
            password = password,
        )

        val wait = PleaseWaitDialog(this)
        wait.setTitle("Signing In")
        wait.setMessage("Please wait")
        wait.isCancelable = false
        wait.show()

        viewModel.signInUser(user).observe(this) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, UserActivity::class.java)
                startActivity(intent)
            }
            wait.dismiss()
            finish()
        }
    }

    fun onSignUp(view: View){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)

        finish()
    }
}
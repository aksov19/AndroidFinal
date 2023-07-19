package ge.aksovreli.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.viewpager2.widget.ViewPager2
import ge.aksovreli.messengerapp.views.ChatActivity
import ge.aksovreli.messengerapp.views.SignInActivity

class UserActivity : AppCompatActivity() {

    private lateinit var settings_button: ImageButton
    private lateinit var home_button: ImageButton
    private lateinit var view_pager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        view_pager = findViewById(R.id.user_viewpager)
        view_pager.adapter = UserAdapter(this, arrayListOf(HomeFragment(), SettingsFragment()))

        home_button = findViewById(R.id.home_button)
        settings_button = findViewById(R.id.settings_button)

        home_button.setOnClickListener{
            if (view_pager.currentItem != 0){
                view_pager.currentItem = 0
            }
        }
        settings_button.setOnClickListener{
            if (view_pager.currentItem != 1){
                view_pager.currentItem = 1
            }
        }
    }

}
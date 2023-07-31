package ge.aksovreli.messengerapp.views

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ge.aksovreli.messengerapp.R
import ge.aksovreli.messengerapp.viewmodels.search.SearchItemListener
import ge.aksovreli.messengerapp.viewmodels.user.UserAdapter
import ge.aksovreli.messengerapp.viewmodels.user.UserViewModel

class UserActivity : AppCompatActivity(), SearchItemListener {

    private lateinit var floatingButton: FloatingActionButton
    private lateinit var settingsButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var viewPager: ViewPager2

    private val viewModel: UserViewModel by viewModels   {
        UserViewModel.getViewModelFactory(
            applicationContext
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        viewPager = findViewById(R.id.user_viewpager)
        viewPager.adapter = UserAdapter(this, arrayListOf(HomeFragment(viewModel, this), SettingsFragment()))

        homeButton = findViewById(R.id.home_button)
        settingsButton = findViewById(R.id.settings_button)
        floatingButton = findViewById(R.id.floating_button)

        homeButton.setOnClickListener{
            if (viewPager.currentItem != 0){
                viewPager.currentItem = 0
            }
        }
        settingsButton.setOnClickListener{
            if (viewPager.currentItem != 1){
                viewPager.currentItem = 1
            }
        }
        floatingButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra("called_by", "floating button")
            startActivity(intent)
        }
    }

    override fun onSearchItemClicked(uid: String?) {
        if (uid != null) {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("other_uid", uid)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Error getting uid", Toast.LENGTH_LONG).show()
        }
    }

}
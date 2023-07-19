package ge.aksovreli.messengerapp.views

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnKeyListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.aksovreli.messengerapp.ChatAdapter
import ge.aksovreli.messengerapp.MessageItem
import ge.aksovreli.messengerapp.R
import ge.aksovreli.messengerapp.databinding.ChatActivityBinding
import ge.aksovreli.messengerapp.viewmodels.chat.ChatViewModel
import kotlin.math.abs

class ChatActivity: AppCompatActivity(), AppBarLayout.OnOffsetChangedListener, OnClickListener, OnKeyListener {
    private lateinit var binding: ChatActivityBinding

    private val viewModel: ChatViewModel by viewModels {
        ChatViewModel.getViewModelFactory(
            applicationContext
        )
    }

    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private lateinit var userUid: String
    private lateinit var otherUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)
        binding = ChatActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appbar.addOnOffsetChangedListener(this)
        binding.toolbar.setNavigationOnClickListener(this)
        binding.MessageInputEditText.setOnKeyListener(this)


        // TODO: add getting signed in user and other user
        userUid = "1"
        otherUid = "2"

        // Loading info first time
        loadOtherUser()
        loadMessages()

        // Add message list update listener
        val messagesDBPath = if (userUid > otherUid) {
            Firebase.database.getReference("messages").child(userUid).child(otherUid)
        } else {
            Firebase.database.getReference("messages").child(otherUid).child(userUid)
        }
        messagesDBPath.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                loadMessages()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadOtherUser() {
        viewModel.getUserByUid(otherUid).observe(this) {
            if (it.first != null) {
                Toast.makeText(this, it.first, Toast.LENGTH_LONG).show()
            } else {
                binding.nameView.text = it.second?.nickname ?: ""
                binding.proffesionView.text = it.second?.profession ?: ""
                // TODO: replace image too
            }
        }
    }

    private fun loadMessages() {
        viewModel.getMessagesBetweenUsers(userUid, otherUid).observe(this) {
            if (it.first != null){
                Toast.makeText(this, it.first, Toast.LENGTH_LONG).show()
            }

            val adapter = ChatAdapter(it.second, userUid)
            binding.MessagesRecyclerView.adapter = adapter
        }
    }

    // Controls toolbar collapse and expand animations
    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (appBarLayout == null)
            return

        val maxScroll = appBarLayout.totalScrollRange
        val percentage = abs(verticalOffset) / (maxScroll.toFloat() + R.dimen.chat_info_margin)
        val percentageAbs = abs(verticalOffset) / (maxScroll.toFloat())

        binding.UserInfoLayout.scaleX = 1 - percentage
        binding.UserInfoLayout.scaleY = 1 - percentage

        var newBottomPadding = resources.getDimensionPixelOffset(R.dimen.chat_info_padding).toFloat()
        newBottomPadding *= (1 - percentageAbs)

        var newLeftPadding = resources.getDimensionPixelOffset(R.dimen.chat_info_padding).toFloat()
        newLeftPadding *= percentageAbs

        binding.userImageView.updatePadding(bottom = newBottomPadding.toInt())
        binding.UserInfoLayout.updatePadding(bottom = newBottomPadding.toInt(), left = (newLeftPadding * 1.5).toInt())
    }

    // Moves to previous page when navigation button is clicked
    override fun onClick(p0: View?) {
        // TODO: add logic when navigation button is clicked (moving back a page)
        Toast.makeText(this, "navigation button clicked", Toast.LENGTH_LONG).show()
    }

    // Adds message send listener on enter key
    override fun onKey(view: View?, actionId: Int, event: KeyEvent?): Boolean {
        if (
            actionId == EditorInfo.IME_ACTION_DONE ||
            (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
        ) {
            val txt = binding.MessageInputEditText.text.toString()
            binding.MessageInputEditText.setText("")
            view!!.hideKeyboard()

            val msg = MessageItem(userUid, txt, System.currentTimeMillis())

            viewModel.addMessageBetweenUsers(userUid, otherUid, msg).observe(this) { errorMsg ->
                if (errorMsg != null)
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }

            return true
        }
        return false
    }
}
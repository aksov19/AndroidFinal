package ge.aksovreli.messengerapp.views

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.view.updatePadding
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.tashila.pleasewait.PleaseWaitDialog
import ge.aksovreli.messengerapp.ChatAdapter
import ge.aksovreli.messengerapp.R
import ge.aksovreli.messengerapp.databinding.ChatActivityBinding
import ge.aksovreli.messengerapp.models.MessageItem
import ge.aksovreli.messengerapp.viewmodels.chat.AudioManager
import ge.aksovreli.messengerapp.viewmodels.chat.ChatViewModel
import kotlin.math.abs

class ChatActivity: AppCompatActivity(), AppBarLayout.OnOffsetChangedListener, OnClickListener {
    private lateinit var binding: ChatActivityBinding
    private lateinit var audioManager: AudioManager

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

        userUid = Firebase.auth.currentUser!!.uid
        otherUid = intent.getStringExtra("other_uid")!!

        val wait = PleaseWaitDialog(this)
        wait.setTitle("Loading messages")
        wait.setMessage("Please wait")
        wait.isCancelable = false
        wait.show()

        loadOtherUser()
        loadMessages()

        wait.dismiss()

        setListeners()
        askForPermissions()
    }

    private fun setListeners() {
        binding.appbar.addOnOffsetChangedListener(this)
        binding.toolbar.setNavigationOnClickListener(this)

        // Add message list update listener
        val messagesDBPath = Firebase.database.getReference("messages").child(userUid).child(otherUid)

        messagesDBPath.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                loadMessages()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun askForPermissions() {
        // ask for mic, read, write permissions
        val permissions = arrayListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        }
        ActivityCompat.requestPermissions(this, permissions.toArray(arrayOf()), 111)
        audioManager = AudioManager(this)
    }

    private fun loadOtherUser() {
        viewModel.getUserByUid(otherUid).observe(this) {
            if (it.first != null) {
                Toast.makeText(this, it.first, Toast.LENGTH_LONG).show()
            } else {
                binding.nameView.text = it.second?.nickname ?: ""
                binding.proffesionView.text = it.second?.profession ?: ""
                val imgUri = it.second?.imgURI
                if (imgUri != "null" && imgUri != "") {
                    Glide.with(this)
                        .load(imgUri)
                        .circleCrop()
                        .into(binding.userImageView)
                }
            }
        }
    }

    private fun loadMessages() {
        viewModel.getMessagesBetweenUsers(userUid, otherUid).observe(this) {
            if (it.first != null){
                Toast.makeText(this, it.first, Toast.LENGTH_LONG).show()
            }

            val adapter = ChatAdapter(it.second, userUid, audioManager)
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
        finish()
    }

    // Adds message send listener on enter key
    fun onSendButtonPressed(view: View) {
        val txt = binding.MessageInputEditText.text.toString()
        binding.MessageInputEditText.setText("")
        view.hideKeyboard()

        val curTime = System.currentTimeMillis()
        val msg = MessageItem(userUid, txt, curTime)
        val audioSavePath = "audio/${userUid}_${curTime}.3gp"

        if (audioManager.audioRecorded) {
//            Toast.makeText(this, "AUDIO RECORDED", Toast.LENGTH_LONG).show()
            uploadAudio(audioSavePath).observe(this) { success ->
                // TODO: process proper errors
                if (success)
                    msg.audioUri = audioSavePath

                viewModel.addMessageBetweenUsers(userUid, otherUid, msg).observe(this) { errorMsg ->
                    if (errorMsg != null)
                        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                }
            }
        } else {
//            Toast.makeText(this, "AUDIO NOT RECORDED", Toast.LENGTH_LONG).show()
            viewModel.addMessageBetweenUsers(userUid, otherUid, msg).observe(this) { errorMsg ->
                if (errorMsg != null)
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }


    fun onVoiceMessageClick(view: View) {
        val errMsg = audioManager.toggleAudioRecording()
        if (errMsg != null)
            Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show()
    }


    fun uploadAudio(savePath: String): LiveData<Boolean> {
        val audioStorageRef = Firebase.storage.reference
        val saveRef = audioStorageRef.child(savePath)

        val ret = MutableLiveData<Boolean>()
        
        // TODO: return proper errors
        saveRef.putFile(audioManager.recordedFile.toUri()).addOnSuccessListener {
            ret.postValue(true)
        }.addOnFailureListener {
            ret.postValue(false)
        }

        return ret
    }
}
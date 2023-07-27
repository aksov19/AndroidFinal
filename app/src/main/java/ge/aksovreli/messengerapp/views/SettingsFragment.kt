package ge.aksovreli.messengerapp.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ge.aksovreli.messengerapp.models.User as myUser
import com.bumptech.glide.Glide
import com.google.firebase.auth.EmailAuthProvider
import ge.aksovreli.messengerapp.R
import ge.aksovreli.messengerapp.models.User

class SettingsFragment : Fragment() {

    private lateinit var password: String
    private lateinit var signOutButton: Button
    private var auth = FirebaseAuth.getInstance()
    private lateinit var imgView: ImageView
    private lateinit var nicknameField: TextView
    private lateinit var occupationField: TextView
    private var imgUrl: String? = null
    private lateinit var occupation: String
    private lateinit var nickname: String
    private lateinit var view: View
    private lateinit var userReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_settings, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val currentUser = auth.currentUser
        currentUser?.let {
            val email = currentUser.email

            // Assuming you have a "users" node in your Firebase database
            userReference = FirebaseDatabase.getInstance().reference.child("users")
            if (email != null) {
                getUserData(email)
            }
        }
        imgView = view.findViewById<ImageView>(R.id.avatarView)
        imgView.setOnClickListener{
            uploadImage()
        }
        val updateButton = view.findViewById<Button>(R.id.updateButton)
        updateButton.setOnClickListener {
            updateInfo(getInfo())
        }
        signOutButton = view.findViewById<Button>(R.id.signOutButton)
        signOutButton.setOnClickListener {
            auth.signOut()

            // Redirect the user to the login screen or any other desired activity
            val intent = Intent(requireContext(), SignInActivity::class.java)
            startActivity(intent)

            // Optional: Clear back stack and finish the current activity to prevent the user from returning using the back button
            requireActivity().finishAffinity()        }
    }

    private fun getInfo(): myUser {
        return User(nickname = nicknameField.text.toString(), profession = occupationField.text.toString(), imgURI = imgUrl)
    }

    private fun updateInfo(user: myUser) {
        val currentUser = auth.currentUser
        currentUser?.let {
            val email = currentUser.email

            // Update the user data in Firebase based on the user's email
            userReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val userSnapshot = snapshot.children.first()
                            val userId = userSnapshot.key

                            userId?.let {
                                userReference.child(userId).setValue(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(requireContext(),"data successfully updated", Toast.LENGTH_LONG).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(requireContext(),"data update failed", Toast.LENGTH_LONG).show()
                                    }
                            }
                        } else {
                            // User not found
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(),"data update failed", Toast.LENGTH_LONG).show()
                    }
                })

            if(user.nickname != nickname){
                val credential = EmailAuthProvider.getCredential(currentUser.email!!, password)

                currentUser.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // User reauthentication successful, continue with changing the email
                            currentUser.updateEmail(user.email!!)
                                .addOnCompleteListener { task1 ->
                                    if (task1.isSuccessful) {
                                        // Email updated successfully
                                    } else {
                                        Toast.makeText(requireContext(),"data update failed", Toast.LENGTH_LONG).show()
                                    }
                                }                        } else {
                            Toast.makeText(requireContext(),"data update failed", Toast.LENGTH_LONG).show()
                        }
                    }
            }


        }
    }

    private val REQUEST_IMAGE_PICKER = 1001
    private fun uploadImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            imgUrl = selectedImage.toString()
            // Load the selected image into the ImageView using Glide
            Glide.with(this)
                .load(selectedImage)
                .circleCrop()
                .into(imgView)
        }
    }
    private fun getUserData(email: String) {
        userReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userSnapshot = snapshot.children.first()
                        nickname = userSnapshot.child("nickname").value.toString()
                        occupation = userSnapshot.child("profession").value.toString()
                        password = userSnapshot.child("password").value.toString()
                        imgUrl = userSnapshot.child("imgURI").value.toString()
                        displayUserData()
                    } else {
                        // User not found
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),"data loading failed", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun displayUserData(){
        occupationField = view.findViewById(R.id.occupationField)
        nicknameField = view.findViewById(R.id.nicknameField)
        nicknameField.text = nickname
        occupationField.text = occupation
        imgUrl?.let {
            Log.v("debug", it)
            if (imgUrl != "null" && imgUrl != "") {
                Glide.with(this)
                    .load(imgUrl)
                    .circleCrop()
                    .into(view.findViewById(R.id.avatarView))
            }
        }
    }

}
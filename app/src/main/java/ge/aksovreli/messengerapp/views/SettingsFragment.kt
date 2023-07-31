package ge.aksovreli.messengerapp.views

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ge.aksovreli.messengerapp.R
import ge.aksovreli.messengerapp.models.User
import java.io.IOException
import ge.aksovreli.messengerapp.models.User as myUser

class SettingsFragment : Fragment() {

    private var profileChanged: Boolean = false
    private lateinit var password: String
    private lateinit var signOutButton: Button
    private var auth = FirebaseAuth.getInstance()
    private lateinit var imgView: ImageView
    private lateinit var nicknameField: TextView
    private lateinit var occupationField: TextView
    private var imgUri: String? = null
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
            openImagePicker()
        }
        val updateButton = view.findViewById<Button>(R.id.updateButton)
        updateButton.setOnClickListener {
            getAndUpdateInfo()
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

    private fun getAndUpdateInfo() {
        if (profileChanged) {
            uploadImage() { url ->
                imgUri = url
                Log.v("profileChange", imgUri!!)
                updateInfo(User(
                    nickname = nicknameField.text.toString(),
                    profession = occupationField.text.toString(),
                    imgURI = imgUri,
                    password = password
                ))
            }
        } else {

            updateInfo(User(
                nickname = nicknameField.text.toString(),
                profession = occupationField.text.toString(),
                imgURI = imgUri,
                password = password
            ))
        }
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
                                Log.v("profileChange", "saving " + user.imgURI!!)
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
            Log.v("crashing", currentUser.email!!)
            Log.v("crashing", password)
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

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var filePath: Uri


    private fun openImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        profileChanged = true
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data!!
            try {
                Glide.with(this)
                    .load(filePath)
                    .circleCrop()
                    .into(view.findViewById(R.id.avatarView))

            //                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, filePath)
//                imgView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(onComplete: (downloadUrl: String) -> Unit) {
        Log.v("profileChange", "HERE!!")
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference

        // Create a reference to the desired location in Firebase Storage
        val imageRef: StorageReference = storageRef.child("images/" + System.currentTimeMillis() + ".jpg")

        // Upload the image to Firebase Storage
        imageRef.putFile(filePath)
            .addOnSuccessListener {
                // Image upload successful
                // You can now get the download URL to save it in the database or use it to display the image
                imageRef.downloadUrl.addOnCompleteListener { downloadUrlTask ->
                    if (downloadUrlTask.isSuccessful) {
                        onComplete(downloadUrlTask.result.toString())
//                        imgUri = downloadUrlTask.result.toString()
//                        Log.v("profileChange", "uploaded on " + imgUri!!)
                        // Save the downloadUrl to your database, if required
                    }
                }
            }
            .addOnFailureListener {
                // Image upload failed, handle the error
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
                        imgUri = userSnapshot.child("imgURI").value.toString()
                        Log.v("crashing", password)
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
        imgUri?.let {
            Log.v("profileChange", imgUri!!)
            if (imgUri != "null" && imgUri != "") {
                Glide.with(this)
                    .load(imgUri)
                    .circleCrop()
                    .into(view.findViewById(R.id.avatarView))
            }
        }    }

}
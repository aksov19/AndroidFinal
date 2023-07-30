package ge.aksovreli.messengerapp.views

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.aksovreli.messengerapp.R
import ge.aksovreli.messengerapp.models.SearchItem
import ge.aksovreli.messengerapp.models.User
import ge.aksovreli.messengerapp.viewmodels.search.SearchAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {
    private lateinit var adapter: SearchAdapter
    private lateinit var searchEditText: EditText
    private lateinit var backButton: ImageButton
    private lateinit var searchRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setListeners()
        if (intent.getStringExtra("called_by") == "floating button"){
            floatingSearch()
        } else {
            barSearch()
        }
    }

    private fun setListeners() {
        backButton = findViewById(R.id.searchBackButton)
        backButton.setOnClickListener {
            finish()
        }

        searchEditText = findViewById(R.id.search_edit_text)
        searchEditText.requestFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private var searchJob: Job? = null
    private fun placeholderSearch(){
        searchRV = findViewById(R.id.searchRV)
        adapter = SearchAdapter(mutableListOf())
        searchRV.adapter = adapter
        getUsers("")
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed in this case
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed in this case
            }

            override fun afterTextChanged(s: Editable?) {
                val searchText = s?.toString()?.trim()
                if (searchText != null && searchText.length >= 3) {
                    // Cancel the previous search job if it exists
                    searchJob?.cancel()
                    // Start a new search job with debounce
                    searchJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(1000) // Wait for 1 second after the user stops typing
                        // Perform the search based on the entered text here
                        // Call a function to send the query to Firebase and display the results.
                        getUsers(searchText)
                    }
                } else {
                    // Clear the search results or show all users if the search text is less than 3 characters.
                    // You can decide how you want to handle this case.
                }
            }
        })
    }

    private fun floatingSearch(){
        placeholderSearch()
    }

    private fun getUsers(query: String){
        val userReference = Firebase.database.getReference("users")
        userReference.orderByChild("nickname")
            .startAt(query)
            .endAt("$query\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Process the queried users here
                    val userList = mutableListOf<User>()
                    val userUids = mutableListOf<String>()

                    for (userSnapshot in snapshot.children) {
                        // Convert DataSnapshot to User object and add to the list
                        val user = userSnapshot.getValue(User::class.java)
                        val uid = userSnapshot.key
                        user?.let { userList.add(it) }
                        uid?.let { userUids.add(it) }
                    }

                    updateList(userList, userUids)
                }


                override fun onCancelled(error: DatabaseError) {
                    // Handle any error that occurs during the query
                }
            })
    }

    private fun updateList(userList: MutableList<User>, uids: MutableList<String>) {
        val searchList = mutableListOf<SearchItem>()
        for((user, uid) in userList.zip(uids)){
            searchList.add(userToSearchItem(user, uid))
        }
        adapter.updateData(searchList)
    }

    private fun barSearch(){
        placeholderSearch()
    }

    private fun userToSearchItem(user: User, uid: String) : SearchItem{
        return SearchItem(name = user.nickname, profession = user.profession, imgUrl = user.imgURI, uid = uid)
    }

}
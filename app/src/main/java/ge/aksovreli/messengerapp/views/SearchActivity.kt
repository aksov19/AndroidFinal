package ge.aksovreli.messengerapp.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import ge.aksovreli.messengerapp.viewmodels.search.SearchItemListener
import ge.aksovreli.messengerapp.viewmodels.search.SearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity(), SearchItemListener {
    private lateinit var adapter: SearchAdapter
    private lateinit var searchEditText: EditText
    private lateinit var backButton: ImageButton
    private lateinit var searchRV: RecyclerView

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModel.getViewModelFactory(
            applicationContext
        )
    }

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
    private fun floatingSearch(){
        searchRV = findViewById(R.id.searchRV)
        adapter = SearchAdapter(mutableListOf(), this)
        searchRV.adapter = adapter
        viewModel.getUsers(""){users, uids ->  updateList(users, uids)}
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val searchText = s?.toString()?.trim()
                if (searchText != null && searchText.length >= 3) {
                    searchJob?.cancel()
                    searchJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        viewModel.getUsers(searchText){users, uids ->  updateList(users, uids)}
                    }
                }
            }
        })
    }
    private fun barSearch(){
        searchRV = findViewById(R.id.searchRV)
        adapter = SearchAdapter(mutableListOf(), this)
        searchRV.adapter = adapter
        viewModel.getFriends(){friends -> updateFriends(friends, "")}
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val searchText = s?.toString()?.trim()
                if (searchText != null && searchText.length >= 3) {
                    searchJob?.cancel()
                    searchJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        viewModel.getFriends(){friends -> updateFriends(friends, searchText)}
                    }
                }
            }
        })
    }

    private fun updateFriends(userList: MutableList<String>, query: String) {
        val userReference = Firebase.database.getReference("users")
        for (uid in userList) {
            userReference.child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        if (user?.nickname!!.contains(query, ignoreCase = true))
                            adapter.addItem(userToSearchItem(user, uid))
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }

    private fun filterByName(usersList: MutableList<SearchItem>, name: String): MutableList<SearchItem> {
        val filteredUsers = usersList.filter { user ->
            user.name?.contains(name, ignoreCase = true) == true
        }.toMutableList()
        return filteredUsers
    }

    private fun updateList(userList: MutableList<User>, uids: MutableList<String>) {
        val searchList = mutableListOf<SearchItem>()
        for ((user, uid) in userList.zip(uids)) {
            searchList.add(userToSearchItem(user, uid))
        }
        adapter.updateData(searchList)
    }

    private fun userToSearchItem(user: User, uid: String) : SearchItem{
        return SearchItem(name = user.nickname, profession = user.profession, imgUrl = user.imgURI, uid = uid)
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
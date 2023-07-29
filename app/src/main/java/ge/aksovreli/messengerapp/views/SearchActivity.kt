package ge.aksovreli.messengerapp.views

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import ge.aksovreli.messengerapp.R
import ge.aksovreli.messengerapp.databinding.ChatActivityBinding
import ge.aksovreli.messengerapp.models.SearchItem
import ge.aksovreli.messengerapp.viewmodels.search.SearchAdapter

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var backButton: ImageButton
    private lateinit var searchRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchRV = findViewById(R.id.searchRV)
        val searchList = ArrayList<SearchItem>()
        val searchItem = SearchItem(name = "Name Surname", profession = "Profession")
        searchList.add(searchItem)
        searchList.add(searchItem)
        searchList.add(searchItem)
        searchList.add(searchItem)
        searchList.add(searchItem)
        searchRV.adapter = SearchAdapter(searchList)

        backButton = findViewById(R.id.searchBackButton)
        backButton.setOnClickListener {
            finish()
        }

        searchEditText = findViewById(R.id.search_edit_text)
        searchEditText.requestFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)

    }


}
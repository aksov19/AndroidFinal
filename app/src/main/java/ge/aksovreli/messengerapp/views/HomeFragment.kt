package ge.aksovreli.messengerapp.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ge.aksovreli.messengerapp.ChatItemAdapter
import ge.aksovreli.messengerapp.R
import ge.aksovreli.messengerapp.models.ChatItem

class HomeFragment : Fragment() {
    private lateinit var searchBar: EditText
    private lateinit var chats_rv: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chats_rv = view.findViewById(R.id.chats_rv)
        val chat_list = ArrayList<ChatItem>()
        val item = ChatItem(name = "Test Name", last_message = "text text text text text text text text text text text text ", date = "15 min")
        chat_list.add(item)
        chat_list.add(item)
        chat_list.add(item)
        chat_list.add(item)
        chat_list.add(item)
        chat_list.add(item)
        chat_list.add(item)
        chat_list.add(item)
        chat_list.add(item)
        chat_list.add(item)
        chat_list.add(item)
        chats_rv.adapter = ChatItemAdapter(chat_list)

        searchBar = view.findViewById(R.id.home_edit_text)
        searchBar.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
            requireActivity().finishAffinity()
        }

        searchBar.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val intent = Intent(requireContext(), SearchActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
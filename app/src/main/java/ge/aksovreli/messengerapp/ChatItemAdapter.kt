package ge.aksovreli.messengerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ge.aksovreli.messengerapp.models.ChatItem


class ChatItemAdapter(chatItems: ArrayList<ChatItem>) :
    RecyclerView.Adapter<ChatItemAdapter.ChatItemViewHolder>() {
    private val chatItems: ArrayList<ChatItem>

    // Constructor to initialize the chat items list
    init {
        this.chatItems = chatItems
    }

    // Create a ViewHolder for the chat item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return ChatItemViewHolder(itemView)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        val chatItem: ChatItem = chatItems[position]
        holder.nameTextView.setText(chatItem.name)
    }

    // Return the number of chat items
    override fun getItemCount(): Int {
        return chatItems.size
    }

    // ViewHolder class for caching views
    class ChatItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        var profileImageView: ImageView
        var nameTextView: TextView
//        var lastMessageTextView: TextView

        init {
//            profileImageView = itemView.findViewById(R.id.profileImageView)
            nameTextView = itemView.findViewById(R.id.nameTextView)
//            lastMessageTextView = itemView.findViewById(R.id.lastMessageTextView)
        }
    }
}
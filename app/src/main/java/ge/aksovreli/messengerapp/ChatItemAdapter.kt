package ge.aksovreli.messengerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ChatAdapter(chatItems: ArrayList<ChatItem>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private val chatItems: ArrayList<ChatItem>

    // Constructor to initialize the chat items list
    init {
        this.chatItems = chatItems
    }

    // Create a ViewHolder for the chat item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(itemView)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatItem: ChatItem = chatItems[position]
        holder.nameTextView.setText(chatItem.name)
    }

    // Return the number of chat items
    override fun getItemCount(): Int {
        return chatItems.size
    }

    // ViewHolder class for caching views
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

package ge.aksovreli.messengerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
        holder.nameTextView.text = chatItem.name
        holder.lastMessageView.text = chatItem.last_message
        holder.dateTextView.text = chatItem.date
        if (chatItem.avatar != "") {
            Glide.with(holder.itemView.context)
                .load(chatItem.avatar)
                .circleCrop()
                .into(holder.avatarView)
        }
        holder.itemView.setOnClickListener {
            //TODO: open chat page
        }
    }

    // Return the number of chat items
    override fun getItemCount(): Int {
        return chatItems.size
    }

    // ViewHolder class for caching views
    class ChatItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var avatarView: ImageView
        var nameTextView: TextView
        var lastMessageView: TextView
        var dateTextView: TextView

        init {
            avatarView = itemView.findViewById(R.id.avatarView)
            nameTextView = itemView.findViewById(R.id.nameTextView)
            lastMessageView = itemView.findViewById(R.id.lastMessageView)
            dateTextView = itemView.findViewById(R.id.dateView)
        }
    }
}
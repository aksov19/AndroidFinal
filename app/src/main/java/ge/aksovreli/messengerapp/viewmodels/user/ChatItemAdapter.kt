package ge.aksovreli.messengerapp.viewmodels.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ge.aksovreli.messengerapp.R
import ge.aksovreli.messengerapp.models.ChatItem
import ge.aksovreli.messengerapp.viewmodels.search.SearchItemListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class ChatItemAdapter(
    chatItems: MutableList<ChatItem>,
    private val searchItemListener: SearchItemListener
) :
    RecyclerView.Adapter<ChatItemAdapter.ChatItemViewHolder>() {
    private val chatItems: MutableList<ChatItem>

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
        holder.dateTextView.text = formatTime(chatItem.date)
        if (chatItem.avatar != "") {
            Glide.with(holder.itemView.context)
                .load(chatItem.avatar)
                .circleCrop()
                .into(holder.avatarView)
        }

        holder.itemView.setOnClickListener {
            searchItemListener.onSearchItemClicked(chatItem.uid)
        }
    }

    private fun formatTime(milliseconds: Long): String {
        val currentTime = System.currentTimeMillis()
        val diff = currentTime - milliseconds
        val diffMinutes = diff / (60 * 1000)
        val diffHours = diff / (60 * 60 * 1000)

        return when {
            diffHours < 1 -> "$diffMinutes min"
            diffHours == 1L -> "$diffHours hour"
            diffHours > 1 -> "$diffHours hours"
            else -> {
                val sdf = SimpleDateFormat("d MMM", Locale.getDefault())
                sdf.timeZone = TimeZone.getDefault()
                val date = Date(milliseconds)
                sdf.format(date)
            }
        }
    }

    // Return the number of chat items
    override fun getItemCount(): Int {
        return chatItems.size
    }

    fun addItem(message: ChatItem) {
        chatItems.add(message)
        chatItems.sortByDescending { item -> item.date }
        notifyDataSetChanged()
    }

    fun clear() {
        chatItems.clear()
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
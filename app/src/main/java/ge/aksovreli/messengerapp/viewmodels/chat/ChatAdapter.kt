package ge.aksovreli.messengerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ChatAdapter(private val messageItems: ArrayList<MessageItem>, private val signedInUid: String) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.chat_message_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return messageItems.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bindItem(messageItems[position], signedInUid)
    }

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var spaceLeft = view.findViewById<Space>(R.id.SpaceL)
        private var timeLeft = view.findViewById<TextView>(R.id.timeViewL)
        private var messageView = view.findViewById<TextView>(R.id.messageView)
        private var spaceRight = view.findViewById<Space>(R.id.SpaceR)
        private var timeRight = view.findViewById<TextView>(R.id.timeViewR)

        fun bindItem(item: MessageItem, signedInUid: String) {
            if (item.senderUid == signedInUid) {
                spaceLeft.visibility = View.VISIBLE
                timeLeft.visibility = View.VISIBLE
                messageView.setBackgroundResource(R.drawable.chat_item_background_right)
                spaceRight.visibility = View.GONE
                timeRight.visibility = View.GONE
            } else {
                spaceLeft.visibility = View.GONE
                timeLeft.visibility = View.GONE
                messageView.setBackgroundResource(R.drawable.chat_item_background_left)
                spaceRight.visibility = View.VISIBLE
                timeRight.visibility = View.VISIBLE
            }
        }
    }
}

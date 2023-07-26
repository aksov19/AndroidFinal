package ge.aksovreli.messengerapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList


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
        private var messageTextView = view.findViewById<TextView>(R.id.messageView)
        private var messageLayout = view.findViewById<LinearLayout>(R.id.MessageLinearLayout)
        private var messageButtonView = view.findViewById<ImageButton>(R.id.voiceMessageButton)
        private var spaceRight = view.findViewById<Space>(R.id.SpaceR)
        private var timeRight = view.findViewById<TextView>(R.id.timeViewR)

        fun bindItem(item: MessageItem, signedInUid: String) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = item.time!!
            val timeText = calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" + calendar.get(Calendar.MINUTE).toString()
            messageTextView.text = item.message
            timeLeft.text = timeText
            timeRight.text = timeText
            
            if (item.senderUid == signedInUid) {
                spaceLeft.visibility = View.VISIBLE
                timeLeft.visibility = View.VISIBLE

                messageLayout.setBackgroundResource(R.drawable.chat_item_background_right)
                messageTextView.setTextColor(Color.parseColor("#FFFFFF"))

                spaceRight.visibility = View.GONE
                timeRight.visibility = View.GONE
            } else {
                spaceLeft.visibility = View.GONE
                timeLeft.visibility = View.GONE

                messageLayout.setBackgroundResource(R.drawable.chat_item_background_left)
                messageTextView.setTextColor(Color.parseColor("#000000"))

                spaceRight.visibility = View.VISIBLE
                timeRight.visibility = View.VISIBLE
            }
        }
    }
}

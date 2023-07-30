package ge.aksovreli.messengerapp.viewmodels.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ge.aksovreli.messengerapp.R
import ge.aksovreli.messengerapp.models.SearchItem


class SearchAdapter(searchItems: MutableList<SearchItem>, private val searchItemListener: SearchItemListener) :
    RecyclerView.Adapter<SearchAdapter.SearchItemViewHolder>() {
    private var searchItems: MutableList<SearchItem>

    // Constructor to initialize the search items list
    init {
        this.searchItems = searchItems
    }

    // Create a ViewHolder for the search item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item, parent, false)
        return SearchItemViewHolder(itemView)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        val searchItem: SearchItem = searchItems[position]
        holder.nameTextView.text = searchItem.name
        holder.professionTextView.text = searchItem.profession
        if (searchItem.imgUrl != "") {
            Glide.with(holder.itemView.context)
                .load(searchItem.imgUrl)
                .circleCrop()
                .into(holder.avatarView)
        }

        holder.itemView.setOnClickListener {
            searchItemListener.onSearchItemClicked(searchItem.uid)
        }
    }

    fun updateData(newList: MutableList<SearchItem>){
        searchItems = newList
        notifyDataSetChanged()
    }

    // Return the number of search items
    override fun getItemCount(): Int {
        return searchItems.size
    }

    // ViewHolder class for caching views
    class SearchItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTextView: TextView
        var professionTextView: TextView
        var avatarView: ImageView

        init {
            nameTextView = itemView.findViewById(R.id.nameTextView)
            professionTextView = itemView.findViewById(R.id.professionTextView)
            avatarView = itemView.findViewById(R.id.searchAvatarView)
        }
    }
}
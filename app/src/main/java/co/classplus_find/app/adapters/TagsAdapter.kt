package co.classplus_find.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import co.classplus_find.app.R
import co.classplus_find.app.databinding.ItemTagsBinding

class TagsAdapter(var fragment: Fragment, var tagList: ArrayList<String>) : RecyclerView.Adapter<TagsAdapter.TagsViewHolder>() {

    private lateinit var onTagRemoved: OnTagRemoved

    init {
        if (fragment is OnTagRemoved)
            onTagRemoved = fragment as OnTagRemoved
    }

    inner class TagsViewHolder(itemsView: ItemTagsBinding) : RecyclerView.ViewHolder(itemsView.root) {
        var tag = itemsView.tag
    }

    fun setList(tagList: ArrayList<String>) {
        this.tagList = tagList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsViewHolder {
        val binding: ItemTagsBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_tags, parent, false);
        return TagsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagsViewHolder, position: Int) {
        val tag = tagList[position]
        holder.apply {
            holder.tag.text = tag
            holder.tag.setOnClickListener {
                tagList.removeAt(position)
                onTagRemoved.onTagRemoved(tagList,position)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    interface OnTagRemoved {
        fun onTagRemoved(tagList: ArrayList<String>, adapterPosition: Int)
    }
}
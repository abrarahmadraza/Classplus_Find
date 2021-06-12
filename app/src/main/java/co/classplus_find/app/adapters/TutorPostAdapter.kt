package co.classplus_find.app.adapters

import android.content.Context
import co.classplus_find.app.data.models.TutorPostModel
import co.classplus_find.app.databinding.ItemPostBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import co.classplus_find.app.R
import com.bumptech.glide.Glide

class TutorPostAdapter(var context: Context, var fragment: Fragment, var postList: ArrayList<TutorPostModel>) : RecyclerView.Adapter<TutorPostAdapter.PostsViewHolder>() {

    private lateinit var onPostClicked: OnPostClicked

    init {
        if (fragment is OnPostClicked)
            onPostClicked = fragment as OnPostClicked
    }

    inner class PostsViewHolder(itemsView: ItemPostBinding) : RecyclerView.ViewHolder(itemsView.root) {
        var caption = itemsView.caption
        var image = itemsView.image
    }

    fun setList(postList: ArrayList<TutorPostModel>) {
        this.postList = postList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val binding: ItemPostBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_post, parent, false);
        return PostsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val post = postList[position]
        Glide.with(context)
            .load(post.image)
            .placeholder(R.drawable.profile_pic)
            .error(R.drawable.profile_pic)
            .into(holder.image)
        holder.caption.text = post.caption
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    interface OnPostClicked {
        fun onPostClicked(postList: ArrayList<TutorPostModel>, adapterPosition: Int)
    }
}
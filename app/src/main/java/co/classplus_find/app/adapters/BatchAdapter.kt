package co.classplus_find.app.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import co.classplus_find.app.R
import co.classplus_find.app.data.models.BatchModel
import co.classplus_find.app.databinding.ItemBatchBinding

class BatchAdapter(var context: Context, var batchList: ArrayList<BatchModel>, var isTutor: Int) : RecyclerView.Adapter<BatchAdapter.BatchViewHolder>() {

    private lateinit var onBatchJoined: OnBatchJoined

    init {
        if (context is OnBatchJoined)
            onBatchJoined = context as OnBatchJoined
    }

    inner class BatchViewHolder(itemsView: ItemBatchBinding) : RecyclerView.ViewHolder(itemsView.root) {
        var name = itemsView.name
        var desc = itemsView.desc
        var date = itemsView.date
        var join = itemsView.btn
    }

    fun setList(batchList: ArrayList<BatchModel>) {
        this.batchList = batchList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatchViewHolder {
        val binding: ItemBatchBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_batch, parent, false);
        return BatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BatchViewHolder, position: Int) {
        val batch = batchList[position]
        holder.apply {
            name.text = batch.name
            desc.text = batch.description
            date.text = batch.date

            if(isTutor == 1){
                join.visibility = View.GONE
            }
            else{
                join.visibility = View.VISIBLE
            }

            holder.join.setOnClickListener {
                onBatchJoined.onBatchJoined(batchList[position],position)
            }
        }
    }

    override fun getItemCount(): Int {
        return batchList.size
    }

    interface OnBatchJoined {
        fun onBatchJoined(batchList: BatchModel, adapterPosition: Int)
    }
}
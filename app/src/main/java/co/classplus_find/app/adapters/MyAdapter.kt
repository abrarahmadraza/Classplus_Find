package co.classplus_find.app.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import co.classplus_find.app.R
import java.util.*

class MyAdapter(list: ArrayList<String>, c: Context) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    var l = list
    var context = c

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return l.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = l.get(position)
        holder.text.text = text
        holder.text.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(p0: View?): Boolean {
                Toast.makeText(context, "Text Copied!", Toast.LENGTH_SHORT).show()
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData? = ClipData.newPlainText("label", text)
                clipboard?.setPrimaryClip(clip as ClipData)
                return true
            }

        })
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text = itemView.findViewById<TextView>(R.id.textView)
    }
}
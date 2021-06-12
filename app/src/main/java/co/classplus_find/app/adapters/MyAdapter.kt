package co.classplus_find.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.classplus_find.app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot

class MyAdapter(list: ArrayList<DataSnapshot>, c: Context) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    var l = list
    var context = c
    var uid=FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return l.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var arr=l.get(position).value as ArrayList<String>
        val msg = arr.get(0)
        val sender=arr.get(1)
        val date=arr.get(2)
        if(sender==uid){
            holder.sent.visibility=View.VISIBLE
            holder.received.visibility=View.GONE
            holder.text2.text=msg
            holder.date2.text=date
        }else{
            holder.sent.visibility=View.GONE
            holder.received.visibility=View.VISIBLE
            holder.text1.text=msg
            holder.date1.text=date
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text1 = itemView.findViewById<TextView>(R.id.textView)
        var text2 = itemView.findViewById<TextView>(R.id.textView1)
        var date1 = itemView.findViewById<TextView>(R.id.date)
        var date2 = itemView.findViewById<TextView>(R.id.date1)
        var sent=itemView.findViewById<LinearLayout>(R.id.sent)
        var received=itemView.findViewById<LinearLayout>(R.id.received)
    }
}
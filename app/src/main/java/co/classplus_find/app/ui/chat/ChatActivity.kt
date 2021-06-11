package co.classplus_find.app.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import co.classplus_find.app.R
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.classplus_find.app.adapters.MyAdapter
import com.bumptech.glide.Glide
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {
    lateinit var recyclerView:RecyclerView
    lateinit var editText: EditText
    lateinit var sendBtn:ImageView
    var ref:DatabaseReference?=null
    var refRecipient:DatabaseReference?=null
    lateinit var list:ArrayList<String>
    lateinit var myAdapter: MyAdapter
    var pos=0
    var recipient=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerView=findViewById(R.id.recyclerView)
        editText=findViewById(R.id.editText)
        sendBtn=findViewById(R.id.sendBtn)

        findViewById<ImageView>(R.id.back).setOnClickListener {
            finish()
        }
        recipient = intent?.extras?.getString("recipient").toString()
        refRecipient=FirebaseDatabase.getInstance().getReference("users/"+recipient+"/teacher")
        refRecipient?.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("name").exists())
                        findViewById<TextView>(R.id.name).text=snapshot.child("name").value.toString()
                    if(snapshot.child("designation").exists())
                        findViewById<TextView>(R.id.designation).text=snapshot.child("designation").value.toString()
                    else
                        findViewById<TextView>(R.id.name).visibility= View.GONE
                    if(snapshot.child("photoUrl").exists())
                    {
                        Glide.with(this@ChatActivity)
                            .load(snapshot.child("photoUrl").value)
                            .placeholder(R.drawable.profile_pic)
                            .error(R.drawable.profile_pic)
                            .into(findViewById(R.id.pic))
                    }

                }
            }

        })

        ref=FirebaseDatabase.getInstance().getReference("chats/"+intent?.extras?.getString("chatId"))

        sendBtn.setOnClickListener {
            if(editText.text.toString()==""){
                Toast.makeText(applicationContext,"Pls Write Something!",Toast.LENGTH_SHORT).show()
            }else{
                ref?.push()?.setValue(editText.text.toString())
                editText.setText("")
            }
        }

        list= ArrayList()
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        myAdapter= MyAdapter(list,applicationContext)
        recyclerView.adapter=myAdapter

        ref?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var temp=ArrayList<String>()
                    for(snap:DataSnapshot in snapshot.children){
                        temp.add(snap.value.toString())

                        if(pos>=list.size)
                            list.add(snap.value.toString())
                        pos++
                    }
                    pos=0
                    myAdapter.notifyDataSetChanged()
                    recyclerView.smoothScrollToPosition(recyclerView.adapter!!.itemCount)
                    Log.i("listValue",list.toString())
                }
            }

        })

    }
}